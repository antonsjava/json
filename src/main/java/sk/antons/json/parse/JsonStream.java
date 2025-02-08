/*
 * Copyright 2018 Anton Straka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sk.antons.json.parse;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import sk.antons.json.JsonValue;
import sk.antons.json.match.Match;
import sk.antons.json.match.PathMatcher;
import sk.antons.json.match.SPM;
import sk.antons.json.parse.JsonScanner.Token;
import sk.antons.json.source.JsonSource;
import sk.antons.json.source.ReaderSource;
import sk.antons.json.source.StringSource;

/**
 * Json parer, which traverse json tree and produces stream of json values
 * according to given path matcher.
 *
 * Useful, when you have big json and you want to extract only small json
 * sub tree at once.
 *
 * Imagine you have jsom like
 * &lt;pre&gt;
 * { &quot;items&quot; : [
 *		{&quot;name&quot;: &quot;name-1&quot;, &quot;value&quot;: 1},
 *		{&quot;name&quot;: &quot;name-2&quot;, &quot;value&quot;: 2},
 *		{&quot;name&quot;: &quot;name-3&quot;, &quot;value&quot;: 3},
 *		{&quot;name&quot;: &quot;name-4&quot;, &quot;value&quot;: 4},
 *		...
 * ]
 * }
 * &lt;/pre&gt;
 * So you can parse whole json and iterate parts. In this case whole
 * json is loaded before traversal. (It is effective for small jsons
 * only)
 * &lt;pre&gt;
 *   JsonValue root = JsonParser.parse(inputstream);
 *   root.find(SPM.path(&quot;items&quot;, &quot;*&quot;)).stream()
 *   // or if you want traverse only values
 *   // root.find(SPM.path(&quot;items&quot;, &quot;*&quot;, &quot;value&quot;)).stream()
 * &lt;/pre&gt;
 * This class allows you to read only parts you want. But it is little
 * bit slower.
 * &lt;pre&gt;
 *   JsonStream.instance(inputstream, SPM.path(&quot;items&quot;, &quot;*&quot;)).stream();
 *   // or if you want traverse only values
 *   // JsonStream.instance(inputstream, SPM.path(&quot;items&quot;, &quot;*&quot;, &quot;value&quot;)).stream();
 * &lt;/pre&gt;
 *
 * So if you have pretty big json which is json array and you are gounig to
 * process it item by item, you can use JsonStream with path &quot;*&quot;.
 * @author antons
 */
public class JsonStream {

    private PathMatcher matcher;
    private JsonSource source;

    /**
     * Instance of JsonSource
     * @param source source for reading json
     * @param matcher path matcher to identify returned values
     */
    public JsonStream(JsonSource source, PathMatcher matcher) {
        this.matcher = matcher;
        this.source = source;
    }

    /**
     * Instance of JsonStream.
     * @param source source for reading json
     * @param matcher path matcher to identify returned values
     * @return new instance
     */
    public static JsonStream instance(JsonSource source, PathMatcher matcher) {
        return new JsonStream(source, matcher);
    }

    /**
     * Instance of JsonStream.
     * @param json string with json
     * @param matcher path matcher to identify returned values
     * @return new instance
     */
    public static JsonStream instance(String json, PathMatcher matcher) {
        return new JsonStream(new StringSource(json), matcher);
    }

    /**
     * Instance of JsonStream.
     * @param reader source for reading json
     * @param matcher path matcher to identify returned values
     * @return new instance
     */
    public static JsonStream instance(Reader reader, PathMatcher matcher) {
        return new JsonStream(new ReaderSource(reader), matcher);
    }

    /**
     * Creates iterator for json subparts identifies by given matcher. After
     * calling the method JsonStream instance is not valid anymore.
     * @return iterator
     */
    public Iterator<JsonValue> iterator() {
        return new JsonIterator(JsonScanner.instance(source));
    }

    /**
     * Creates stream of json subparts identifies by given matcher. After
     * calling the method JsonStream instance is not valid anymore.
     * @return stream
     */
    public Stream<JsonValue> stream() {

        return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(
                iterator()
                , Spliterator.IMMUTABLE
            ), false);

    }

    private static class Context {
        boolean array = false;
        boolean first = true;
        int arrayCounter = 0;
        Context prev;
        List<String> path = new ArrayList<>();

        public static Context array(Context prev) {
            Context c = new Context();
            c.array = true;
            c.prev = prev;
            if(prev != null) c.path.addAll(prev.path);
            return c;
        }
        public static Context object(Context prev) {
            Context c = new Context();
            c.array = false;
            c.prev = prev;
            if(prev != null) c.path.addAll(prev.path);
            return c;
        }

        public void setPath(String value) {
            if((!first) && (this.path.size() > 0)) this.path.remove(path.size()-1);
            this.path.add(value);
            this.first = false;
        }
    }

    private class JsonIterator implements Iterator<JsonValue> {
        JsonScanner scanner;

        public JsonIterator(JsonScanner scanner) {
            this.scanner = scanner;
        }

        //List<String> path = new ArrayList<>();
        Context context = null;
        boolean finished = false;

        private void fixPathBeforeItemSTart() {
            if((context != null) && context.array) {
                context.setPath(String.valueOf(context.arrayCounter++));
            }
        }

        private JsonValue nextPath() {
//            if(context != null) {
//                fixPathAfterItemEnd();
//                if(context.array) {
//                    Match match = matcher.match(path, null);
//                    if(match == Match.FULLY) {
//                        JsonValue v = scanner.readNext();
//                        if(v == null) {
//                            context = context.prev;
//                            if(path.size()>0) path.remove(path.size()-1);
//                        } else {
//                            return v;
//                        }
//                    }
//                }
//            }
            Token token = scanner.next();
            if(token == null) return null;
            while(token != null) {
                if(token == Token.OBJECT_START) {
                    fixPathBeforeItemSTart();
                    context = Context.object(context);
                } else if(token == Token.ARRAY_START) {
                    fixPathBeforeItemSTart();
                    context = Context.array(context);
                    Match match = matcher.match(context.path, null);
                    if(match == Match.FULLY) {
                        return scanner.readNext();
                    } else if(match == Match.NOPE) {
                        scanner.readNext();
                    }
                } else if(token == Token.NAME) {
                    context.setPath(scanner.stringValue());
                    Match match = matcher.match(context.path, null);
                    if(match == Match.FULLY) {
                        return scanner.readNext();
                    } else if(match == Match.NOPE) {
                        scanner.readNext();
                    }
                } else if(token == Token.OBJECT_END) {
                    context = context.prev;
                } else if(token == Token.ARRAY_END) {
                    context = context.prev;
                } else { //literal
                    fixPathBeforeItemSTart();
                }
                token = scanner.next();
            }
            return null;
        }


        private JsonValue nextone = null;
        @Override
        public boolean hasNext() {
            if(finished) return false;
            nextone = nextPath();
            if(nextone == null) finished = true;
            return nextone != null;
        }

        @Override
        public JsonValue next() {
            if(finished) return null;
            return nextone;
        }
    }


    public static void main(String[] argv) throws Exception {
        Reader reader = new FileReader("/home/antons/Downloads/api-docs.json");
        JsonStream stream = JsonStream.instance(reader, SPM.path("components", "schemas", "*"));

        Iterator<JsonValue> iter = stream.iterator();
        while(iter.hasNext()) {
            JsonValue next = iter.next();
            System.out.println(" ----====----- " + next.toCompactString());
        }

    }
}
