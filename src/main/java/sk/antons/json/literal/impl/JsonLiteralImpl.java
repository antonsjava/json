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
package sk.antons.json.literal.impl;

import java.util.List;
import sk.antons.json.JsonValue;
import sk.antons.json.impl.JsonArrayImpl;
import sk.antons.json.impl.JsonAttributeImpl;
import sk.antons.json.literal.JsonLiteral;
import sk.antons.json.impl.JsonValueImpl;
import sk.antons.json.match.Match;
import sk.antons.json.match.PathMatcher;

/**
 *
 * @author antons
 */
public abstract class JsonLiteralImpl extends JsonValueImpl implements JsonLiteral {
    protected String literal;
    protected int offset;
    protected int length;
    private boolean cachedValue = false;
    protected String cachedValueString = null;

    public abstract Type type();

    protected JsonLiteralImpl(String literal) {
        if(literal == null) literal = "null";
        this.literal = literal;
        this.offset = 0;
        this.length = literal.length();
    }
    
    protected JsonLiteralImpl(String literal, int offset, int length) {
        if(literal == null) literal = "null";
        this.literal = literal;
        this.offset = offset;
        this.length = length;
    }

    public static JsonLiteralImpl instance(String literal, int offset, int length) {
        return parse(literal, offset, length);
    }

    public String literal() {
        return literal.substring(offset, offset + length);
    }

    public String stringValue() {
        if(cachedValue) return cachedValueString;
        cachedValueString = literal();
        cachedValue = true;
        return cachedValueString;
    }
    

    private static JsonLiteralImpl parse(String literal) {
        if(literal == null) return new JsonNullLiteralImpl();
        return parse(literal, 0, literal.length());
    }

    private static boolean equalsTo(String literal, int offset, int length, char[] value) {
        if(length != value.length) return false;
        int pos = offset;
        for(int i = 0; i < length; i++) {
            if(literal.charAt(pos++) != value[i]) return false;
        }
        return true;
    }
    
    private static final char[] NULL = new char[]{'n', 'u', 'l', 'l'};
    private static final char[] TRUE = new char[]{'t', 'r', 'u', 'e'};
    private static final char[] FALSE = new char[]{'f', 'a', 'l', 's', 'e'};
    private static JsonLiteralImpl parse(String literal, int offset, int length) {
        JsonLiteralImpl value = null;
        char first = literal.charAt(offset);
        if(first == '"') {
            if(literal.charAt(offset + length - 1) =='"') value = new JsonStringLiteralImpl(literal, offset, length);
            else parseEx("seems to be string but is not terminated by '\"'", literal, offset, length);
        } else if(equalsTo(literal, offset, length, NULL)) {
            value = new JsonNullLiteralImpl();
        } else if((equalsTo(literal, offset, length, TRUE)) || (JsonLiteralImpl.equalsTo(literal, offset, length, FALSE))) {
            value = new JsonBoolLiteralImpl(literal, offset, length);
        } else if((first == '-') || (first == '+') || ((first >= '0' && first <= '9'))) {
            int literalLen = offset + length;
            int ePos = -1;
            int dotPos = -1;
            for(int i = offset; i < literalLen; i++) {
                char c = literal.charAt(i);
                
                if(('0' <= c) && ('9' >= c)) { //OK
                } else if(('-' == c) || ('+' == c)) {
                    if((i != offset) && (i != ePos+1)) {
                        parseEx("seems to be a number, but sign character is on wrong place", literal, offset, length);
                    }
                } else if(('e' == c) || ('E' == c)) {
                    if(ePos < 0) ePos = i;
                    else parseEx("seems to be a number, but more than one 'e' inside", literal, offset, length);
                } else if('.' == c) {
                    if(ePos < 0) {
                        if(dotPos < 0) dotPos = i;
                        else parseEx("seems to be a number, but more than one '.' inside", literal, offset, length);
                    } else {
                        parseEx("seems to be a number, but '.' can't be in exponent", literal, offset, length);
                    }
                } else {
                    parseEx("seems to be a number, but contains invalid character '"+c+"'", literal, offset, length);
                }
            }
            if(ePos > -1) value = new JsonExpLiteralImpl(literal, offset, length, dotPos, ePos);
            else if(dotPos > -1) value = new JsonFracLiteralImpl(literal, offset, length, dotPos);
            else value = new JsonIntLiteralImpl(literal, offset, length);
        } else {
            parseEx("unknown literal type", literal, offset, length);
        }
        return value;
    }
    
    private static void parseEx(String note, String literal, int offset, int length) {
        throw new IllegalArgumentException("JSValue '"+literal.substring(offset, offset + length)+"' " + note);
    }

    public static enum Type {
        NULL
        , BOOL
        , INT
        , FRAC
        , EXP
        , STRING
        ;
    }

    @Override
    public void toCompactString(StringBuilder sb) {
        sb.append(literal());
    }

    @Override
    public void toPrettyString(StringBuilder sb, String prefix, String indent) {
        sb.append(literal());
    }

    @Override
    public JsonValue findFirst(PathMatcher matcher, List<String> path) {
        Match result = matcher.match(path, this);
        if(result == Match.FULLY) return this;
        return null;
    }

    @Override
    public void findAll(PathMatcher matcher, List<JsonValue> values, List<String> path) {
        Match result = matcher.match(path, this);
        if(result == Match.FULLY) values.add(this);;
    }


    @Override
    public void remove() {
        if(group == null) return;
        if(group instanceof JsonAttributeImpl) ((JsonAttributeImpl)group).remove();
        else if(group instanceof JsonArrayImpl) ((JsonArrayImpl)group).remove(this);
        else {}
        setGroup(null);
    }
}
