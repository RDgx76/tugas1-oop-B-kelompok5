package model;

public abstract class Kendaraan {
    private String id;
    private String nama;
    private String platNomor;
    private String status; // "TERSEDIA" atau "DIPINJAM"
    private double tarifPerHari;

    public Kendaraan(String id, String nama, String platNomor, double tarifPerHari) {
        this.id = id;
        this.nama = nama;
        this.platNomor = platNomor;
        this.status = "TERSEDIA";
        this.tarifPerHari = tarifPerHari;
    }

    // Abstract method, wajib diimplementasi subclass
    public abstract String getJenis();

    // Getters
    public String getId() { return id; }
    public String getNama() { return nama; }
    public String getPlatNomor() { return platNomor; }
    public String getStatus() { return status; }
    public double getTarifPerHari() { return tarifPerHari; }

    // Setters
    public void setStatus(String status) { this.status = status; }
    public void setTarifPerHari(double tarifPerHari) { this.tarifPerHari = tarifPerHari; }

    public boolean isTersedia() {
        return this.status.equals("TERSEDIA");
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | %s | Rp%.0f/hari",
                id, nama, platNomor, status, tarifPerHari);
    }
}