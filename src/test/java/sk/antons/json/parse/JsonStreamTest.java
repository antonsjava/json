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


import java.io.StringReader;
import java.util.Iterator;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;
import sk.antons.json.impl.JsonObjectImpl;
import sk.antons.json.JsonValue;
import sk.antons.json.match.SPM;

/**
 *
 * @author antons
 */
public class JsonStreamTest {
	private static Logger log = Logger.getLogger(JsonStreamTest.class.getName());

    @Test
	public void root() throws Exception {
    	String json = "[{\"name\":\"name1\"},{\"name\":\"name2\"},{\"name\":\"name3\"}]";
        Iterator<JsonValue> iter = JsonStream.instance(json, SPM.path("*")).iterator();
        int counter = 0;
        while(iter.hasNext()) {
            JsonValue next = iter.next();
            System.out.println(" ---->>>>>----->>>>>> "+ next.toCompactString());
            counter++;
            Assert.assertTrue("row: " + counter, next.isObject());
        }
        Assert.assertEquals(3, counter);
    }

    @Test
	public void nonroot() throws Exception {
    	String json = "[{\"name\":\"name1\"},{\"name\":\"name2\"},{\"name\":\"name3\"}]";
        Iterator<JsonValue> iter = JsonStream.instance(json, SPM.path("*", "name")).iterator();
        int counter = 0;
        while(iter.hasNext()) {
            JsonValue next = iter.next();
            counter++;
            Assert.assertTrue("row: " + counter, next.isLiteral());
            Assert.assertEquals("row: " + counter, "name" + counter, next.asStringLiteral().stringValue());
        }
        Assert.assertEquals(3, counter);
    }
}
