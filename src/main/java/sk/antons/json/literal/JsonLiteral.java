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
package sk.antons.json.literal;

import sk.antons.json.JsonValue;

/**
 * Abstract literal value. It is parent of all literal classes.
 * @author antons
 */
public interface JsonLiteral extends JsonValue {
    
    /**
     * String representing literal. ("foo", 123, true, null, 12.3....)
     * @return literal string
     */
    String literal() ;

    /**
     * String value of this literal. (for string literal "foo" it returns foo)
     * @return string value of this literal
     */
    String stringValue() ;
}
