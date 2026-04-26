package service;

import model.Kendaraan;
import model.Peminjaman;
import util.DateUtil;

import java.time.LocalDate;

public class DendaCalculator {

    // Denda = tarif per hari kendaraan × jumlah hari terlambat
    public static double hitung(Peminjaman peminjaman, Kendaraan kendaraan, LocalDate tanggalKembaliAktual) {
        long selisih = DateUtil.hitungSelisihHari(peminjaman.getTanggalKembaliRencana(), tanggalKembaliAktual);
        if (selisih <= 0) return 0;
        return selisih * kendaraan.getTarifPerHari();
    }

    public static double hitungTotalBiaya(Peminjaman peminjaman, Kendaraan kendaraan, LocalDate tanggalKembaliAktual) {
        long hariPinjam = DateUtil.hitungSelisihHari(peminjaman.getTanggalPinjam(), tanggalKembaliAktual);
        if (hariPinjam <= 0) hariPinjam = 1;
        double biayaSewa = hariPinjam * kendaraan.getTarifPerHari();
        double denda     = hitung(peminjaman, kendaraan, tanggalKembaliAktual);
        return biayaSewa + denda;
    }
}