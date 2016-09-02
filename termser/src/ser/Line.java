package ser;

import org.java_websocket.WebSocket;

import java.util.HashMap;

/**
 * Created by tom on 16-8-15.
 */
public class Line {
    public static HashMap<String, Line> mid = new HashMap<>();
    public static HashMap<WebSocket, Line> mapp = new HashMap<>();
    public static HashMap<WebSocket, Line> mterm = new HashMap<>();

    Line(String id, WebSocket term) {
        this.id = id;
        this.term = term;
    }

    String    id = null;
    WebSocket app = null;
    WebSocket term = null;
}
