package id.ac.ukdw.rplbo.wacanalibrary.utils;

import java.sql.*;

public class DatabaseHelper {
    private static final String URL = "jdbc:sqlite:wacanalibrary.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            // 1. Tabel Pegawai (Untuk Autentikasi Login)
            stmt.execute("CREATE TABLE IF NOT EXISTS Pegawai (" +
                    "idPegawai TEXT PRIMARY KEY, " +
                    "username TEXT, " +
                    "password TEXT, " +
                    "role TEXT)");

            // 2. Tabel Anggota
            stmt.execute("CREATE TABLE IF NOT EXISTS Anggota (" +
                    "idAnggota TEXT PRIMARY KEY, " +
                    "namaLengkap TEXT, " +
                    "tipe TEXT, " +
                    "batasPinjam INTEGER, " +
                    "aktifSejak TEXT, " +
                    "status TEXT)");

            // 3. Tabel Buku
            stmt.execute("CREATE TABLE IF NOT EXISTS Buku (" +
                    "idBuku TEXT PRIMARY KEY, " +
                    "judul TEXT, " +
                    "pengarang TEXT, " +
                    "tipe TEXT, " +
                    "kategori TEXT, " +
                    "tahunTerbit INTEGER, " +
                    "halaman INTEGER, " +
                    "status TEXT)");

            // 4. Tabel Transaksi
            stmt.execute("CREATE TABLE IF NOT EXISTS Transaksi (" +
                    "idTransaksi TEXT PRIMARY KEY, " +
                    "idAnggota TEXT, " +
                    "idPegawai TEXT, " +
                    "tanggalPinjam TEXT, " +
                    "tanggalJatuhTempo TEXT, " +
                    "tanggalKembali TEXT, " +
                    "totalDenda REAL, " +
                    "statusTransaksi TEXT, " +
                    "metodePembayaran TEXT)");

            // 5. Tabel Pivot Transaksi_Buku
            stmt.execute("CREATE TABLE IF NOT EXISTS Transaksi_Buku (" +
                    "idTransaksi TEXT, " +
                    "idBuku TEXT, " +
                    "PRIMARY KEY (idTransaksi, idBuku), " +
                    "FOREIGN KEY(idTransaksi) REFERENCES Transaksi(idTransaksi), " +
                    "FOREIGN KEY(idBuku) REFERENCES Buku(idBuku))");

            // Masukkan data dummy BUKU jika kosong
            ResultSet rsBuku = stmt.executeQuery("SELECT COUNT(*) AS total FROM Buku");
            if (rsBuku.next() && rsBuku.getInt("total") == 0) {
                stmt.execute("INSERT INTO Buku VALUES ('B-E1D7', 'Matahari', 'Tere Liye', 'Buku', 'Sastra', 2016, 400, 'Tersedia')");
                stmt.execute("INSERT INTO Buku VALUES ('B-A851', 'Kamus Besar Bahasa Indonesia', 'Kemendikbud', 'Buku', 'Karya Umum / Referensi', 2016, 1500, 'Rusak')");
                stmt.execute("INSERT INTO Buku VALUES ('B-A582', 'Ilmu Sosial Dasar', 'Elly M. Setiadi', 'Buku', 'Ilmu Sosial', 2010, 250, 'Tersedia')");
                stmt.execute("INSERT INTO Buku VALUES ('B-5853', 'Kamus Inggris Indonesia', 'John M. Echols', 'Buku', 'Bahasa', 2014, 600, 'Tersedia')");
                stmt.execute("INSERT INTO Buku VALUES ('B-C963', 'Fisika Dasar', 'Halliday & Resnick', 'Buku', 'Ilmu Murni / Sains', 2013, 800, 'Tersedia')");
                stmt.execute("INSERT INTO Buku VALUES ('B-D3AB', 'Dasar-Dasar Pemrograman', 'Abdul Kadir', 'Buku', 'Teknologi / Ilmu Terapan', 2015, 450, 'Tersedia')");
                stmt.execute("INSERT INTO Buku VALUES ('B-3D96', 'Musik', 'Andina Subarja', 'Buku', 'Seni & Rekreasi', 2025, 120, 'Tersedia')");
                stmt.execute("INSERT INTO Buku VALUES ('B-B8BD', 'Sejarah Nasional Indonesia', 'Marwati Djoened Poesponegoro', 'Buku', 'Sejarah & Geografi', 2008, 500, 'Tersedia')");
                stmt.execute("INSERT INTO Buku VALUES ('B-93DE', 'Laskar Pelangi', 'Andrea Hirata', 'Buku', 'Sastra', 2005, 529, 'Tersedia')");
                stmt.execute("INSERT INTO Buku VALUES ('B-1DBF', 'Bulan', 'Tere Liye', 'Buku', 'Sastra', 2015, 400, 'Tersedia')");
                stmt.execute("INSERT INTO Buku VALUES ('B-BAB2', 'Bumi', 'Tere Liye', 'Buku', 'Sastra', 2014, 440, 'Tersedia')");
                stmt.execute("INSERT INTO Buku VALUES ('B-40B9', 'Bintang', 'Tere Liye', 'Buku', 'Sastra', 2017, 392, 'Tersedia')");
                stmt.execute("INSERT INTO Buku VALUES ('B-3984', 'Hujan', 'Tere Liye', 'Buku', 'Sastra', 2016, 320, 'Tersedia')");
            }

            // Masukkan data dummy ANGGOTA jika kosong
            ResultSet rsAnggota = stmt.executeQuery("SELECT COUNT(*) AS total FROM Anggota");
            if (rsAnggota.next() && rsAnggota.getInt("total") == 0) {
                stmt.execute("INSERT INTO Anggota VALUES ('LIB-8492', 'Dita Pranata Tandisau', 'Mahasiswa', 5, '2021-08-12', 'Aktif')");

            }

            // Masukkan akun Pegawai default jika belum ada (agar bisa login)
            stmt.execute("INSERT OR IGNORE INTO Pegawai (idPegawai, username, password, role) " +
                    "VALUES ('PEG-001', 'admin', 'admin123', 'Admin')");

            System.out.println("Skema Database terintegrasi siap.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}