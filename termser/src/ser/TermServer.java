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
public class TermServer extends WebSocketServer {
    TermServer(InetSocketAddress addr) {
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
                Line line = new Line(user, ws);
                Line.mid.put(user, line);
                Line.mterm.put(ws, line);
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
        G.log("New term: " + webSocket.getRemoteSocketAddress());
    }
    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        Line line = Line.mterm.get(webSocket);
        if (line != null) {
            line.term = null;
            // remove this line from map of id
            Line.mid.put(line.id, null);
            Line.mapp.put(line.app, null);
        }
        Line.mterm.put(webSocket, null);
    }
    @Override
    public void onMessage(WebSocket webSocket, String s) {
        // find corresponding app's websocket
        Line line = Line.mterm.get(webSocket);
        WebSocket app = line == null ? null : line.app;
        if (app != null)            // line is linked
            app.send(s);
        else
            verify(webSocket, s);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        G.log("Error of term: " + e);
    }
}

