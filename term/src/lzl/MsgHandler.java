package lzl;

import org.java_websocket.WebSocket;
import org.json.JSONArray;

import org.json.JSONException;

import java.nio.ByteBuffer;
import java.util.HashMap;
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
 * 4. 关闭 { 灯光 | 风扇 }
 * 5. 红色 | 蓝色 | 绿色 | 白色 | 紫色 | 关灯
 * 6. 加速 | 减速
 * 4. 报告所有的数据
 */
class TextBuffer {
    private String text;
    private int i = 0;
    TextBuffer(String t) {
        text = t;
    }
    // 接下来的n个字符
    public String next(int n) {
        try {
            String str = text.substring(i, i + n);
            i += n;
            return str;
        } catch (Exception e) {
            return null;
        }
    }
    // 接下来的一个10进制数字
    public String nextDec() {
        String ret = "";
        while (true) {
            if (i >= text.length()) break;
            char c = text.charAt(i++);
            if (c >= '0' && c <= '9')
                ret += c;
            else
                break;
        }
        return ret.isEmpty() ? null : ret;
    }
}
class Text2Json {
    private String err = null;
    private String tmsg = null;

    private static HashMap<String, String> colors = new HashMap<String, String>();
    static {
        String[] ct = { // color table
            "红", "r", "绿", "g", "蓝", "b", "白", "rgb", "紫", "gb"
        };
        for (int i = 0; i < ct.length; )
            colors.put(ct[i++], ct[i++]);
    }

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
                err = "你啥也不说是啥意思？";
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
                    String rgb = colors.get(tb.next(1));
                    if (rgb == null) {
                        err = "不懂你说的啥颜色~~";
                        return null;
                    }
                    return G.msgArr("setlight", rgb);
                }
                // 获取灯光颜色
                if (nx.equals("颜色"))
                    return G.msgArr("getlight");
                err = "不懂你想对灯光做啥？"; return null;
            } else if (prefix.equals("风扇")) {
                String nx = tb.next(2);
                if (nx == null) {
                    err = "你想对风扇做啥？"; return null;
                }
                if (nx.equals("速度")) {
                    nx = tb.next(2);
                    if (nx == null)
                        return G.msgArr("getspeed");
                    if (nx.equals("设为")) {
                        String n = tb.nextDec();
                        if (n == null) {
                            err = "风速要设为多少？"; return null;
                        }
                        return G.msgArr("setspeed", Integer.parseInt(n));
                    }
                    err = "你要对风扇干啥?"; return null;
                }
                if (nx.equals("加速"))
                    return G.msgArr("JiaSu");
                if (nx.equals("减速"))
                    return G.msgArr("JianSu");
                err = "不懂你想对风扇做啥？"; return null;
            } else if (prefix.equals("温湿度")) {
                return G.msgArr("gethumiture");
            } else if (prefix.equals("光照")) {
                return G.msgArr("getbrightness");
            } else if (prefix.equals("关闭")) {
                String obj = tb.next(2);
                if (obj == null) {
                    err = "要关闭什么？"; return null;
                }
                if (obj.equals("灯光"))
                    return G.msgArr("setlight", "");
                if (obj.equals("风扇"))
                    return G.msgArr("setspeed", 0);
                err = "不懂你要关闭什么？"; return null;
            } else if (prefix.equals("关灯")) {
                return G.msgArr("setlight", "");
            } else if (prefix.equals("红色")) {
                return G.msgArr("setlight", "r");
            } else if (prefix.equals("蓝色")) {
                return G.msgArr("setlight", "b");
            } else if (prefix.equals("绿色")) {
                return G.msgArr("setlight", "g");
            } else if (prefix.equals("紫色")) {
                return G.msgArr("setlight", "rb");
            } else if (prefix.equals("白色")) {
                return G.msgArr("setlight", "rgb");
            } else if (prefix.equals("加速")) {
                return G.msgArr("JiaSu");
            } else if (prefix.equals("减速")) {
                return G.msgArr("JianSu");
            }
            err = "不懂你说啥？";
            return null;
        } catch (Exception e) {
            err = "你说啥？";
            return null;
        }
    }
}

/**
 * Created by tom on 16-8-16.
 */
public class MsgHandler {
    static Timer timer = new Timer();
    static MyTask task = null;
    static Text2Json t2j = new Text2Json();

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
    void JiaSu(WebSocket ws, JSONArray data) {
        int speed = Main.sensor.getSpeed() + 20;
        Main.sensor.setSpeed(speed > 100 ? 100 : speed);
    }
    void JianSu(WebSocket ws, JSONArray data) {
        int speed = Main.sensor.getSpeed() - 20;
        Main.sensor.setSpeed(speed < 0 ? 0 : speed);
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
            JSONArray ja = t2j.convert(cmd);
            String err = t2j.error();
            // 文本解析出错
            if (err != null) {
                ws.send(G.msg("texterr", err));
                return;
            }
            Main.client.handle(ja);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String cmd2jsonstr(String cmd) {
            JSONArray ja = t2j.convert(cmd);
            String err = t2j.error();
            // 文本解析出错
            if (err != null) {
                return "<PARSE ERROR>"+err;
            }
            return ja.toString();
    }
    // 连线出错，用户名或密码不对
    void LINKERR(WebSocket ws, JSONArray data) {
        System.out.println(G.LINKERR);
    }
}
