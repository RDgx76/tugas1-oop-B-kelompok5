package model;

import java.time.LocalDate;

public class Peminjaman {
    private String id;
    private String idUser;
    private String idKendaraan;
    private LocalDate tanggalPinjam;
    private LocalDate tanggalKembaliRencana;
    private LocalDate tanggalKembaliAktual; // null kalau belum dikembalikan
    private String status; // "AKTIF" atau "SELESAI"
    private double denda;

    public Peminjaman(String id, String idUser, String idKendaraan,
                      LocalDate tanggalPinjam, LocalDate tanggalKembaliRencana) {
        this.id = id;
        this.idUser = idUser;
        this.idKendaraan = idKendaraan;
        this.tanggalPinjam = tanggalPinjam;
        this.tanggalKembaliRencana = tanggalKembaliRencana;
        this.tanggalKembaliAktual = null;
        this.status = "AKTIF";
        this.denda = 0;
    }

    // Getters
    public String getId() { return id; }
    public String getIdUser() { return idUser; }
    public String getIdKendaraan() { return idKendaraan; }
    public LocalDate getTanggalPinjam() { return tanggalPinjam; }
    public LocalDate getTanggalKembaliRencana() { return tanggalKembaliRencana; }
    public LocalDate getTanggalKembaliAktual() { return tanggalKembaliAktual; }
    public String getStatus() { return status; }
    public double getDenda() { return denda; }

    // Setters
    public void setTanggalKembaliAktual(LocalDate tanggalKembaliAktual) {
        this.tanggalKembaliAktual = tanggalKembaliAktual;
    }
    public void setStatus(String status) { this.status = status; }
    public void setDenda(double denda) { this.denda = denda; }

    public boolean isAktif() {
        return this.status.equals("AKTIF");
    }

    @Override
    public String toString() {
        return String.format("[%s] User: %s | Kendaraan: %s | Pinjam: %s | Rencana Kembali: %s | Status: %s | Denda: Rp%.0f",
                id, idUser, idKendaraan, tanggalPinjam, tanggalKembaliRencana, status, denda);
    }
}