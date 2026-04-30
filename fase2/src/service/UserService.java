package service;

import database.DataLoader;
import model.User;
import util.InputHelper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UserService {
    private final List<User> users;

    public UserService(List<User> users) {
        Objects.requireNonNull(users, "Daftar user tidak boleh null");

        this.users = users;
    }

    public void tampilkanSemuaUser() {
        System.out.println("\n========== DAFTAR USER ==========");
        if (users.isEmpty()) {
            System.out.println("Belum ada user terdaftar.");
            return;
        }
        for (User u : users) {
            System.out.println("  " + u);
        }
    }

    public Optional<User> cariUserById(String id) {
        Objects.requireNonNull(id, "ID tidak boleh null");

        return users.stream()
            .filter(u -> u.getId().equalsIgnoreCase(id))
            .findFirst();
    }

    public void tambahUser() {
        System.out.println("\n--- Tambah User Baru ---");
        String nama = InputHelper.inputString("Nama       : ");
        String telp = InputHelper.inputString("No. Telepon: ");

        boolean sudahAda = users.stream()
            .anyMatch(u -> u.getNoTelepon().equals(telp));
        if (sudahAda) {
            System.out.println("Tidak dapat menambahkan user baru. Nomor telepon sudah terdaftar.");
            return;
        }

        if (!telp.matches("^08\\d{8,11}$")) {
            System.out.println("Tidak dapat menambahkan user baru. Format nomor telepon tidak valid.\nContoh nomor telepon valid adalah 081234678");
            return;
        }

        String id = generateId();
        User user = new User(id, nama, telp);
        users.add(user);
        DataLoader.simpanUser(users);
        System.out.println("User berhasil ditambahkan dengan ID: " + id);
    }

    public boolean hapusUser(String id) {
        User target = cariUserById(id).orElse(null);
        if (target == null) return false;

        users.remove(target);
        DataLoader.simpanUser(users);
        return true;
    }

    public List<User> getDaftarUser() {
        return Collections.unmodifiableList(users);
    }

    private String generateId() {
        int max = users.stream()
            .map(u -> u.getId().replace("U", ""))
            .mapToInt(s -> {
                try { return Integer.parseInt(s); }
                catch (NumberFormatException e) {
                    System.err.println("Peringatan: ID tidak standar: " + s);
                    return 0;
                }
            })
            .max()
            .orElse(0);
        return String.format("U%03d", max + 1);
    }
}