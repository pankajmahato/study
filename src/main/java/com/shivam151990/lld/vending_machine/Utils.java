package com.shivam151990.lld.vending_machine;

public class Utils {
    public static void stop() {
        System.out.println(">>> Press ENTER to continue <<<");
        try {
            new java.util.Scanner(System.in).nextLine();
        } catch (Exception ignored) {
        }
    }
}
