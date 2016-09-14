package lzl;

import java.util.Scanner;

/**
 * Created by tom on 16-9-8.
 */
public class Test {
    public static void main(String[] args) {
        MsgHandler mh = new MsgHandler();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.nextLine();
            System.out.println(mh.cmd2jsonstr(line));
        }
    }
}
