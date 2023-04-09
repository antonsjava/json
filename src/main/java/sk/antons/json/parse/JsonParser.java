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

import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import sk.antons.json.impl.JsonArrayImpl;
import sk.antons.json.impl.JsonAttributeImpl;
import sk.antons.json.impl.JsonObjectImpl;
import sk.antons.json.literal.impl.JsonLiteralImpl;
import sk.antons.json.literal.impl.JsonStringLiteralImpl;
import sk.antons.json.parse.traversal.JsonContentHandler;
import sk.antons.json.source.JsonSource;
import sk.antons.json.source.ReaderSource;
import sk.antons.json.source.StringSource;
import sk.antons.json.parse.traversal.TraversalParser;
import sk.antons.json.JsonValue;
import sk.antons.json.impl.JsonMember;
import sk.antons.json.impl.JsonGroup;

/**
 * Json text parser. Converts json data from string format to tree of objects.
 * @author antons
 */
public class JsonParser {


    /**
     * Parse json from string.
     * @param json string with json object array of just literal.
     * @return Json value representing the json data.
     */
    public static JsonValue parse(String json) {
        return parse(StringSource.instance(json));
    }

    /**
     * Parse json from reader.
     * @param json reader with json object array of just literal.
     * @return Json value representing the json data.
     */
    public static JsonValue parse(Reader json) {
        return parse(ReaderSource.instance(json));
    }

