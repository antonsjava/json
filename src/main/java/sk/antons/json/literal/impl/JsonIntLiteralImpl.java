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

import sk.antons.json.JsonValue;
import sk.antons.json.literal.JsonIntLiteral;

/**
 *
 * @author antons
 */
public class JsonIntLiteralImpl extends JsonLiteralImpl implements JsonIntLiteral {
    
    private boolean cachedValue = false;
    private long cachedValueInt = 0;
    
    public JsonIntLiteralImpl(long value) {
        super(String.valueOf(value));
    }

    public JsonIntLiteralImpl(String literal, int offset, int length) {
        super(literal, offset, length);
    }

    @Override
    public Type type() { return Type.INT; }
    
    public long longValue() {
        if(cachedValue) return cachedValueInt;
        cachedValueInt = Long.parseLong(stringValue());
        cachedValue = true;
        return cachedValueInt;
    }
    
    @Override
    public JsonValue copy() {
        return new JsonIntLiteralImpl(literal, offset, length);
    }

    @Override
    public Object asTemplateParam() {
        return longValue();
    }
}
