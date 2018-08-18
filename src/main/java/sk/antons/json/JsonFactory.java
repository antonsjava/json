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
package sk.antons.json;

import java.math.BigDecimal;
import sk.antons.json.impl.JsonArrayImpl;
import sk.antons.json.impl.JsonObjectImpl;
import sk.antons.json.literal.JsonBoolLiteral;
import sk.antons.json.literal.JsonExpLiteral;
import sk.antons.json.literal.JsonFracLiteral;
import sk.antons.json.literal.JsonIntLiteral;
import sk.antons.json.literal.JsonLiteral;
import sk.antons.json.literal.JsonNullLiteral;
import sk.antons.json.literal.JsonStringLiteral;
import sk.antons.json.literal.impl.JsonBoolLiteralImpl;
import sk.antons.json.literal.impl.JsonExpLiteralImpl;
import sk.antons.json.literal.impl.JsonFracLiteralImpl;
import sk.antons.json.literal.impl.JsonIntLiteralImpl;
import sk.antons.json.literal.impl.JsonLiteralImpl;
import sk.antons.json.literal.impl.JsonNullLiteralImpl;
import sk.antons.json.literal.impl.JsonStringLiteralImpl;

/**
 * Factory class for all relevant json instances.
 *
 * @author antons
 */
public class JsonFactory {

    /**
     * Json array instance factory method.
     * @return new instance of json array.
     */
    public static JsonArray array() { return new JsonArrayImpl(); }

    /**
     * Json object instance factory method.
     * @return new instance of json object.
     */
    public static JsonObject object() { return new JsonObjectImpl(); }

    /**
     * Json null literal instance factory method.
     * @return new instance of json null literal.
     */
    public static JsonNullLiteral nullLiteral() { return new JsonNullLiteralImpl(); }
    
    /**
     * Json boolean literal instance factory method.
     * @param value internal value for new literal instance.
     * @return new instance of json boolean literal.
     */
    public static JsonBoolLiteral boolLiteral(boolean value) { return new JsonBoolLiteralImpl(value); }
    
    /**
     * Json exponent literal instance factory method.
     * @param value internal value for new literal instance.
     * @return new instance of json exponent literal.
     */
    public static JsonExpLiteral expLiteral(BigDecimal value) { return new JsonExpLiteralImpl(value); }
    
    /**
     * Json fractional literal instance factory method.
     * @param value internal value for new literal instance.
     * @return new instance of json fractional literal.
     */
    public static JsonFracLiteral fracLiteral(BigDecimal value) { return new JsonFracLiteralImpl(value); }
    
    /**
     * Json integer literal instance factory method.
     * @param value internal value for new literal instance.
     * @return new instance of json integer literal.
     */
    public static JsonIntLiteral intLiteral(long value) { return new JsonIntLiteralImpl(value); }
    
    /**
     * Json string literal instance factory method.
     * @param value internal value for new literal instance.
     * @return new instance of json string literal.
     */
    public static JsonStringLiteral stringLiteral(String value) { return JsonStringLiteralImpl.fromValue(value); }

    /**
     * Parse string value and return identified literal. (remember that string 
     * literals are enclosed with quotas like "foo")
     * @param value string representation of literal (like "foo", null, true, 123213, 12.4, 12E-1)
     * @return JsonLiteral instance
     */
    public static JsonLiteral parseLiteral(String value) { return JsonLiteralImpl.instance(value, 0, value.length()); }
    
    
}
