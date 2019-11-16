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
import java.util.List;
import sk.antons.json.JsonArray;
import sk.antons.json.JsonObject;
import sk.antons.json.JsonValue;
import sk.antons.json.literal.JsonBoolLiteral;
import sk.antons.json.literal.JsonExpLiteral;
import sk.antons.json.literal.JsonFracLiteral;
import sk.antons.json.literal.JsonIntLiteral;
import sk.antons.json.literal.JsonLiteral;
import sk.antons.json.literal.JsonNullLiteral;
import sk.antons.json.literal.JsonStringLiteral;
import sk.antons.json.match.PathMatcher;

/**
 *
 * @author antons
 */
public abstract class JsonValueImpl implements JsonValue, JsonMember {
    
    protected JsonGroup group = null;
    @Override
    public JsonGroup group() { return group; }
    @Override
    public void setGroup(JsonGroup group) { this.group = group; }

    @Override public JsonObject asObject() { return (JsonObject)this; }
    @Override public JsonArray asArray() { return (JsonArray)this; }
    
    @Override public JsonNullLiteral asNullLiteral() { return (JsonNullLiteral)this; }
    @Override public JsonBoolLiteral asBoolLiteral() { return (JsonBoolLiteral)this; }
    @Override public JsonExpLiteral asExpLiteral() { return (JsonExpLiteral)this; }
    @Override public JsonFracLiteral asFracLiteral() { return (JsonFracLiteral)this; }
    @Override public JsonIntLiteral asIntLiteral() { return (JsonIntLiteral)this; }
    @Override public JsonStringLiteral asStringLiteral() { return (JsonStringLiteral)this; }
    @Override public JsonLiteral asLiteral() { return (JsonLiteral)this; }
    
    @Override public boolean isObject() { return this instanceof JsonObject; }
    @Override public boolean isArray() { return this instanceof JsonArray; }
    
    @Override public boolean isNullLiteral() { return this instanceof JsonNullLiteral; }
    @Override public boolean isBoolLiteral() { return this instanceof JsonBoolLiteral; }
    @Override public boolean isExpLiteral() { return this instanceof JsonExpLiteral; }
    @Override public boolean isFracLiteral() { return this instanceof JsonFracLiteral; }
    @Override public boolean isIntLiteral() { return this instanceof JsonIntLiteral; }
    @Override public boolean isStringLiteral() { return this instanceof JsonStringLiteral; }
    @Override public boolean isLiteral() { return this instanceof JsonLiteral; }

    @Override
    public String toCompactString() {
        StringBuilder sb = new StringBuilder();
        toCompactString(sb);
        return sb.toString();
    }

    @Override
    public String toPrettyString(String indent) {
        StringBuilder sb = new StringBuilder();
        toPrettyString(sb, "", indent);
        return sb.toString();
    }

    public abstract void toCompactString(StringBuilder sb);
    public abstract void toPrettyString(StringBuilder sb, String prefix, String indent);

    @Override
    public List<JsonValue> findAll(PathMatcher matcher) {
        List<JsonValue> list = new ArrayList<JsonValue>();
        List<String> path = new ArrayList<String>();
        findAll(matcher, list, path);
        return list;
    }

    @Override
    public JsonValue findFirst(PathMatcher matcher) {
        List<String> path = new ArrayList<String>();
        return findFirst(matcher, path);
    }

    @Override
    public List<String> findAllLiterals(PathMatcher matcher) {
        List<String> rv = new ArrayList<String>();
        List<JsonValue> values = findAll(matcher);
        if(values.isEmpty()) return rv;
        List<String> list = new ArrayList<String>();
        for(JsonValue value : values) {
            if(value instanceof JsonLiteral) {
                list.add(value.asLiteral().stringValue());
            } else {
                return rv;
            }
        }
        return list;
    }

    @Override
    public String findFirstLiteral(PathMatcher matcher) {
        JsonValue value = findFirst(matcher);
        if(value == null) return null;
        if(value instanceof JsonLiteral) return value.asLiteral().stringValue();
        return null;
    }

    
    
    public abstract JsonValue findFirst(PathMatcher matcher, List<String> path);
    public abstract void findAll(PathMatcher matcher, List<JsonValue> values, List<String> path);

    @Override
    public JsonValue parent() {
        if(this instanceof JsonMember) {
            JsonGroup g = ((JsonMember)this).group();
            while(g != null) {
                if(g instanceof JsonValue) break;
                if(g instanceof JsonMember) {
                    g = ((JsonMember)g).group();
                } else {
                    g = null;
                }
            }
            if(g instanceof JsonValue) return (JsonValue)g;
            return null;
        } else {
            return null;
        }
    }

    @Override
    public int parentIndex() {
        if(this instanceof JsonMember) {
            JsonMember m = (JsonMember)this;
            JsonGroup g = m.group();
            while(g != null) {
                if(g instanceof JsonValue) break;
                if(g instanceof JsonMember) {
                    m = (JsonMember)g;
                    g = m.group();
                } else {
                    g = null;
                }
            }
            if(g == null) return -1;
            return g.memberIndex(m);
        } else {
            return -1;
        }
    }

    @Override
    public String[] path() {
        List<String> list = new ArrayList<String>();
        JsonMember m = null;
        JsonGroup g = null;
        if(this instanceof JsonMember) {
            m = (JsonMember)this;
            while(m != null) {
                g = m.group();
                if(g != null) {
                    if(g instanceof JsonArrayImpl) {
                        int i = g.memberIndex(m);
                        list.add(String.valueOf(i));
                    } else if(g instanceof JsonAttributeImpl) {
                        JsonAttributeImpl a = (JsonAttributeImpl)g;
                        list.add(a.name().stringValue());
                    }
                    if(g instanceof JsonMember) m = (JsonMember)g;
                    else m = null;
                } else {
                    m = null;
                }
            }
        }
        String[] rv = new String[list.size()]; 
        for(int i = 0; i < rv.length; i++) {
            rv[i] = list.get(rv.length-1-i);
        }
        return rv;
    }

    @Override
    public boolean isDescendantOf(JsonValue parent) {
        if(parent == null) return false;
        if(parent == this) return true;
        JsonMember m = null;
        JsonGroup g = null;
        if(this instanceof JsonMember) {
            m = (JsonMember)this;
            g = m.group();
            if(g == null) return false;
            return g.isDescendantOf(parent);
        } else {
            return false;
        }
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
    public void replaceBy(JsonValue newValue) {
        if(group == null) return;
        if(group instanceof JsonAttributeImpl) ((JsonAttributeImpl)group).setValue(newValue);
        else if(group instanceof JsonArrayImpl) ((JsonArrayImpl)group).replaceBy(this, newValue);
        else {}
        setGroup(null);
    }
        
}
