package model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public class Peminjaman {
    private String id;
    private String idUser;
    private String idKendaraan;
    private LocalDate tanggalPinjam;
    private LocalDate tanggalKembaliRencana;
    private LocalDate tanggalKembaliAktual; // null kalau belum dikembalikan
    private StatusPeminjaman status;
    private long denda;

    public Peminjaman(String id, String idUser, String idKendaraan,
                      LocalDate tanggalPinjam, LocalDate tanggalKembaliRencana) {
        Objects.requireNonNull(id, "ID tidak boleh null");
        Objects.requireNonNull(idUser, "ID user tidak boleh null");
        Objects.requireNonNull(idKendaraan, "ID kendaraan tidak boleh null");
        Objects.requireNonNull(tanggalPinjam, "Tanggal pinjam tidak boleh null");
        Objects.requireNonNull(tanggalKembaliRencana, "Tanggal kembali rencana tidak boleh null");

        if (id.isBlank())          throw new IllegalArgumentException("ID tidak boleh kosong");
        if (idUser.isBlank())      throw new IllegalArgumentException("ID user tidak boleh kosong");
        if (idKendaraan.isBlank()) throw new IllegalArgumentException("ID kendaraan tidak boleh kosong");
        if (!tanggalKembaliRencana.isAfter(tanggalPinjam)) {
            throw new IllegalArgumentException(
                "Tanggal kembali rencana harus setelah tanggal pinjam"
            );
        }

        this.id = id;
        this.idUser = idUser;
        this.idKendaraan = idKendaraan;
        this.tanggalPinjam = tanggalPinjam;
        this.tanggalKembaliRencana = tanggalKembaliRencana;
        this.tanggalKembaliAktual = null;
        this.status = StatusPeminjaman.AKTIF;
        this.denda = 0;
    }

    // Getters
    public String getId() { return id; }
    public String getIdUser() { return idUser; }
    public String getIdKendaraan() { return idKendaraan; }
    public LocalDate getTanggalPinjam() { return tanggalPinjam; }
    public LocalDate getTanggalKembaliRencana() { return tanggalKembaliRencana; }
    public Optional<LocalDate> getTanggalKembaliAktual() { return Optional.ofNullable(tanggalKembaliAktual); }
    public StatusPeminjaman getStatus() { return status; }
    public long getDenda() { return denda; }

    // Setters
    public void setTanggalKembaliAktual(LocalDate tanggalKembaliAktual) {
        this.tanggalKembaliAktual = tanggalKembaliAktual;
    }
    public void setStatus(StatusPeminjaman status) {
        Objects.requireNonNull(status, "Status tidak boleh null"); 
        this.status = status; 
    }
    public void setDenda(long denda) {
        if (denda < 0) {
            throw new IllegalArgumentException(
                "Denda tidak boleh negatif, diterima: " + denda
            );
        } 
        this.denda = denda; 
    }

    public boolean isAktif() {
        return this.status == StatusPeminjaman.AKTIF;
    }

    public void selesaikan(LocalDate tanggalKembaliAktual, long denda) {
        Objects.requireNonNull(tanggalKembaliAktual, "Tanggal kembali aktual tidak boleh null");
        if (denda < 0) throw new IllegalArgumentException("Denda tidak boleh negatif");
        if (this.status == StatusPeminjaman.SELESAI) {
            throw new IllegalStateException("Peminjaman sudah selesai sebelumnya");
        }

        this.tanggalKembaliAktual = tanggalKembaliAktual;
        this.denda = denda;
        this.status = StatusPeminjaman.SELESAI;
    }

    @Override
    public String toString() {
        return String.format("[%s] User: %s | Kendaraan: %s | Pinjam: %s | Rencana Kembali: %s | Status: %s | Denda: Rp%,d",
                id, idUser, idKendaraan, tanggalPinjam, tanggalKembaliRencana, status, denda);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Peminjaman)) return false;
        Peminjaman that = (Peminjaman) o;
        return Objects.equals(id, that.id)
            && Objects.equals(idUser, that.idUser)
            && Objects.equals(idKendaraan, that.idKendaraan)
            && Objects.equals(tanggalPinjam, that.tanggalPinjam);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idUser, idKendaraan, tanggalPinjam);
    }
}