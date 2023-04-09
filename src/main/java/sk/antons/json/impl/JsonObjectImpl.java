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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import sk.antons.json.JsonAttribute;
import sk.antons.json.JsonObject;
import sk.antons.json.JsonValue;
import sk.antons.json.literal.impl.JsonStringLiteralImpl;
import sk.antons.json.match.Match;
import sk.antons.json.match.PathMatcher;
import sk.antons.json.template.JsonObjectMapWrapper;

/**
 *
 * @author antons
 */
public class JsonObjectImpl extends JsonValueImpl implements JsonObject, JsonGroup, JsonMember {

    private List<JsonAttributeImpl> attrs = new ArrayList<JsonAttributeImpl>();

    public List<JsonAttributeImpl> attrs() { return attrs; }


    @Override
    protected void toCompactString(Appendable sb) {
        try {
            sb.append('{');
            boolean first = true;
            for(JsonAttributeImpl attr : attrs) {
                if(first) first = false;
                else sb.append(',');
                sb.append(attr.name().literal());
                sb.append(":");
                ((JsonValueImpl)attr.value()).toCompactString(sb);
            }
            sb.append('}');
        } catch(Exception e) {
            if(e instanceof RuntimeException) throw (RuntimeException)e;
            throw new IllegalStateException(e);
        }
    }

