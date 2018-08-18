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

/**
 * Json source provider created from string.
 * @author antons
 */
public class StringSource implements JsonSource {
    private String content;
    private int length = -1;
    private int offset = 0;

    public StringSource(String content) {
        if(content == null) content = "";//throw new IllegalArgumentException("Unable to parse null Json string");
        this.content = content;
        this.length = content.length();
    }

    public static StringSource instance(String content) {
        return new StringSource(content);
    }

    @Override
    public void move() {
        offset++;
    }


    @Override
    public int current() {
        if(offset < length) return content.charAt(offset);
        return -1;
    }

    
    @Override
    public int next() {
        if(offset < length) return content.charAt(offset++);
        return -1;
    }

    @Override
    public int startRecording() {
        return offset;
    }

    @Override
    public int stopRecording() {
        return offset;
    }

    @Override
    public String recordedContent() {
        return content;
    }

    
}
