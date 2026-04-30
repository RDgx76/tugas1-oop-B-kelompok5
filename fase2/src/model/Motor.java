package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import database.FileDatabase;

public class Motor extends Kendaraan {
    private int kapasitasCc;
    private JenisMotor jenisMotor;

    public Motor(String id, String nama, String platNomor, long tarifPerHari,
                 int kapasitasCc, JenisMotor jenisMotor) {
        super(id, nama, platNomor, tarifPerHari);

        if (kapasitasCc <= 0) {
            throw new IllegalArgumentException(
                "CC mesin harus lebih dari 0, diterima: " + kapasitasCc
            );
        }
        Objects.requireNonNull(jenisMotor, "Jenis motor tidak boleh null");

        this.kapasitasCc = kapasitasCc;
        this.jenisMotor = jenisMotor;
    }

    @Override
    public JenisKendaraan getJenis() {
        return JenisKendaraan.MOTOR;
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

        entries.add(FileDatabase.buildEntryInt("kapasitasCc", getKapasitasCc()));
        entries.add(FileDatabase.buildEntry("jenisMotor", getJenisMotor().name().toLowerCase()));
        
        return entries;
    }

    // Getters
    public int getKapasitasCc() { return kapasitasCc; }
    public JenisMotor getJenisMotor() { return jenisMotor; }

    // Setters
    public void setKapasitasCc(int kapasitasCc) {
        if (kapasitasCc <= 0) {
            throw new IllegalArgumentException(
                "CC mesin harus lebih dari 0, diterima: " + kapasitasCc
            );
        } 
        this.kapasitasCc = kapasitasCc; 
    }
    public void setJenisMotor(JenisMotor jenisMotor) { this.jenisMotor = jenisMotor; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | %d CC | %s", kapasitasCc, jenisMotor);
    }
}