package lzl;

import org.java_websocket.WebSocket;
import org.json.JSONArray;

import org.json.JSONException;
import ser.G;

/**
 * Created by tom on 16-8-16.
 */
public class MsgHandler {
    // 消息回显，app发送什么就返回什么
    void echo(WebSocket ws, JSONArray data) {
        ws.send(data.toString());
    }
    // 获取温湿度
    void gethumiture(WebSocket ws, JSONArray data) {
        String wsd = Main.sensor.get_humiture();
        String[] sa = wsd.split(",");
//        System.out.println(wsd);
        ws.send(G.msg("humiture", sa[0], sa[1]));
    }
    //
    void getspeed(WebSocket ws, JSONArray data) {
        ws.send(G.msg("speed", Main.sensor.getSpeed()));
    }
    void getlight(WebSocket ws, JSONArray data) {
        ws.send(G.msg("light", Main.sensor.getLight()));
    }
    void setspeed(WebSocket ws, JSONArray data) {
        try {
            Main.sensor.setSpeed(data.getInt(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    void setlight(WebSocket ws, JSONArray data) {
        try {
            Main.sensor.setLight(data.getString(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // 未连线
    void UNLINK(WebSocket ws, JSONArray data) {
    }
    // 连线出错，用户名或密码不对
    void LINKERR(WebSocket ws, JSONArray data) {
    }
}
