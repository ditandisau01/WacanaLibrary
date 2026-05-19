module id.ac.ukdw.rplbo.wacanalibrary {
    requires javafx.controls;
    requires javafx.fxml;

    // Membutuhkan library SQL untuk database SQLite
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    // Mengizinkan JavaFX untuk mengakses package utama
    opens id.ac.ukdw.rplbo.wacanalibrary to javafx.fxml;

    // MENGIZINKAN JAVAFX MENGAKSES FOLDER CONTROLLERS (Ini solusi dari error Anda)
    opens id.ac.ukdw.rplbo.wacanalibrary.controllers to javafx.fxml;

    exports id.ac.ukdw.rplbo.wacanalibrary;
    exports id.ac.ukdw.rplbo.wacanalibrary.controllers;
}