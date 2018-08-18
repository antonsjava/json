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
package sk.antons.json.parse.traversal;


import sk.antons.json.source.StringSource;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;
import sk.antons.json.literal.impl.JsonBoolLiteralImpl;
import sk.antons.json.literal.impl.JsonExpLiteralImpl;
import sk.antons.json.literal.impl.JsonFracLiteralImpl;
import sk.antons.json.literal.impl.JsonIntLiteralImpl;
import sk.antons.json.literal.impl.JsonLiteralImpl;
import sk.antons.json.literal.impl.JsonNullLiteralImpl;
import sk.antons.json.literal.impl.JsonStringLiteralImpl;

/**
 *
 * @author antons
 */
public class SimpleTraversalTest {
	private static Logger log = Logger.getLogger(SimpleTraversalTest.class.getName());
	
    @Test
	public void charEscapeSimple() throws Exception {
        //            0                     1                                 2                    3                                                  4                             5                                6                                7 
        //            012       34567     890       123        456 789        01234           5678901        234   5 6          789                   0123        45  6789          012            3456        78   90          12             34567890123456789
    	String json = "{\"menu\": { \"id\": \"file\", \"value\": 12, \"popup\": { \"menuitem\": [ { \"value\": 9.23, \"onclick\": \"CreateNewDoc()\" }, {\"value\":123 , \"onclick\": \"OpenDoc()\"},{\"value\":12E2,\"onclick\":\"CloseDoc()\"}] } }}";
    	String sequence = "c{S: { S: S, S: I, S: { S: [ { S: F, S: S }, {S:I , S: S},{S:E,S:S}] } }}$";
        //                 01234567890123456789012345678901234567890123456789012345678901234567890123456789
        //                 0         1         2         3         4         5         6         7 
    	Handler handler = new Handler(sequence);
        TraversalParser parser = TraversalParser.instance(StringSource.instance(json), handler);
        parser.parse();
    }
    
    
    
    private static class Handler implements JsonContentHandler {

        private String sequence;
        int pos = 0;

        public Handler(String sequence) {
            this.sequence = sequence;
        }

        
        private void test(char c) {
        	
//        	if(sequence.charAt(pos) != c) {
//        		log.info("position: " + pos);
//        		log.info("requested char: '" + c + "'");
//        		log.info("presented char: '" + sequence.charAt(pos) + "'");
//        	}
        	Assert.assertEquals("Difference at position "+pos + " rxpected '"+c+"' found '"+sequence.charAt(pos)+"'", sequence.charAt(pos), c);
        }

        @Override
        public void startDocument() {
            log.info("--> startDocument");
        	test('c');
        	pos++;
        }

        @Override
        public void endDocument() {
            log.info("--> endDocument");
        	test('$');
        	pos++;
        }

        @Override
        public void startArray() {
            log.info("--> startArray");
        	test('[');
        	pos++;
        }

        @Override
        public void endArray() {
            log.info("--> endArray");
        	test(']');
        	pos++;
        }

        @Override
        public void startObject() {
            log.info("--> startObject");
        	test('{');
        	pos++;
        }

        @Override
        public void endObject() {
            log.info("--> endObject");
        	test('}');
        	pos++;
        }

        @Override
        public void valueSeparator() {
            log.info("--> valueSeparator");
        	test(',');
        	pos++;
        }

        @Override
        public void nameSeparator() {
            log.info("--> nameSeparator");
        	test(':');
        	pos++;
        }

        @Override
        public void literal(JsonLiteralImpl literal) {
            log.info("--> literal --" + literal.literal() + "-- " + literal.getClass());
        	if(literal instanceof JsonStringLiteralImpl) test('S');
            else if(literal instanceof JsonNullLiteralImpl) test('N');
            else if(literal instanceof JsonBoolLiteralImpl) test('B');
            else if(literal instanceof JsonIntLiteralImpl) test('I');
            else if(literal instanceof JsonFracLiteralImpl) test('F');
            else if(literal instanceof JsonExpLiteralImpl) test('E');
            else Assert.assertFalse("Unknown literal type " + literal.getClass(), true);
        	pos++;
        }

        @Override
        public void whiteSpace(String content, int offset, int length) {
            log.info("--> whiteSpace --" + content.substring(offset, offset + length) + "--");
        	test(' ');
        	pos++;
        }

        @Override
        public String contextInfo() {
            return null;
        }

        
        
    }

    
}
