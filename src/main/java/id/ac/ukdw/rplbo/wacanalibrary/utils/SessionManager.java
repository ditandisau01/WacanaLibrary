package id.ac.ukdw.rplbo.wacanalibrary.utils;

public class SessionManager {
    private static String idPengguna;
    private static String namaLengkap;
    private static String role; // "Admin" atau "Anggota"

    public static void setSession(String id, String nama, String r) {
        idPengguna = id;
        namaLengkap = nama;
        role = r;
    }

    public static String getIdPengguna() { return idPengguna; }
    public static String getNamaLengkap() { return namaLengkap; }
    public static String getRole() { return role; }

    public static void clear() {
        idPengguna = null;
        namaLengkap = null;
        role = null;
    }
}