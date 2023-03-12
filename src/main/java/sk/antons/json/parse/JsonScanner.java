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

import java.io.Reader;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.regex.Pattern;
import sk.antons.json.JsonArray;
import sk.antons.json.JsonFactory;
import sk.antons.json.JsonObject;
import sk.antons.json.JsonValue;
import sk.antons.json.source.JsonSource;
import sk.antons.json.source.ReaderSource;
import sk.antons.json.source.StringSource;
import sk.antons.json.util.JsonEscaper;

/**
 * Json parer, which traverse json tree and produces events defined in 
 * JsonContentHandler.
 * @author antons
 */
public class JsonScanner {

    private JsonSource source;
    private Token current;
    private boolean namePossible = false;
    private LinkedList<Container> stack = new LinkedList<Container>();
    private boolean isLiteral = false;
    private String buff;
    private int startpos;
    private int endpos;

    public JsonScanner(JsonSource source) {
        this.source = source;
    }

    public static JsonScanner instance(JsonSource source) {
        return new JsonScanner(source);
    }
    
    public static JsonScanner instance(String json) {
        return new JsonScanner(new StringSource(json));
    }
    
    public static JsonScanner instance(Reader reader) {
        return new JsonScanner(new ReaderSource(reader));
    }
    
    public Token current() { return current; }
    public Token next() {
        current = nextTokenImpl();
        return current;
    }
    public Token skipNext() { 
        int num = 0;
        Token t = null;
        do {
            t = next();
            switch(t) {
                case ARRAY_START: 
                    num++;
                    break;
                case ARRAY_END: 
                    num--;
                    break;
                case OBJECT_START: 
                    num++;
                    break;
                case OBJECT_END: 
                    num--;
                    break;
            }
        } while((t != null) && (num > 0));
        return next();
    }
    
    public JsonValue readNext() {
        Token token = next();
        if(token == null) throw new IllegalStateException("no tohen ");
        switch(token) {
            case ARRAY_START:
                JsonArray array = JsonFactory.array();
                JsonValue v = readNext();
                while(v != null) {
                    array.add(v);
                    v = readNext();
                }
                return array;
            case ARRAY_END:
                return null;
            case OBJECT_START:
                JsonObject object = JsonFactory.object();
                token = next();
                while(token == Token.NAME) { 
                    object.add(stringValue(), readNext());
                    token = next();
                }
                if(token != Token.OBJECT_END) throw new IllegalStateException("expected OBJECT_END but tohen " +token + " found - ");
                return object;
            case LITERAL_TEXT:
                return JsonFactory.stringLiteral(stringValue());
            case LITERAL_BD:
                return JsonFactory.expLiteral(bdValue());
            case LITERAL_BOOL:
                return JsonFactory.boolLiteral(booleanValue());
            case LITERAL_INT:
                return JsonFactory.intLiteral(intValue());
            case LITERAL_NULL:
                return JsonFactory.nullLiteral();
            default:
                throw new IllegalStateException("unknown tohen " +token + " - ");
        }
    }
    
    public String stringValue() {
        return stringValueImpl();
    }
    
    public long intValue() {
        return Long.parseLong(stringValueImpl());
    }
    
    public BigDecimal bdValue() {
        return new BigDecimal(stringValueImpl());
    }
    
    public boolean booleanValue() {
        return "true".equals((stringValueImpl()));
    }
    
    private String stringValueImpl() {
        if(isLiteral) {
            char c = buff.charAt(startpos);
            if(c == '"') {
                return JsonEscaper.unescape(buff, startpos+1, endpos-startpos-2);
            } else {
                return buff.substring(startpos, endpos);
            }
        } else {
            throw new IllegalArgumentException("Current token is not literal");
        }
    }
    

