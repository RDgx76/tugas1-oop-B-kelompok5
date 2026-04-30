package util;

import java.util.Scanner;

public class InputHelper {
    private static final Scanner scanner = new Scanner(System.in);

    private InputHelper() {
        throw new AssertionError("InputHelper adalah utility class dan tidak boleh diinstansiasi");
    }

    public static String inputString(String prompt, boolean allowEmpty) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!allowEmpty && value.isEmpty()) {
                System.out.println("  Input tidak boleh kosong.");
                continue;
            }
            return value;
        }
    }

    public static String inputString(String prompt) {
        return inputString(prompt, false);
    }

    public static int inputInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value < min || value > max) {
                    System.out.printf("  Input harus antara %d dan %d.%n", min, max);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("  Input tidak valid. Masukkan angka bulat.");
            }
        }
    }

    public static int inputInt(String prompt) {
        return inputInt(prompt, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public static double inputDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value < min || value > max) {
                    System.out.printf("  Input harus antara %.2f dan %.2f.%n", min, max);
                continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("  Input tidak valid. Masukkan angka.");
            }
        }
    }

    public static double inputDouble(String prompt) {
        return inputDouble(prompt, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    public static long inputLong(String prompt, long min, long max) {
        while (true) {
            System.out.print(prompt);
            try {
                long value = Long.parseLong(scanner.nextLine().trim());
                if (value < min || value > max) {
                    System.out.printf("  Input harus antara %d dan %d.%n", min, max);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("  Input tidak valid. Masukkan angka bulat.");
            }
        }
    }

    public static long inputLong(String prompt) {
        return inputLong(prompt, Long.MIN_VALUE, Long.MAX_VALUE);
    }
}