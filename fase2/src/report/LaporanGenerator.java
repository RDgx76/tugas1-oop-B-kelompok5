package report;

import model.Kendaraan;
import model.Peminjaman;
import model.StatusPeminjaman;
import model.User;
import service.PeminjamanService;
import service.UserService;
import util.DateUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class LaporanGenerator {
    private final PeminjamanService peminjamanService;
    private final UserService       userService;

    public LaporanGenerator(PeminjamanService peminjamanService, UserService userService) {
        Objects.requireNonNull(peminjamanService, "PeminjamanService tidak boleh null");
        Objects.requireNonNull(userService, "UserService tidak boleh null");
        this.peminjamanService = peminjamanService;
        this.userService       = userService;
    }

    public void tampilkanSemuaPeminjaman() {
        List<Peminjaman> semua = peminjamanService.getDaftarPeminjaman();
        System.out.println("\n========================================");
        System.out.println("       LAPORAN SEMUA PEMINJAMAN         ");
        System.out.println("========================================");
        if (semua.isEmpty()) {
            System.out.println("Belum ada data peminjaman.");
            return;
        }
        for (Peminjaman p : semua) {
            tampilkanDetailPeminjaman(p);
        }
        System.out.println("========================================");
        System.out.println("Total transaksi: " + semua.size());
    }

    public void tampilkanPeminjamanAktif() {
        List<Peminjaman> aktif = peminjamanService.getDaftarPeminjamanAktif();
        System.out.println("\n========================================");
        System.out.println("      LAPORAN PEMINJAMAN AKTIF          ");
        System.out.println("========================================");
        if (aktif.isEmpty()) {
            System.out.println("Tidak ada peminjaman aktif.");
            return;
        }
        for (Peminjaman p : aktif) {
            tampilkanDetailPeminjaman(p);
        }
        System.out.println("========================================");
        System.out.println("Total aktif: " + aktif.size());
    }

    public void tampilkanLaporanPendapatan() {
        List<Peminjaman> semua = peminjamanService.getDaftarPeminjaman();
        long totalDenda   = 0;
        long totalPendapatan = 0;
        int  totalSelesai = 0;

        List<Kendaraan> kendaraan = peminjamanService.getDaftarKendaraan();

        for (Peminjaman p : semua) {
            if (p.getStatus() == StatusPeminjaman.SELESAI) {
                totalSelesai++;
                totalDenda += p.getDenda();

                Kendaraan k = cariKendaraan(kendaraan, p.getIdKendaraan());
                if (k != null && p.getTanggalKembaliAktual().isPresent()) {
                    long hari = DateUtil.hitungSelisihHari(p.getTanggalPinjam(), p.getTanggalKembaliAktual().get());
                    if (hari <= 0) hari = 1;
                    totalPendapatan += (hari * k.getTarifPerHari()) + p.getDenda();
                }
            }
        }

        System.out.println("\n========================================");
        System.out.println("         LAPORAN PENDAPATAN             ");
        System.out.println("========================================");
        System.out.println("  Transaksi selesai : " + totalSelesai);
        System.out.printf("  Total denda       : Rp%,d%n", totalDenda);
        System.out.printf("  Total pendapatan  : Rp%,d%n", totalPendapatan);
        System.out.println("========================================");
    }

    public void eksporLaporan() {
        String tanggalHariIni = DateUtil.format(LocalDate.now())
            .orElseThrow(() -> new IllegalStateException("Gagal format tanggal hari ini"));
        String namaFile = "data/laporan_" + tanggalHariIni + ".txt";
        StringBuilder sb = new StringBuilder();

        sb.append("========================================\n");
        sb.append("       LAPORAN SISTEM PEMINJAMAN        \n");
        sb.append("   Tanggal: ").append(tanggalHariIni).append("\n");
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
        for (User u : userService.getDaftarUser()) {
            sb.append(u.toString()).append("\n");
        }

        try {
            database.FileDatabase.writeFile(namaFile, sb.toString());
            System.out.println("Laporan berhasil diekspor ke: " + namaFile);
        } catch (java.io.UncheckedIOException e) {
            System.out.println("Gagal mengekspor laporan: " + e.getMessage());
        }
    }

    private void tampilkanDetailPeminjaman(Peminjaman p) {
        User u      = userService.cariUserById(p.getIdUser()).orElse(null);
        String nama = (u != null) ? u.getNama() : "Unknown";
        System.out.println("  ID  : " + p.getId() + " | Status: " + p.getStatus().name());
        System.out.println("  User: " + nama + " (" + p.getIdUser() + ")");
        System.out.println("  Kendaraan      : " + p.getIdKendaraan());
        System.out.println("  Tgl Pinjam     : " + DateUtil.format(p.getTanggalPinjam()).orElse("-"));
        System.out.println("  Rencana Kembali: " + DateUtil.format(p.getTanggalKembaliRencana()).orElse("-"));
        System.out.println("  Aktual Kembali : " + DateUtil.format(p.getTanggalKembaliAktual().orElse(null)).orElse("Belum dikembalikan"));
        System.out.printf ("  Denda          : Rp%,d%n", p.getDenda());
        System.out.println("  ----------------------------------------");
    }

    private Kendaraan cariKendaraan(List<Kendaraan> list, String id) {
        return list.stream()
            .filter(k -> k.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
}