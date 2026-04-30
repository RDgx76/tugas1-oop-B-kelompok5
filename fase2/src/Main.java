import database.DataLoader;
import model.Kendaraan;
import model.Peminjaman;
import model.User;
import report.LaporanGenerator;
import service.PeminjamanService;
import service.UserService;
import util.InputHelper;
import util.ConsoleUtil;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Semua dibuat lokal di main(), lalu diteruskan sebagai parameter
        List<Kendaraan>  kendaraan  = DataLoader.loadKendaraan();
        List<User>       users      = DataLoader.loadUser();
        List<Peminjaman> peminjaman = DataLoader.loadPeminjaman();

        UserService userService = new UserService(users);

        PeminjamanService peminjamanService = new PeminjamanService(
            peminjaman, kendaraan, userService
        );

        LaporanGenerator laporanGenerator = new LaporanGenerator(
            peminjamanService, userService
        );

        System.out.println("==============================================");
        System.out.println("   SISTEM PEMINJAMAN KENDARAAN               ");
        System.out.println("==============================================");
        System.out.println("Data dimuat: "
                + kendaraan.size()  + " kendaraan, "
                + users.size()      + " user, "
                + peminjaman.size() + " transaksi.");

        menuUtama(peminjamanService, userService, laporanGenerator);
    }

    private static void menuUtama(PeminjamanService peminjamanService,
                    UserService userService,
                    LaporanGenerator laporanGenerator) {
        while (true) {
            ConsoleUtil.clearScreen();
            System.out.println("\n============= MENU UTAMA =============");
            System.out.println("  1. Manajemen Kendaraan");
            System.out.println("  2. Manajemen User");
            System.out.println("  3. Transaksi Peminjaman");
            System.out.println("  4. Laporan");
            System.out.println("  0. Keluar");
            System.out.println("======================================");
            int pilihan = InputHelper.inputInt("Pilih menu: ", 0, 4);
            ConsoleUtil.clearScreen();

            switch (pilihan) {
                case 1: 
                        menuKendaraan(peminjamanService);
                        break;
                case 2: 
                        menuUser(userService);   
                        break;
                case 3: 
                        menuTransaksi(peminjamanService);
                        break;
                case 4: 
                        menuLaporan(laporanGenerator);
                        break;
                case 0:
                    System.out.println("Terima kasih. Program selesai.");
                    return;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }

    private static void menuKendaraan(PeminjamanService peminjamanService) {
        while (true) {
            ConsoleUtil.clearScreen();
            System.out.println("\n------- MANAJEMEN KENDARAAN -------");
            System.out.println("  1. Lihat Semua Kendaraan");
            System.out.println("  2. Tambah Kendaraan");
            System.out.println("  3. Hapus Kendaraan");
            System.out.println("  0. Kembali");
            int pilihan = InputHelper.inputInt("Pilih: ", 0, 3);
            ConsoleUtil.clearScreen();

            switch (pilihan) {
                case 1: 
                    peminjamanService.tampilkanSemuaKendaraan();
                    ConsoleUtil.pauseScreen();
                    break;
                case 2: 
                    peminjamanService.tambahKendaraan();
                    ConsoleUtil.pauseScreen();         
                    break;
                case 3: 
                    peminjamanService.hapusKendaraan();  
                    ConsoleUtil.pauseScreen();        
                    break;
                case 0: return;
                default: System.out.println("Pilihan tidak valid.");
            }
        }
    }

    private static void menuUser(UserService userService) {
        while (true) {
            ConsoleUtil.clearScreen();
            System.out.println("\n---------- MANAJEMEN USER ----------");
            System.out.println("  1. Lihat Semua User");
            System.out.println("  2. Tambah User");
            System.out.println("  3. Hapus User");
            System.out.println("  0. Kembali");
            int pilihan = InputHelper.inputInt("Pilih: ", 0, 3);
            ConsoleUtil.clearScreen();

            switch (pilihan) {
                case 1: 
                    userService.tampilkanSemuaUser(); 
                    ConsoleUtil.pauseScreen();
                    break;
                case 2: 
                    userService.tambahUser();
                    ConsoleUtil.pauseScreen();
                    break;
                case 3: 
                    userService.hapusUser();
                    ConsoleUtil.pauseScreen();
                    break;
                case 0: return;
                default: System.out.println("Pilihan tidak valid.");
            }
        }
    }

    private static void menuTransaksi(PeminjamanService peminjamanService) {
        while (true) {
            ConsoleUtil.clearScreen();
            System.out.println("\n--------- TRANSAKSI PEMINJAMAN ---------");
            System.out.println("  1. Pinjam Kendaraan");
            System.out.println("  2. Kembalikan Kendaraan");
            System.out.println("  3. Lihat Peminjaman Aktif");
            System.out.println("  0. Kembali");
            int pilihan = InputHelper.inputInt("Pilih: ", 0, 3);
            ConsoleUtil.clearScreen();

            switch (pilihan) {
                case 1: 
                    peminjamanService.pinjamKendaraan();   
                    ConsoleUtil.pauseScreen(); 
                    break;
                case 2: 
                    peminjamanService.kembalikanKendaraan();
                    ConsoleUtil.pauseScreen();
                    break;
                case 3:
                    System.out.println("\nPeminjaman Aktif:");
                    for (Peminjaman p : peminjamanService.getPeminjamanAktif()) {
                        System.out.println("  " + p);
                    }
                    ConsoleUtil.pauseScreen();
                    break;
                case 0: return;
                default: System.out.println("Pilihan tidak valid.");
            }
        }
    }

    private static void menuLaporan(LaporanGenerator laporanGenerator) {
        while (true) {
            ConsoleUtil.clearScreen();
            System.out.println("\n------------ LAPORAN ------------");
            System.out.println("  1. Semua Transaksi");
            System.out.println("  2. Transaksi Aktif");
            System.out.println("  3. Laporan Pendapatan");
            System.out.println("  4. Ekspor Laporan ke File .txt");
            System.out.println("  0. Kembali");
            int pilihan = InputHelper.inputInt("Pilih: ", 0, 4);
            ConsoleUtil.clearScreen();

            switch (pilihan) {
                case 1: 
                    laporanGenerator.laporanSemuaPeminjaman(); 
                    ConsoleUtil.pauseScreen();
                    break;
                case 2: 
                    laporanGenerator.laporanPeminjamanAktif();
                    ConsoleUtil.pauseScreen(); 
                    break;
                case 3:
                    laporanGenerator.laporanPendapatan();
                    ConsoleUtil.pauseScreen();
                    break;
                case 4: 
                    laporanGenerator.eksporLaporan();
                    ConsoleUtil.pauseScreen();
                    break;
                case 0: return;
                default: System.out.println("Pilihan tidak valid.");
            }
        }
    }
}