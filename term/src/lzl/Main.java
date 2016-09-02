package lzl;

import java.net.URI;
import java.net.URISyntaxException;

public class Main {
    static Sensor sensor;

    public static void main(String[] args) {
        sensor = new Sensor();
        try {
            Client client = new Client(new URI("ws://codesoul.top:8008"));
            client.run();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
