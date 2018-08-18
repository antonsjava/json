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

import sk.antons.json.util.JsonEscaper;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author antons
 */
public class EscapeTest {
	
    @Test
	public void charEscapeSimple() throws Exception {
        for(int i = 0; i < 255; i++) {
            char c = (char)i;
            Assert.assertTrue("escape char   '"+c+"' - ", c == JsonEscaper.unescapeChar(JsonEscaper.escapeChar(c)));
        }
    }
    
    @Test
	public void charEscapeSpecial() throws Exception {
        String s = "1\n2\r3\t4\b5\\6\"7";
        String s2 = "1\\n2\\r3\\t4\\b5\\\\6\\\"7";
        System.out.println(" --- " + s);
        System.out.println(" --- " + s2);
        System.out.println(" --- " + JsonEscaper.escape(s));
        System.out.println(" --- " + JsonEscaper.unescape(s2));
        Assert.assertTrue("escape   '"+s+"' - " + s2, s2.equals(JsonEscaper.escape(s)));
        Assert.assertTrue("unescape   '"+s+"' - " + s2, s.equals(JsonEscaper.unescape(s2)));
    }
}
