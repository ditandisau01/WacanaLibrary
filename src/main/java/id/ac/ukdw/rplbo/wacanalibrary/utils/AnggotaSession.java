package id.ac.ukdw.rplbo.wacanalibrary.utils;

public class AnggotaSession {
    private static String idAnggota;
    private static String namaAnggota;

    public static void setAnggota(String id, String nama) {
        idAnggota = id;
        namaAnggota = nama;
    }

    public static String getIdAnggota() { return idAnggota; }
    public static String getNamaAnggota() { return namaAnggota; }

    public static void clear() {
        idAnggota = null;
        namaAnggota = null;
    }
}