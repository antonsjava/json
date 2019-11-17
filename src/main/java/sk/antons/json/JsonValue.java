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

import java.util.List;
import sk.antons.json.literal.JsonBoolLiteral;
import sk.antons.json.literal.JsonExpLiteral;
import sk.antons.json.literal.JsonFracLiteral;
import sk.antons.json.literal.JsonIntLiteral;
import sk.antons.json.literal.JsonLiteral;
import sk.antons.json.literal.JsonNullLiteral;
import sk.antons.json.literal.JsonStringLiteral;
import sk.antons.json.match.PathMatcher;

/**
 * Generic json value. Represents all object, arrays and literals.
 * 
 * @author antons
 */
public interface JsonValue {
    
    /**
     * Produces compact string representation of this json value.
     * @return compact string value
     */
    String toCompactString();
    
    /**
     * Produces pretty (readable) string representation of this json value.
     * @param indent string used for indend nested levels. (ussually tab or some spaces)
     * @return pretty string value
     */
    String toPrettyString(String indent);

    /**
     * Cast this value instance to JsonObject
     * @return this value instance bud narrow casted to JsonObject.
     */
    JsonObject asObject();

    /**
     * Cast this value instance to JsonArray
     * @return this value instance bud narrow casted to JsonArray.
     */
    JsonArray asArray();
    
    /**
     * Cast this value instance to JsonNullLiteral
     * @return this value instance bud narrow casted to JsonNullLiteral.
     */
    JsonNullLiteral asNullLiteral();
    
    /**
     * Cast this value instance to JsonBoolLiteral
     * @return this value instance bud narrow casted to JsonBoolLiteral.
     */
    JsonBoolLiteral asBoolLiteral();
    
    /**
     * Cast this value instance to JsonExpLiteralImpl
     * @return this value instance bud narrow casted to JsonExpLiteral.
     */
    JsonExpLiteral asExpLiteral();
    
    /**
     * Cast this value instance to JsonFracLiteral
     * @return this value instance bud narrow casted to JsonFracLiteral.
     */
    JsonFracLiteral asFracLiteral();
    
    /**
     * Cast this value instance to JsonIntLiteral
     * @return this value instance bud narrow casted to JsonIntLiteral.
     */
    JsonIntLiteral asIntLiteral();
    
    /**
     * Cast this value instance to JsonStringLiteral
     * @return this value instance bud narrow casted to JsonStringLiteral.
     */
    JsonStringLiteral asStringLiteral();
    
    /**
     * Cast this value instance to JsonLiteral
     * @return this value instance bud narrow casted to JsonLiteral.
     */
    JsonLiteral asLiteral();

    /**
     * Checks if this value is instance of JsonObject
     * @return result of the check
     */
    boolean isObject();

    /**
     * Checks if this value is instance of JsonArray
     * @return result of the check
     */
    boolean isArray();

    /**
     * Checks if this value is instance of JsonNullLiteral
     * @return result of the check
     */
    boolean isNullLiteral();

    /**
     * Checks if this value is instance of JsonBoolLiteral
     * @return result of the check
     */
    boolean isBoolLiteral();

    /**
     * Checks if this value is instance of JsonExpLiteral
     * @return result of the check
     */
    boolean isExpLiteral();

    /**
     * Checks if this value is instance of JsonFracLiteral
     * @return result of the check
     */
    boolean isFracLiteral();

    /**
     * Checks if this value is instance of JsonIntLiteral
     * @return result of the check
     */
    boolean isIntLiteral();

    /**
     * Checks if this value is instance of JsonStringLiteral
     * @return result of the check
     */
    boolean isStringLiteral();

    /**
     * Checks if this value is instance of JsonLiteral
     * @return result of the check
     */
    boolean isLiteral();

    /**
     * Find all json values with defined path
     * @param matcher Matcher to used for identifying returned values.
     * @return all json values defined by path
     */
    List<JsonValue> findAll(PathMatcher matcher);
    
    /**
     * Find first json value with defined path
     * @param matcher Matcher to used for identifying returned value.
     * @return first json value defined by path
     */
    JsonValue findFirst(PathMatcher matcher);

    /**
     * Find all json value with defined path and converts them to string value
     * @param matcher Matcher to used for identifying returned values.
     * @return all json value defined by path converted to string
     */
    List<String> findAllLiterals(PathMatcher matcher);
    
    /**
     * Find first json value with defined path and converts it to string value
     * @param matcher Matcher to used for identifying returned value.
     * @return first json value defined by path converted to string
     */
    String findFirstLiteral(PathMatcher matcher);
    
    /**
     * Parent of this value in json structure
     * @return parent json value or null if this instance is root
     */
    JsonValue parent();

    /**
     * Index of this instance in parent object
     * @return index of this instance in parent object or -1 if this is root
     */
    int parentIndex();

    /**
     * Returns path of this value in json structure
     * @return path of this instance
     */
    String[] path();
    
    /**
     * Returns path of this value in json structure
     * @return path of this instance
     */
    String pathAsString();

    /**
     * Removes this instance from parent object.
     */
    void remove();


    /**
     * Replaces this instance in parent object by this value.
     * Do nothing if this object is root and works like remove()
     * is new value is null;
     * @param newValue new value which must replace this instance.
     */
    void replaceBy(JsonValue newValue);

    /**
     * Returns true is this value is descendant of parent value;
     * @param parent possible paretn value
     * @return true if parent is parent of this.
     */
    boolean isDescendantOf(JsonValue parent);
    
    /**
     * Makes deep copy of this value.
     * @return copy of this
     */
    JsonValue copy();
    
}
