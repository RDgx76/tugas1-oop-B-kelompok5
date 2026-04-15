package model;

public class Motor extends Kendaraan {
    private int cc;
    private String jenisMotor; // "MATIC", "MANUAL", "SPORT"

    public Motor(String id, String nama, String platNomor, double tarifPerHari,
                 int cc, String jenisMotor) {
        super(id, nama, platNomor, tarifPerHari);
        this.cc = cc;
        this.jenisMotor = jenisMotor;
    }

    @Override
    public String getJenis() {
        return "Motor";
    }

    // Getters
    public int getCc() { return cc; }
    public String getJenisMotor() { return jenisMotor; }

    // Setters
    public void setCc(int cc) { this.cc = cc; }
    public void setJenisMotor(String jenisMotor) { this.jenisMotor = jenisMotor; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | %d CC | %s", cc, jenisMotor);
    }
}