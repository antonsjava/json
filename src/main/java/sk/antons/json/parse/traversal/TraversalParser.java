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
package sk.antons.json.parse.traversal;

import sk.antons.json.source.JsonSource;
import sk.antons.json.literal.impl.JsonLiteralImpl;

/**
 * Json parer, which traverse json tree and produces events defined in 
 * JsonContentHandler.
 * @author antons
 */
public class TraversalParser {
    private JsonSource source;
    private JsonContentHandler handler;

    private boolean started = false;

    public TraversalParser(JsonSource source, JsonContentHandler handler) {
        this.source = source;
        this.handler = handler;
    }

    public static TraversalParser instance(JsonSource source, JsonContentHandler handler) {
        return new TraversalParser(source, handler);
    }

    /**
     * Parses provided json source and produces events for provided handler.
     */
    public void parse() {
        if(started) throw new IllegalStateException("parser can be started only once");
        try {
            started = true;
            handler.startDocument();
            int c = source.current();
            while(c != -1) {
                if(c == '{') {
                    handler.startObject();
                    source.move();
                } else if(c == '}') {
                    handler.endObject();
                    source.move();
                } else if(c == '[') {
                    handler.startArray();
                    source.move();
                } else if(c == ']') {
                    handler.endArray();
                    source.move();
                } else if(c == ':') {
                    handler.nameSeparator();
                    source.move();
                } else if(c == ',') {
                    handler.valueSeparator();
                    source.move();
                } else if(isWhiteSpace(c)) {
                    skipWhiteSpace();
                } else {
                    skipLiteral();
                }
                c = source.current();
            }
            handler.endDocument();
        } catch(StopTraverse e) {
            //regular stop
        } catch(Exception e) {
            
            String contextInfo = null;
            try {
                contextInfo = handler.contextInfo();
            } catch(Exception ee) {
                // ignore
            }
            String message = "Unable to parse input because of " + e.getMessage();
            if(contextInfo != null) message = message + " [" + contextInfo + "]";
            throw new IllegalArgumentException(message, e);
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
        int startpos = source.startRecording();
        int c = source.current();
        while((c != -1) && (isWhiteSpace(c))) {
            source.move();
            c = source.current();
        }
        int endpos = source.stopRecording();
        String str = source.recordedContent();
        handler.whiteSpace(str, startpos, endpos - startpos);
    }

    private void skipLiteral() {
        int c = source.current();
        if(c == '"') skipLiteralEscaped();
        else skipLiteralSimple();
    }

    private void skipLiteralSimple() {
        int startpos = source.startRecording();
        int c = source.current();
        while((c != -1) && (!isNonLiteral(c))) {
            source.move();
            c = source.current();
        }
        int endpos = source.stopRecording();
        String str = source.recordedContent();
        handler.literal(JsonLiteralImpl.instance(str, startpos, endpos-startpos));
    }
    
    private void skipLiteralEscaped() {
        int startpos = source.startRecording();
        boolean escape = false;
        source.move();
        int c = source.current();
        while(c != -1) {
        System.out.println(" " + ((char)c) + " - " + escape);
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
        int endpos = source.stopRecording();
        String str = source.recordedContent();
        handler.literal(JsonLiteralImpl.instance(str, startpos, endpos-startpos));
    }
}
