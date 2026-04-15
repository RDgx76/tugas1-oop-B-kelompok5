package model;

public class User {
    private String id;
    private String nama;
    private String noTelepon;

    public User(String id, String nama, String noTelepon) {
        this.id = id;
        this.nama = nama;
        this.noTelepon = noTelepon;
    }

    // Getters
    public String getId() { return id; }
    public String getNama() { return nama; }
    public String getNoTelepon() { return noTelepon; }

    // Setters
    public void setNama(String nama) { this.nama = nama; }
    public void setNoTelepon(String noTelepon) { this.noTelepon = noTelepon; }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s", id, nama, noTelepon);
    }
}