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

    public Optional<User> cariById(String id) {
        Objects.requireNonNull(id, "ID tidak boleh null");

        return users.stream()
            .filter(u -> u.getId().equalsIgnoreCase(id))
            .findFirst();
    }

    public void tambahUser() {
        System.out.println("\n--- Tambah User Baru ---");
        String id = generateId();
        String nama = InputHelper.inputString("Nama       : ");
        String telp = InputHelper.inputString("No. Telepon: ");

        User user = new User(id, nama, telp);
        users.add(user);
        DataLoader.saveUser(users);
        System.out.println("User berhasil ditambahkan dengan ID: " + id);
    }

    public void hapusUser() {
        tampilkanSemuaUser();
        if (users.isEmpty()) return;

        String id = InputHelper.inputString("\nMasukkan ID user yang akan dihapus: ");
        User target = cariById(id).orElse(null);

        if (target == null) {
            System.out.println("User dengan ID " + id + " tidak ditemukan.");
            return;
        }

        users.remove(target);
        DataLoader.saveUser(users);
        System.out.println("User " + target.getNama() + " berhasil dihapus.");
    }

    public List<User> getUsers() {
        return Collections.unmodifiableList(users);
    }

    private String generateId() {
        int max = 0;
        for (User u : users) {
            try {
                int num = Integer.parseInt(u.getId().replace("U", ""));
                if (num > max) max = num;
            } catch (NumberFormatException e) {
                System.err.println("Peringatan: ID user tidak mengikuti format standar: " + u.getId());
            }
        }
        return String.format("U%03d", max + 1);
    }
}