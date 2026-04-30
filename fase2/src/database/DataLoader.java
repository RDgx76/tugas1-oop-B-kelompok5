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

    public static List<Kendaraan> muatKendaraan() {
        List<Kendaraan> list = new ArrayList<>();
        String json = FileDatabase.readFile(PATH_KENDARAAN);
        for (String obj : FileDatabase.parseJsonArray(json)) {
            String id    = FileDatabase.getValue(obj, "id");
            String nama  = FileDatabase.getValue(obj, "nama");
            String plat  = FileDatabase.getValue(obj, "platNomor");
            JenisKendaraan jenis;
            try {
                jenis = JenisKendaraan.valueOf(
                    FileDatabase.getValue(obj, "jenis").toUpperCase()
                );
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException(
                    "Jenis kendaraan tidak valid di JSON", e
                );
            }
            StatusKendaraan status;
            try {
                status = StatusKendaraan.valueOf(
                    FileDatabase.getValue(obj, "status").toUpperCase()
                );
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException(
                    "Status kendaraan tidak valid untuk id='" + id + "'", e
                );
            }
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
                    int kapasitas;
                    try {
                        kapasitas = Integer.parseInt(FileDatabase.getValue(obj, "kapasitasPenumpang"));
                    } catch (NumberFormatException e) {
                        throw new IllegalStateException(
                            "Data korup: kapasitasPenumpang tidak valid untuk kendaraan id='" + id + "'", e
                        );
                    }
                    Transmisi transmisi;
                    try {
                        transmisi = Transmisi.valueOf(
                            FileDatabase.getValue(obj, "transmisi").toUpperCase()
                        );
                    } catch (IllegalArgumentException e) {
                        throw new IllegalStateException(
                            "Transmisi tidak valid untuk kendaraan id='" + id + "'", e
                        );
                    }
                    k = new Mobil(id, nama, plat, tarif, kapasitas, transmisi, status);
                    break;
                case MOTOR:
                    int kapasitasCc;
                    try {
                        kapasitasCc = Integer.parseInt(FileDatabase.getValue(obj, "kapasitasCc"));
                    } catch (NumberFormatException e) {
                        throw new IllegalStateException(
                            "Data korup: kapasitasCc tidak valid untuk motor id='" + id + "'", e
                        );
                    }
                    JenisMotor jenisMotor;
                    try {
                        jenisMotor = JenisMotor.valueOf(
                            FileDatabase.getValue(obj, "jenisMotor").toUpperCase()
                        );
                    } catch (IllegalArgumentException e) {
                        throw new IllegalStateException(
                            "JenisMotor tidak valid untuk motor id='" + id + "'", e
                        );
                    }
                    k = new Motor(id, nama, plat, tarif, kapasitasCc, jenisMotor, status);
                    break;
                default: throw new IllegalStateException("Jenis tidak ditangani: " + jenis);
            }
            list.add(k);
        }
        return list;
    }

    public static List<User> muatUser() {
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

    public static List<Peminjaman> muatPeminjaman() {
        List<Peminjaman> list = new ArrayList<>();
        String json = FileDatabase.readFile(PATH_PEMINJAMAN);
        for (String obj : FileDatabase.parseJsonArray(json)) {
            String id                   = FileDatabase.getValue(obj, "id");
            String idUser               = FileDatabase.getValue(obj, "idUser");
            String idKendaraan          = FileDatabase.getValue(obj, "idKendaraan");
            String strTanggalPinjam     = FileDatabase.getValue(obj, "tanggalPinjam");
            String strTanggalRencana    = FileDatabase.getValue(obj, "tanggalKembaliRencana");
            String strTanggalAktual     = FileDatabase.getValue(obj, "tanggalKembaliAktual");
            StatusPeminjaman status;
            try {
                status = StatusPeminjaman.valueOf(
                    FileDatabase.getValue(obj, "status").toUpperCase()
                );
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException(
                    "Status peminjaman tidak valid untuk id='" + id + "'", e
                );
            }
            long denda;
            try {
                denda = Long.parseLong(FileDatabase.getValue(obj, "denda"));
            } catch (NumberFormatException e) {
                throw new IllegalStateException(
                    "Data korup: denda tidak valid untuk peminjaman id='" + id + "'", e
                );
            }

            LocalDate tanggalPinjam = DateUtil.parse(strTanggalPinjam)
                .orElseThrow(() -> new IllegalStateException(
                        "tanggalPinjam tidak valid untuk peminjaman id='" + id + "'"
                ));
            LocalDate tanggalRencana = DateUtil.parse(strTanggalRencana)
                .orElseThrow(() -> new IllegalStateException(
                        "tanggalKembaliRencana tidak valid untuk peminjaman id='" + id + "'"
                ));

            LocalDate tanggalKembaliAktual = DateUtil.parse(strTanggalAktual)
                .orElse(null);

            Peminjaman p = new Peminjaman(id, idUser, idKendaraan,
                    tanggalPinjam, tanggalRencana, tanggalKembaliAktual,status, denda);

            list.add(p);
        }
        return list;
    }

    public static void simpanKendaraan(List<Kendaraan> list) {
        List<String> objects = new ArrayList<>();
        for (Kendaraan k : list) {
            objects.add(FileDatabase.buildObjectFromMap(k.toJsonMap()));
        }
        FileDatabase.writeFile(PATH_KENDARAAN, FileDatabase.buildArray(objects));
    }

    public static void simpanUser(List<User> list) {
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

    public static void simpanPeminjaman(List<Peminjaman> list) {
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
                    FileDatabase.buildEntry("status", p.getStatus().name()),
                    FileDatabase.buildEntryLong("denda", p.getDenda())
            ));
        }
        FileDatabase.writeFile(PATH_PEMINJAMAN, FileDatabase.buildArray(objects));
    }
}