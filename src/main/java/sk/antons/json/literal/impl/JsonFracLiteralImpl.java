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

import java.math.BigDecimal;
import sk.antons.json.literal.JsonFracLiteral;

/**
 *
 * @author antons
 */
public class JsonFracLiteralImpl extends JsonLiteralImpl implements JsonFracLiteral {
    
    private boolean cachedValue = false;
    private BigDecimal cachedValueBd = null;
    private int dotPos = -1;
    
    public JsonFracLiteralImpl(BigDecimal bd) {
        super(bd.toPlainString());
        int pos = literal.indexOf(".");
        dotPos = pos;
    }

    public JsonFracLiteralImpl(String literal, int offset, int length, int dotPos) {
        super(literal, offset, length);
        this.dotPos = dotPos;
    }

    @Override
    public Type type() { return Type.FRAC; }
    
    public BigDecimal bdValue() {
        if(cachedValue) return cachedValueBd;
        cachedValueBd = new BigDecimal(stringValue());
        cachedValue = true;
        return cachedValueBd;
    }
    
}