    /**
     * Parse json from reader. But first it reads as String and than parse it as string.
     * It is faster for small jsnons.
     * @param json reader with json object array of just literal.
     * @return Json value representing the json data.
     */
    public static JsonValue parsePrefetched(Reader json) {
        try {
            if(!(json instanceof BufferedReader)) json = new BufferedReader(json);
            StringBuilder buffer = new StringBuilder();
            char[] arr = new char[2048];
            int numCharsRead;
            while ((numCharsRead = json.read(arr, 0, arr.length)) != -1) {
                buffer.append(arr, 0, numCharsRead);
            }
            json.close();
            String targetString = buffer.toString();
            return parse(StringSource.instance(targetString));
        } catch(Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Parse json from source.
     * @param source json source data.
     * @return Json value representing the json data.
     */
    public static JsonValue parse(JsonSource source) {
        Handler handler = new Handler();
        TraversalParser tparser = TraversalParser.instance(source, handler);
        tparser.parse();
        return handler.getValue();
    }


    private static enum Step {
        START_DOCUMENT(1)
        , END_DOCUMENT(1<<1)
        , START_ARRAY(1<<2)
        , END_ARRAY(1<<3)
        , START_OBJECT(1<<4)
        , END_OBJECT(1<<5)
        , VALUE_SEPARATOR(1<<6)
        , NAME_SEPARATOR(1<<7)
        , LITERAL(1<<8)
        , WHITE_SPACE(1<<9)
        ;
        Step(int bit) { this.bit = bit; }
        int bit;
    }

    private static class Handler implements JsonContentHandler {

        private Object current = null;
        private Step lastStep = null;

        public JsonValue getValue() { return (JsonValue)current; }

        private int expected = 0;
        private int deprecated = 0;

        private String path() {
            List<String> items = new ArrayList<String>();
            Object ccurrent = current;
            while(ccurrent != null) {
                if(ccurrent instanceof JsonLiteralImpl) {
                    items.add(((JsonLiteralImpl)ccurrent).literal());
                } else if(ccurrent instanceof JsonAttributeImpl) {
                    items.add(((JsonAttributeImpl)ccurrent).name().literal());
                } else if(ccurrent instanceof JsonArrayImpl) {
                    items.add(String.valueOf(((JsonArrayImpl)ccurrent).values().size()));
                } else {
                   //ignore
                }
                if(ccurrent instanceof JsonMember) ccurrent = ((JsonMember)ccurrent).group();
                else ccurrent = null;
            }
            StringBuilder sb = new StringBuilder();
            for(int i = items.size() - 1; i >= 0; i--) {
                if(sb.length() > 0) sb.append(" / ");
                sb.append(items.get(i));
            }
            return sb.toString();
        }

        private static String expectedValues(int expected) {
            StringBuilder sb = new StringBuilder();
            for(Step value : Step.values()) {
                if((value.bit & expected) == 0) continue;
                if(sb.length() > 0) sb.append(", ");
                sb.append(value);
            }
            return sb.toString();
        }

        private void expects(Step step) {
            if(expected != 0) {
                if((expected & step.bit) == 0) parseEx("Element " + step + " was not espected at this position. Expected one of ("+expectedValues(expected)+")");
            }
            if(deprecated != 0) {
                if((deprecated & step.bit) != 0) parseEx("Element " + step + " was not depricated at this position");
            }
        }

        private void parseEx(String note) {
            throw new IllegalArgumentException("Unable to parse source because of: " + note );
        }

        @Override
        public void startDocument() {
            expects(Step.START_DOCUMENT);
        	if(current != null) parseEx("start document after something parsed");
            lastStep = Step.START_DOCUMENT;
            expected = Step.LITERAL.bit | Step.START_ARRAY.bit | Step.START_OBJECT.bit | Step.END_DOCUMENT.bit;
        }

        @Override
        public void endDocument() {
            expects(Step.END_DOCUMENT);
            lastStep = Step.END_DOCUMENT;
            expected = Step.START_DOCUMENT.bit; // depricate all
        }

        @Override
        public void startArray() {
            expects(Step.START_ARRAY);
        	JsonArrayImpl array = new JsonArrayImpl();
        	if((current != null) && (! (current instanceof JsonGroup))) parseEx("Bad start of JsonArray");
        	array.setGroup((JsonGroup)current);
            if(current != null) {
                if(current instanceof JsonAttributeImpl) {
                    JsonAttributeImpl ja = (JsonAttributeImpl)current;
                    ja.setValue(array);
                } else if(current instanceof JsonArrayImpl) {
                    JsonArrayImpl ja = (JsonArrayImpl)current;
                    ja.values().add(array);
                } else {
                    parseEx("Bad start of JsonArray");
                }
            }
            current = array;
            lastStep = Step.START_ARRAY;
            expected = Step.LITERAL.bit
                | Step.START_ARRAY.bit
                | Step.START_OBJECT.bit
                | Step.END_DOCUMENT.bit
                | Step.END_ARRAY.bit;
        }

        @Override
        public void endArray() {
            expects(Step.END_ARRAY);
        	if((current == null) || (! (current instanceof JsonArrayImpl))) parseEx("Bad end of JsonArray");
            //current = ((JsonArrayImpl)current).group();
            JsonArrayImpl jo = (JsonArrayImpl)current;
            if(jo.group() == null) {
                expected = Step.END_DOCUMENT.bit;
            } else {
                current = jo.group();
                if(current instanceof JsonAttributeImpl) {
                    current = ((JsonAttributeImpl)current).group();
                    expected = Step.VALUE_SEPARATOR.bit | Step.END_OBJECT.bit;
                } else if(current instanceof JsonArrayImpl) {
                    expected = Step.VALUE_SEPARATOR.bit | Step.END_ARRAY.bit;
                } else {
                    parseEx("End of json object was not expected");
                }
            }
            lastStep = Step.END_ARRAY;
        }

        @Override
        public void startObject() {
            expects(Step.START_OBJECT);
        	if((current != null) && (! (current instanceof JsonGroup))) parseEx("New json object was not expected here " + current);
            JsonObjectImpl jo = new JsonObjectImpl();
            if(current != null) {
                if(current instanceof JsonAttributeImpl) {
                    JsonAttributeImpl ja = (JsonAttributeImpl)current;
                    ja.setValue(jo);
                } else if(current instanceof JsonArrayImpl) {
                    JsonArrayImpl ja = (JsonArrayImpl)current;
                    ja.values().add(jo);
                } else {
                    parseEx("Bad start of JsonObject");
                }
            }
            jo.setGroup((JsonGroup)current);
            current = jo;
            expected = Step.LITERAL.bit | Step.END_OBJECT.bit;
            lastStep = Step.START_OBJECT;
        }

        @Override
        public void endObject() {
            expects(Step.END_OBJECT);
            if((current != null) && (!(current instanceof JsonObjectImpl))) parseEx("End of json object was not expected");
            JsonObjectImpl jo = (JsonObjectImpl)current;
            if(jo.group() == null) {
                expected = Step.END_DOCUMENT.bit;
            } else {
                current = jo.group();
                if(current instanceof JsonAttributeImpl) {
                    current = ((JsonAttributeImpl)current).group();
                    expected = Step.VALUE_SEPARATOR.bit | Step.END_OBJECT.bit;
                } else if(current instanceof JsonArrayImpl) {
                    expected = Step.VALUE_SEPARATOR.bit | Step.END_ARRAY.bit;
                } else {
                    parseEx("End of json object was not expected");
                }
            }
            lastStep = Step.END_ARRAY;
        }

        @Override
        public void valueSeparator() {
            expects(Step.VALUE_SEPARATOR);
            lastStep = Step.VALUE_SEPARATOR;
            expected = Step.LITERAL.bit
                | Step.START_ARRAY.bit
                | Step.START_OBJECT.bit;
        }

        @Override
        public void nameSeparator() {
            expects(Step.NAME_SEPARATOR);
            lastStep = Step.NAME_SEPARATOR;
            expected = Step.LITERAL.bit
                | Step.START_ARRAY.bit
                | Step.START_OBJECT.bit;
        }

        @Override
        public void literal(JsonLiteralImpl literal) {
            expects(Step.LITERAL);
            if(current == null) {
                current = literal;
                expected = Step.END_DOCUMENT.bit ;
            } else if(current instanceof JsonArrayImpl) {
                ((JsonArrayImpl)current).values().add(literal);
                expected = Step.VALUE_SEPARATOR.bit | Step.END_ARRAY.bit;
            } else if(current instanceof JsonObjectImpl) {
                JsonObjectImpl jo = (JsonObjectImpl)current;
                if(!(literal instanceof JsonStringLiteralImpl)) parseEx("expected string literal but found " + literal.literal());
                JsonAttributeImpl attr = new JsonAttributeImpl();
                attr.setGroup(jo);
                jo.attrs().add(attr);
                attr.setName((JsonStringLiteralImpl)literal);
                current = attr;
                expected = Step.NAME_SEPARATOR.bit ;
            } else if(current instanceof JsonAttributeImpl) {
                JsonAttributeImpl attr = (JsonAttributeImpl)current;
                attr.setValue(literal);
                current = attr.group();
                expected = Step.VALUE_SEPARATOR.bit | Step.END_OBJECT.bit;
            } else {
                parseEx("Literal " + literal.literal() + " was not expected at this place");
            }
            lastStep = Step.LITERAL;
        }

        @Override
        public void whiteSpace(String content, int offset, int length) {
        }

        @Override
        public String contextInfo() {
            return path();
        }



    }

}
