package lzl;

import org.java_websocket.WebSocket;
import org.json.JSONArray;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by tom on 16-8-16.
 */
public class Handler {
    Handler(WebSocket ws, Object cb) {
        this.cb = cb;
        this.ws = ws;
    }

    boolean handle(String cmd, JSONArray data) {
        try {
            Method me = cb.getClass().getDeclaredMethod(cmd, WebSocket.class, JSONArray.class);
            if (me == null) return false;
            me.invoke(cb, ws, data);
        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
            return false;
        } catch (InvocationTargetException e) {
//            e.printStackTrace();
            return false;
        } catch (IllegalAccessException e) {
//            e.printStackTrace();
            return false;
        }
        return true;
    }

    private Object cb;
    private WebSocket ws;
}
