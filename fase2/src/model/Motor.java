package model;

import java.util.Map;
import java.util.Objects;

public class Motor extends Kendaraan {
    private final int kapasitasCc;
    private final JenisMotor jenisMotor;

    public Motor(String id, String nama, String platNomor, long tarifPerHari,
                 int kapasitasCc, JenisMotor jenisMotor, StatusKendaraan status) {
        super(id, nama, platNomor, tarifPerHari, status);

        if (kapasitasCc <= 0) {
            throw new IllegalArgumentException(
                "CC mesin harus lebih dari 0, diterima: " + kapasitasCc
            );
        }
        Objects.requireNonNull(jenisMotor, "Jenis motor tidak boleh null");

        this.kapasitasCc = kapasitasCc;
        this.jenisMotor = jenisMotor;
    }

    public Motor(String id, String nama, String platNomor, long tarifPerHari,
                 int kapasitasCc, JenisMotor jenisMotor) {
        this(id, nama, platNomor, tarifPerHari, kapasitasCc, jenisMotor, StatusKendaraan.TERSEDIA);
    }

    @Override
    public JenisKendaraan getJenis() {
        return JenisKendaraan.MOTOR;
    }

    @Override
    public Map<String, String> toJsonMap() {
        Map<String, String> map = baseJsonMap(); // ambil field umum dari parent
        map.put("kapasitasCc",  String.valueOf(kapasitasCc));
        map.put("jenisMotor",   jenisMotor.name());
        return map;
    }

    // Getters
    public int getKapasitasCc() { return kapasitasCc; }
    public JenisMotor getJenisMotor() { return jenisMotor; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | %d CC | %s", kapasitasCc, jenisMotor.name());
    }
}