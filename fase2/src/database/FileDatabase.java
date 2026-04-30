package database;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class FileDatabase {

    private FileDatabase() {
        throw new AssertionError("FileDatabase adalah utility class dan tidak boleh diinstansiasi");
    }

    public static String readFile(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) return "[]";
            return Files.readString(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Gagal membaca file: " + path, e);
        }
    }

    public static void writeFile(String path, String content) {
        try {
            File file = new File(path);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
                throw new UncheckedIOException(
                    new IOException("Gagal membuat direktori: " + parentDir.getAbsolutePath())
                );
            }
            try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
                writer.write(content);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Gagal menulis file: " + path, e);
        }
    }

    // JSON parser & builder manual (tanpa library eksternal)

    /**
     * Memecah string JSON array menjadi list string per objek.
     * Input : [{"a":"1"},{"b":"2"}]
     * Output: ["{\"a\":\"1\"}", "{\"b\":\"2\"}"]
     */
    public static List<String> parseJsonArray(String json) {
        java.util.List<String> list = new ArrayList<>();
        json = json.trim();
        if (json.equals("[]") || json.isEmpty()) return list;

        // Hapus kurung siku luar
        json = json.substring(1, json.length() - 1).trim();

        int depth = 0;
        int start = 0;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                if (depth == 0) start = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0) {
                    list.add(json.substring(start, i + 1).trim());
                }
            }
        }
        return list;
    }

    /**
     * Ambil nilai string dari JSON object string.
     * Contoh: getValue("{\"nama\":\"Budi\"}", "nama") → "Budi"
     */
    public static String getValue(String json, String key) {
        String search = "\"" + key + "\"";
        int keyIndex = json.indexOf(search);
        if (keyIndex == -1) return null;

        int colonIndex = json.indexOf(":", keyIndex + search.length());
        if (colonIndex == -1) return null;

        int valueStart = colonIndex + 1;
        while (valueStart < json.length() && json.charAt(valueStart) == ' ') valueStart++;

        if (json.charAt(valueStart) == '"') {
            // Nilai string
            int end = json.indexOf('"', valueStart + 1);
            // Tangani escaped quote
            while (end != -1 && json.charAt(end - 1) == '\\') {
                end = json.indexOf('"', end + 1);
            }
            return json.substring(valueStart + 1, end);
        } else {
            // Nilai non-string (angka, boolean, null)
            int end = valueStart;
            while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') end++;
            return json.substring(valueStart, end).trim();
        }
    }

    /**
     * Build JSON key-value pair string.
     * Contoh: buildEntry("nama", "Budi") → "\"nama\":\"Budi\""
     */
    public static String buildEntry(String key, String value) {
        return "\"" + key + "\":\"" + value + "\"";
    }

    public static String buildEntryLong(String key, long value) {
        return "\"" + key + "\":" + value;
    }

    public static String buildEntryInt(String key, int value) {
        return "\"" + key + "\":" + value;
    }

    /**
     * Bungkus beberapa entry menjadi JSON object.
     * Contoh: buildObject("\"a\":\"1\"", "\"b\":\"2\"") → {"a":"1","b":"2"}
     */
    public static String buildObject(String... entries) {
        return "{" + String.join(",", entries) + "}";
    }

    /**
     * Bungkus list object menjadi JSON array.
     */
    public static String buildArray(java.util.List<String> objects) {
        return "[" + String.join(",", objects) + "]";
    }

    public static String buildObjectFromMap(Map<String, String> map) {
        List<String> entries = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key   = entry.getKey();
            String value = entry.getValue();

            // Deteksi apakah nilai adalah angka — simpan tanpa tanda kutip
            if (isNumeric(value)) {
                entries.add(buildEntryLong(key, Long.parseLong(value)));
            } else {
                entries.add(buildEntry(key, value));
            }
        }
        return buildObject(entries.toArray(new String[0]));
    }

    private static boolean isNumeric(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}