/*
 * 
 */
package sk.antons.json.match;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author antons
 */
public class PathSplitter {
    private static String SLASH = "__SLASH__";
    private static String ASTERIX = "__ASTERIX__";
    private static String QUESTION = "__QUESTION__";
    private static String TILDA = "__TILDA__";
    
    private static String hideExcapes(String value) {
        if(value == null) return null;
        value = value.replace("\\/", SLASH);
        value = value.replace("\\*", ASTERIX);
        value = value.replace("\\?", QUESTION);
        value = value.replace("\\~", TILDA);
        return value;
    }
    private static String showExcapes(String value) {
        if(value == null) return null;
        value = value.replace(SLASH, "\\/");
        value = value.replace(ASTERIX, "\\*");
        value = value.replace(QUESTION, "\\?");
        value = value.replace(TILDA, "\\~");
        return value;
    }

    public static List<String> split(String path) {
        List<String> p = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        path = hideExcapes(path);
        int len = path.length();
        for(int i = 0; i < len; i++) {
            char c = path.charAt(i);
            if(c == '/') {
                if(sb.length()>0) p.add(showExcapes(sb.toString()));
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        if(sb.length()>0) p.add(showExcapes(sb.toString()));
        return p;
    }
}
