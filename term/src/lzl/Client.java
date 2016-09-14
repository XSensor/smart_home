package lzl;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;

import java.net.URI;
import G.G;

/**
 * Created by tom on 16-8-14.
 */
public class Client extends WebSocketClient {
    public static String USER = "test";
    public static String PASSWORD = "12345";

    private Handler hand;

    Client(URI u) {
        super(u);
    }
    Client(URI u, String user, String pw) {
        super(u); USER = user; PASSWORD = pw;
    }
    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        send(G.msg(G.LINK, USER, PASSWORD));
        hand = new Handler(getConnection(), new MsgHandler());
        System.out.println("linked to cloud.");
    }

    @Override
    public void onMessage(String s) {
        try {
            handle(new JSONArray(s));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onClose(int i, String s, boolean b) {
    }
    @Override
    public void onError(Exception e) {
        G.log(e.toString());
    }

    public void handle(JSONArray data) {
        try {
            String msg = data.getString(0);
            if (!hand.handle(msg, data)) {
                G.log("Handle " + msg + " FAILURE.");
            } else {
                G.log("Handle " + msg + " SUCCESS.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
