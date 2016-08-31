package lzl;

public class Sensor {
    public Sensor() {
        this.init();
        set_light(this.color);
        set_speed(this.speed);
    }
    private String color = "r";
    private int speed = 50;
    // 初始化硬件传感器
    public native void init();
    // 获取温湿度
    public native String get_humiture();
    // 获取光照强度
    public native float get_brightness();
    // 设置灯光颜色
    public native void setLight(String color);
    public void set_light(String color) {
        this.color = color;
        setLight(color);
    }
    // 设置电机速度
    public native void setSpeed(int speed);
    public void set_speed(int speed) {
        this.speed = speed;
        setSpeed(speed);
    }
}
