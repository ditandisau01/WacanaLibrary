package id.ac.ukdw.rplbo.wacanalibrary.utils;

import java.sql.*;

public class DatabaseHelper {
    private static final String URL = "jdbc:sqlite:wacanalibrary.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS Pegawai (" +
                    "idPegawai TEXT PRIMARY KEY, username TEXT, password TEXT, role TEXT)");

            // TAMBAHAN: Kolom username untuk Anggota
            stmt.execute("CREATE TABLE IF NOT EXISTS Anggota (" +
                    "idAnggota TEXT PRIMARY KEY, nim TEXT UNIQUE, username TEXT UNIQUE, " +
                    "namaLengkap TEXT, password TEXT, tipe TEXT, batasPinjam INTEGER, " +
                    "aktifSejak TEXT, status TEXT)");

            stmt.execute("CREATE TABLE IF NOT EXISTS Buku (" +
                    "idBuku TEXT PRIMARY KEY, isbn TEXT, judul TEXT, pengarang TEXT, " +
                    "tipe TEXT, kategori TEXT, tahunTerbit INTEGER, halaman INTEGER, " +
                    "gambar TEXT, status TEXT)");

            stmt.execute("CREATE TABLE IF NOT EXISTS Transaksi (" +
                    "idTransaksi TEXT PRIMARY KEY, idAnggota TEXT, idPegawai TEXT, " +
                    "tanggalPinjam TEXT, tanggalJatuhTempo TEXT, tanggalKembali TEXT, " +
                    "totalDenda REAL, statusTransaksi TEXT, metodePembayaran TEXT)");

            stmt.execute("CREATE TABLE IF NOT EXISTS Transaksi_Buku (" +
                    "idTransaksi TEXT, idBuku TEXT, PRIMARY KEY (idTransaksi, idBuku), " +
                    "FOREIGN KEY(idTransaksi) REFERENCES Transaksi(idTransaksi), " +
                    "FOREIGN KEY(idBuku) REFERENCES Buku(idBuku))");

            stmt.execute("INSERT OR IGNORE INTO Pegawai (idPegawai, username, password, role) " +
                    "VALUES ('PEG-001', 'admin', 'admin123', 'Admin')");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}