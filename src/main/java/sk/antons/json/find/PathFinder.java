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
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
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
     * Juste helper methods which converts all() to stream.
     * @return stream from all() list 
     */
    public Stream<JsonValue> stream() {
        return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(
                all().iterator()
                , Spliterator.IMMUTABLE
            ), false);
    }
    
    /**
     * Find first json value with defined path
     * @return first json value defined by path matcher
     */
    public JsonValue first() {
        return root.findFirst(matcher);
    }
    
    /**
     * Just helper class for creating Optional from first()
     * @return optional from first()
     */
    public Optional<JsonValue> optional() {
        return Optional.ofNullable(first());
    }

    /**
     * Find all json values with defined path and converts them to string value
     * @return all json value defined by path matcher converted to string
     */
    public List<String> allLiterals() {
        return root.findAllLiterals(matcher);
    }


    /**
     * Just helper method, which converts allLiterals() to strem.
     * @return stream from allLiterals() list
     */
    public Stream<String> streamLiterals() {
        return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(
                allLiterals().iterator()
                , Spliterator.IMMUTABLE
            ), false);
    }
    
    /**
     * Find first json value with defined path and converts it to string value
     * @return first json value defined by path matcher converted to string
     */
    public String firstLiteral() {
        return root.findFirstLiteral(matcher);
    }
    
    /**
     * Juste helper method for converting firstLiteral() to Optional.
     * @return optional from firstLiteral()
     */
    public Optional<String> optionalLiteral() {
        return Optional.ofNullable(firstLiteral());
    }

}
