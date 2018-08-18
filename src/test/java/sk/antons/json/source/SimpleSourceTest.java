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
package sk.antons.json.source;


import java.io.StringReader;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author antons
 */
public class SimpleSourceTest {
	private static Logger log = Logger.getLogger(SimpleSourceTest.class.getName());
    
    @Test
	public void nextandcurrent() throws Exception {
    	String json = "123456789abcdefghijklmnoprstuvz";
        log.info("test : " + json);
        StringSource string = StringSource.instance(json);
        ReaderSource reader = ReaderSource.instance(new StringReader(json));
        for(int i = 0; i < json.length(); i++) {
            int a = string.next();
            int b = reader.next();
            int ac = string.current();
            int bc = reader.current();
            log.info("next    at "+i+" - string '"+((char)a)+"' reader '"+((char)b)+"'");
            log.info("current at "+i+" - string '"+((char)ac)+"' reader '"+((char)bc)+"'");
            Assert.assertEquals("position next" + i, (char)a, (char)b);
            Assert.assertEquals("position current " + i, (char)ac, (char)bc);
        }
    }
    
    @Test
	public void record() throws Exception {
    	String json = "123456789abcdefghijklmnoprstuvz";
        log.info("test : " + json);
        StringSource string = StringSource.instance(json);
        ReaderSource reader = ReaderSource.instance(new StringReader(json));
        int recordindex = -1;
        int recordlen = 3;
        int stringstart = -1;
        int stringend = -1;
        int readerstart = -1;
        int readerend = -1;
        
        for(int i = 0; i < json.length(); i++) {
            int a = string.next();
            int b = reader.next();
            recordindex++;
            if(recordindex == 0) {
                stringstart = string.startRecording();
                readerstart = reader.startRecording();
            } else if(recordindex == recordlen) {
                stringend = string.stopRecording();
                readerend = reader.stopRecording();
                recordindex = -1;
                String s1 = string.recordedContent();
                String s2 = reader.recordedContent();
                log.info(" string from "+stringstart+" to "+stringend+" - " +s1);
                log.info(" reader from "+readerstart+" to "+readerend+" - " +s2);
                String ss1 = s1.substring(stringstart, stringend);
                String ss2 = s2.substring(readerstart, readerend);
                log.info(" string - " +ss1);
                log.info(" reader - " +ss2);
                log.info(" -------------------------------");
                Assert.assertEquals("recorded " + i, ss1, ss2);
            }
            
        }
    }
}
