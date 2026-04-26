package service;

import database.DataLoader;
import model.Kendaraan;
import model.Peminjaman;
import model.User;
import util.DateUtil;
import util.InputHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PeminjamanService {
    private List<Peminjaman> daftarPeminjaman;
    private List<Kendaraan>  daftarKendaraan;
    private UserService      userService;

    public PeminjamanService(List<Peminjaman> daftarPeminjaman,
                             List<Kendaraan> daftarKendaraan,
                             UserService userService) {
        this.daftarPeminjaman = daftarPeminjaman;
        this.daftarKendaraan  = daftarKendaraan;
        this.userService      = userService;
    }

    public void pinjamKendaraan() {
        System.out.println("\n========== PINJAM KENDARAAN ==========");

        // 1. Pilih user
        String idUser = InputHelper.inputString("Masukkan ID User: ");
        User user = userService.cariById(idUser);
        if (user == null) {
            System.out.println("User tidak ditemukan.");
            return;
        }
        System.out.println("User   : " + user.getNama());

        // 2. Tampilkan kendaraan tersedia
        List<Kendaraan> tersedia = getKendaraanTersedia();
        if (tersedia.isEmpty()) {
            System.out.println("Tidak ada kendaraan yang tersedia saat ini.");
            return;
        }
        System.out.println("\nKendaraan Tersedia:");
        for (Kendaraan k : tersedia) {
            System.out.println("  " + k);
        }

        // 3. Pilih kendaraan
        String idKendaraan = InputHelper.inputString("\nMasukkan ID Kendaraan: ");
        Kendaraan kendaraan = cariKendaraanById(idKendaraan);
        if (kendaraan == null || !kendaraan.isTersedia()) {
            System.out.println("Kendaraan tidak tersedia.");
            return;
        }

        // 4. Durasi pinjam
        int durasi = InputHelper.inputInt("Durasi pinjam (hari)  : ");
        if (durasi <= 0) {
            System.out.println("Durasi tidak valid.");
            return;
        }

        // 5. Buat peminjaman
        LocalDate tPinjam  = LocalDate.now();
        LocalDate tRencana = tPinjam.plusDays(durasi);
        String idPeminjaman = generateIdPeminjaman();

        Peminjaman p = new Peminjaman(idPeminjaman, idUser, idKendaraan, tPinjam, tRencana);
        kendaraan.setStatus("DIPINJAM");

        daftarPeminjaman.add(p);
        DataLoader.savePeminjaman(daftarPeminjaman);
        DataLoader.saveKendaraan(daftarKendaraan);

        System.out.println("\n✓ Peminjaman berhasil!");
        System.out.println("  ID Peminjaman    : " + idPeminjaman);
        System.out.println("  Kendaraan        : " + kendaraan.getNama());
        System.out.println("  Tanggal Pinjam   : " + DateUtil.format(tPinjam));
        System.out.println("  Rencana Kembali  : " + DateUtil.format(tRencana));
        System.out.printf ("  Estimasi Biaya   : Rp%.0f%n", (double)(durasi) * kendaraan.getTarifPerHari());
    }

    public void kembalikanKendaraan() {
        System.out.println("\n========== KEMBALIKAN KENDARAAN ==========");

        // Tampilkan peminjaman aktif
        List<Peminjaman> aktif = getPeminjamanAktif();
        if (aktif.isEmpty()) {
            System.out.println("Tidak ada peminjaman aktif saat ini.");
            return;
        }
        System.out.println("Peminjaman Aktif:");
        for (Peminjaman p : aktif) {
            System.out.println("  " + p);
        }

        String idPeminjaman = InputHelper.inputString("\nMasukkan ID Peminjaman: ");
        Peminjaman peminjaman = cariPeminjamanById(idPeminjaman);

        if (peminjaman == null || !peminjaman.isAktif()) {
            System.out.println("Peminjaman tidak ditemukan atau sudah selesai.");
            return;
        }

        Kendaraan kendaraan = cariKendaraanById(peminjaman.getIdKendaraan());
        if (kendaraan == null) {
            System.out.println("Data kendaraan tidak ditemukan.");
            return;
        }

        LocalDate tAktual = LocalDate.now();
        double denda      = DendaCalculator.hitung(peminjaman, kendaraan, tAktual);
        double totalBiaya = DendaCalculator.hitungTotalBiaya(peminjaman, kendaraan, tAktual);

        peminjaman.setTanggalKembaliAktual(tAktual);
        peminjaman.setDenda(denda);
        peminjaman.setStatus("SELESAI");
        kendaraan.setStatus("TERSEDIA");

        DataLoader.savePeminjaman(daftarPeminjaman);
        DataLoader.saveKendaraan(daftarKendaraan);

        System.out.println("\n✓ Pengembalian berhasil!");
        System.out.println("  Kendaraan        : " + kendaraan.getNama());
        System.out.println("  Tanggal Kembali  : " + DateUtil.format(tAktual));
        System.out.println("  Rencana Kembali  : " + DateUtil.format(peminjaman.getTanggalKembaliRencana()));
        if (denda > 0) {
            System.out.printf("  Denda Keterlambatan: Rp%.0f%n", denda);
        } else {
            System.out.println("  Tidak ada denda.");
        }
        System.out.printf("  Total Biaya      : Rp%.0f%n", totalBiaya);
    }

    public void tambahKendaraan() {
        System.out.println("\n--- Tambah Kendaraan ---");
        System.out.println("Jenis: 1. Mobil  2. Motor");
        int pilihan = InputHelper.inputInt("Pilih jenis: ");

        String id    = generateIdKendaraan();
        String nama  = InputHelper.inputString("Nama kendaraan : ");
        String plat  = InputHelper.inputString("Plat nomor     : ");
        double tarif = InputHelper.inputDouble("Tarif per hari : Rp");

        Kendaraan k;
        if (pilihan == 1) {
            int kapasitas    = InputHelper.inputInt("Kapasitas penumpang: ");
            String transmisi = InputHelper.inputString("Transmisi (MANUAL/OTOMATIS): ").toUpperCase();
            k = new model.Mobil(id, nama, plat, tarif, kapasitas, transmisi);
        } else {
            int cc           = InputHelper.inputInt("CC mesin: ");
            String jenisMotor= InputHelper.inputString("Jenis motor (MATIC/MANUAL/SPORT): ").toUpperCase();
            k = new model.Motor(id, nama, plat, tarif, cc, jenisMotor);
        }

        daftarKendaraan.add(k);
        DataLoader.saveKendaraan(daftarKendaraan);
        System.out.println("Kendaraan berhasil ditambahkan dengan ID: " + id);
    }

    public void tampilkanSemuaKendaraan() {
        System.out.println("\n========== DAFTAR KENDARAAN ==========");
        if (daftarKendaraan.isEmpty()) {
            System.out.println("Belum ada kendaraan terdaftar.");
            return;
        }
        for (Kendaraan k : daftarKendaraan) {
            System.out.println("  " + k);
        }
    }

    public void hapusKendaraan() {
        tampilkanSemuaKendaraan();
        if (daftarKendaraan.isEmpty()) return;

        String id = InputHelper.inputString("\nMasukkan ID kendaraan yang akan dihapus: ");
        Kendaraan target = cariKendaraanById(id);

        if (target == null) {
            System.out.println("Kendaraan tidak ditemukan.");
            return;
        }
        if (!target.isTersedia()) {
            System.out.println("Kendaraan sedang dipinjam, tidak bisa dihapus.");
            return;
        }

        daftarKendaraan.remove(target);
        DataLoader.saveKendaraan(daftarKendaraan);
        System.out.println("Kendaraan " + target.getNama() + " berhasil dihapus.");
    }

    public List<Kendaraan> getKendaraanTersedia() {
        List<Kendaraan> list = new ArrayList<>();
        for (Kendaraan k : daftarKendaraan) {
            if (k.isTersedia()) list.add(k);
        }
        return list;
    }

    public List<Peminjaman> getPeminjamanAktif() {
        List<Peminjaman> list = new ArrayList<>();
        for (Peminjaman p : daftarPeminjaman) {
            if (p.isAktif()) list.add(p);
        }
        return list;
    }

    public List<Peminjaman> getDaftarPeminjaman() {
        return daftarPeminjaman;
    }

    public List<Kendaraan> getDaftarKendaraan() {
        return daftarKendaraan;
    }

    private Kendaraan cariKendaraanById(String id) {
        for (Kendaraan k : daftarKendaraan) {
            if (k.getId().equalsIgnoreCase(id)) return k;
        }
        return null;
    }

    private Peminjaman cariPeminjamanById(String id) {
        for (Peminjaman p : daftarPeminjaman) {
            if (p.getId().equalsIgnoreCase(id)) return p;
        }
        return null;
    }

    private String generateIdPeminjaman() {
        int max = 0;
        for (Peminjaman p : daftarPeminjaman) {
            try {
                int num = Integer.parseInt(p.getId().replace("P", ""));
                if (num > max) max = num;
            } catch (NumberFormatException ignored) {}
        }
        return String.format("P%03d", max + 1);
    }

    private String generateIdKendaraan() {
        int max = 0;
        for (Kendaraan k : daftarKendaraan) {
            try {
                int num = Integer.parseInt(k.getId().replace("K", ""));
                if (num > max) max = num;
            } catch (NumberFormatException ignored) {}
        }
        return String.format("K%03d", max + 1);
    }
}