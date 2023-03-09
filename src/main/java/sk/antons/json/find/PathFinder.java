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
package sk.antons.json.find;

import sk.antons.json.JsonValue;
import java.util.List;
import sk.antons.json.match.PathMatcher;
import sk.antons.json.match.SPM;

/**
 * Helper class for find by path functionality
 * @author antons
 */
public class PathFinder {

    JsonValue root;
    PathMatcher matcher;
 
    /**
     * New instance or finder
     * @param root root json node
     * @param matcher path matcher for find functionality
     */
    public PathFinder(JsonValue root, PathMatcher matcher) {
        this.root = root;
        this.matcher = matcher;
    }

    /**
     * New instance of PathFinder 
     * @param root root json node.
     * @param matcher path matcher for find 
     * @return new instance
     */
    public static PathFinder of(JsonValue root, PathMatcher matcher) { return new PathFinder(root, matcher); }
    
    /**
     * New instance of PathFinder 
     * @param root root json node.
     * @param path path for simple path matcher
     * @return new instance
     */
    public static PathFinder of(JsonValue root, String... path) { return new PathFinder(root, SPM.path(path)); }

    /**
     * Find all json values with defined path
     * @return all json values defined by path matcher
     */
    List<JsonValue> all() {
        return root.findAll(matcher);
    }
    
    /**
     * Find first json value with defined path
     * @return first json value defined by path matcher
     */
    JsonValue first() {
        return root.findFirst(matcher);
    }

    /**
     * Find all json values with defined path and converts them to string value
     * @return all json value defined by path matcher converted to string
     */
    List<String> allLiterals() {
        return root.findAllLiterals(matcher);
    }
    
    /**
     * Find first json value with defined path and converts it to string value
     * @return first json value defined by path matcher converted to string
     */
    String firstLiteral(PathMatcher matcher) {
        return root.findFirstLiteral(matcher);
    }
    

}
