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

import java.util.List;
import sk.antons.json.JsonValue;

/**
 * SimplePathMatcher provider with short name;
 * @author antons
 */
public class NothingMather implements PathMatcher {


    public static NothingMather instance() { return new NothingMather(); }

    @Override
    public Match match(List<String> currentpath, JsonValue currentvalue) {
        return Match.NOPE;
    }

}
