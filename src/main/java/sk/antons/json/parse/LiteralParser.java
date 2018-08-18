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

import java.util.ArrayList;
import java.util.List;
import sk.antons.json.literal.JsonLiteral;
import sk.antons.json.literal.impl.JsonLiteralImpl;
import sk.antons.json.match.Match;
import sk.antons.json.match.PathMatcher;
import sk.antons.json.parse.traversal.JsonContentHandler;
import sk.antons.json.parse.traversal.StopTraverse;
import sk.antons.json.parse.traversal.TraversalParser;
import sk.antons.json.source.JsonSource;

/**
 * Json parser, which finds only literal values.
 * It does not build all json data tree. Simple traverse that data tree and 
 * finds specified literal values.
 * @author antons
 */
public class LiteralParser {
    
    /**
     * First literal with mathed path.
     * @param source json source
     * @param matcher mather to identify resulted literal
     * @return first matched literal from json.
     */
    public static JsonLiteral first(JsonSource source, PathMatcher matcher) {
        Handler handler = new Handler(matcher, true);
        TraversalParser.instance(source, handler).parse();
        List<JsonLiteral> literals = handler.literals();
        if(literals.isEmpty()) return null;
        return literals.get(0);
    }
    
    /**
     * All literals with mathed path.
     * @param source json source
     * @param matcher mather to identify resulted literals
     * @return all matched literals from json.
     */
    public static List<JsonLiteral> all(JsonSource source, PathMatcher matcher) {
        Handler handler = new Handler(matcher, false);
        TraversalParser.instance(source, handler).parse();
        List<JsonLiteral> literals = handler.literals();
        return literals;
    }
    
    /**
     * First literal with mathed path converted to string.
     * @param source json source
     * @param matcher mather to identify resulted literal
     * @return first matched literal from json converted to string.
     */
    public static String firstAsString(JsonSource source, PathMatcher matcher) {
        JsonLiteral literal = first(source, matcher);
        if(literal == null) return null;
        return literal.stringValue();
    }
    
    /**
     * All literals with mathed path converted to string.
     * @param source json source
     * @param matcher mather to identify resulted literals
     * @return all matched literals from json converted to string.
     */
    public static List<String> allAsString(JsonSource source, PathMatcher matcher) {
        List<JsonLiteral> literals = all(source, matcher);
        List<String> values = new ArrayList<String>();
        for(JsonLiteral literal : literals) {
            values.add(literal.stringValue());
        }
        return values;
    } 

    private static class Handler implements JsonContentHandler {
        private PathMatcher matcher = null;
        List<String> path = new ArrayList<String>();
        List<Object> realpath = new ArrayList<Object>();
        private boolean onlyFirstOne = false;
        List<JsonLiteral> literals = new ArrayList<JsonLiteral>();

        private boolean nextStringLiteralIsAttr = false;

        public Handler(PathMatcher matcher, boolean onlyFirstOne) {
            this.matcher = matcher;
            this.onlyFirstOne = onlyFirstOne;
        }

        public List<JsonLiteral> literals() { return literals; }
            
        @Override
        public String contextInfo() {
            StringBuilder sb = new StringBuilder("path: ");
            for(String item : path) {
                sb.append('/').append(item);
            }
            return sb.toString();
        }

        @Override
        public void startDocument() {
        }

        @Override
        public void endDocument() {
        }

        @Override
        public void startArray() {
            path.add("0");
            realpath.add(0);
        }

        @Override
        public void endArray() {
            path.remove(path.size()-1);
            realpath.remove(realpath.size()-1);
        }

        @Override
        public void startObject() {
            nextStringLiteralIsAttr = true;
        }

        @Override
        public void endObject() {
            nextStringLiteralIsAttr = false;
        }

        @Override
        public void valueSeparator() {
            Object o = realpath.get(realpath.size()-1);
            path.remove(path.size()-1);
            realpath.remove(realpath.size()-1);
            if(o instanceof Integer) {
                int i = (Integer)o;
                i++;
                path.add(""+i);
                realpath.add(i);
            } else {
                nextStringLiteralIsAttr = true;
            }

        }

        @Override
        public void nameSeparator() {
        }

        @Override
        public void literal(JsonLiteralImpl literal) {
            if(nextStringLiteralIsAttr) {
                if(!literal.isStringLiteral()) throw new IllegalArgumentException("Expecten name of the attribute and is " + literal);
                path.add(literal.stringValue());
                realpath.add(literal.stringValue());
                nextStringLiteralIsAttr = false;
            } else {
                if(matcher.match(path, literal) == Match.FULLY) {
                    literals.add(literal);
                    if(onlyFirstOne) throw StopTraverse.instance();
                }
            }
        }

        @Override
        public void whiteSpace(String content, int offset, int length) {
        }

    }
}