    private Token nextTokenImpl() {
        try {
            isLiteral = false;
            int c = source.current();
            while(c != -1) {
                switch (c) {
                    case '{':
                        stack.push(Container.OBJECT);
                        namePossible = true;
                        source.move();
                        return Token.OBJECT_START;
                    case '}':
                        stack.pop();
                        namePossible = false;
                        source.move();
                        return Token.OBJECT_END;
                    case '[':
                        stack.push(Container.ARRAY);
                        namePossible = false;
                        source.move();
                        return Token.ARRAY_START;
                    case ']':
                        stack.pop();
                        namePossible = false;
                        source.move();
                        return Token.ARRAY_END;
                    case ':':
                        namePossible = false;
                        source.move();
                        break;
                    case ',':
                        namePossible = stack.peek() == Container.OBJECT;
                        source.move();
                        break;
                    case ' ':
                    case '\n':
                    case '\t':
                    case '\r':
                        skipWhiteSpace(c);
                        break;
                    case '"':
                        skipLiteralEscaped(c);
                        isLiteral = true;
                        if(namePossible) return Token.NAME;
                        else return Token.LITERAL_TEXT;
                    default:
                        Token literal = skipLiteralSimple(c);
                        isLiteral = true;
                        if(namePossible) return Token.NAME;
                        else return literal;
                }
                c = source.current();
            }
            return null;
        } catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private boolean isWhiteSpace(int c) {
        switch (c) {
            case ' ': return true;
            case '\t': return true;
            case '\n': return true;
            case '\r': return true;
            default: return false;
        }
    }
    
    private boolean isNonLiteral(int c) {
        switch (c) {
            case ':': return true;
            case ',': return true;
            case '{': return true;
            case '}': return true;
            case '[': return true;
            case ']': return true;
            case ' ': return true;
            case '\t': return true;
            case '\n': return true;
            case '\r': return true;
            default: return false;
        }
    }
    
//    private void skipWhiteSpace2(int c) {
//        while((c != -1) && (isWhiteSpace(c))) {
//            source.move();
//            c = source.current();
//        }
//    }

    private void skipWhiteSpace(int c) {
        boolean cont = true;
        mainloop:
        while(cont) {
            switch (c) {
                case -1:
                    cont = false;
                    break mainloop;
                case ' ':
                case '\n':
                case '\t':
                case '\r':
                    break;
                default:
                    cont = false;
                    break mainloop;
            }
            source.move();
            c = source.current();
        }
    }

    private void skipLiteral(int c) {
        if(c == '"') skipLiteralEscaped(c);
        else skipLiteralSimple(c);
    }

    private Token skipLiteralSimple(int c) {
        startpos = source.startRecording();
        boolean cont = true;
        mainloop:
        while(cont) {
            switch (c) {
                case -1:
                    cont = false;
                    break mainloop;
                case ':':
                case ',':
                case '{':
                case '}':
                case '[':
                case ']':
                case ' ':
                case '\n':
                case '\t':
                case '\r':
                    cont = false;
                    break mainloop;
                default:
            }
            source.move();
            c = source.current();
        }
        endpos = source.stopRecording();
        buff = source.recordedContent();
        return toLiteralToken(buff, startpos, endpos);
    }
    
    private void skipLiteralEscaped(int c) {
        startpos = source.startRecording();
        boolean escape = false;
        source.move();
        c = source.current();
        mainloop:
        while(c != -1) {
                switch (c) {
                    case '\\':
                        source.move();
                        break;
                    case '"':
                        source.move();
                        break mainloop;
                    default:
                }
            source.move();
            c = source.current();
        }
        endpos = source.stopRecording();
        buff = source.recordedContent();
    }


    private static Pattern boolPattern = Pattern.compile("true|false");
    private static Pattern intPattern = Pattern.compile("[+-]?[0-9]+");
    private static Pattern bdPattern = Pattern.compile("[+-]?[0-9]+\\.?[0-9]+([eE][+-]?[0-9]+)?");
    
    private static Token toLiteralToken(String text, int startpos, int endpos) {
        if(text == null) return Token.LITERAL_NULL;
        if(text.length() == 0) return Token.LITERAL_TEXT;
        char c = text.charAt(0);
        if(c == 'n') {
            return "null".equals(text.subSequence(startpos, endpos)) ? Token.LITERAL_NULL : Token.LITERAL_TEXT; 
        } else if((c == 't') || (c == 'f')) {
            return boolPattern.matcher(text.subSequence(startpos, endpos)).matches() ? Token.LITERAL_BOOL : Token.LITERAL_TEXT; 
        } else if((c == '+') || (c == '-') || ( (c >= '0') && (c <= '9') )) {
            if(intPattern.matcher(text.subSequence(startpos, endpos)).matches()) return Token.LITERAL_INT;
            return bdPattern.matcher(text.subSequence(startpos, endpos)).matches() ? Token.LITERAL_BD : Token.LITERAL_TEXT; 
        } else {
            return Token.LITERAL_TEXT;
        }
    }
    
    public static enum Token {
        ARRAY_START
        , ARRAY_END
        , OBJECT_START
        , OBJECT_END
        , NAME
        , LITERAL_TEXT
        , LITERAL_BOOL
        , LITERAL_INT
        , LITERAL_BD
        , LITERAL_NULL
        ;
    }

    private static enum Container {
        OBJECT
        , ARRAY
        ;
    }
}
