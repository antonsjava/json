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
package sk.antons.json.match.wild;

import sk.antons.json.match.*;
import java.util.ArrayList;
import java.util.List;
import sk.antons.json.JsonValue;

/**
 * Check path with provided sequence of string values. Double asterix character 
 * can be used for any subpath in path.
 * 
 * Single asterix and question mark can be used for substrings in path element.
 * 
 * ["items-*", "**", "name"] can match paths in length >=2 starting with items-<something> 
 * and ending with name. ( /items-12/foo/bar/name)
 * 
 * @author antons
 */
public class WildPathMatcher implements PathMatcher {
    private List<String> items = null;
    private List<Element> elements = new ArrayList<Element>();

    public WildPathMatcher(List<String> path) {
        this.items = path;
        init();
    }
    
    public WildPathMatcher(String... path) {
        if(path == null) return;
        this.items = new ArrayList<String>();
        for(int i = 0; i < path.length; i++) {
            this.items.add(path[i]);
        }
        init();
    }

    public static WildPathMatcher instance(List<String> path) {
        return new WildPathMatcher(path);
    }
    
    public static WildPathMatcher instance(String... path) {
        return new WildPathMatcher(path);
    }
    
    public static WildPathMatcher fromPath(String path) {
        return new WildPathMatcher(PathSplitter.split(path));
    }
    

    private void init() {
        for(String item : items) {
            if("**".equals(item)) elements.add(AllElement.instance());
            else elements.add(WildElement.instance(item));
        }
    }
    
    @Override
    public Match match(List<String> currentpath, JsonValue currentvalue) {
        if(currentpath == null) return Match.MAYBE;
        if(currentpath.size()== 0) return Match.MAYBE;
        return tryMatch(currentpath, 0, 0);
    }
    
    public Match tryMatch(List<String> currentpath, int element, int path) {
        if(element >= elements.size()) {
            if(path == currentpath.size()) return Match.FULLY;
            else if(path < currentpath.size()) return Match.NOPE;
            else return Match.MAYBE;
        } else if(path >= currentpath.size()) {
            return Match.MAYBE;
        } else {
            Element el = elements.get(element);
            int min = el.minLength();
            int max = el.maxLength(currentpath.size() - path);
            Match rv = Match.NOPE;
            for(int i = max; i >= min; i--) {
                boolean match = true;
                for(int j = 0; j < i; j++) {
                    String item = currentpath.get(path+j);
                    match = el.match(item);
                    if(!match) break;
                }
                if(match) {
                    Match result = tryMatch(currentpath, element+1, path+i);
                    if(result == Match.FULLY) return result;
                    if(result == Match.MAYBE) rv = result;
                }
            }
            return rv;
        }
        
    }
    
    private static interface Element {
        boolean match(String value);
        int minLength();
        int maxLength(int limit);
    }

    private static class WildElement implements Element {
        WildMatcher matcher = null;
        
        public WildElement(String pattern) {
            matcher = WildMatcher.instance(pattern);
        }

        public static WildElement instance(String pattern) { return new WildElement(pattern); }
        
        @Override
        public boolean match(String value) {
            return matcher.match(value);
        }

        @Override public int minLength() { return 1;}
        @Override public int maxLength(int limit) { return 1; }
        
    }
    
    private static class AllElement implements Element {
        
        public AllElement() {}

        public static AllElement instance() { return new AllElement(); }
        
        @Override public boolean match(String value) { return true; }
        @Override public int minLength() { return 0;}
        @Override public int maxLength(int limit) { return limit; }
    }
    
}
