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
 * Json array instance. 
 *
 * @author antons
 */
public interface JsonArray extends JsonValue {
    
    /**
     * Checks if this json array contains no value.
     * @return true if json array is empty.
     */
    boolean isEmpty();

    /**
     * Size of json array.
     * @return size of json array.
     */
    int size();

    /**
     * Clears content of json array.
     */
    void clear();

    /**
     * Reads n'th item from json array.
     * @param index order if returned item (first element has index=0)
     * @return element of json array at 'index' position
     */
    JsonValue get(int index);

    /**
     * Reads first element of json array.
     * @return first element of json array or null if array is empty.
     */
    JsonValue first();

    /**
     * Reads last element of json array.
     * @return last element of json array or null if array is empty.
     */
    JsonValue last();

    /**
     * Removes n'th element from json array.
     * @param index position of element in json array which should be removed
     * @return removed element
     */
    JsonValue remove(int index);

    /**
     * Adds new element to json array.
     * @param value value to be added to the array. Should not be empty 
     * @return this json array instance
     */
    JsonArray add(JsonValue value);

    /**
     * Adds new element to json array at specified position.
     * @param value value to be added to the array. Should not be empty 
     * @param index position where value should be added;
     * @return this json array instance
     */
    JsonArray add(JsonValue value, int index);

    /**
     * Converts this instance to regulat list of values.
     * @return list of json array values.
     */
    List<JsonValue> toList();

}
