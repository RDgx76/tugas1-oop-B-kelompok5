import database.DataLoader;
import model.Kendaraan;
import model.Peminjaman;
import model.User;
import report.LaporanGenerator;
import service.PeminjamanService;
import service.UserService;
import util.InputHelper;

import java.util.List;

public class main {

    private static UserService       userService;
    private static PeminjamanService peminjamanService;
    private static LaporanGenerator  laporanGenerator;

    public static void main(String[] args) {
        inisialisasi();
        menuUtama();
        InputHelper.closeScanner();
    }

    private static void inisialisasi() {
        List<Kendaraan>  kendaraan   = DataLoader.loadKendaraan();
        List<User>       users       = DataLoader.loadUser();
        List<Peminjaman> peminjaman  = DataLoader.loadPeminjaman();

        userService       = new UserService(users);
        peminjamanService = new PeminjamanService(peminjaman, kendaraan, userService);
        laporanGenerator  = new LaporanGenerator(peminjamanService, userService);

        System.out.println("==============================================");
        System.out.println("   SISTEM PEMINJAMAN KENDARAAN               ");
        System.out.println("==============================================");
        System.out.println("Data dimuat: "
                + kendaraan.size()  + " kendaraan, "
                + users.size()      + " user, "
                + peminjaman.size() + " transaksi.");
    }

    private static void menuUtama() {
        while (true) {
            System.out.println("\n============= MENU UTAMA =============");
            System.out.println("  1. Manajemen Kendaraan");
            System.out.println("  2. Manajemen User");
            System.out.println("  3. Transaksi Peminjaman");
            System.out.println("  4. Laporan");
            System.out.println("  0. Keluar");
            System.out.println("======================================");
            int pilihan = InputHelper.inputInt("Pilih menu: ");

            switch (pilihan) {
                case 1: menuKendaraan();   break;
                case 2: menuUser();        break;
                case 3: menuTransaksi();   break;
                case 4: menuLaporan();     break;
                case 0:
                    System.out.println("Terima kasih. Program selesai.");
                    return;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }

    private static void menuKendaraan() {
        while (true) {
            System.out.println("\n------- MANAJEMEN KENDARAAN -------");
            System.out.println("  1. Lihat Semua Kendaraan");
            System.out.println("  2. Tambah Kendaraan");
            System.out.println("  3. Hapus Kendaraan");
            System.out.println("  0. Kembali");
            int pilihan = InputHelper.inputInt("Pilih: ");

            switch (pilihan) {
                case 1: peminjamanService.tampilkanSemuaKendaraan(); break;
                case 2: peminjamanService.tambahKendaraan();         break;
                case 3: peminjamanService.hapusKendaraan();          break;
                case 0: return;
                default: System.out.println("Pilihan tidak valid.");
            }
        }
    }

    private static void menuUser() {
        while (true) {
            System.out.println("\n---------- MANAJEMEN USER ----------");
            System.out.println("  1. Lihat Semua User");
            System.out.println("  2. Tambah User");
            System.out.println("  3. Hapus User");
            System.out.println("  0. Kembali");
            int pilihan = InputHelper.inputInt("Pilih: ");

            switch (pilihan) {
                case 1: userService.tampilkanSemuaUser(); break;
                case 2: userService.tambahUser();         break;
                case 3: userService.hapusUser();          break;
                case 0: return;
                default: System.out.println("Pilihan tidak valid.");
            }
        }
    }

    private static void menuTransaksi() {
        while (true) {
            System.out.println("\n--------- TRANSAKSI PEMINJAMAN ---------");
            System.out.println("  1. Pinjam Kendaraan");
            System.out.println("  2. Kembalikan Kendaraan");
            System.out.println("  3. Lihat Peminjaman Aktif");
            System.out.println("  0. Kembali");
            int pilihan = InputHelper.inputInt("Pilih: ");

            switch (pilihan) {
                case 1: peminjamanService.pinjamKendaraan();    break;
                case 2: peminjamanService.kembalikanKendaraan();break;
                case 3:
                    System.out.println("\nPeminjaman Aktif:");
                    for (Peminjaman p : peminjamanService.getPeminjamanAktif()) {
                        System.out.println("  " + p);
                    }
                    break;
                case 0: return;
                default: System.out.println("Pilihan tidak valid.");
            }
        }
    }

    private static void menuLaporan() {
        while (true) {
            System.out.println("\n------------ LAPORAN ------------");
            System.out.println("  1. Semua Transaksi");
            System.out.println("  2. Transaksi Aktif");
            System.out.println("  3. Laporan Pendapatan");
            System.out.println("  4. Ekspor Laporan ke File .txt");
            System.out.println("  0. Kembali");
            int pilihan = InputHelper.inputInt("Pilih: ");

            switch (pilihan) {
                case 1: laporanGenerator.laporanSemuaPeminjaman(); break;
                case 2: laporanGenerator.laporanPeminjamanAktif(); break;
                case 3: laporanGenerator.laporanPendapatan();      break;
                case 4: laporanGenerator.eksporLaporan();          break;
                case 0: return;
                default: System.out.println("Pilihan tidak valid.");
            }
        }
    }
}