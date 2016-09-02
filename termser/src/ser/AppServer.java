package ser;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONException;

import java.net.InetSocketAddress;
/**
 * Created by tom on 16-8-14.
 */
public class AppServer extends WebSocketServer {
    AppServer(InetSocketAddress addr) {
        super(addr);
    }

    private void verify(WebSocket ws, String s) {
        try {
            JSONArray ja = new JSONArray(s);
            String msg = ja.getString(0);
            if (!msg.equals(G.LINK)) {
                ws.send(G.msg(G.UNLINK));
                return;
            }
            String user = ja.getString(1);
            String passwd = ja.getString(2);
            if (Main.verify(user, passwd)) {
                Line line = Line.mid.get(user);
                WebSocket term = line == null ? null : line.term;
                if (term == null) {
                    // term is offline
                    ws.send(G.msg("TERMOFF"));
                    return;
                }
                // previous app's websocket
                if (line.app != null && Line.mapp.get(line.app) != null) {
                    Line.mapp.put(line.app, null);
                }
                // map this websocket
                Line.mapp.put(ws, line);
                line.app = ws;
            } else {
                // username or password error
                ws.send(G.msg(G.LINKERR));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        G.log("New app: " + webSocket.getRemoteSocketAddress());
    }
    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        Line line = Line.mapp.get(webSocket);
        if (line != null) {
            line.app = null;
        }
    }
    @Override
    public void onMessage(WebSocket webSocket, String s) {
        // find corresponding term's websocket
        Line line = Line.mapp.get(webSocket);
        WebSocket term = line == null ? null : line.term;
        if (term != null)           // line is linked
            term.send(s);
        else
            verify(webSocket, s);
    }
    @Override
    public void onError(WebSocket webSocket, Exception e) {
        G.log("Error of app: " + e);
    }
}
