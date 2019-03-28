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
package sk.antons.json.util;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author antons
 */
public class FormatTest {

    private static String json = "{\"menu\": {\n" +
"  \"id\": \"file\",\n" +
"  \"value\": \"File\",\n" +
"  \"popup\": {\n" +
"    \"menuitem\": [\n" +
"      {\"value\": \"New\", \"onclick\": \"CreateNewDoc()\"},\n" +
"      {\"value\": \"Open\", \"onclick\": \"OpenDoc()\"},\n" +
"      {\"value\": \"Close\", \"onclick\": \"CloseDoc()\"}\n" +
"    ]\n" +
"  }\n" +
"}}";
	
    @Test
	public void formatNoChange() throws Exception {
        Assert.assertEquals(json, JsonFormat.from(json).toText());
    }
    
    @Test
	public void formatNoIndent() throws Exception {
        Assert.assertEquals("{\"menu\":{\"id\":\"file\",\"value\":\"File\",\"popup\":{\"menuitem\":[{\"value\":\"New\",\"onclick\":\"CreateNewDoc()\"},{\"value\":\"Open\",\"onclick\":\"OpenDoc()\"},{\"value\":\"Close\",\"onclick\":\"CloseDoc()\"}]}}}", JsonFormat.from(json).noindent().toText());
    }
    
    @Test
	public void formatIndent() throws Exception {
        Assert.assertEquals("{\n" +
"  \"menu\" : {\n" +
"    \"id\" : \"file\",\n" +
"    \"value\" : \"File\",\n" +
"    \"popup\" : {\n" +
"      \"menuitem\" : [\n" +
"        {\n" +
"          \"value\" : \"New\",\n" +
"          \"onclick\" : \"CreateNewDoc()\"\n" +
"        },{\n" +
"          \"value\" : \"Open\",\n" +
"          \"onclick\" : \"OpenDoc()\"\n" +
"        },{\n" +
"          \"value\" : \"Close\",\n" +
"          \"onclick\" : \"CloseDoc()\"\n" +
"        }\n" +
"      ]\n" +
"    }\n" +
"  }\n" +
"}", JsonFormat.from(json).indent(2, ' ').toText());
    }
    
    @Test
	public void formatIndentCut() throws Exception {
        Assert.assertEquals("{\n" +
"  \"menu\" : {\n" +
"    \"id\" : \"file\",\n" +
"    \"value\" : \"File\",\n" +
"    \"popup\" : {\n" +
"      \"menuitem\" : [\n" +
"        {\n" +
"          \"value\" : \"New\",\n" +
"          \"onclick\" : \"CreateNew ...\"\n" +
"        },{\n" +
"          \"value\" : \"Open\",\n" +
"          \"onclick\" : \"OpenDoc()\"\n" +
"        },{\n" +
"          \"value\" : \"Close\",\n" +
"          \"onclick\" : \"CloseDoc( ...\"\n" +
"        }\n" +
"      ]\n" +
"    }\n" +
"  }\n" +
"}", JsonFormat.from(json).indent(2, ' ').cutStringLiterals(9).toText());
    }
}
