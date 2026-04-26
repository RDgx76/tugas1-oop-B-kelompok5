package service;

import database.DataLoader;
import model.User;
import util.InputHelper;

import java.util.List;

public class UserService {
    private List<User> users;

    public UserService(List<User> users) {
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

    public User cariById(String id) {
        for (User u : users) {
            if (u.getId().equalsIgnoreCase(id)) return u;
        }
        return null;
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
        User target = cariById(id);

        if (target == null) {
            System.out.println("User dengan ID " + id + " tidak ditemukan.");
            return;
        }

        users.remove(target);
        DataLoader.saveUser(users);
        System.out.println("User " + target.getNama() + " berhasil dihapus.");
    }

    public List<User> getUsers() {
        return users;
    }

    private String generateId() {
        int max = 0;
        for (User u : users) {
            try {
                int num = Integer.parseInt(u.getId().replace("U", ""));
                if (num > max) max = num;
            } catch (NumberFormatException ignored) {}
        }
        return String.format("U%03d", max + 1);
    }
}