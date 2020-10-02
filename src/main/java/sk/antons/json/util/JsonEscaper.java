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

import java.math.BigDecimal;

/**
 * Json string escaper for string literals.
 * @author antons
 */
public class JsonEscaper {
    private static char[] hex = new char[]{'0', '1', '2', '3', '4', '5', '6', '7'
                                         , '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static int fromHex(char c) {
        if((c >= '0') && (c <= '9')) return c - '0';
        if((c >= 'A') && (c <= 'F')) return c - 'A' + 10;
        if((c >= 'a') && (c <= 'f')) return c - 'a' + 10;
        throw new IllegalArgumentException("Can't convert char '" + c + "' to hex number");
    }

    private static char tohex(int c) {
         return hex[c & 0xf];
    }
    
    /**
     * Escapes character
     * @param c input character
     * @return escaped sequence for provided input
     */
    public static String escapeChar(int c) {
        char[] arr = new char[6];
        arr[0] = '\\';
        arr[1] = 'u';
        arr[5] = tohex(c);
        c = c >> 4;
        arr[4] = tohex(c);
        c = c >> 4;
        arr[3] = tohex(c);
        c = c >> 4;
        arr[2] = tohex(c);
        return new String(arr);
    }

    /**
     * Unescape escaped string into character.
     * @param value escapes sequence
     * @return char defined by input.
     */
    public static char unescapeChar(String value) {
        return unescapeChar(value, 0);
    }
    
    /**
     * Unescape escaped string into character.
     * @param value string
     * @param pos position of escape sequence in string
     * @return char defined by input.
     */
    public static char unescapeChar(String value, int pos) {
        if(value == null) throw new IllegalArgumentException("Can't unescape string '" + value + "' to char. {Should be \\uHHHH}");
        if(pos + 6 > value.length()) throw new IllegalArgumentException("Can't unescape string '" + value + "' to char. {Should be \\uHHHH} ");
        if(value.charAt(pos) != '\\') throw new IllegalArgumentException("Can't unescape string '" + value + "' to char. {Should be \\uHHHH} ");
        if(value.charAt(pos+1) != 'u') throw new IllegalArgumentException("Can't unescape string '" + value + "' to char. {Should be \\uHHHH} ");
        int rv = 0;
        int x = fromHex(value.charAt(pos+2));
        rv = rv | (x & 0xF);
        rv = rv << 4 ;
        x = fromHex(value.charAt(pos+3));
        rv = rv | (x & 0xF);
        rv = rv << 4 ;
        x = fromHex(value.charAt(pos+4));
        rv = rv | (x & 0xF);
        rv = rv << 4 ;
        x = fromHex(value.charAt(pos+5));
        rv = rv | (x & 0xF);

        return (char)rv;
    }
    
    /**
     * Escape input string.
     * @param value input string
     * @return escaped string.
     */
    public static String escape(String value) {
        return escape(value, false);
    }
    
    /**
     * Escape input string.
     * @param value input string
     * @param escapeNonAscii escape non asci charasters
     * @return escaped string
     */
    public static String escape(String value, boolean escapeNonAscii) {
        if(value == null) return null;
        return escape(value, escapeNonAscii, 0, value.length());
    }
    
    /**
     * Escape input string.
     * @param value input string
     * @param escapeNonAscii escape non asci charasters
     * @param offset start position of string to be escaped
     * @param length length of string to be escaped
     * @return escaped string
     */
    public static String escape(String value, boolean escapeNonAscii, int offset, int length) {
        if(value == null) return null;
        StringBuilder sb = new StringBuilder();
        int len = offset + length;
        for(int i = offset; i < len; i++) {
            char c = value.charAt(i);
            if(c == '"') sb.append("\\\"");
            else if(c == '\\') sb.append("\\\\");
            else if(c == '\n') sb.append("\\n");
            else if(c == '\t') sb.append("\\t");
            else if(c == '\r') sb.append("\\r");
            //else if(c == '/') sb.append("\\/");
            else if(c == '\b') sb.append("\\b");
            else if(c == '\f') sb.append("\\f");
            else if(escapeNonAscii && (c > 127)) sb.append(escapeChar(c));
            else sb.append(c);
        }
        return sb.toString();
    }

    /**
     * Unescape string
     * @param value escaped string
     * @return unescaped string
     */
    public static String unescape(String value) {
        if(value == null) return null;
        return unescape(value, 0, value.length());
    }

    /**
     * Unescape string
     * @param value escaped string
     * @param offset start position of string to be unescaped
     * @param length length of string to be unescaped
     * @return unescaped string
     */
    public static String unescape(String value, int offset, int length) {
        if(value == null) return null;
        StringBuilder sb = new StringBuilder();
        int len = offset + length;
        boolean escape = false;
        for(int i = offset; i < len; i++) {
            char c = value.charAt(i);
            if(c == '\\') {
                if(escape) {
                    sb.append(c);
                    escape = false;
                } else escape = true;
            } else if(escape) {
                if(c == 'n') sb.append('\n');
                else if(c == '"') sb.append('"');
                else if(c == 't') sb.append('\t');
                else if(c == 'r') sb.append('\r');
                else if(c == '/') sb.append('/');
                else if(c == 'b') sb.append('\b');
                else if(c == 'f') sb.append('\f');
                else if(c == 'u') {
                    sb.append(unescapeChar(value, i-1));
                    i = i+4;
                } else throw new IllegalArgumentException("Can't unescape char '" + c + "'");
                escape = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    
    }

    public static void main(String[] params) {
        String s1 = "+\u013e\u0161\u010d\u0165\u017e\u00fd\u017e\u00fd\u00e1\u00ed\u00e9\u00e9==\u00b4";
        System.out.println(" --- " + s1);
        System.out.println(" --- " + JsonEscaper.unescape(s1));
        System.out.println(" --- " + JsonEscaper.escape(s1, true));
        System.out.println(" --- " + JsonEscaper.unescape(JsonEscaper.escape(s1, true)));

        BigDecimal bd = new BigDecimal("12312.221e4");
        System.out.println(" >>> " + bd.toString());
        System.out.println(" >>> " + bd.toPlainString());
        System.out.println(" >>> " + bd.toEngineeringString());
    }     
    
}
