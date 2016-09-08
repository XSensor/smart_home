package G;

import org.json.JSONArray;

public class G {
    public static String LINK = "LINK";
    public static String UNLINK = "UNLINK";
    public static String LINKERR = "LINKERR";
    public static String LINKSUC = "LINKSUC";
    public static String TERMOFF = "TERMOFF";
    public static void log(String format, Object... args) {
        System.out.println(String.format(format, args));
    }

    public static JSONArray msgArr(Object... args) {
        JSONArray ja = new JSONArray();
        for (Object obj : args) {
            ja.put(obj);
        }
        return ja;
    }
    public static String msg(Object... args) {
        return msgArr(args).toString();
    }
}
