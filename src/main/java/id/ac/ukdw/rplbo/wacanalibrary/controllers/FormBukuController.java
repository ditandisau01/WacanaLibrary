package id.ac.ukdw.rplbo.wacanalibrary.controllers;

import id.ac.ukdw.rplbo.wacanalibrary.models.Buku;
import id.ac.ukdw.rplbo.wacanalibrary.dao.BukuDao;
import id.ac.ukdw.rplbo.wacanalibrary.dao.impl.BukuDaoImpl;
import id.ac.ukdw.rplbo.wacanalibrary.utils.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;

public class FormBukuController {

    private final BukuDao bukuDao = new BukuDaoImpl();
    private Buku bukuEdit;

    @FXML private TextField txtJudul, txtIsbn, txtPenulis, txtTahun, txtHalaman, txtGambar;
    @FXML private ComboBox<String> cbKategori, cbStatus;

    private boolean isEditMode = false;
    private String idBukuEdit = "";

    @FXML
    public void initialize() {
        cbKategori.setItems(FXCollections.observableArrayList(
                "Karya Umum / Referensi",
                "Ilmu Murni / Sains",
                "Teknologi / Ilmu Terapan",
                "Ilmu Sosial",
                "Bahasa",
                "Sastra",
                "Sejarah & Geografi",
                "Seni & Rekreasi"
        ));
        cbStatus.setItems(FXCollections.observableArrayList("Tersedia", "Dipinjam", "Rusak"));
        cbStatus.setValue("Tersedia");
    }

    public void setEditData(Buku buku) {
        this.isEditMode = true;
        this.bukuEdit = buku;
        this.idBukuEdit = buku.idBukuProperty().get();

        txtJudul.setText(buku.judulProperty().get());
        txtIsbn.setText(buku.getIsbn());
        txtPenulis.setText(buku.pengarangProperty().get());
        cbKategori.setValue(buku.kategoriProperty().get());
        txtTahun.setText(String.valueOf(buku.tahunTerbitProperty().get()));
        txtHalaman.setText(String.valueOf(buku.halamanProperty().get()));
        txtGambar.setText(buku.getGambar());
        cbStatus.setValue(buku.statusProperty().get());
    }

    @FXML
    private void handlePilihGambar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Gambar Sampul Buku");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(txtJudul.getScene().getWindow());
        if (file != null) {
            txtGambar.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void handleSimpan() {
        try {
            if (isEditMode) {
                Buku buku = new Buku(
                        idBukuEdit,
                        txtIsbn.getText(),
                        txtJudul.getText(),
                        txtPenulis.getText(),
                        bukuEdit.tipeProperty().get(),
                        cbKategori.getValue(),
                        Integer.parseInt(txtTahun.getText()),
                        Integer.parseInt(txtHalaman.getText()),
                        txtGambar.getText(),
                        cbStatus.getValue()
                );
                bukuDao.updateBuku(buku);
            } else {
                String idBuku = "B-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
                Buku buku = new Buku(
                        idBuku,
                        txtIsbn.getText(),
                        txtJudul.getText(),
                        txtPenulis.getText(),
                        "Buku",
                        cbKategori.getValue(),
                        Integer.parseInt(txtTahun.getText()),
                        Integer.parseInt(txtHalaman.getText()),
                        txtGambar.getText(),
                        cbStatus.getValue()
                );
                bukuDao.tambahBuku(buku);
            }
            tutupJendela();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Gagal menyimpan data buku. Pastikan form diisi dengan benar.");
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
