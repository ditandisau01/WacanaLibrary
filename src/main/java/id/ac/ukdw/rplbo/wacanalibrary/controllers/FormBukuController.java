package id.ac.ukdw.rplbo.wacanalibrary.controllers;

import id.ac.ukdw.rplbo.wacanalibrary.models.Buku;
import id.ac.ukdw.rplbo.wacanalibrary.utils.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;

public class FormBukuController {

    // Menambahkan txtHalaman
    @FXML private TextField txtJudul, txtPenulis, txtTahun, txtHalaman;
    @FXML private ComboBox<String> cbKategori, cbStatus;

    private boolean isEditMode = false;
    private String idBukuEdit = "";

    @FXML
    public void initialize() {
        cbKategori.setItems(FXCollections.observableArrayList("Technology", "History", "Science", "Arts", "Fiction", "Urbanism", "Modernism", "Postmodern", "Sastra"));
        cbStatus.setItems(FXCollections.observableArrayList("Tersedia", "Dipinjam", "Rusak"));
        cbStatus.setValue("Tersedia");
    }

    public void setEditData(Buku buku) {
        this.isEditMode = true;
        this.idBukuEdit = buku.idBukuProperty().get();

        txtJudul.setText(buku.judulProperty().get());
        txtPenulis.setText(buku.pengarangProperty().get());
        cbKategori.setValue(buku.kategoriProperty().get());
        txtTahun.setText(String.valueOf(buku.tahunTerbitProperty().get()));

        // Memasukkan data halaman ke form saat mode edit
        txtHalaman.setText(String.valueOf(buku.halamanProperty().get()));

        cbStatus.setValue(buku.statusProperty().get());
    }

    @FXML
    private void handleSimpan() {
        try (Connection conn = DatabaseHelper.getConnection()) {
            PreparedStatement pstmt;

            if (isEditMode) {
                // UPDATE Kueri (Menambahkan parameter halaman)
                String query = "UPDATE Buku SET judul = ?, pengarang = ?, kategori = ?, tahunTerbit = ?, halaman = ?, status = ? WHERE idBuku = ?";
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, txtJudul.getText());
                pstmt.setString(2, txtPenulis.getText());
                pstmt.setString(3, cbKategori.getValue());
                pstmt.setInt(4, Integer.parseInt(txtTahun.getText()));
                pstmt.setInt(5, Integer.parseInt(txtHalaman.getText())); // Parameter Halaman
                pstmt.setString(6, cbStatus.getValue());
                pstmt.setString(7, idBukuEdit);
            } else {
                // INSERT Kueri (Menambahkan parameter halaman)
                String idBuku = "B-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
                String query = "INSERT INTO Buku (idBuku, judul, pengarang, tipe, kategori, tahunTerbit, halaman, status) VALUES (?, ?, ?, 'Buku', ?, ?, ?, ?)";
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, idBuku);
                pstmt.setString(2, txtJudul.getText());
                pstmt.setString(3, txtPenulis.getText());
                pstmt.setString(4, cbKategori.getValue());
                pstmt.setInt(5, Integer.parseInt(txtTahun.getText()));
                pstmt.setInt(6, Integer.parseInt(txtHalaman.getText())); // Parameter Halaman
                pstmt.setString(7, cbStatus.getValue());
            }

            pstmt.executeUpdate();
            tutupJendela();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Gagal menyimpan data buku. Pastikan tahun dan halaman berisi angka valid.");
        }
    }

    @FXML
    private void handleBatal() {
        tutupJendela();
    }

    private void tutupJendela() {
        Stage stage = (Stage) txtJudul.getScene().getWindow();
        stage.close();
    }
}