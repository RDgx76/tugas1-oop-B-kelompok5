package service;

import model.Kendaraan;
import model.Peminjaman;
import model.StatusPeminjaman;
import util.DateUtil;

import java.time.LocalDate;
import java.util.Objects;

public class DendaCalculator {
    private DendaCalculator() {
        throw new AssertionError("DendaCalculator adalah utility class dan tidak boleh diinstansiasi");
    }

    // Denda = tarif per hari kendaraan × jumlah hari terlambat
    public static long hitung(Peminjaman peminjaman, Kendaraan kendaraan, LocalDate tanggalKembaliAktual) {
        Objects.requireNonNull(peminjaman, "Peminjaman tidak boleh null");
        Objects.requireNonNull(kendaraan, "Kendaraan tidak boleh null");
        Objects.requireNonNull(tanggalKembaliAktual, "Tanggal kembali aktual tidak boleh null");

        if (peminjaman.getStatus() == StatusPeminjaman.SELESAI) {
            throw new IllegalStateException(
                "Tidak dapat menghitung denda — peminjaman id='"
                + peminjaman.getId() + "' sudah selesai"
            );
        }

        long selisih = DateUtil.hitungSelisihHari(peminjaman.getTanggalKembaliRencana(), tanggalKembaliAktual);
        if (selisih <= 0) return 0;
        return selisih * kendaraan.getTarifPerHari();
    }

    public static long hitungTotalBiaya(Peminjaman peminjaman, Kendaraan kendaraan, LocalDate tanggalKembaliAktual) {
        Objects.requireNonNull(peminjaman, "Peminjaman tidak boleh null");
        Objects.requireNonNull(kendaraan, "Kendaraan tidak boleh null");
        Objects.requireNonNull(tanggalKembaliAktual, "Tanggal kembali aktual tidak boleh null");

        long hariPinjam = DateUtil.hitungSelisihHari(peminjaman.getTanggalPinjam(), tanggalKembaliAktual);

        if (hariPinjam <= 0) hariPinjam = 1;
        
        long biayaSewa = hariPinjam * kendaraan.getTarifPerHari();
        long denda     = hitung(peminjaman, kendaraan, tanggalKembaliAktual);
        return biayaSewa + denda;
    }
}