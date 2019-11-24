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
package sk.antons.json.template;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import sk.antons.json.JsonObject;
import sk.antons.json.JsonValue;
import sk.antons.json.impl.JsonAttributeImpl;
import sk.antons.json.impl.JsonObjectImpl;

/**
 * Implementation class
 * @author antons
 */
public class JsonObjectMapWrapper implements Map {
    
    JsonObjectImpl object;

    public JsonObjectMapWrapper(JsonObject object) {
        this.object = (JsonObjectImpl)object;
    }

    public static JsonObjectMapWrapper instance(JsonObject object) {
        return new JsonObjectMapWrapper(object);
    }
    
    @Override
    public int size() {
        return object.size();
    }

    @Override
    public boolean isEmpty() {
        return object.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if(key == null) return false;
        if(!(key instanceof String)) return false;
        String name = (String) key;
        JsonValue value = object.first(name);
        return value != null;
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object get(Object key) {
        if(key == null) return null;
        if(!(key instanceof String)) return null;
        String name = (String) key;
        JsonValue value = object.first(name);
        if(value == null) return null;
        return value.asTemplateParam();
    }

    @Override
    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void putAll(Map m) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set keySet() {
        Set set = new HashSet();
        List<JsonAttributeImpl> list = object.attrs();
        for(JsonAttributeImpl jsonAttributeImpl : list) {
            set.add(jsonAttributeImpl.name().stringValue());
        }
        return set;
    }

    @Override
    public Collection values() {
        List rv = new ArrayList();
        List<JsonAttributeImpl> list = object.attrs();
        for(JsonAttributeImpl jsonAttributeImpl : list) {
            rv.add(jsonAttributeImpl.value().asTemplateParam());
        }
        return rv;
    }

    @Override
    public Set entrySet() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
