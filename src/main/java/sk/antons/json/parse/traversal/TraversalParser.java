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
                switch (c) {
                    case '{':
                        handler.startObject();
                        source.move();
                        break;
                    case '}':
                        handler.endObject();
                        source.move();
                        break;
                    case '[':
                        handler.startArray();
                        source.move();
                        break;
                    case ']':
                        handler.endArray();
                        source.move();
                        break;
                    case ':':
                        handler.nameSeparator();
                        source.move();
                        break;
                    case ',':
                        handler.valueSeparator();
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
                        break;
                    default: 
                        skipLiteralSimple(c);
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

    private void skipWhiteSpace2(int c) {
        while((c != -1) && (isWhiteSpace(c))) {
            source.move();
            c = source.current();
        }
    }
    
    private void skipWhiteSpace(int c) {
        int startpos = source.startRecording();
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
        int endpos = source.stopRecording();
        String str = source.recordedContent();
        handler.whiteSpace(str, startpos, endpos - startpos);
    }

    private void skipLiteral(int c) {
        if(c == '"') skipLiteralEscaped(c);
        else skipLiteralSimple(c);
    }

    private void skipLiteralSimple2(int c) {
        int startpos = source.startRecording();
        while((c != -1) && (!isNonLiteral(c))) {
            source.move();
            c = source.current();
        }
        int endpos = source.stopRecording();
        String str = source.recordedContent();
        handler.literal(JsonLiteralImpl.instance(str, startpos, endpos-startpos));
    }
    
    private void skipLiteralSimple(int c) {
        int startpos = source.startRecording();
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
        int endpos = source.stopRecording();
        String str = source.recordedContent();
        handler.literal(JsonLiteralImpl.instance(str, startpos, endpos-startpos));
    }
    
    private void skipLiteralEscaped(int c) {
        int startpos = source.startRecording();
        boolean escape = false;
        source.move();
        c = source.current();
        mainloop:
        while(c != -1) {
            if(escape) {
                escape = false;
                //source.move();
            } else {
                switch (c) {
                    case '\\':
                        escape = true;
                    case '"':
                        source.move();
                        break mainloop;
                    default:
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
