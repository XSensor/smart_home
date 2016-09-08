package lzl;

import org.java_websocket.WebSocket;
import org.json.JSONArray;

import org.json.JSONException;

import java.util.Timer;
import java.util.TimerTask;

import G.G;

class MyTask extends TimerTask {
    private WebSocket ws;
    MyTask(WebSocket ws) {
        this.ws = ws;
    }
    @Override
    public void run() {
        Sensor s = Main.sensor;
        String color = s.getLight();
        String[] humiture = s.get_humiture().split(",");
        Integer speed = s.getSpeed();
        Float bright = s.get_brightness();

        ws.send(G.msg("alldata", color, humiture[0], humiture[1], speed, bright));
    }
}

// 转换自然语言文本为json数据对象
/* 支持列表
 * 1. 灯光 {颜色 | 设为{红|绿|蓝}色}
 * 2. 风扇 {速度 [设为INT] | {加速|减速}[INT]}
 * 3. 温度 | 湿度 | 温湿度 | 光照
 * 4. 报告所有的数据
 */
class TextBuffer {
    private String text;
    private int i = 0;
    TextBuffer(String t) {
        text = t;
    }

    public String next(int n) {
        try {
            String str = text.substring(i, i + n);
            i += n;
            return str;
        } catch (Exception e) {
            return null;
        }
    }
}
class Text2Json {
    private String err = null;
    private String tmsg = null;

    public String error() {
        return err;
    }
    public String textmsg() {
        return tmsg;
    }
    public JSONArray convert(String cmd) {
        err = null;
        tmsg = null;
        TextBuffer tb = new TextBuffer(cmd);
        String prefix = tb.next(2);
        try {
            if (prefix == null) {
                err = "啥也不说什么意思？";
                return null;
            } else if (prefix.equals("灯光")) {
                String nx = tb.next(2);
                if (nx == null) {
                    // 不知道要对灯光干嘛
                    err = "你想对灯光做啥？";
                    return null;
                }
                if (nx.equals("设为")) {
                    // 设置颜色
                    String rgb = Color2RGB(tb.next(1));
                    if (rgb == null) {
                        err = "不懂你说的啥颜色~~";
                        return null;
                    }
                    return G.msgArr("setlight", rgb);
                }
                if (nx.equals("颜色")) {
                    // 获取灯光颜色
                    return G.msgArr("getlight");
                }
            } else if (prefix.equals("风扇")) {
                String nx = tb.next(2);
                if (nx.equals("速度")) {
                    nx = tb.next(2);
                    if (nx == null)
                        return G.msgArr("getspeed");
                    if (nx.equals("设为")) {
                        return null;
                    }
                    err = "你要对风扇干啥?"; return null;
                }
                if (nx.equals("加速")) {
                    return null;
                }
                if (nx.equals("减速")) {
                    return null;
                }
            } else if (prefix.equals("温湿度")) {
                return G.msgArr("gethumiture");
            } else if (prefix.equals("光照")) {
                return G.msgArr("getbrightness");
            }
            err = "不懂你说啥？";
            return null;
        } catch (Exception e) {
            err = "你说啥？";
            return null;
        }
    }
    // 把汉字颜色转换为rgb组合
    private String Color2RGB(String color) {
        if (color == null) return null;
        //
        return null;
    }
}

/**
 * Created by tom on 16-8-16.
 */
public class MsgHandler {
    static Timer timer = new Timer();
    static MyTask task = null;

    // 自动向app发送传感器数据
    void openauto(WebSocket ws, JSONArray data) {
        if (task == null) {
            task = new MyTask(ws);
        }
        timer.schedule(task, 0, 1500);
    }
    // 关闭自动发送
    void closeauto(WebSocket ws, JSONArray data) {
        timer.cancel();
    }
    // 消息回显，app发送什么就返回什么
    void echo(WebSocket ws, JSONArray data) {
        ws.send(data.toString());
    }
    // 获取温湿度
    void gethumiture(WebSocket ws, JSONArray data) {
        String wsd = Main.sensor.get_humiture();
        String[] sa = wsd.split(",");
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

    void textcmd(WebSocket ws, JSONArray data) {
        try {
            String cmd = data.getString(1);
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
