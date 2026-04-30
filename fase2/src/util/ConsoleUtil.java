package util;

public class ConsoleUtil {
    private ConsoleUtil() {
        throw new AssertionError("Utility class");
    }

    public static void clearScreen() {
        // Coba ANSI escape code dulu
        String ansiClear = "\033[H\033[2J";
        System.out.print(ansiClear);
        System.out.flush();

        // Fallback baris kosong untuk terminal yang tidak support ANSI
        // Keduanya dijalankan — tidak ada ruginya
        for (int i = 0; i < 3; i++) System.out.println();
    }

    public static void pauseScreen() {
        System.out.println("\nTekan Enter untuk melanjutkan...");
        InputHelper.inputString("", true);
    }
}