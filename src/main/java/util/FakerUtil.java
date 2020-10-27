package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class FakerUtil {
    public static void main(String[] args) {
        FakerUtil.init();
    }

    /**
     * 执行程序入口
     * d 和 F 表示大王， f = 小王， 1 表示 10，其它字母输入小写会自动转化成大写
     * 输入 r 重新洗牌并且重置程序，最多输入54个数值后，程序关闭
     */
    public static void init() {
        System.out.println("==================================================");
        List<String> unitLib = new ArrayList<String>();
        unitLib.add("F");
        unitLib.add("f");
        unitLib.add("2");
        unitLib.add("A");
        unitLib.add("K");
        unitLib.add("Q");
        unitLib.add("J");
        for (int i = 10; i > 2; i--) {
            unitLib.add(String.valueOf(i));
        }
        System.out.println(unitLib);
        List<String> lib = FakerUtil.createLib();

        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < 55; i++) {
            String input = scanner.nextLine();
            if ("r".equals(input)) {
                i = 0;
                lib = FakerUtil.createLib();
                System.out.println("====================Library reset!=====================");
                continue;
            }
            for (int j = 0; j < input.length(); j++) {
                if ("F".equals(input.substring(j, j + 1)) || "f".equals(input.substring(j, j + 1))) {
                    lib.remove(input.substring(j, j + 1));
                } else if ("d".equals(input.substring(j, j + 1))) {
                    lib.remove("F");
                } else if ("1".equals(input.substring(j, j + 1))) {
                    lib.remove("10");
                } else {
                    lib.remove(input.substring(j, j + 1).toUpperCase());
                }
            }
            System.out.println(input);
            System.out.println("==================================================");
            System.out.println(lib);
            StringBuffer outline = new StringBuffer("Outline: ");
            StringBuffer BOOM = new StringBuffer("BOOM: ");

            if (lib.contains("F") && lib.contains("f")) {
                BOOM.append("F,");
            }
            for (String e : unitLib) {
                int amount = Collections.frequency(lib, e);
                if (amount > 0) {
                    outline.append(e + " *" + amount + ", ");
                }
                if (amount == 4) {
                    BOOM.append(e + ", ");
                }
            }

            System.out.println(outline.toString());
            System.out.println(BOOM.toString());
            System.out.println("==================================================");
        }
        scanner.close();
        System.out.println("====================End of execution!=====================");
    }

    private static List<String> createLib() {
        List<String> lib = new ArrayList<String>();
        lib.add("F");
        lib.add("f");
        for (int i = 0; i < 4; i++) {
            lib.add("K");
        }
        for (int i = 0; i < 4; i++) {
            lib.add("Q");
        }
        for (int i = 0; i < 4; i++) {
            lib.add("J");
        }
        for (int i = 0; i < 4; i++) {
            lib.add("2");
        }
        for (int i = 0; i < 4; i++) {
            lib.add("A");
        }
        for (int i = 10; i > 2; i--) {
            for (int j = 0; j < 4; j++) {
                lib.add(String.valueOf(i));//添加 3 到 10 的牌，每个牌有4张
            }
        }
        System.out.println(lib);
        System.out.println("======================Create library success!========================");
        return lib;
    }

}