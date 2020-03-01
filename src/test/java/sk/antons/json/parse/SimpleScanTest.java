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
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;
import sk.antons.json.JsonArray;
import sk.antons.json.impl.JsonObjectImpl;
import sk.antons.json.literal.impl.JsonBoolLiteralImpl;
import sk.antons.json.literal.impl.JsonIntLiteralImpl;
import sk.antons.json.literal.impl.JsonNullLiteralImpl;
import sk.antons.json.JsonValue;

/**
 *
 * @author antons
 */
public class SimpleScanTest {
	private static Logger log = Logger.getLogger(SimpleScanTest.class.getName());
    
    @Test
	public void nullRealTest() throws Exception {
    	String json = null;
        JsonScanner scanner = JsonScanner.instance(json);
        JsonScanner.Token token = scanner.next();
        Assert.assertNull("final: " + token, token);
    }
    
    @Test
	public void nullTest() throws Exception {
    	String json = "null";
        JsonScanner scanner = JsonScanner.instance(json);
        JsonScanner.Token token = scanner.next();
        Assert.assertEquals("first: " + token, JsonScanner.Token.LITERAL, token);
        Assert.assertEquals("first value: " + scanner.stringValue(), "null", scanner.stringValue());
        token = scanner.next();
        Assert.assertNull("final: " + token, token);
    }
    
    @Test
	public void jsonBoolTest() throws Exception {
    	String json = "true";
        JsonScanner scanner = JsonScanner.instance(json);
        JsonScanner.Token token = scanner.next();
        Assert.assertEquals("first: " + token, JsonScanner.Token.LITERAL, token);
        Assert.assertEquals("first value: " + scanner.stringValue(), true, scanner.booleanValue());
        token = scanner.next();
        Assert.assertNull("final: " + token, token);
    }
    
	
    //@Test
	public void charEscapeSimple() throws Exception {
        //            0                     1                                 2                    3                                                  4                             5                                6                                7 
        //            012       34567     890       123        456 789        01234           5678901        234   5 6          789                   0123        45  6789          012            3456        78   90          12             34567890123456789
    	String json = "{\"menu\": { \"id\": \"file\", \"value\": 12, \"popup\": { \"menuitem\": [ { \"value\": 9.23, \"onclick\": \"CreateNewDoc()\" }, {\"value\":123 , \"onclick\": \"OpenDoc()\"},{\"value\":12E2,\"onclick\":\"CloseDoc()\"}] } }}";
        log.info("test : " + json);
        //JsonValue value = JsonParser.parse(json);
        JsonValue value = JsonParser.parse(new StringReader(json));
        Assert.assertNotNull("result: " + value, value);
        log.info("test result : " + value.toCompactString());
        log.info("test result : " + value.toPrettyString("  "));
        Assert.assertTrue("class: " + value.getClass(), value instanceof JsonObjectImpl);
    }
    
    @Test
	public void scan() throws Exception {
    	String json = "[{\"name\":\"John\",\"age\":30,\"cars\":[\"Ford\",\"BMW\",\"Fiat\"]}]";
        log.info("test : " + json);
        JsonScanner scanner = JsonScanner.instance(json);
        JsonScanner.Token token = scanner.next();
        Assert.assertEquals("first: " + token, JsonScanner.Token.ARRAY_START, token);
        token = scanner.next();
        Assert.assertEquals("first: " + token, JsonScanner.Token.OBJECT_START, token);
        token = scanner.next();
        Assert.assertEquals("first: " + token, JsonScanner.Token.NAME, token);
        Assert.assertEquals("first: " + token, "name", scanner.stringValue());
        token = scanner.next();
        Assert.assertEquals("first: " + token, JsonScanner.Token.LITERAL, token);
        Assert.assertEquals("first: " + token, "John", scanner.stringValue());
        token = scanner.next();
        Assert.assertEquals("first: " + token, JsonScanner.Token.NAME, token);
        token = scanner.next();
        Assert.assertEquals("first: " + token, JsonScanner.Token.LITERAL, token);
        token = scanner.next();
        Assert.assertEquals("first: " + token, JsonScanner.Token.NAME, token);
        token = scanner.next();
        Assert.assertEquals("first: " + token, JsonScanner.Token.ARRAY_START, token);
        token = scanner.next();
        Assert.assertEquals("first: " + token, JsonScanner.Token.LITERAL, token);
        token = scanner.next();
        Assert.assertEquals("first: " + token, JsonScanner.Token.LITERAL, token);
        Assert.assertEquals("first: " + token, "BMW", scanner.stringValue());
        token = scanner.next();
        Assert.assertEquals("first: " + token, JsonScanner.Token.LITERAL, token);
        token = scanner.next();
        Assert.assertEquals("first: " + token, JsonScanner.Token.ARRAY_END, token);
        token = scanner.next();
        Assert.assertEquals("first: " + token, JsonScanner.Token.OBJECT_END, token);
        token = scanner.next();
        Assert.assertEquals("first: " + token, JsonScanner.Token.ARRAY_END, token);
        token = scanner.next();
        Assert.assertNull("final: " + token, token);
        
//        while(token != null) {            
//            log.info("token " + token);
//            token = scanner.next();
//        }
    }
    @Test
	public void scanskip() throws Exception {
    	String json = "[{\"name\":\"John\",\"age\":30,\"cars\":[\"Ford\",\"BMW\",\"Fiat\"]}]";
        log.info("test : " + json);
        JsonScanner scanner = JsonScanner.instance(json);
        JsonScanner.Token token = scanner.next();
        Assert.assertEquals("first: " + token, JsonScanner.Token.ARRAY_START, token);
        token = scanner.next();
        Assert.assertEquals("first: " + token, JsonScanner.Token.OBJECT_START, token);
        token = scanner.next();
        Assert.assertEquals("first: " + token, JsonScanner.Token.NAME, token);
        Assert.assertEquals("first: " + token, "name", scanner.stringValue());
        token = scanner.skipNext();
        //Assert.assertEquals("first: " + token, JsonScanner.Token.LITERAL, token);
        //Assert.assertEquals("first: " + token, "John", scanner.stringValue());
        //token = scanner.next();
        Assert.assertEquals("first: " + token, JsonScanner.Token.NAME, token);
        token = scanner.next();
        Assert.assertEquals("first: " + token, JsonScanner.Token.LITERAL, token);
        token = scanner.next();
        Assert.assertEquals("first: " + token, JsonScanner.Token.NAME, token);
        token = scanner.skipNext();
        //Assert.assertEquals("first: " + token, JsonScanner.Token.ARRAY_START, token);
        //token = scanner.next();
        //Assert.assertEquals("first: " + token, JsonScanner.Token.LITERAL, token);
        //token = scanner.next();
        //Assert.assertEquals("first: " + token, JsonScanner.Token.LITERAL, token);
        //Assert.assertEquals("first: " + token, "BMW", scanner.stringValue());
        //token = scanner.next();
        //Assert.assertEquals("first: " + token, JsonScanner.Token.LITERAL, token);
        //token = scanner.next();
        //Assert.assertEquals("first: " + token, JsonScanner.Token.ARRAY_END, token);
        //token = scanner.next();
        Assert.assertEquals("first: " + token, JsonScanner.Token.OBJECT_END, token);
        token = scanner.next();
        Assert.assertEquals("first: " + token, JsonScanner.Token.ARRAY_END, token);
        token = scanner.next();
        Assert.assertNull("final: " + token, token);
        
//        while(token != null) {            
//            log.info("token " + token);
//            token = scanner.next();
//        }
    }
}
