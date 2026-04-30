package model;

import java.util.Objects;

public class User {
    private final String id;
    private String nama;
    private String noTelepon;

    public User(String id, String nama, String noTelepon) {
        Objects.requireNonNull(id, "ID tidak boleh null");
        Objects.requireNonNull(nama, "Nama tidak boleh null");
        Objects.requireNonNull(noTelepon, "Nomor telepon tidak boleh null");

        if (id.isBlank())        throw new IllegalArgumentException("ID tidak boleh kosong");
        if (nama.isBlank())      throw new IllegalArgumentException("Nama tidak boleh kosong");
        if (noTelepon.isBlank()) throw new IllegalArgumentException("Nomor telepon tidak boleh kosong");

        this.id = id;
        this.nama = nama;
        this.noTelepon = noTelepon;
    }

    // Getters
    public String getId() { return id; }
    public String getNama() { return nama; }
    public String getNoTelepon() { return noTelepon; }

    // Setters
    public void setNama(String nama) { 
        Objects.requireNonNull(nama, "Nama tidak boleh null");
        if (nama.isBlank()) throw new IllegalArgumentException("Nama tidak boleh kosong");

        this.nama = nama; 
    }
    public void setNoTelepon(String noTelepon) { 
        Objects.requireNonNull(noTelepon, "Nomor telepon tidak boleh null");
        if (noTelepon.isBlank()) throw new IllegalArgumentException("Nomor telepon tidak boleh kosong");

        this.noTelepon = noTelepon; 
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s", id, nama, noTelepon);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User that = (User) o;
        return Objects.equals(id, that.id)
            && Objects.equals(nama, that.nama)
            && Objects.equals(noTelepon, that.noTelepon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nama, noTelepon);
    }
}