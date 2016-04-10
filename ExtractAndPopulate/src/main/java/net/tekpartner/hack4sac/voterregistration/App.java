package net.tekpartner.hack4sac.voterregistration;

import com.google.common.base.CaseFormat;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
//        System.out.println("Hello World!");

        String before = " hello     there   ";
        String after = before.trim().replaceAll(" +", "_").toUpperCase();
        String camelCase = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, after);
        System.out.println(after);
        System.out.println(camelCase);
    }
}
