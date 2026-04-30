package model;

import java.util.List;
import java.util.Objects;

public abstract class Kendaraan {
    private final String id;
    private String nama;
    private final String platNomor;
    private StatusKendaraan status; // "TERSEDIA" atau "DIPINJAM"
    private long tarifPerHari;

    public Kendaraan(String id, String nama, String platNomor, long tarifPerHari) {
        Objects.requireNonNull(id, "ID tidak boleh null");
        Objects.requireNonNull(nama, "Nama tidak boleh null");
        Objects.requireNonNull(platNomor, "Plat nomor tidak boleh null");
        
        if (id.isBlank())       throw new IllegalArgumentException("ID tidak boleh kosong");
        if (nama.isBlank())     throw new IllegalArgumentException("Nama tidak boleh kosong");
        if (platNomor.isBlank()) throw new IllegalArgumentException("Plat nomor tidak boleh kosong");
        if (tarifPerHari <= 0)  throw new IllegalArgumentException("Tarif harus lebih dari 0");
        
        this.id = id;
        this.nama = nama;
        this.platNomor = platNomor;
        this.status = StatusKendaraan.TERSEDIA;
        this.tarifPerHari = tarifPerHari;
    }

    // Abstract method, wajib diimplementasi subclass
    public abstract JenisKendaraan getJenis();
    public abstract List<String> toJsonEntries();

    // Getters
    public String getId() { return id; }
    public String getNama() { return nama; }
    public String getPlatNomor() { return platNomor; }
    public StatusKendaraan getStatus() { return status; }
    public long getTarifPerHari() { return tarifPerHari; }

    // Setters
    public void setStatus(StatusKendaraan status) { 
        Objects.requireNonNull(status, "Status tidak boleh null");
        this.status = status; 
    }
    public void setTarifPerHari(long tarifPerHari) {
        if (tarifPerHari <= 0) {
            throw new IllegalArgumentException("Tarif harus lebih dari 0, diterima: " + tarifPerHari);
        } 
        this.tarifPerHari = tarifPerHari; 
    }

    public void pinjam() {
        if (this.status == StatusKendaraan.DIPINJAM) {
            throw new IllegalStateException(
                "Kendaraan id='" + id + "' sudah dalam status DIPINJAM"
            );
        }
        setStatus(StatusKendaraan.DIPINJAM);
    }

    public void kembalikan() {
        if (this.status == StatusKendaraan.TERSEDIA) {
            throw new IllegalStateException(
                "Kendaraan id='" + id + "' sudah dalam status TERSEDIA"
            );
        }
        setStatus(StatusKendaraan.TERSEDIA);
    }

    public boolean isTersedia() {
        return this.status == StatusKendaraan.TERSEDIA;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | %s | Rp%,d/hari",
                id, nama, platNomor, status, tarifPerHari);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Kendaraan)) return false;
        Kendaraan that = (Kendaraan) o;
        return Objects.equals(id, that.id)
            && Objects.equals(platNomor, that.platNomor)
            && Objects.equals(nama, that.nama);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, platNomor, nama);
    }
}