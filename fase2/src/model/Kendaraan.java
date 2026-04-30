package model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public abstract class Kendaraan implements JsonSerializable {
    private final String id;
    private final String nama;
    private final String platNomor;
    private final long tarifPerHari;
    private StatusKendaraan status; 

    public Kendaraan(String id, String nama, String platNomor, long tarifPerHari, StatusKendaraan status) {
        Objects.requireNonNull(id, "ID tidak boleh null");
        Objects.requireNonNull(nama, "Nama tidak boleh null");
        Objects.requireNonNull(platNomor, "Plat nomor tidak boleh null");
        Objects.requireNonNull(status, "Status kendaraan tidak boleh null");
        
        if (id.isBlank())       throw new IllegalArgumentException("ID tidak boleh kosong");
        if (nama.isBlank())     throw new IllegalArgumentException("Nama tidak boleh kosong");
        if (platNomor.isBlank()) throw new IllegalArgumentException("Plat nomor tidak boleh kosong");
        if (tarifPerHari <= 0)  throw new IllegalArgumentException("Tarif harus lebih dari 0");
        
        this.id = id;
        this.nama = nama;
        this.platNomor = platNomor;
        this.status = status;
        this.tarifPerHari = tarifPerHari;
    }

    public Kendaraan(String id, String nama, String platNomor, long tarifPerHari) { 
        this(id, nama, platNomor, tarifPerHari, StatusKendaraan.TERSEDIA);
    }

    // Abstract method, wajib diimplementasi subclass
    public abstract JenisKendaraan getJenis();

    // Getters
    public String getId() { return id; }
    public String getNama() { return nama; }
    public String getPlatNomor() { return platNomor; }
    public StatusKendaraan getStatus() { return status; }
    public long getTarifPerHari() { return tarifPerHari; }

    // Setters
    private void setStatus(StatusKendaraan status) { 
        Objects.requireNonNull(status, "Status tidak boleh null");
        this.status = status; 
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

    // Field umum semua kendaraan dikumpulkan di method helper
    protected Map<String, String> baseJsonMap() {
        Map<String, String> map = new LinkedHashMap<>(); // LinkedHashMap — urutan terjaga
        map.put("id",           getId());
        map.put("nama",         getNama());
        map.put("platNomor",    getPlatNomor());
        map.put("jenis",        getJenis().name());
        map.put("status",       getStatus().name());
        map.put("tarifPerHari", String.valueOf(getTarifPerHari()));
        return map;
    }

    // Subclass wajib implementasi — menambahkan field spesifiknya sendiri
    @Override
    public abstract Map<String, String> toJsonMap();

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | %s | Rp%,d/hari",
                id, nama, platNomor, status.name(), tarifPerHari);
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