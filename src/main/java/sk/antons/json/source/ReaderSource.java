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

import java.io.BufferedReader;
import java.io.Reader;

/**
 * Json source created from Reader
 * @author antons
 */
public class ReaderSource implements JsonSource {
    private Reader reader = null;
    int current = -2;
    StringBuilder prev = new StringBuilder();
    StringBuilder sb = null;
    int offset = 0;

    public ReaderSource(Reader reader) {
        if(reader == null) throw new IllegalArgumentException("Unable to parse null Json reader");
        if(!(reader instanceof BufferedReader)) reader = new BufferedReader(reader);
        this.reader = reader;
    }

    public static ReaderSource instance(Reader reader) {
        return new ReaderSource(reader);
    }

    private boolean finito = false;
    private int read() {
        try {
            if(finito) return -1;
            int i = reader.read();
            if((i != -1) && (sb != null)) sb.append((char)i);
            finito = i == -1;
            return i;
        } catch(Exception e) {
            throw new IllegalStateException(e);
        }
    }


    @Override
    public int current() {
        if(current == -2) current = read();
        return current;
    }

    @Override
    public int next() {
        if(current == -2) current = read();
        int rv = current;
        current = read();
        return rv;
    }

    @Override
    public void move() {
        next();
    }

    @Override
    public int startRecording() {
        sb = prev;
        sb.setLength(0);
        if(current > -1) sb.append((char)current);
        return 0;
    }

    @Override
    public int stopRecording() {
        int rv = sb.length() - 1;
        if(current == -1) rv++;
        sb = null;
        return rv;
    }

    @Override
    public String recordedContent() {
        return prev.toString();
    }

}
