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

/**
 * Json object. 
 * @author antons
 */
public interface JsonObject extends JsonValue {
    
    /**
     * Checks if object has any attribute.
     * @return true if object has no attribute
     */
    boolean isEmpty();

    /**
     * Size of json object.
     * @return number of attributes.
     */
    int size();
    
    /**
     * Clears all attributes from this json object.
     */
    void clear();
    
    /**
     * Reads n'th attribute of this json object
     * @param index of the attribute which is read (first attribute has index=0)
     * @return attribute at index position
     */
    JsonAttribute attr(int index);
    
    /**
     * Reads first attribute of this json object with specified name
     * @param name of the attribute which is read (first attribute with the name will be returned)
     * @return first attribute with the name
     */
    JsonAttribute attr(String name);
    
    /**
     * Reads all attributes of this json object with specified name
     * @param name of the attributes which is read (all attributes with the name will be returned)
     * @return all attributes with the name
     */
    List<JsonAttribute> attrs(String name);

    /**
     * Removes n'th attribute from this json object.
     * @param index position of attribute which should be removed
     * @return remoced attribute
     */
    JsonAttribute removeAttr(int index);

    /**
     * Converts attributes to regular list.
     * @return 
     */
    List<JsonAttribute> toList();
    
    /**
     * Returns value of first attribute.
     * @return first attribute value or null if json object is empty
     */
    JsonValue first();
    
    /**
     * Returns value of last attribute.
     * @return last attribute value or null if json object is empty
     */
    JsonValue last();
    
    /**
     * Adds attribute ti this json object at last position.
     * @param name name of the attribute
     * @param value value of the attribute
     * @return this json object
     */
    JsonObject add(String name, JsonValue value);
    
    /**
     * Adds attribute ti this json object at specified position.
     * @param name name of the attribute
     * @param value value of the attribute
     * @param index position of the attribute
     * @return this json object
     */
    JsonObject add(String name, JsonValue value, int index);
    
    /**
     * Index of first attribute with specidied name
     * @param name name of the attribute which should be found
     * @return index fist attribute with the name of null if it is not found 
     */
    int firstIndex(String name);

    /**
     * Value of first attribute with specidied name
     * @param name name of the attribute which should be found
     * @return value of fist attribute with the name of null if it is not found 
     */
    JsonValue first(String name);

    /**
     * Values of all attributes with specidied name
     * @param name name of the attributes which should be found
     * @return values of all attributes with the name 
     */
    List<JsonValue> all(String name);
    
    /**
     * Removes all values with specified name.
     * @param name of attributes to be removed
     * @return this json object
     */
    JsonObject removeAll(String name);
}
