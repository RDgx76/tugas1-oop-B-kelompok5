package model;

import java.util.Map;
import java.util.Objects;

public class Mobil extends Kendaraan {
    private final int kapasitasPenumpang;
    private final Transmisi transmisi;

    public Mobil(String id, String nama, String platNomor, long tarifPerHari,
                 int kapasitasPenumpang, Transmisi transmisi, StatusKendaraan status) {
        super(id, nama, platNomor, tarifPerHari, status);

        if (kapasitasPenumpang <= 0) {
            throw new IllegalArgumentException("Kapasitas penumpang harus lebih dari 0");
        }
        Objects.requireNonNull(transmisi, "Transmisi tidak boleh null");

        this.kapasitasPenumpang = kapasitasPenumpang;
        this.transmisi = transmisi;
    }

    public Mobil(String id, String nama, String platNomor, long tarifPerHari,
                 int kapasitasPenumpang, Transmisi transmisi) {
        this(id, nama, platNomor, tarifPerHari, kapasitasPenumpang, transmisi, StatusKendaraan.TERSEDIA);
    }

    @Override
    public JenisKendaraan getJenis() {
        return JenisKendaraan.MOBIL;
    }

    @Override
    public Map<String, String> toJsonMap() {
        Map<String, String> map = baseJsonMap(); // ambil field umum dari parent
        map.put("kapasitasPenumpang", String.valueOf(kapasitasPenumpang));
        map.put("transmisi",          transmisi.name());
        return map;
    }

    // Getters
    public int getKapasitasPenumpang() { return kapasitasPenumpang; }
    public Transmisi getTransmisi() { return transmisi; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | %d penumpang | %s", kapasitasPenumpang, transmisi.name());
    }
}