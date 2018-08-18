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

import sk.antons.json.literal.JsonStringLiteral;
import sk.antons.json.util.JsonEscaper;

/**
 *
 * @author antons
 */
public class JsonStringLiteralImpl extends JsonLiteralImpl implements JsonStringLiteral {

    public JsonStringLiteralImpl(String literal) {
        super(literal);
    }

    public JsonStringLiteralImpl(String literal, int offset, int length) {
        super(literal, offset, length);
    }

    public static JsonStringLiteralImpl fromValue(String value) {
        if(value == null) value = "null";
        value = JsonEscaper.escape(value, true);
        return new JsonStringLiteralImpl("\""+value+"\"");
    }

    @Override
    public Type type() { return Type.STRING; }
    
    public String stringValue() {
        if(cachedValue) return cachedValueString;
        cachedValueString = JsonEscaper.unescape(literal, offset+1, length-2);
        cachedValue = true;
        return cachedValueString;
    }
    
}
