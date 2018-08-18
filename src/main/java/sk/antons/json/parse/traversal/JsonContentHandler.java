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

import sk.antons.json.literal.impl.JsonLiteralImpl;

/**
 * Json event consumer. It is used together with Traversal parser 
 * to generate events corresponding to json tree.
 * 
 * Each method can throw StopTraverse exception to regularry stop processing.
 * 
 * @author antons
 */
public interface JsonContentHandler {
    
    /**
     * Help information for parser. It is used in case of error. 
     * @return information about state of parsing.
     */
    String contextInfo();
    
    /**
     * Start of the json document event.
     */
    void startDocument() ;
    
    /**
     * End of the json document event.
     */
    void endDocument() ;
    
    /**
     * Start of the json array event.
     */
    void startArray() ;
    
    /**
     * End of the json array event.
     */
    void endArray() ;
    
    /**
     * Start of the json object event.
     */
    void startObject() ;
    
    /**
     * End of the json object event.
     */
    void endObject() ;
    
    /**
     * Value separator json event.
     */
    void valueSeparator() ;
    
    /**
     * Name separaton json event.
     */
    void nameSeparator();
    
    /**
     * Json literal event.
     * @param literal provided literal
     */
    void literal(JsonLiteralImpl literal);

    /**
     * white space json event
     * @param content String where whitespace is located
     * @param offset start position of white space in content
     * @param length length of white space.
     */
    void whiteSpace(String content, int offset, int length);
}
