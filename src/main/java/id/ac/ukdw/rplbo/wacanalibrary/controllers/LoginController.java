package id.ac.ukdw.rplbo.wacanalibrary.controllers;

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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            tampilkanError("Username dan Password tidak boleh kosong!");
            return;
        }

        if (validasiDatabase(username, password)) {
            bukaDashboardUtama();
        } else {
            tampilkanError("Username atau password salah!");
        }
    }

    private boolean validasiDatabase(String username, String password) {
        String query = "SELECT * FROM Pegawai WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            return rs.next(); // Jika ada data, login berhasil
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void tampilkanError(String pesan) {
        lblError.setText(pesan);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }

    private void bukaDashboardUtama() {
        try {
            // Ambil jendela (Stage) saat ini (Login), lalu tutup
            Stage stageSaatIni = (Stage) txtUsername.getScene().getWindow();
            stageSaatIni.close();

            // Muat file MainLayout.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainLayout.fxml"));
            Parent root = loader.load();

            // Buat jendela (Stage) baru untuk Dashboard utama
            Stage mainStage = new Stage();
            mainStage.setTitle("Wacana Library - Dashboard Operasional");

            // Atur Scene
            mainStage.setScene(new Scene(root));

            // PENTING: Urutan ini yang membuat tampilan langsung penuh (Full Screen/Maximized)
            // tanpa menyisakan ruang kosong di pinggir layar
            mainStage.show();           // 1. Tampilkan dulu ke layar
            mainStage.setMaximized(true); // 2. Perintahkan untuk langsung layar penuh

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}