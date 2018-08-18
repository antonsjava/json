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
package sk.antons.json.parse;


import java.util.List;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;
import sk.antons.json.match.SimplePathMatcher;
import sk.antons.json.source.StringSource;

/**
 *
 * @author antons
 */
public class SimpleLiteralParseTest {
	private static Logger log = Logger.getLogger(SimpleLiteralParseTest.class.getName());
    
    
    @Test
	public void carFirst() throws Exception {
    	String json = "[{\"name\":\"John\",\"age\":30,\"cars\":[\"Ford\",\"BMW\",\"Fiat\"]}]";
        log.info("test : " + json);
        String value = LiteralParser.firstAsString(StringSource.instance(json), SimplePathMatcher.instance("*", "cars", "*"));
        Assert.assertEquals("result: ", value, "Ford");
    }
    @Test
	public void carAll() throws Exception {
    	String json = "[{\"name\":\"John\",\"age\":30,\"cars\":[\"Ford\",\"BMW\",\"Fiat\"]}]";
        log.info("test : " + json);
        List<String> values = LiteralParser.allAsString(StringSource.instance(json), SimplePathMatcher.instance("*", "cars", "*"));
        Assert.assertEquals("result: ", values.size(), 3);
        Assert.assertEquals("result: ", values.get(2), "Fiat");
    }
    
}
