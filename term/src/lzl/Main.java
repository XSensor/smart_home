package lzl;

import java.net.URI;
import java.net.URISyntaxException;

public class Main {
    static Sensor sensor;
    static Client client;

    static String serAddr = "codesoul.top";

    static String uri = "ws://" + serAddr + ":8008";

    public static void main(String[] args) {
        // Configures
        for (int i = 0; i < args.length; i++) {
            switch (i) {
                case 0:
                    uri = "ws://" + args[i] + ":8008";
                    break;
            }
        }

        sensor = new Sensor();
        try {
            client = new Client(new URI(uri));
            client.run();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
