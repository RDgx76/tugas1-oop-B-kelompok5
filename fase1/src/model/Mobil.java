package model;

public class Mobil extends Kendaraan {
    private int kapasitasPenumpang;
    private String transmisi; // "MANUAL" atau "OTOMATIS"

    public Mobil(String id, String nama, String platNomor, double tarifPerHari,
                 int kapasitasPenumpang, String transmisi) {
        super(id, nama, platNomor, tarifPerHari);
        this.kapasitasPenumpang = kapasitasPenumpang;
        this.transmisi = transmisi;
    }

    @Override
    public String getJenis() {
        return "Mobil";
    }

    // Getters
    public int getKapasitasPenumpang() { return kapasitasPenumpang; }
    public String getTransmisi() { return transmisi; }

    // Setters
    public void setKapasitasPenumpang(int kapasitasPenumpang) {
        this.kapasitasPenumpang = kapasitasPenumpang;
    }
    public void setTransmisi(String transmisi) { this.transmisi = transmisi; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | %d penumpang | %s", kapasitasPenumpang, transmisi);
    }
}