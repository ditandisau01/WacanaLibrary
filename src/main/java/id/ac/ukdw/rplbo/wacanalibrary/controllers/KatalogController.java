package id.ac.ukdw.rplbo.wacanalibrary.controllers;

import id.ac.ukdw.rplbo.wacanalibrary.models.Buku;
import id.ac.ukdw.rplbo.wacanalibrary.utils.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class KatalogController {

    @FXML private TextField searchField;
    @FXML private TableView<Buku> tabelBuku;

    @FXML private TableColumn<Buku, Void> colOperasi;
    @FXML private TableColumn<Buku, String> colId, colJudul, colPengarang, colKategori, colStatus;

    // PERBAIKAN 1: Tambahkan colHalaman di sini agar tabel bisa merender datanya
    @FXML private TableColumn<Buku, Number> colTahun, colHalaman;

    private ObservableList<Buku> bukuList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(cellData -> cellData.getValue().idBukuProperty());
        colJudul.setCellValueFactory(cellData -> cellData.getValue().judulProperty());
        colPengarang.setCellValueFactory(cellData -> cellData.getValue().pengarangProperty());
        colKategori.setCellValueFactory(cellData -> cellData.getValue().kategoriProperty());
        colTahun.setCellValueFactory(cellData -> cellData.getValue().tahunTerbitProperty());

        // PERBAIKAN 2: Petakan kolom ke properti halaman
        if(colHalaman != null) {
            colHalaman.setCellValueFactory(cellData -> cellData.getValue().halamanProperty());
        }

        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        tabelBuku.setRowFactory(tv -> new TableRow<Buku>() {
            @Override
            protected void updateItem(Buku item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) setStyle("");
                else if (item.getStatus().equalsIgnoreCase("Rusak")) {
                    setStyle("-fx-background-color: #ffcccc;");
                } else if (item.getStatus().equalsIgnoreCase("Dipinjam")) {
                    setStyle("-fx-background-color: #fff3cd;");
                } else {
                    setStyle("-fx-background-color: #d4edda;");
                }
            }
        });

        loadDataFromDatabase();
        setupPencarianDinamis();
        setupKolomOperasi();
    }

    private void loadDataFromDatabase() {
        bukuList.clear();
        String query = "SELECT * FROM Buku";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                bukuList.add(new Buku(
                        rs.getString("idBuku"),
                        rs.getString("judul"),
                        rs.getString("pengarang"),
                        rs.getString("tipe"),
                        rs.getString("kategori"),
                        rs.getInt("tahunTerbit"),
                        rs.getInt("halaman"), // PERBAIKAN 3: Ini adalah argumen ke-7 yang hilang!
                        rs.getString("status")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupPencarianDinamis() {
        FilteredList<Buku> filteredData = new FilteredList<>(bukuList, b -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(buku -> {
                if (newValue == null || newValue.isEmpty()) return true;

                String lowerCaseFilter = newValue.toLowerCase();

                if (buku.judulProperty().get().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (buku.idBukuProperty().get().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (buku.pengarangProperty().get().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (buku.kategoriProperty().get().toLowerCase().contains(lowerCaseFilter)) return true;
                else if (String.valueOf(buku.tahunTerbitProperty().get()).contains(lowerCaseFilter)) return true;
                else if (String.valueOf(buku.halamanProperty().get()).contains(lowerCaseFilter)) return true;
                else if (buku.statusProperty().get().toLowerCase().contains(lowerCaseFilter)) return true;

                return false;
            });
        });

        SortedList<Buku> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tabelBuku.comparatorProperty());
        tabelBuku.setItems(sortedData);
    }

    @FXML
    private void bukaFormTambah() {
        bukaForm(null);
    }

    private void setupKolomOperasi() {
        Callback<TableColumn<Buku, Void>, TableCell<Buku, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Buku, Void> call(final TableColumn<Buku, Void> param) {
                return new TableCell<>() {
                    private final Button btnEdit = new Button("Edit");
                    private final Button btnHapus = new Button("Hapus");
                    private final HBox pane = new HBox(5, btnEdit, btnHapus);

                    {
                        btnEdit.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 4;");
                        btnHapus.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 4;");

                        btnEdit.setOnAction(event -> {
                            Buku data = getTableView().getItems().get(getIndex());
                            bukaForm(data);
                        });

                        btnHapus.setOnAction(event -> {
                            Buku data = getTableView().getItems().get(getIndex());
                            hapusDataBuku(data);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) setGraphic(null);
                        else setGraphic(pane);
                    }
                };
            }
        };
        colOperasi.setCellFactory(cellFactory);
    }

    private void bukaForm(Buku bukuEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FormBuku.fxml"));
            Parent root = loader.load();

            FormBukuController controller = loader.getController();

            if (bukuEdit != null) {
                controller.setEditData(bukuEdit);
            }

            Stage stage = new Stage();
            stage.setTitle(bukuEdit == null ? "Tambah Buku Baru" : "Ubah Data Buku");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadDataFromDatabase();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hapusDataBuku(Buku buku) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText("Hapus Buku: " + buku.judulProperty().get() + "?");
        alert.setContentText("Data yang dihapus tidak dapat dikembalikan.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection conn = DatabaseHelper.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Buku WHERE idBuku = ?")) {
                    pstmt.setString(1, buku.idBukuProperty().get());
                    pstmt.executeUpdate();
                    loadDataFromDatabase();
                } catch (Exception e) {
                    Alert err = new Alert(Alert.AlertType.ERROR, "Buku gagal dihapus karena masih terhubung dengan data transaksi.");
                    err.show();
                }
            }
        });
    }
}