    @Override
    protected void toPrettyString(Appendable sb, String prefix, String indent) {
        try {
            sb.append("{\n");
            boolean first = true;
            for(JsonAttributeImpl attr : attrs) {
                if(first) first = false;
                else sb.append(",\n");
                sb.append(prefix).append(indent);
                attr.name().writeCompact(sb);
                sb.append(" : ");
                ((JsonValueImpl)attr.value()).toPrettyString(sb, prefix + indent, indent);
            }
            sb.append("\n");
            sb.append(prefix).append("}");
        } catch(Exception e) {
            if(e instanceof RuntimeException) throw (RuntimeException)e;
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean isEmpty() {
        return attrs.isEmpty();
    }

    @Override
    public int size() {
        return attrs.size();
    }

    @Override
    public void clear() {
        attrs.clear();
    }

    @Override
    public JsonAttribute attr(int index) {
        return attrs.get(index);
    }

    @Override
    public JsonValue first() {
        if(attrs.isEmpty()) return null;
        return attrs.get(0).value();
    }

    @Override
    public JsonValue last() {
        if(attrs.isEmpty()) return null;
        return attrs.get(attrs.size()-1).value();
    }



    @Override
    public JsonAttribute removeAttr(int index) {
        return attrs.remove(index);
    }

    @Override
    public List<JsonAttribute> toList() {
        List<JsonAttribute> list = new ArrayList<JsonAttribute>();
        list.addAll(attrs);
        return list;
    }

    private int findIndex(String name) {
        if(attrs.isEmpty()) return -1;
        int size = attrs.size();
        for(int i = 0; i < size; i++) {
            JsonAttribute attr = attrs.get(i);
            if(name.equals(attr.name().stringValue())) return i;
        }
        return -1;
    }

    @Override
    public JsonObject add(String name, JsonValue value) {
        if(name == null) throw new IllegalArgumentException("JsonAttribute name can't be null");
        if(value == null) throw new IllegalArgumentException("JsonAttribute value can't be null");
        JsonAttributeImpl attr = new JsonAttributeImpl();
        attr.setGroup(this);
        attr.setName(JsonStringLiteralImpl.fromValue(name));
        attr.setValue(value);
        if(value instanceof JsonMember) ((JsonMember)value).setGroup(attr);
        attrs.add(attr);
        return this;
    }

    @Override
    public JsonObject add(String name, JsonValue value, int index) {
        if(name == null) throw new IllegalArgumentException("JsonAttribute name can't be null");
        if(value == null) throw new IllegalArgumentException("JsonAttribute value can't be null");
        JsonAttributeImpl attr = new JsonAttributeImpl();
        attr.setGroup(this);
        attr.setName(JsonStringLiteralImpl.fromValue(name));
        attr.setValue(value);
        attrs.add(index, attr);
        return this;
    }

    @Override
    public int firstIndex(String name) {
        return firstIndex(name);
    }

    @Override
    public JsonValue first(String name) {
        if(name == null) return null;
        if(attrs.isEmpty()) return null;
        Iterator<JsonAttributeImpl> iter = attrs.iterator();
        while(iter.hasNext()) {
            JsonAttributeImpl attr = iter.next();
            if(name.equals(attr.name().stringValue())) return attr.value();
        }
        return null;
    }

    @Override
    public List<JsonValue> all(String name) {
        List<JsonValue> list = new ArrayList<JsonValue>();
        if(name == null) return list;
        if(attrs.isEmpty()) return list;
        Iterator<JsonAttributeImpl> iter = attrs.iterator();
        while(iter.hasNext()) {
            JsonAttributeImpl attr = iter.next();
            if(name.equals(attr.name().stringValue())) list.add(attr.value());
        }
        return list;
    }

    @Override
    public JsonObject removeAll(String name) {
        if(name == null) return this;
        if(attrs.isEmpty()) return this;
        int size = attrs.size();
        Iterator<JsonAttributeImpl> iter = attrs.iterator();
        while(iter.hasNext()) {
            JsonAttributeImpl attr = iter.next();
            if(name.equals(attr.name().stringValue())) iter.remove();
        }
        return this;
    }

    @Override
    public int memberIndex(JsonMember m) {
        if(m == null) return -1;
        if(!(m instanceof JsonAttribute)) return -1;
        return attrs.indexOf(m);
    }



    @Override
    public JsonValue findFirst(PathMatcher matcher, List<String> path) {
        Match result = matcher.match(path, this);
        if(result == Match.NOPE) return null;
        if(result == Match.FULLY) return this;
        int index = 0;
        for(JsonAttribute attr : attrs) {
            String name = attr.name().stringValue();
            path.add(name);
            JsonValue rv = ((JsonValueImpl)attr.value()).findFirst(matcher, path);
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
        for(JsonAttribute attr : attrs) {
            String name = attr.name().stringValue();
            path.add(name);
            ((JsonValueImpl)attr.value()).findAll(matcher, values, path);
            path.remove(path.size()-1);
        }
    }


    @Override
    public JsonAttribute attr(String name) {
        if(name == null) return null;
        if(attrs.isEmpty()) return null;
        Iterator<JsonAttributeImpl> iter = attrs.iterator();
        while(iter.hasNext()) {
            JsonAttributeImpl attr = iter.next();
            if(name.equals(attr.name().stringValue())) return attr;
        }
        return null;
    }

    @Override
    public List<JsonAttribute> attrs(String name) {
        List<JsonAttribute> list = new ArrayList<JsonAttribute>();
        if(name == null) return list;
        if(attrs.isEmpty()) return list;
        Iterator<JsonAttributeImpl> iter = attrs.iterator();
        while(iter.hasNext()) {
            JsonAttributeImpl attr = iter.next();
            if(name.equals(attr.name().stringValue())) list.add(attr);
        }
        return list;
    }

    public void remove(JsonAttribute attr) {
        if(attr == null) return;
        attrs.remove(attr);
    }

    @Override
    public void remove() {
        if(group == null) return;
        if(group instanceof JsonAttributeImpl) ((JsonAttributeImpl)group).remove();
        else if(group instanceof JsonArrayImpl) ((JsonArrayImpl)group).remove(this);
        else {}
        setGroup(null);
    }

    @Override
    public JsonValue copy() {
        JsonObjectImpl rv = new JsonObjectImpl();
        for(int i = 0; i < attrs.size(); i++) {
            JsonAttribute attr = attr(i);
            rv.add(attr.name().stringValue(), attr.value().copy());
        }
        return rv;
    }

    @Override
    public Object asTemplateParam() {
        return JsonObjectMapWrapper.instance(this);
    }

}
