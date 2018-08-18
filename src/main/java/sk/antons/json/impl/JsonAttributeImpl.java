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
package sk.antons.json.impl;

import sk.antons.json.JsonAttribute;
import sk.antons.json.JsonValue;
import sk.antons.json.literal.impl.JsonStringLiteralImpl;

/**
 *
 * @author antons
 */
public class JsonAttributeImpl implements JsonAttribute, JsonGroup, JsonMember {
    private JsonStringLiteralImpl name = null;;
    private JsonValue value = null;

    public JsonStringLiteralImpl name() { return name; };
    public JsonValue value() { return value; }

    public void setName(JsonStringLiteralImpl name) { this.name = name; }
    public void setValue(JsonValue value) { 
        if(value == null) throw new NullPointerException("Attribute value can't be null");
        if(value instanceof JsonMember) ((JsonMember)value).setGroup(this);
        this.value = value; 
    }
    
    
    
    JsonObjectImpl group = null;
    @Override
    public JsonObjectImpl group() { return group; }
    @Override
    public void setGroup(JsonGroup group) { this.group = (JsonObjectImpl) group; }

    @Override
    public int memberIndex(JsonMember m) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void remove() {
        if(group == null) return;
        if(group instanceof JsonObjectImpl) ((JsonObjectImpl)group).remove(this);
        else {}
        setGroup(null);
    }

}
