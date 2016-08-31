import lzl.Sensor;

import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        System.load("/home/pi/sensor.so");
        Sensor sensor = new Sensor();
        Scanner scan = new Scanner(System.in);
        while (true) {
            String line = scan.nextLine();
            String[] arr = line.split("\\s+");
            switch (arr[0]) {
            case "light":
                sensor.set_light(arr[1]);
                break;
            case "speed":
                sensor.set_speed(Integer.parseInt(arr[1]));
                break;
            case "bright":
                System.out.println(sensor.get_brightness());
                break;
            case "humiture":
                System.out.println(sensor.get_humiture());
                break;
            default:
                System.out.println("<Invalid command>");
                break;
            }
        }
    }
}
