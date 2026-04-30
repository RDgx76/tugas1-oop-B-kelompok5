# 🚗 Sistem Peminjaman Kendaraan

Aplikasi sistem peminjaman kendaraan berbasis console yang dibangun dengan Java. Proyek ini dibuat sebagai latihan konsep Object-Oriented Programming (OOP) dan tugas kuliah.

---

## 📋 Fitur

- Manajemen kendaraan (tambah, lihat, hapus)
- Manajemen user / peminjam
- Proses peminjaman dan pengembalian kendaraan
- Perhitungan denda keterlambatan otomatis
- Riwayat transaksi peminjaman
- Laporan peminjaman (console & file .txt)
- Penyimpanan data persisten menggunakan file JSON

---

## 🏗️ Struktur Direktori

```
vehicle-rental-system/
│
├── src/
│   ├── Main.java                       # Entry point & menu utama
│   │
│   ├── model/
│   │   ├── JsonSerializable.java       # Interface class JsonSerializable
│   │   ├── Kendaraan.java              # Abstract class kendaraan
│   │   ├── Mobil.java                  # Subclass mobil
│   │   ├── Motor.java                  # Subclass motor
│   │   ├── User.java                   # Data peminjam
│   │   ├── Peminjaman.java             # Record transaksi
│   │   ├── JenisKendaraan.java         # Enum untuk jenis kendaraan
│   │   ├── JenisMotor.java             # Enum untuk jenis motor
│   │   ├── Trasmisi.java               # Enum untuk transmisi
│   │   ├── StatusKendaraan.java        # Enum untuk status kendaraan
│   │   └── StatusPeminjaman.java       # Enum untuk status peminjaman     
│   │
│   ├── service/
│   │   ├── PeminjamanService.java      # Logika bisnis peminjaman
│   │   ├── UserService.java            # Logika bisnis user
│   │   └── DendaCalculator.java        # Perhitungan denda
│   │
│   ├── database/
│   │   ├── FileDatabase.java           # Baca/tulis file JSON
│   │   └── DataLoader.java             # Load data saat startup
│   │
│   ├── report/
│   │   └── LaporanGenerator.java       # Generate laporan
│   │
│   └── util/
│       ├── InputHelper.java            # Wrapper input Scanner
│       ├── ConsoleUtil.java            # Utilitas console
│       └── DateUtil.java               # Utilitas tanggal
│
└── data/
    ├── kendaraan.json                  # Data semua kendaraan
    ├── user.json                       # Data semua user
    └── peminjaman.json                 # Riwayat transaksi
```

---

## 🧱 Konsep OOP yang Digunakan

| Konsep | Implementasi |
|--------|-------------|
| **Inheritance** | `Mobil` dan `Motor` extends `Kendaraan`; constructor chaining via `super()` memastikan validasi terpusat di parent class |
| **Abstraction** | `Kendaraan` sebagai abstract class dengan method abstract `getJenis()`; interface `JsonSerializable` sebagai kontrak serialisasi; enum menyembunyikan nilai konstanta (`StatusKendaraan`, `StatusPeminjaman`, `Transmisi`, dll) |
| **Encapsulation** | Field private dengan akses terkontrol melalui method bisnis (`pinjam()`, `kembalikan()`, `selesaikan()`); setter dijadikan `private` atau dihapus; collection dikembalikan sebagai `unmodifiableList` |
| **Polymorphism** | Method `getJenis()`, `toJsonMap()`, dan `toString()` di-override di `Mobil` dan `Motor` dan dipanggil dari referensi `Kendaraan`; interface `JsonSerializable` memungkinkan polimorfisme lintas hierarki |
| **Separation of Concerns** | Model, Service, Database, Report, dan UI dipisah per lapisan; dependency mengalir satu arah, maksudnya Database bergantung ke Model via interface, bukan sebaliknya |

---

## 🔄 Arsitektur Lapisan

```
[ Console Input/Output ]   ← Main.java, Menu
         ↓
[ Business Logic       ]   ← service/
         ↓
[ Data Access          ]   ← database/
         ↓
[ File System          ]   ← data/*.json
```

Setiap lapisan hanya berkomunikasi dengan lapisan di bawahnya secara langsung.

---

## 🚀 Cara Menjalankan

### Prasyarat
- Java JDK 11 atau lebih baru
- Terminal / Command Prompt

### Kompilasi

```bash
# Masuk ke direktori proyek (sesuaikan dengan lokasi folder kamu)
cd path/to/Sistem-Peminjaman-Kendaraan

# Buat folder output
mkdir -p out

# Kompilasi semua file Java
javac -d out -sourcepath src src/Main.java src/model/*.java src/service/*.java src/database/*.java src/report/*.java src/util/*.java

# Jalankan
java -cp out Main
```

### Jalankan

```bash
java -cp out Main
```

---

## 💾 Format Data JSON

### kendaraan.json
```json
[
  {
    "id": "K001",
    "nama": "Toyota Avanza",
    "platNomor": "B 1234 ABC",
    "jenis": "Mobil",
    "status": "TERSEDIA",
    "tarifPerHari": 300000
  }
]
```

### user.json
```json
[
  {
    "id": "U001",
    "nama": "Budi Santoso",
    "noTelepon": "08123456789"
  }
]
```

### peminjaman.json
```json
[
  {
    "id": "P001",
    "idUser": "U001",
    "idKendaraan": "K001",
    "tanggalPinjam": "2024-01-15",
    "tanggalKembaliRencana": "2024-01-17",
    "tanggalKembaliAktual": "2024-01-18",
    "status": "SELESAI",
    "denda": 50000
  }
]
```

---

## 📌 Alur Proses Peminjaman

```
1. User memilih menu "Pinjam Kendaraan"
2. Input ID user
3. Sistem menampilkan daftar kendaraan yang tersedia
4. User memilih kendaraan dan memasukkan durasi pinjam
5. Sistem membuat record Peminjaman baru
6. Status kendaraan diubah → "DIPINJAM"
7. Data disimpan ke peminjaman.json & kendaraan.json
```

## 📌 Alur Proses Pengembalian

```
1. User memilih menu "Kembalikan Kendaraan"
2. Input ID peminjaman aktif
3. Sistem menghitung denda jika terlambat
4. Status kendaraan diubah → "TERSEDIA"
5. Record peminjaman diupdate → "SELESAI"
6. Data disimpan ke file JSON
```

---

## ⚠️ Catatan Pengembangan

- File JSON di folder `data/` dibuat otomatis saat pertama kali program dijalankan jika belum ada.
- Denda dihitung berdasarkan keterlambatan per hari dari tanggal rencana kembali.
- Semua input user divalidasi sebelum diproses.
- Belum ada fitur edit informasi pada program ini.

---

## 👨‍💻 Teknologi

- **Bahasa:** Java
- **Interface:** Console / Terminal
- **Penyimpanan:** File JSON (tanpa library eksternal)
- **JDK:** 11+
