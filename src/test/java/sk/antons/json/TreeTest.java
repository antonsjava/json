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
package sk.antons.json;


import java.util.List;
import sk.antons.json.parse.*;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;
import sk.antons.json.match.SimplePathMatcher;

/**
 *
 * @author antons
 */
public class TreeTest {
	private static Logger log = Logger.getLogger(TreeTest.class.getName());

    private JsonObject build() {
        JsonArray a = JsonFactory.array()
            .add(JsonFactory.object()
                    .add("value", JsonFactory.stringLiteral("New"))
                    .add("onclick", JsonFactory.stringLiteral("CreateNewDoc()")))
            .add(JsonFactory.object()
                    .add("value", JsonFactory.stringLiteral("Open"))
                    .add("onclick", JsonFactory.stringLiteral("OpenDoc()")))
            .add(JsonFactory.object()
                    .add("value", JsonFactory.stringLiteral("Close"))
                    .add("onclick", JsonFactory.stringLiteral("CloseDoc()")));
        JsonObject o = JsonFactory.object()
            .add("menuitem", a);
        JsonObject rv = JsonFactory.object()
            .add("id", JsonFactory.stringLiteral("file"))
            .add("value", JsonFactory.stringLiteral("File"))
            .add("popup", o);
        rv = JsonFactory.object().add("menu", rv);

        JsonObject person = JsonFactory.object()
            .add("name", JsonFactory.stringLiteral("John"))
            .add("surname", JsonFactory.stringLiteral("Smith"))
            .add("surname", JsonFactory.array()
                    .add(JsonFactory.stringLiteral("Mudr."))
                    .add(JsonFactory.stringLiteral("Phd."))
                );
        return rv;
    }

    private JsonObject parse() {
        JsonObject o = JsonParser.parse("{\"menu\": {\n" +
"  \"id\": \"file\",\n" +
"  \"value\": \"File\",\n" +
"  \"popup\": {\n" +
"    \"menuitem\": [\n" +
"      {\"value\": \"New\", \"onclick\": \"CreateNewDoc()\"},\n" +
"      {\"value\": \"Open\", \"onclick\": \"OpenDoc()\"},\n" +
"      {\"value\": \"Close\", \"onclick\": \"CloseDoc()\"}\n" +
"    ]\n" +
"  }\n" +
"}}").asObject();
        return o;
    }

	public void objectTest(JsonObject o) throws Exception {
        Assert.assertNotNull(o);
        System.out.println(" ------------------------");
        System.out.println(o.toPrettyString("  "));
        System.out.println(" ------------------------");
        JsonValue v = null;
        List<JsonValue> list = null;
        v = o.first("menu");
        list = o.all("menu");
        Assert.assertNotNull("first menu", v);
        Assert.assertFalse("all menu", list.isEmpty());
        Assert.assertTrue("list contains menu", list.contains(v));
        Assert.assertEquals("parent index", 0, v.parentIndex());
        Assert.assertEquals("parent ", o, v.parent());

        v = o.findFirst(SimplePathMatcher.instance("menu", "popup", "menuitem", "*", "value"));
        list = o.findAll(SimplePathMatcher.instance("menu", "popup", "menuitem", "*", "value"));
        System.out.println("vvv " + v.toCompactString());
        System.out.println("lll " + list);

        Assert.assertNotNull("first menu", v);
        Assert.assertFalse("all menu", list.isEmpty());
        Assert.assertTrue("list contains menu", list.contains(v));
        Assert.assertEquals("value New", "New", v.asStringLiteral().stringValue());
        Assert.assertEquals("size ", 3, list.size());

        String[] path = v.path();
        Assert.assertEquals("size", 5, path.length);
        Assert.assertEquals( "menu", path[0]);
        Assert.assertEquals( "popup", path[1]);
        Assert.assertEquals( "menuitem", path[2]);
        Assert.assertEquals( "0", path[3]);
        Assert.assertEquals( "value", path[4]);


    }

    @Test
	public void parseTest() throws Exception {
        JsonObject o = parse();
        objectTest(o);
    }

    @Test
	public void buildTest() throws Exception {
        JsonObject o = build();
        objectTest(o);
    }

    @Test
	public void find() throws Exception {
        JsonObject o = parse();
        List<JsonValue> list = o.find("menu", "popup", "menuitem", "*", "value").all();
        Assert.assertEquals(3, list.size());
        JsonValue xx = o.find("menu", "popup", "menuitem", "*", "value").first();
        Assert.assertTrue(xx.isLiteral());
        Assert.assertEquals("New", xx.asStringLiteral().stringValue());
        JsonValue xx2 = xx.find("..", "onclick").first();
        Assert.assertEquals("CreateNewDoc()", xx2.asStringLiteral().stringValue());
        xx2 = xx.find("..", "..", "..", "..", "value").first();
        Assert.assertEquals("File", xx2.asStringLiteral().stringValue());
        xx2 = xx.findPath("../../../../value").first();
        Assert.assertEquals("File", xx2.asStringLiteral().stringValue());

    }

}
