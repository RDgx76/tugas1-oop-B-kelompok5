package service;

import database.DataLoader;
import model.JenisMotor;
import model.Kendaraan;
import model.Peminjaman;
import model.Transmisi;
import model.User;
import util.DateUtil;
import util.InputHelper;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class PeminjamanService {
    private final List<Peminjaman> daftarPeminjaman;
    private final List<Kendaraan>  daftarKendaraan;
    private final UserService      userService;

    public PeminjamanService(List<Peminjaman> daftarPeminjaman,
                             List<Kendaraan> daftarKendaraan,
                             UserService userService) {
        Objects.requireNonNull(daftarPeminjaman, "Daftar peminjaman tidak boleh null");
        Objects.requireNonNull(daftarKendaraan, "Daftar kendaraan tidak boleh null");
        Objects.requireNonNull(userService, "UserService tidak boleh null");

        this.daftarPeminjaman = daftarPeminjaman;
        this.daftarKendaraan  = daftarKendaraan;
        this.userService      = userService;
    }

    public void pinjamKendaraan() {
        System.out.println("\n========== PINJAM KENDARAAN ==========");

        // 1. Pilih user
        String idUser = InputHelper.inputString("Masukkan ID User: ");
        User user = userService.cariUserById(idUser).orElse(null);
        if (user == null) {
            System.out.println("User tidak ditemukan.");
            return;
        }
        System.out.println("User   : " + user.getNama());

        // 2. Tampilkan kendaraan tersedia
        List<Kendaraan> tersedia = getDaftarKendaraanTersedia();
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
        Kendaraan kendaraan = cariKendaraanById(idKendaraan).filter(Kendaraan::isTersedia).orElse(null);
        if (kendaraan == null) {
            System.out.println("Kendaraan tidak tersedia.");
            return;
        }

        // 4. Durasi pinjam
        int durasi = InputHelper.inputInt("Durasi pinjam (hari)  : ", 1, 365);

        // 5. Buat peminjaman
        LocalDate tPinjam  = LocalDate.now();
        LocalDate tRencana = tPinjam.plusDays(durasi);
        String idPeminjaman = generateIdPeminjaman();

        Peminjaman p = new Peminjaman(idPeminjaman, idUser, idKendaraan, tPinjam, tRencana);
        kendaraan.pinjam();

        daftarPeminjaman.add(p);
        DataLoader.simpanPeminjaman(daftarPeminjaman);
        DataLoader.simpanKendaraan(daftarKendaraan);

        System.out.println("\nPeminjaman berhasil!");
        System.out.println("  ID Peminjaman    : " + idPeminjaman);
        System.out.println("  Kendaraan        : " + kendaraan.getNama());
        System.out.println("  Tanggal Pinjam   : " + DateUtil.format(tPinjam).orElse("-"));
        System.out.println("  Rencana Kembali  : " + DateUtil.format(tRencana).orElse("-"));
        System.out.printf ("  Estimasi Biaya   : Rp%,d%n", (long)(durasi) * kendaraan.getTarifPerHari());
    }

    public void kembalikanKendaraan() {
        System.out.println("\n========== KEMBALIKAN KENDARAAN ==========");

        // Tampilkan peminjaman aktif
        List<Peminjaman> aktif = getDaftarPeminjamanAktif();
        if (aktif.isEmpty()) {
            System.out.println("Tidak ada peminjaman aktif saat ini.");
            return;
        }
        System.out.println("Peminjaman Aktif:");
        for (Peminjaman p : aktif) {
            System.out.println("  " + p);
        }

        String idPeminjaman = InputHelper.inputString("\nMasukkan ID Peminjaman: ");
        Peminjaman peminjaman = cariPeminjamanById(idPeminjaman).orElse(null);

        if (peminjaman == null || !peminjaman.isAktif()) {
            System.out.println("Peminjaman tidak ditemukan atau sudah selesai.");
            return;
        }

        Kendaraan kendaraan = cariKendaraanById(peminjaman.getIdKendaraan()).orElse(null);
        if (kendaraan == null) {
            System.out.println("Data kendaraan tidak ditemukan.");
            return;
        }

        LocalDate tanggalAktual = LocalDate.now();
        long denda      = DendaCalculator.hitung(peminjaman, kendaraan, tanggalAktual);
        long totalBiaya = DendaCalculator.hitungTotalBiaya(peminjaman, kendaraan, tanggalAktual);

        peminjaman.selesaikan(tanggalAktual, denda);
        kendaraan.kembalikan();

        DataLoader.simpanPeminjaman(daftarPeminjaman);
        DataLoader.simpanKendaraan(daftarKendaraan);

        System.out.println("\nPengembalian berhasil!");
        System.out.println("  Kendaraan        : " + kendaraan.getNama());
        System.out.println("  Tanggal Kembali  : " + DateUtil.format(tanggalAktual).orElse("-"));
        System.out.println("  Rencana Kembali  : " + DateUtil.format(peminjaman.getTanggalKembaliRencana()).orElse("-"));
        if (denda > 0) {
            System.out.printf("  Denda Keterlambatan: Rp%,d%n", denda);
        } else {
            System.out.println("  Tidak ada denda.");
        }
        System.out.printf("  Total Biaya      : Rp%,d%n", totalBiaya);
    }

    public void tambahKendaraan() {
        System.out.println("\n--- Tambah Kendaraan ---");
        System.out.println("Jenis: 1. Mobil  2. Motor");
        int pilihan = InputHelper.inputInt("Pilih jenis: ", 1, 2);

        String id    = generateIdKendaraan();
        String nama  = InputHelper.inputString("Nama kendaraan : ");
        String plat  = InputHelper.inputString("Plat nomor     : ");
        long tarif = InputHelper.inputLong("Tarif per hari : Rp");

        Kendaraan k;
        if (pilihan == 1) {
            int kapasitas    = InputHelper.inputInt("Kapasitas penumpang: ");
            Transmisi transmisi;
            try {
                transmisi = Transmisi.valueOf(
                    InputHelper.inputString("Transmisi (MANUAL/OTOMATIS): ").toUpperCase()
                );
            } catch (IllegalArgumentException e) {
                System.out.println("Transmisi tidak valid. Pilihan: MANUAL, OTOMATIS");
                return;
            }
            k = new model.Mobil(id, nama, plat, tarif, kapasitas, transmisi);
        } else if (pilihan == 2) {
            int cc           = InputHelper.inputInt("CC mesin: ");
            JenisMotor jenisMotor;
            try {
                jenisMotor = JenisMotor.valueOf(
                    InputHelper.inputString("Jenis motor (MATIC/MANUAL/SPORT): ").toUpperCase()
                );
            } catch (IllegalArgumentException e) {
                System.out.println("Jenis motor tidak valid. Pilihan: MATIC, MANUAL, SPORT");
                return;
            }
            k = new model.Motor(id, nama, plat, tarif, cc, jenisMotor);
        } else {
            // Tidak akan tercapai karena inputInt(min=1, max=2)
            // tapi tetap eksplisit untuk kejelasan
            throw new IllegalStateException("Pilihan tidak valid: " + pilihan);
        }

        daftarKendaraan.add(k);
        DataLoader.simpanKendaraan(daftarKendaraan);
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
        Kendaraan target = cariKendaraanById(id).orElse(null);

        if (target == null) {
            System.out.println("Kendaraan tidak ditemukan.");
            return;
        }
        if (!target.isTersedia()) {
            System.out.println("Kendaraan sedang dipinjam, tidak bisa dihapus.");
            return;
        }

        daftarKendaraan.remove(target);
        DataLoader.simpanKendaraan(daftarKendaraan);
        System.out.println("Kendaraan " + target.getNama() + " berhasil dihapus.");
    }

    public List<Kendaraan> getDaftarKendaraanTersedia() {
        return daftarKendaraan.stream()
            .filter(Kendaraan::isTersedia)
            .collect(Collectors.toList());
    }

    public List<Peminjaman> getDaftarPeminjamanAktif() {
        return daftarPeminjaman.stream()
            .filter(Peminjaman::isAktif)
            .collect(Collectors.toList());
    }

    public List<Peminjaman> getDaftarPeminjaman() {
        return Collections.unmodifiableList(daftarPeminjaman);
    }

    public List<Kendaraan> getDaftarKendaraan() {
        return Collections.unmodifiableList(daftarKendaraan);
    }

    private Optional<Kendaraan> cariKendaraanById(String id) {
        return daftarKendaraan.stream()
            .filter(k -> k.getId().equalsIgnoreCase(id))
            .findFirst();
    }

    private Optional<Peminjaman> cariPeminjamanById(String id) {
        return daftarPeminjaman.stream()
            .filter(p -> p.getId().equalsIgnoreCase(id))
            .findFirst();
    }

    private <T> String generateId(List<T> list, String prefix,
                           java.util.function.Function<T, String> idExtractor) {
        int max = list.stream()
            .map(idExtractor)
            .map(id -> id.replace(prefix, ""))
            .mapToInt(s -> {
                try { return Integer.parseInt(s); }
                catch (NumberFormatException e) { return 0; }
            })
            .max()
            .orElse(0);
        return String.format("%s%03d", prefix, max + 1);
    }

    private String generateIdPeminjaman() {
        return generateId(daftarPeminjaman, "P", Peminjaman::getId);
    }

    private String generateIdKendaraan() {
        return generateId(daftarKendaraan, "K", Kendaraan::getId);
    }
}