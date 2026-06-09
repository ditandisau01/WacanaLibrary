package id.ac.ukdw.rplbo.wacanalibrary.dao.impl;

import id.ac.ukdw.rplbo.wacanalibrary.dao.BukuDao;
import id.ac.ukdw.rplbo.wacanalibrary.models.Buku;
import id.ac.ukdw.rplbo.wacanalibrary.utils.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BukuDaoImpl implements BukuDao {

    @Override
    public List<Buku> getAllBuku() {
        List<Buku> listBuku = new ArrayList<>();
        String query = "SELECT * FROM Buku ORDER BY judul ASC";

        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                listBuku.add(mapResultSetToBuku(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return listBuku;
    }

    @Override
    public Buku getBukuById(String idBuku) {
        String query = "SELECT * FROM Buku WHERE idBuku = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idBuku);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToBuku(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public void tambahBuku(Buku buku) {
        String query = "INSERT INTO Buku (idBuku, isbn, judul, pengarang, tipe, kategori, tahunTerbit, halaman, gambar, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            setStatementParams(ps, buku);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void updateBuku(Buku buku) {
        String query = "UPDATE Buku SET isbn=?, judul=?, pengarang=?, tipe=?, kategori=?, tahunTerbit=?, halaman=?, gambar=?, status=? " +
                "WHERE idBuku=?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, buku.isbnProperty().get());
            ps.setString(2, buku.judulProperty().get());
            ps.setString(3, buku.pengarangProperty().get());
            ps.setString(4, buku.tipeProperty().get());
            ps.setString(5, buku.kategoriProperty().get());
            ps.setInt(6, buku.tahunTerbitProperty().get());
            ps.setInt(7, buku.halamanProperty().get());
            ps.setString(8, buku.gambarProperty().get());
            ps.setString(9, buku.statusProperty().get());
            ps.setString(10, buku.idBukuProperty().get()); // WHERE idBuku
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void hapusBuku(String idBuku) {
        String query = "DELETE FROM Buku WHERE idBuku = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idBuku);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public List<Buku> cariBuku(String keyword) {
        List<Buku> list = new ArrayList<>();
        String query = "SELECT * FROM Buku WHERE judul LIKE ? OR pengarang LIKE ? OR isbn LIKE ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            String likeKey = "%" + keyword + "%";
            ps.setString(1, likeKey); ps.setString(2, likeKey); ps.setString(3, likeKey);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToBuku(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private Buku mapResultSetToBuku(ResultSet rs) throws SQLException {
        return new Buku(
                rs.getString("idBuku"), rs.getString("isbn"), rs.getString("judul"),
                rs.getString("pengarang"), rs.getString("tipe"), rs.getString("kategori"),
                rs.getInt("tahunTerbit"), rs.getInt("halaman"), rs.getString("gambar"),
                rs.getString("status")
        );
    }

    private void setStatementParams(PreparedStatement ps, Buku b) throws SQLException {
        ps.setString(1, b.idBukuProperty().get()); ps.setString(2, b.isbnProperty().get());
        ps.setString(3, b.judulProperty().get());  ps.setString(4, b.pengarangProperty().get());
        ps.setString(5, b.tipeProperty().get());   ps.setString(6, b.kategoriProperty().get());
        ps.setInt(7, b.tahunTerbitProperty().get()); ps.setInt(8, b.halamanProperty().get());
        ps.setString(9, b.gambarProperty().get()); ps.setString(10, b.statusProperty().get());
    }
}