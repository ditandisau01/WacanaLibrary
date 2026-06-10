package id.ac.ukdw.rplbo.wacanalibrary.controllers;

import id.ac.ukdw.rplbo.wacanalibrary.utils.AnggotaSession;
import id.ac.ukdw.rplbo.wacanalibrary.utils.DatabaseHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;

public class LoginController {

    private final id.ac.ukdw.rplbo.wacanalibrary.dao.PegawaiDao pegawaiDao = new id.ac.ukdw.rplbo.wacanalibrary.dao.impl.PegawaiDaoImpl();
    private final id.ac.ukdw.rplbo.wacanalibrary.dao.AnggotaDao anggotaDao = new id.ac.ukdw.rplbo.wacanalibrary.dao.impl.AnggotaDaoImpl();

    @FXML private TextField txtUsername; // Digunakan sebagai NIM atau Username Admin
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;

    @FXML
    private void handleLogin() {
        String inputId = txtUsername.getText().trim();
        String password = txtPassword.getText();

        if (inputId.isEmpty() || password.isEmpty()) {
            tampilkanError("NIM/Identitas dan Password tidak boleh kosong!");
            return;
        }

        // 1. Validasi Admin
        if (validasiAdmin(inputId, password)) {
            bukaDashboard("/fxml/MainLayout.fxml", "Wacana Library - Dashboard Operasional");
            return;
        }

        // 2. Validasi Anggota berdasarkan NIM/NIDN/NIS
        if (validasiAnggota(inputId, password)) {
            bukaDashboard("/fxml/AnggotaMainLayout.fxml", "Wacana Library - Portal Anggota");
            return;
        }

        tampilkanError("NIM atau password salah!");
    }

    private boolean validasiAdmin(String username, String password) {
        id.ac.ukdw.rplbo.wacanalibrary.models.Pegawai pegawai = pegawaiDao.getPegawaiByUsernameAndPassword(username, password);
        return pegawai != null;
    }

    private boolean validasiAnggota(String inputId, String password) {
        id.ac.ukdw.rplbo.wacanalibrary.models.Anggota anggota = anggotaDao.getAnggotaByNimAndPassword(inputId, password);
        if (anggota != null) {
            if (anggota.statusProperty().get().equalsIgnoreCase("Tidak Aktif")) {
                tampilkanError("Akun anggota Anda sedang dinonaktifkan.");
                return false;
            }
            AnggotaSession.setAnggota(anggota.idAnggotaProperty().get(), anggota.namaLengkapProperty().get());
            return true;
        }
        return false;
    }

    private void bukaDashboard(String fxml, String title) {
        try {
            Stage mainStage = new Stage();
            mainStage.setTitle(title);
            mainStage.setScene(new Scene(FXMLLoader.load(getClass().getResource(fxml))));
            mainStage.setMaximized(true);
            mainStage.show();
            ((Stage) txtUsername.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
            tampilkanError("Terjadi kesalahan memuat dashboard.");
        }
    }

    private void tampilkanError(String pesan) {
        lblError.setText(pesan);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }
}
