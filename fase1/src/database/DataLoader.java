package database;

import model.*;
import util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class DataLoader {

    private static final String PATH_KENDARAAN  = "data/kendaraan.json";
    private static final String PATH_USER        = "data/user.json";
    private static final String PATH_PEMINJAMAN  = "data/peminjaman.json";

    public static List<Kendaraan> loadKendaraan() {
        List<Kendaraan> list = new ArrayList<>();
        String json = FileDatabase.readFile(PATH_KENDARAAN);
        for (String obj : FileDatabase.parseJsonArray(json)) {
            String jenis = FileDatabase.getValue(obj, "jenis");
            String id    = FileDatabase.getValue(obj, "id");
            String nama  = FileDatabase.getValue(obj, "nama");
            String plat  = FileDatabase.getValue(obj, "platNomor");
            String status= FileDatabase.getValue(obj, "status");
            double tarif = Double.parseDouble(FileDatabase.getValue(obj, "tarifPerHari"));

            Kendaraan k;
            if (jenis.equals("Mobil")) {
                int kapasitas    = Integer.parseInt(FileDatabase.getValue(obj, "kapasitasPenumpang"));
                String transmisi = FileDatabase.getValue(obj, "transmisi");
                k = new Mobil(id, nama, plat, tarif, kapasitas, transmisi);
            } else {
                int cc           = Integer.parseInt(FileDatabase.getValue(obj, "cc"));
                String jenisMotor= FileDatabase.getValue(obj, "jenisMotor");
                k = new Motor(id, nama, plat, tarif, cc, jenisMotor);
            }
            k.setStatus(status);
            list.add(k);
        }
        return list;
    }

    public static List<User> loadUser() {
        List<User> list = new ArrayList<>();
        String json = FileDatabase.readFile(PATH_USER);
        for (String obj : FileDatabase.parseJsonArray(json)) {
            String id    = FileDatabase.getValue(obj, "id");
            String nama  = FileDatabase.getValue(obj, "nama");
            String telp  = FileDatabase.getValue(obj, "noTelepon");
            list.add(new User(id, nama, telp));
        }
        return list;
    }

    public static List<Peminjaman> loadPeminjaman() {
        List<Peminjaman> list = new ArrayList<>();
        String json = FileDatabase.readFile(PATH_PEMINJAMAN);
        for (String obj : FileDatabase.parseJsonArray(json)) {
            String id          = FileDatabase.getValue(obj, "id");
            String idUser      = FileDatabase.getValue(obj, "idUser");
            String idKendaraan = FileDatabase.getValue(obj, "idKendaraan");
            String tPinjam     = FileDatabase.getValue(obj, "tanggalPinjam");
            String tRencana    = FileDatabase.getValue(obj, "tanggalKembaliRencana");
            String tAktual     = FileDatabase.getValue(obj, "tanggalKembaliAktual");
            String status      = FileDatabase.getValue(obj, "status");
            double denda       = Double.parseDouble(FileDatabase.getValue(obj, "denda"));

            Peminjaman p = new Peminjaman(id, idUser, idKendaraan,
                    DateUtil.parse(tPinjam), DateUtil.parse(tRencana));
            p.setStatus(status);
            p.setDenda(denda);
            if (!tAktual.equals("null") && !tAktual.isEmpty()) {
                p.setTanggalKembaliAktual(DateUtil.parse(tAktual));
            }
            list.add(p);
        }
        return list;
    }

    public static void saveKendaraan(List<Kendaraan> list) {
        List<String> objects = new ArrayList<>();
        for (Kendaraan k : list) {
            List<String> entries = new ArrayList<>();
            entries.add(FileDatabase.buildEntry("id", k.getId()));
            entries.add(FileDatabase.buildEntry("nama", k.getNama()));
            entries.add(FileDatabase.buildEntry("platNomor", k.getPlatNomor()));
            entries.add(FileDatabase.buildEntry("jenis", k.getJenis()));
            entries.add(FileDatabase.buildEntry("status", k.getStatus()));
            entries.add(FileDatabase.buildEntryNumber("tarifPerHari", k.getTarifPerHari()));

            if (k instanceof Mobil) {
                Mobil m = (Mobil) k;
                entries.add(FileDatabase.buildEntryInt("kapasitasPenumpang", m.getKapasitasPenumpang()));
                entries.add(FileDatabase.buildEntry("transmisi", m.getTransmisi()));
            } else if (k instanceof Motor) {
                Motor m = (Motor) k;
                entries.add(FileDatabase.buildEntryInt("cc", m.getCc()));
                entries.add(FileDatabase.buildEntry("jenisMotor", m.getJenisMotor()));
            }
            objects.add(FileDatabase.buildObject(entries.toArray(new String[0])));
        }
        FileDatabase.writeFile(PATH_KENDARAAN, FileDatabase.buildArray(objects));
    }

    public static void saveUser(List<User> list) {
        List<String> objects = new ArrayList<>();
        for (User u : list) {
            objects.add(FileDatabase.buildObject(
                    FileDatabase.buildEntry("id", u.getId()),
                    FileDatabase.buildEntry("nama", u.getNama()),
                    FileDatabase.buildEntry("noTelepon", u.getNoTelepon())
            ));
        }
        FileDatabase.writeFile(PATH_USER, FileDatabase.buildArray(objects));
    }

    public static void savePeminjaman(List<Peminjaman> list) {
        List<String> objects = new ArrayList<>();
        for (Peminjaman p : list) {
            objects.add(FileDatabase.buildObject(
                    FileDatabase.buildEntry("id", p.getId()),
                    FileDatabase.buildEntry("idUser", p.getIdUser()),
                    FileDatabase.buildEntry("idKendaraan", p.getIdKendaraan()),
                    FileDatabase.buildEntry("tanggalPinjam", DateUtil.format(p.getTanggalPinjam())),
                    FileDatabase.buildEntry("tanggalKembaliRencana", DateUtil.format(p.getTanggalKembaliRencana())),
                    FileDatabase.buildEntry("tanggalKembaliAktual", DateUtil.format(p.getTanggalKembaliAktual())),
                    FileDatabase.buildEntry("status", p.getStatus()),
                    FileDatabase.buildEntryNumber("denda", p.getDenda())
            ));
        }
        FileDatabase.writeFile(PATH_PEMINJAMAN, FileDatabase.buildArray(objects));
    }
}