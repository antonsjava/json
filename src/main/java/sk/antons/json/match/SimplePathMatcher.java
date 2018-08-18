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
package sk.antons.json.match;

import java.util.ArrayList;
import java.util.List;
import sk.antons.json.JsonValue;

/**
 * Check path with provided sequence of string values. Asterix character 
 * can be used for any name in path.
 * 
 * ["items", "*", "name"] can match paths in length 3 starting with items 
 * and ending with name.
 * 
 * @author antons
 */
public class SimplePathMatcher implements PathMatcher {
    private List<String> items = null;

    public SimplePathMatcher(List<String> path) {
        this.items = path;
    }
    
    public SimplePathMatcher(String... path) {
        if(path == null) return;
        this.items = new ArrayList<String>();
        for(int i = 0; i < path.length; i++) {
            this.items.add(path[i]);
        }
    }

    public static SimplePathMatcher instance(List<String> path) {
        return new SimplePathMatcher(path);
    }
    
    public static SimplePathMatcher instance(String... path) {
        return new SimplePathMatcher(path);
    }
    
    @Override
    public Match match(List<String> currentpath, JsonValue currentvalue) {
        if(currentpath == null) return Match.NOPE;
        int len = currentpath.size();
        if(len == 0) return Match.MAYBE;
        if(len > items.size()) return Match.NOPE;
        int index = len-1;
        String current = currentpath.get(index);
        if(current == null) return Match.NOPE;
        String item = items.get(index);
        if((current.equals(item)) || ("*".equals(item))) {
            if(len == items.size()) return Match.FULLY;
            else return Match.MAYBE;
        }
        return Match.NOPE;
    }
    
}
