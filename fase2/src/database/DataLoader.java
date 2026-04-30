package database;

import model.*;
import util.DateUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {

    private static final String PATH_KENDARAAN  = "data/kendaraan.json";
    private static final String PATH_USER        = "data/user.json";
    private static final String PATH_PEMINJAMAN  = "data/peminjaman.json";

    private DataLoader() {
        throw new AssertionError("DataLoader adalah utility class dan tidak boleh diinstansiasi");
    }

    public static List<Kendaraan> loadKendaraan() {
        List<Kendaraan> list = new ArrayList<>();
        String json = FileDatabase.readFile(PATH_KENDARAAN);
        for (String obj : FileDatabase.parseJsonArray(json)) {
            JenisKendaraan jenis = JenisKendaraan.valueOf(
                FileDatabase.getValue(obj, "jenis").toUpperCase()
            );
            String id    = FileDatabase.getValue(obj, "id");
            String nama  = FileDatabase.getValue(obj, "nama");
            String plat  = FileDatabase.getValue(obj, "platNomor");
            StatusKendaraan status= StatusKendaraan.valueOf(
                FileDatabase.getValue(obj, "status").toUpperCase()
            );
            long tarif;
            try {
                tarif = Long.parseLong(FileDatabase.getValue(obj, "tarifPerHari"));
            } catch (NumberFormatException e) {
                throw new IllegalStateException(
                    "Data korup: tarifPerHari tidak valid untuk kendaraan id='" + id + "'", e
                );
            }

            Kendaraan k;
            switch (jenis) {
                case MOBIL:
                    int kapasitas = Integer.parseInt(FileDatabase.getValue(obj, "kapasitasPenumpang"));
                    Transmisi transmisi = Transmisi.valueOf(
                        FileDatabase.getValue(obj, "transmisi").toUpperCase()
                    );
                    k = new Mobil(id, nama, plat, tarif, kapasitas, transmisi);
                    break;
                case MOTOR:
                    int kapasitasCc = Integer.parseInt(FileDatabase.getValue(obj, "kapasitasCc"));
                    JenisMotor jenisMotor= JenisMotor.valueOf(
                        FileDatabase.getValue(obj, "jenisMotor").toUpperCase()
                    );
                    k = new Motor(id, nama, plat, tarif, kapasitasCc, jenisMotor);
                    break;
                default: throw new IllegalStateException("Jenis tidak ditangani: " + jenis);
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
            String id                   = FileDatabase.getValue(obj, "id");
            String idUser               = FileDatabase.getValue(obj, "idUser");
            String idKendaraan          = FileDatabase.getValue(obj, "idKendaraan");
            String strTanggalPinjam     = FileDatabase.getValue(obj, "tanggalPinjam");
            String strTanggalRencana    = FileDatabase.getValue(obj, "tanggalKembaliRencana");
            String strTanggalAktual     = FileDatabase.getValue(obj, "tanggalKembaliAktual");
            StatusPeminjaman status     = StatusPeminjaman.valueOf(
                    FileDatabase.getValue(obj, "status").toUpperCase()
            );
            long denda                  = Long.parseLong(FileDatabase.getValue(obj, "denda"));

            LocalDate tanggalPinjam = DateUtil.parse(strTanggalPinjam)
                .orElseThrow(() -> new IllegalStateException(
                        "tanggalPinjam tidak valid untuk peminjaman id='" + id + "'"
                ));
            LocalDate tanggalRencana = DateUtil.parse(strTanggalRencana)
                .orElseThrow(() -> new IllegalStateException(
                        "tanggalKembaliRencana tidak valid untuk peminjaman id='" + id + "'"
                ));

            Peminjaman p = new Peminjaman(id, idUser, idKendaraan,
                    tanggalPinjam, tanggalRencana);

            p.setStatus(status);
            p.setDenda(denda);
            DateUtil.parse(strTanggalAktual).ifPresent(tgl ->
                p.selesaikan(tgl, denda)
            );

            list.add(p);
        }
        return list;
    }

    public static void saveKendaraan(List<Kendaraan> list) {
        List<String> objects = new ArrayList<>();
        for (Kendaraan k : list) {
            objects.add(FileDatabase.buildObject(
                k.toJsonEntries().toArray(new String[0])
            ));
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
                    FileDatabase.buildEntry("tanggalPinjam", 
                            DateUtil.format(p.getTanggalPinjam())
                            .orElseThrow(() -> new IllegalStateException(
                                "tanggalPinjam null untuk peminjaman id='" + p.getId() + "'"
                            ))
                    ),
                    FileDatabase.buildEntry("tanggalKembaliRencana", 
                            DateUtil.format(p.getTanggalKembaliRencana())
                            .orElseThrow(() -> new IllegalStateException(
                                "tanggalKembaliRencana null untuk peminjaman id='" + p.getId() + "'"
                            ))
                    ),
                    FileDatabase.buildEntry("tanggalKembaliAktual", DateUtil.format(p.getTanggalKembaliAktual().orElse(null)).orElse("null")),
                    FileDatabase.buildEntry("status", p.getStatus().name().toLowerCase()),
                    FileDatabase.buildEntryLong("denda", p.getDenda())
            ));
        }
        FileDatabase.writeFile(PATH_PEMINJAMAN, FileDatabase.buildArray(objects));
    }
}