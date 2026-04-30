package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import database.FileDatabase;

public class Mobil extends Kendaraan {
    private int kapasitasPenumpang;
    private Transmisi transmisi;

    public Mobil(String id, String nama, String platNomor, long tarifPerHari,
                 int kapasitasPenumpang, Transmisi transmisi) {
        super(id, nama, platNomor, tarifPerHari);

        if (kapasitasPenumpang <= 0) {
            throw new IllegalArgumentException("Kapasitas penumpang harus lebih dari 0");
        }
        Objects.requireNonNull(transmisi, "Transmisi tidak boleh null");

        this.kapasitasPenumpang = kapasitasPenumpang;
        this.transmisi = transmisi;
    }

    @Override
    public JenisKendaraan getJenis() {
        return JenisKendaraan.MOBIL;
    }
    @Override
    public List<String> toJsonEntries() {
        List<String> entries = new ArrayList<>();
        entries.add(FileDatabase.buildEntry("id", getId()));
        entries.add(FileDatabase.buildEntry("nama", getNama()));
        entries.add(FileDatabase.buildEntry("platNomor", getPlatNomor()));
        entries.add(FileDatabase.buildEntry("jenis", getJenis().name().toLowerCase()));
        entries.add(FileDatabase.buildEntry("status", getStatus().name().toLowerCase()));
        entries.add(FileDatabase.buildEntryLong("tarifPerHari", getTarifPerHari()));

        entries.add(FileDatabase.buildEntryInt("kapasitasPenumpang", getKapasitasPenumpang()));
        entries.add(FileDatabase.buildEntry("transmisi", getTransmisi().name().toLowerCase()));
        
        return entries;
    }

    // Getters
    public int getKapasitasPenumpang() { return kapasitasPenumpang; }
    public Transmisi getTransmisi() { return transmisi; }

    // Setters
    public void setKapasitasPenumpang(int kapasitasPenumpang) {
        if (kapasitasPenumpang <= 0) {
            throw new IllegalArgumentException(
                "Kapasitas penumpang harus lebih dari 0, diterima: " + kapasitasPenumpang
            );
        }
        this.kapasitasPenumpang = kapasitasPenumpang;
    }
    public void setTransmisi(Transmisi transmisi) { this.transmisi = transmisi; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | %d penumpang | %s", kapasitasPenumpang, transmisi);
    }
}