package model;

import java.util.Map;

public interface JsonSerializable {
    /**
     * Mengubah objek menjadi Map key-value untuk keperluan serialisasi.
     * Implementasi tidak boleh bergantung pada format penyimpanan apapun.
     *
     * @return Map berisi data objek dalam bentuk String
     */
    Map<String, String> toJsonMap();
}