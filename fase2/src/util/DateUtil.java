package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

public class DateUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DateUtil() {
        throw new AssertionError("DateUtil adalah utility class dan tidak boleh diinstansiasi");
    }

    /**
     * Mengonversi objek {@link LocalDate} menjadi string teks sesuai dengan format yang ditentukan.
     * 
     * @param date objek tanggal yang akan diformat, boleh null.
     * @return {@link Optional} berisi string tanggal yang telah diformat, 
     *         atau {@link Optional#empty()} jika input null.
     */
    public static Optional<String> format(LocalDate date) {
        if (date == null) return Optional.empty();
        return Optional.of(date.format(FORMATTER));
    }

    /**
     * Mengonversi string teks menjadi objek {@link LocalDate} menggunakan format yang telah ditentukan.
     * 
     * @param dateStr teks tanggal yang akan diparse
     * @return {@link Optional} berisi {@link LocalDate} jika berhasil, atau {@link Optional#empty()} 
     *         jika input null atau teks "null".
     * @throws IllegalArgumentException jika teks tidak dapat diparse menjadi tanggal yang valid.
     */
    public static Optional<LocalDate> parse(String dateStr) {
        if (dateStr == null || dateStr.equals("null")) return Optional.empty();
        try {
            return Optional.of(LocalDate.parse(dateStr, FORMATTER));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format tanggal tidak valid: '" + dateStr + "'. Format yang diharapkan: yyyy-MM-dd", e);
        }
    }

    /**
     * Menghitung selisih hari antara dua tanggal.
     * Mengembalikan nilai negatif jika {@code ke} terjadi sebelum {@code dari}.
     * 
     * @param dari tanggal awal, tidak boleh null.
     * @param ke tanggal akhir, tidak boleh null.
     * @return jumlah hari antara dua tanggal (positif jika 'ke' di masa depan, 
     *         negatif jika 'ke' di masa lalu).
     * @throws NullPointerException jika salah satu parameter bernilai null.
     */
    public static long hitungSelisihHari(LocalDate dari, LocalDate ke) {
        Objects.requireNonNull(dari, "Parameter 'dari' tidak boleh null");
        Objects.requireNonNull(ke, "Parameter 'ke' tidak boleh null");
        return ChronoUnit.DAYS.between(dari, ke);
    }
}