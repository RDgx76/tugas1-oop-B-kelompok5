package model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public class Peminjaman {
    private final String id;
    private final String idUser;
    private final String idKendaraan;
    private final LocalDate tanggalPinjam;
    private final LocalDate tanggalKembaliRencana;
    private LocalDate tanggalKembaliAktual; // null kalau belum dikembalikan
    private StatusPeminjaman status;
    private long denda;

    public Peminjaman(String id, String idUser, String idKendaraan,
                      LocalDate tanggalPinjam, LocalDate tanggalKembaliRencana, LocalDate tanggalKembaliAktual, StatusPeminjaman status, long denda) {
        Objects.requireNonNull(id, "ID tidak boleh null");
        Objects.requireNonNull(idUser, "ID user tidak boleh null");
        Objects.requireNonNull(idKendaraan, "ID kendaraan tidak boleh null");
        Objects.requireNonNull(tanggalPinjam, "Tanggal pinjam tidak boleh null");
        Objects.requireNonNull(tanggalKembaliRencana, "Tanggal kembali rencana tidak boleh null");
        Objects.requireNonNull(status, "Status peminjaman tidak boleh null");


        if (id.isBlank())          throw new IllegalArgumentException("ID tidak boleh kosong");
        if (idUser.isBlank())      throw new IllegalArgumentException("ID user tidak boleh kosong");
        if (idKendaraan.isBlank()) throw new IllegalArgumentException("ID kendaraan tidak boleh kosong");
        if (!tanggalKembaliRencana.isAfter(tanggalPinjam)) {
            throw new IllegalArgumentException(
                "Tanggal kembali rencana harus setelah tanggal pinjam"
            );
        }
        if (denda < 0) {
            throw new IllegalArgumentException("Denda tidak boleh negatif");
        }
        // Validasi konsistensi status dengan field lain
        if (status == StatusPeminjaman.SELESAI && tanggalKembaliAktual == null) {
            throw new IllegalArgumentException(
                "Peminjaman SELESAI harus memiliki tanggal kembali aktual"
            );
        }
        if (status == StatusPeminjaman.AKTIF && tanggalKembaliAktual != null) {
            throw new IllegalArgumentException(
                "Peminjaman AKTIF tidak boleh memiliki tanggal kembali aktual"
            );
        }


        this.id = id;
        this.idUser = idUser;
        this.idKendaraan = idKendaraan;
        this.tanggalPinjam = tanggalPinjam;
        this.tanggalKembaliRencana = tanggalKembaliRencana;
        this.tanggalKembaliAktual = tanggalKembaliAktual;
        this.status = status;
        this.denda = denda;
    }

    public Peminjaman(String id, String idUser, String idKendaraan,
                      LocalDate tanggalPinjam, LocalDate tanggalKembaliRencana) {
        this(id, idUser, idKendaraan, tanggalPinjam, tanggalKembaliRencana, null, StatusPeminjaman.AKTIF, 0L);
                      
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
    private void setTanggalKembaliAktual(LocalDate tanggalKembaliAktual) {
        this.tanggalKembaliAktual = tanggalKembaliAktual;
    }
    private void setStatus(StatusPeminjaman status) {
        Objects.requireNonNull(status, "Status tidak boleh null"); 
        this.status = status; 
    }
    private void setDenda(long denda) {
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
       if (tanggalKembaliAktual.isBefore(tanggalPinjam)) {
            throw new IllegalArgumentException(
            "Tanggal kembali aktual tidak boleh sebelum tanggal pinjam"
            );
        }

        setTanggalKembaliAktual(tanggalKembaliAktual);
        setDenda(denda);
        setStatus(StatusPeminjaman.SELESAI);
    }

    @Override
    public String toString() {
        return String.format("[%s] User: %s | Kendaraan: %s | Pinjam: %s | Rencana Kembali: %s | Status: %s | Denda: Rp%,d",
                id, idUser, idKendaraan, tanggalPinjam, tanggalKembaliRencana, status.name(), denda);
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