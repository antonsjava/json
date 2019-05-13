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
package sk.antons.json.literal;

import java.math.BigDecimal;
import java.math.BigInteger;
import sk.antons.json.literal.impl.JsonExpLiteralImpl;
import sk.antons.json.literal.impl.JsonNullLiteralImpl;
import sk.antons.json.literal.impl.JsonFracLiteralImpl;
import sk.antons.json.literal.impl.JsonBoolLiteralImpl;
import sk.antons.json.literal.impl.JsonStringLiteralImpl;
import sk.antons.json.literal.impl.JsonIntLiteralImpl;
import sk.antons.json.literal.impl.JsonLiteralImpl;
import org.junit.Assert;
import org.junit.Test;
import java.util.logging.Logger;
/**literal
 *
 * @author antons
 */
public class LiteralTest {
    private static Logger log = Logger.getLogger(LiteralTest.class.getName());
	
    @Test
	public void nullTest() throws Exception {
        String value = "null";
        log.info("-------- nullTest: value '"+value+"' --------");
        JsonLiteralImpl literal = JsonLiteralImpl.instance(value, 0, value.length());
        Assert.assertEquals("class ",  literal.getClass(), JsonNullLiteralImpl.class);
        Assert.assertEquals("literal ",  literal.literal(), "null");
    }
    
    @Test
	public void stringTest() throws Exception {
        String value = "\"jablko\"";
        log.info("-------- stringTest: value '"+value+"' --------");
        JsonLiteralImpl literal = JsonLiteralImpl.instance(value, 0, value.length());
        Assert.assertEquals("class ",  literal.getClass(), JsonStringLiteralImpl.class);
        Assert.assertEquals("literal ",  literal.literal(), "\"jablko\"");
        value = "\"jab\\\"lko\"";
        log.info("-------- stringTest: value '"+value+"' --------");
        literal = JsonLiteralImpl.instance(value, 0, value.length());
        Assert.assertEquals("class ",  literal.getClass(), JsonStringLiteralImpl.class);
        Assert.assertEquals("literal ",  literal.literal(), "\"jab\\\"lko\"");
    }
    
    @Test
	public void boolTest() throws Exception {
        String value = "true";
        log.info("-------- bool: value '"+value+"' --------");
        JsonLiteralImpl literal = JsonLiteralImpl.instance(value, 0, value.length());
        Assert.assertEquals("class ",  literal.getClass(), JsonBoolLiteralImpl.class);
        Assert.assertEquals("literal ",  literal.literal(), "true");
        value = "false";
        log.info("-------- bool: value '"+value+"' --------");
        literal = JsonLiteralImpl.instance(value, 0, value.length());
        Assert.assertEquals("class ",  literal.getClass(), JsonBoolLiteralImpl.class);
        Assert.assertEquals("literal ",  literal.literal(), "false");
    }
    
    @Test
	public void intTest() throws Exception {
        String value = "123";
        log.info("-------- int: value '"+value+"' --------");
        JsonLiteralImpl literal = JsonLiteralImpl.instance(value, 0, value.length());
        Assert.assertEquals("class ",  literal.getClass(), JsonIntLiteralImpl.class);
        Assert.assertEquals("literal ",  literal.literal(), "123");
        value = "-123";
        log.info("-------- int: value '"+value+"' --------");
        literal = JsonLiteralImpl.instance(value, 0, value.length());
        Assert.assertEquals("class ",  literal.getClass(), JsonIntLiteralImpl.class);
        Assert.assertEquals("literal ",  literal.literal(), "-123");
    }
    
    @Test
	public void  fracTest() throws Exception {
        String value = "123.2";
        log.info("-------- frac: value '"+value+"' --------");
        JsonLiteralImpl literal = JsonLiteralImpl.instance(value, 0, value.length());
        Assert.assertEquals("class ",  literal.getClass(), JsonFracLiteralImpl.class);
        Assert.assertEquals("literal ",  literal.literal(), "123.2");
        value = "-123.2";
        log.info("-------- frac: value '"+value+"' --------");
        literal = JsonLiteralImpl.instance(value, 0, value.length());
        Assert.assertEquals("class ",  literal.getClass(), JsonFracLiteralImpl.class);
        Assert.assertEquals("literal ",  literal.literal(), "-123.2");
    }
    
    @Test
	public void fracTest2() throws Exception {
        String value = "500.00";
        log.info("-------- frac: value '"+value+"' --------");
        JsonLiteralImpl literal = JsonLiteralImpl.instance(value, 0, value.length());
        Assert.assertEquals("class ",  literal.getClass(), JsonFracLiteralImpl.class);
        Assert.assertEquals("literal ",  literal.literal(), "500.00");
        Assert.assertEquals("bd ",  literal.asFracLiteral().bdValue(), new BigDecimal("500.00"));
    }
    
    @Test
	public void  expTest() throws Exception {
        String value = "123.2e12";
        log.info("-------- exp: value '"+value+"' --------");
        JsonLiteralImpl literal = JsonLiteralImpl.instance(value, 0, value.length());
        Assert.assertEquals("class ",  literal.getClass(), JsonExpLiteralImpl.class);
        Assert.assertEquals("literal ",  literal.literal(), "123.2e12");
        value = "-123.2e-12";
        log.info("-------- exp: value '"+value+"' --------");
        literal = JsonLiteralImpl.instance(value, 0, value.length());
        Assert.assertEquals("class ",  literal.getClass(), JsonExpLiteralImpl.class);
        Assert.assertEquals("literal ",  literal.literal(), "-123.2e-12");
    }
}
