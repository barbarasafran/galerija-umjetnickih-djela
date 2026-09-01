package org.example.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.model.Korisnik;
import org.example.service.AdminService;

import java.io.IOException;

public class GlavniProzorController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private Label labelDobrodoslica;

    @FXML
    private Menu izbornikAdmin;

    private Korisnik prijavljeniKorisnik;
    private final AdminService adminService = new AdminService();

    public void postaviKorisnika(Korisnik korisnik) {
        this.prijavljeniKorisnik = korisnik;
        labelDobrodoslica.setText("Dobrodošli, " + korisnik.getKorisnickoIme() + "! (" + korisnik.getUloga() + ")");
        izbornikAdmin.setVisible(korisnik.jeAdministrator());
    }

    @FXML
    private void onPregledDjela() {
        ucitajUCentar("/fxml/djelo_view.fxml");
    }

    @FXML
    private void onPregledUmjetnika() {
        ucitajUCentar("/fxml/umjetnik_view.fxml");
    }

    @FXML
    private void onPregledStilova() {
        ucitajUCentar("/fxml/stil_pokret_view.fxml");
    }

    @FXML
    private void onPregledMaterijala() {
        ucitajUCentar("/fxml/materijal_tehnika_view.fxml");
    }

    @FXML
    private void onPregledIzlozbi() {
        ucitajUCentar("/fxml/izlozba_view.fxml");
    }

    @FXML
    private void onDetaljiDjela() {
        ucitajUCentar("/fxml/djelo_detalj_view.fxml");
    }

    @FXML
    private void onStatistika() {
        ucitajUCentar("/fxml/statistika_view.fxml");
    }

    @FXML
    private void onObrisiPodatke() {
        boolean potvrdjeno = org.example.util.AlertUtil.potvrdi("Potvrda brisanja",
                "Ovo će trajno obrisati SVE podatke iz baze (umjetnike, djela, stilove, materijale, izložbe) "
                        + "i sve spremljene slike. Jesi li sigurna?");

        if (potvrdjeno) {
            pokreniPozadinskiZadatak(
                    "Brisanje podataka",
                    napredak -> adminService.obrisiSvePodatke(napredak)
            );
        }
    }

    @FXML
    private void onUcitajPodatke() {
        pokreniPozadinskiZadatak(
                "Učitavanje početnih podataka",
                napredak -> adminService.ucitajPocetnePodatke(napredak)
        );
    }

    @FXML
    private void onNapraviBackup() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Spremi backup baze (XML)");
        fileChooser.setInitialFileName("backup_galerija.xml");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("XML datoteke", "*.xml"));

        java.io.File odrediste = fileChooser.showSaveDialog(rootPane.getScene().getWindow());
        if (odrediste == null) {
            return;
        }

        pokreniPozadinskiZadatak(
                "Izrada backupa baze",
                napredak -> adminService.napraviBackup(odrediste, napredak)
        );
    }

    /**
     * Pokreće zadanu operaciju u pozadinskoj dretvi (Task), kako se JavaFX Application
     * dretva (glavna, "UI" dretva) ne bi zamrznula tijekom sporih operacija poput
     * brisanja/učitavanja podataka i XML parsiranja (I7 kriterij).
     */
    private void pokreniPozadinskiZadatak(String naslov, java.util.function.Consumer<java.util.function.Consumer<String>> posao) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                // updateMessage je thread-safe metoda - smije se zvati iz pozadinske dretve
                posao.accept(this::updateMessage);
                return null;
            }
        };

        Label labelStatus = new Label("Pokrećem...");
        labelStatus.textProperty().bind(task.messageProperty());
        ProgressIndicator indikator = new ProgressIndicator();

        javafx.scene.layout.VBox sadrzaj = new javafx.scene.layout.VBox(15, indikator, labelStatus);
        sadrzaj.setAlignment(javafx.geometry.Pos.CENTER);
        sadrzaj.setPadding(new javafx.geometry.Insets(25));

        Stage dijalogStage = new Stage();
        dijalogStage.setTitle(naslov);
        dijalogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dijalogStage.initOwner(rootPane.getScene().getWindow());
        dijalogStage.setScene(new Scene(sadrzaj, 320, 160));
        dijalogStage.setResizable(false);

        task.setOnSucceeded(e -> {
            dijalogStage.close();
            org.example.util.AlertUtil.prikaziInfo(naslov + " uspješno završeno.");
        });

        task.setOnFailed(e -> {
            dijalogStage.close();
            org.example.util.AlertUtil.prikaziGresku("Greška: " + task.getException().getMessage());
        });

        new Thread(task).start();
        dijalogStage.show();
    }

    @FXML
    private void onOdjava() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(root, 500, 400));
            stage.setTitle("Galerija umjetničkih djela - Prijava");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ucitajUCentar(String putanjaFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(putanjaFxml));
            Parent ekran = loader.load();
            rootPane.setCenter(ekran);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}