package report;

import database.DataLoader;
import model.Kendaraan;
import model.Peminjaman;
import model.User;
import service.PeminjamanService;
import service.UserService;
import util.DateUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class LaporanGenerator {
    private PeminjamanService peminjamanService;
    private UserService       userService;

    public LaporanGenerator(PeminjamanService peminjamanService, UserService userService) {
        this.peminjamanService = peminjamanService;
        this.userService       = userService;
    }

    public void laporanSemuaPeminjaman() {
        List<Peminjaman> semua = peminjamanService.getDaftarPeminjaman();
        System.out.println("\n========================================");
        System.out.println("       LAPORAN SEMUA PEMINJAMAN         ");
        System.out.println("========================================");
        if (semua.isEmpty()) {
            System.out.println("Belum ada data peminjaman.");
            return;
        }
        for (Peminjaman p : semua) {
            cetakDetailPeminjaman(p);
        }
        System.out.println("========================================");
        System.out.println("Total transaksi: " + semua.size());
    }

    public void laporanPeminjamanAktif() {
        List<Peminjaman> aktif = peminjamanService.getPeminjamanAktif();
        System.out.println("\n========================================");
        System.out.println("      LAPORAN PEMINJAMAN AKTIF          ");
        System.out.println("========================================");
        if (aktif.isEmpty()) {
            System.out.println("Tidak ada peminjaman aktif.");
            return;
        }
        for (Peminjaman p : aktif) {
            cetakDetailPeminjaman(p);
        }
        System.out.println("========================================");
        System.out.println("Total aktif: " + aktif.size());
    }

    public void laporanPendapatan() {
        List<Peminjaman> semua = peminjamanService.getDaftarPeminjaman();
        double totalDenda   = 0;
        double totalPendapatan = 0;
        int    totalSelesai = 0;

        List<Kendaraan> kendaraan = peminjamanService.getDaftarKendaraan();

        for (Peminjaman p : semua) {
            if (p.getStatus().equals("SELESAI")) {
                totalSelesai++;
                totalDenda += p.getDenda();

                Kendaraan k = cariKendaraan(kendaraan, p.getIdKendaraan());
                if (k != null && p.getTanggalKembaliAktual() != null) {
                    long hari = DateUtil.hitungSelisihHari(p.getTanggalPinjam(), p.getTanggalKembaliAktual());
                    if (hari <= 0) hari = 1;
                    totalPendapatan += hari * k.getTarifPerHari();
                }
                totalPendapatan += p.getDenda();
            }
        }

        System.out.println("\n========================================");
        System.out.println("         LAPORAN PENDAPATAN             ");
        System.out.println("========================================");
        System.out.println("  Transaksi selesai : " + totalSelesai);
        System.out.printf ("  Total denda       : Rp%.0f%n", totalDenda);
        System.out.printf ("  Total pendapatan  : Rp%.0f%n", totalPendapatan);
        System.out.println("========================================");
    }

    public void eksporLaporan() {
        String namaFile = "data/laporan_" + DateUtil.format(java.time.LocalDate.now()) + ".txt";
        StringBuilder sb = new StringBuilder();

        sb.append("========================================\n");
        sb.append("       LAPORAN SISTEM PEMINJAMAN        \n");
        sb.append("   Tanggal: ").append(DateUtil.format(java.time.LocalDate.now())).append("\n");
        sb.append("========================================\n\n");

        List<Peminjaman> semua = peminjamanService.getDaftarPeminjaman();
        sb.append("SEMUA TRANSAKSI (").append(semua.size()).append(")\n");
        sb.append("----------------------------------------\n");
        for (Peminjaman p : semua) {
            sb.append(p.toString()).append("\n");
        }

        sb.append("\n");
        sb.append("DAFTAR KENDARAAN\n");
        sb.append("----------------------------------------\n");
        for (Kendaraan k : peminjamanService.getDaftarKendaraan()) {
            sb.append(k.toString()).append("\n");
        }

        sb.append("\n");
        sb.append("DAFTAR USER\n");
        sb.append("----------------------------------------\n");
        for (User u : userService.getUsers()) {
            sb.append(u.toString()).append("\n");
        }

        try {
            new java.io.File("data").mkdirs();
            FileWriter fw = new FileWriter(namaFile);
            fw.write(sb.toString());
            fw.close();
            System.out.println("Laporan berhasil diekspor ke: " + namaFile);
        } catch (IOException e) {
            System.out.println("Gagal mengekspor laporan: " + e.getMessage());
        }
    }

    private void cetakDetailPeminjaman(Peminjaman p) {
        User u      = userService.cariById(p.getIdUser());
        String nama = (u != null) ? u.getNama() : "Unknown";
        System.out.println("  ID  : " + p.getId() + " | Status: " + p.getStatus());
        System.out.println("  User: " + nama + " (" + p.getIdUser() + ")");
        System.out.println("  Kendaraan      : " + p.getIdKendaraan());
        System.out.println("  Tgl Pinjam     : " + DateUtil.format(p.getTanggalPinjam()));
        System.out.println("  Rencana Kembali: " + DateUtil.format(p.getTanggalKembaliRencana()));
        System.out.println("  Aktual Kembali : " + DateUtil.format(p.getTanggalKembaliAktual()));
        System.out.printf ("  Denda          : Rp%.0f%n", p.getDenda());
        System.out.println("  ----------------------------------------");
    }

    private Kendaraan cariKendaraan(List<Kendaraan> list, String id) {
        for (Kendaraan k : list) {
            if (k.getId().equals(id)) return k;
        }
        return null;
    }
}