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

/**
 *
 * @author antons
 */
public class StopTraverse extends RuntimeException {

    public StopTraverse() {
    }

    public StopTraverse(String message) {
        super(message);
    }

    public StopTraverse(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static StopTraverse instance() {
        return new StopTraverse();
    }    
    
    public static StopTraverse instance(String message) {
        return new StopTraverse(message);
    }    
    
    public static StopTraverse instance(String message, Throwable t) {
        return new StopTraverse(message, t);
    }    
    
}
