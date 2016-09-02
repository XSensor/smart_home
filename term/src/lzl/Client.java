package lzl;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;

import java.net.URI;
import ser.G;

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
    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        send(G.msg(G.LINK, USER, PASSWORD));
        hand = new Handler(getConnection(), new MsgHandler());
    }

    @Override
    public void onMessage(String s) {
        try {
            JSONArray ja = new JSONArray(s);
            if (!hand.handle(ja.getString(0), ja)) {
                G.log("Handle " + s + " FAILURE.");
            }
        } catch (JSONException e) {
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
}
