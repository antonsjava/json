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
                if(c == '{') {
                    stack.push(Container.OBJECT);
                    namePossible = true;
                    source.move();
                    return Token.OBJECT_START;
                } else if(c == '}') {
                    stack.pop();
                    namePossible = false;
                    source.move();
                    return Token.OBJECT_END;
                } else if(c == '[') {
                    stack.push(Container.ARRAY);
                    namePossible = false;
                    source.move();
                    return Token.ARRAY_START;
                } else if(c == ']') {
                    stack.pop();
                    namePossible = false;
                    source.move();
                    return Token.ARRAY_END;
                } else if(c == ':') {
                    namePossible = false;
                    source.move();
                } else if(c == ',') {
                    namePossible = stack.peek() == Container.OBJECT;
                    source.move();
                } else if(isWhiteSpace(c)) {
                    skipWhiteSpace();
                } else {
                    skipLiteral();
                    isLiteral = true;
                    if(namePossible) return Token.NAME;
                    else return Token.LITERAL;
                }
                c = source.current();
            }
            return null;
        } catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private boolean isWhiteSpace(int c) {
        if(c == ' ') return true;
        if(c == '\t') return true;
        if(c == '\n') return true;
        return c == '\r';
    }
    
    private boolean isNonLiteral(int c) {
        if(isWhiteSpace(c)) return true;
        if(c == ':') return true;
        if(c == ',') return true;
        if(c == '{') return true;
        if(c == '}') return true;
        if(c == '[') return true;
        if(c == ']') return true;
        return false;
    }
    
    private void skipWhiteSpace() {
        int c = source.current();
        while((c != -1) && (isWhiteSpace(c))) {
            source.move();
            c = source.current();
        }
    }

    private void skipLiteral() {
        int c = source.current();
        if(c == '"') skipLiteralEscaped();
        else skipLiteralSimple();
    }

    private void skipLiteralSimple() {
        startpos = source.startRecording();
        int c = source.current();
        while((c != -1) && (!isNonLiteral(c))) {
            source.move();
            c = source.current();
        }
        endpos = source.stopRecording();
        buff = source.recordedContent();
    }
    
    private void skipLiteralEscaped() {
        startpos = source.startRecording();
        boolean escape = false;
        source.move();
        int c = source.current();
        while(c != -1) {
            if(escape) {
                escape = false;
                //source.move();
            } else {
                if(c == '\\') {
                    escape = true;
                } else {
                    //escape = false;
                    if(c == '"') {
                        source.move();
                        break;
                    }
                }
            }
            source.move();
            c = source.current();
        }
        endpos = source.stopRecording();
        buff = source.recordedContent();
    }

    public static enum Token {
        ARRAY_START
        , ARRAY_END
        , OBJECT_START
        , OBJECT_END
        , NAME
        , LITERAL
        ;
    }

    private static enum Container {
        OBJECT
        , ARRAY
        ;
    }
}
