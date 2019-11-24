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

import sk.antons.json.JsonValue;
import java.util.ArrayList;
import java.util.List;
import sk.antons.json.JsonArray;
import sk.antons.json.match.Match;
import sk.antons.json.match.PathMatcher;
import sk.antons.json.template.JsonArrayListWrapper;

/**
 *
 * @author antons
 */
public class JsonArrayImpl extends JsonValueImpl implements JsonArray, JsonGroup, JsonMember {
    private List<JsonValue> values = new ArrayList<JsonValue>();
    
    public List<JsonValue> values() { return values; }

    @Override
    public void toCompactString(StringBuilder sb) {
        sb.append('[');
        boolean first = true;
        for(JsonValue value : values) {
            if(first) first = false;
            else sb.append(',');
            ((JsonValueImpl)value).toCompactString(sb);
        }
        sb.append(']');
    }

    @Override
    public void toPrettyString(StringBuilder sb, String prefix, String indent) {
        sb.append("[\n").append(prefix).append(indent);
        boolean first = true;
        for(JsonValue value : values) {
            if(first) first = false;
            else sb.append(",");
            ((JsonValueImpl)value).toPrettyString(sb, prefix + indent , indent);
        }
        sb.append("\n");
        sb.append(prefix).append(']');
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public void clear() {
        values.clear();
    }

    @Override
    public JsonValue get(int index) {
        return values.get(index);
    }

    @Override
    public JsonValue remove(int index) {
        return values.remove(index);
    }

    @Override
    public JsonValue first() {
        if(values.isEmpty()) return null;
        return values.get(0);
    }

    @Override
    public JsonValue last() {
        if(values.isEmpty()) return null;
        return values.get(values.size()-1);
    }

    

    @Override
    public JsonArray add(JsonValue value) {
        if(value == null) throw new IllegalArgumentException("JsonArray member can't be null");
        values.add(value);
        if(value instanceof JsonMember) ((JsonMember)value).setGroup(this);
        return this;
    }

    @Override
    public JsonArray add(JsonValue value, int index) {
        if(value == null) throw new IllegalArgumentException("JsonArray member can't be null");
        values.add(index, value);
        if(value instanceof JsonMember) ((JsonMember)value).setGroup(this);
        return this;
    }

    

    @Override
    public List<JsonValue> toList() {
        List<JsonValue> list = new ArrayList<JsonValue>();
        list.addAll(values);
        return list;
    }


    @Override
    public int memberIndex(JsonMember m) {
        if(m == null) return -1;
        if(!(m instanceof JsonValue)) return -1;
        return values.indexOf(m);
    }

    
    @Override
    public JsonValue findFirst(PathMatcher matcher, List<String> path) {
        Match result = matcher.match(path, this);
        if(result == Match.NOPE) return null;
        if(result == Match.FULLY) return this;
        int index = 0;
        for(JsonValue item : values) {
            String name = "" + index++;
            path.add(name);
            JsonValue rv = ((JsonValueImpl)item).findFirst(matcher, path);
            if(rv != null) return rv;
            path.remove(path.size()-1);
        }
        return null;
    }

    @Override
    public void findAll(PathMatcher matcher, List<JsonValue> values, List<String> path) {
        Match result = matcher.match(path, this);
        if(result == Match.NOPE) {
            return;
        }
        if(result == Match.FULLY) {
            values.add(this);
            return ;
        }
        int index = 0;
        for(JsonValue item : this.values) {
            String name = "" + index++;
            path.add(name);
            ((JsonValueImpl)item).findAll(matcher, values, path);
            path.remove(path.size()-1);
        }
    }
    
    public void remove(JsonValue value) {
        if(value == null) return;
        values.remove(value);
    }


    public void replaceBy(JsonValue oldValue, JsonValue newValue) {
        int index = memberIndex((JsonMember)oldValue);
        if(index < 0) {
            add(newValue);
        } else {
            remove(oldValue);
            add(newValue, index);
        }
    }

    @Override
    public JsonValue copy() {
        JsonArrayImpl rv = new JsonArrayImpl();
        for(JsonValue value : values) {
            rv.add(value.copy());
        }
        return rv;
    }

    @Override
    public Object asTemplateParam() {
        return JsonArrayListWrapper.instance(this);
    }



}
