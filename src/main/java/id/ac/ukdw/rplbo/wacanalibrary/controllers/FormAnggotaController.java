package id.ac.ukdw.rplbo.wacanalibrary.controllers;

import id.ac.ukdw.rplbo.wacanalibrary.models.Anggota;
import id.ac.ukdw.rplbo.wacanalibrary.dao.AnggotaDao;
import id.ac.ukdw.rplbo.wacanalibrary.dao.impl.AnggotaDaoImpl;
import id.ac.ukdw.rplbo.wacanalibrary.utils.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;

public class FormAnggotaController {

    private final AnggotaDao anggotaDao = new AnggotaDaoImpl();
    private Anggota anggotaEdit;

    @FXML private TextField txtNama;
    @FXML private ComboBox<String> cbTipe, cbStatus;

    private boolean isEditMode = false;
    private String idAnggotaEdit = "";

    @FXML
    public void initialize() {
        cbTipe.setItems(FXCollections.observableArrayList("Mahasiswa", "Dosen", "Staff"));
        cbStatus.setItems(FXCollections.observableArrayList("Aktif", "Tidak Aktif"));
        cbStatus.setValue("Aktif");
    }

    public void setEditData(Anggota anggota) {
        this.isEditMode = true;
        this.anggotaEdit = anggota;
        this.idAnggotaEdit = anggota.idAnggotaProperty().get();

        txtNama.setText(anggota.namaLengkapProperty().get());
        cbTipe.setValue(anggota.tipeProperty().get());
        cbStatus.setValue(anggota.statusProperty().get());
    }

    @FXML
    private void handleSimpan() {
        int batasPinjam = 5;
        if (cbTipe.getValue() != null && cbTipe.getValue().equals("Dosen")) batasPinjam = 10;
        else if (cbTipe.getValue() != null && cbTipe.getValue().equals("Staff")) batasPinjam = 10;

        try {
            if (isEditMode) {
                Anggota anggota = new Anggota(
                        idAnggotaEdit,
                        anggotaEdit.nimProperty().get(),
                        txtNama.getText(),
                        anggotaEdit.passwordProperty().get(),
                        cbTipe.getValue(),
                        batasPinjam,
                        anggotaEdit.aktifSejakProperty().get(),
                        cbStatus.getValue()
                );
                anggotaDao.updateAnggota(anggota);
            } else {
                String idAnggota = "LIB-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
                String tanggalSekarang = java.time.LocalDate.now().toString();

                Anggota anggota = new Anggota(
                        idAnggota,
                        null,
                        txtNama.getText(),
                        null,
                        cbTipe.getValue(),
                        batasPinjam,
                        tanggalSekarang,
                        cbStatus.getValue()
                );
                anggotaDao.tambahAnggota(anggota);
            }
            tutupJendela();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBatal() {
        tutupJendela();
    }

    private void tutupJendela() {
        Stage stage = (Stage) txtNama.getScene().getWindow();
        stage.close();
    }
}
