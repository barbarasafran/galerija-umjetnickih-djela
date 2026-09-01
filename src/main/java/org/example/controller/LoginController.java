package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.model.Korisnik;
import org.example.service.KorisnikService;
import org.example.util.ConfigUtil;
import org.example.xml.AppConfig;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField poljeKorisnickoIme;

    @FXML
    private PasswordField poljeLozinka;

    @FXML
    private Label labelGreska;

    @FXML
    private Button gumbPrijava;

    @FXML
    private Button gumbRegistracija;

    private final KorisnikService korisnickService = new KorisnikService();

    @FXML
    private void onPrijava() {
        String korisnickoIme = poljeKorisnickoIme.getText();
        String lozinka = poljeLozinka.getText();

        labelGreska.setText("");

        korisnickService.prijava(korisnickoIme, lozinka).ifPresentOrElse(
                this::otvoriGlavniProzor,
                () -> labelGreska.setText("Pogrešno korisničko ime ili lozinka")
        );
    }

    @FXML
    private void onRegistracija() {
        String korisnickoIme = poljeKorisnickoIme.getText();
        String lozinka = poljeLozinka.getText();

        labelGreska.setText("");

        try {
            korisnickService.registrirajKorisnika(korisnickoIme, lozinka);
            labelGreska.setStyle("-fx-text-fill: green;");
            labelGreska.setText("Registracija uspješna! Sad se možeš prijaviti.");
        } catch (IllegalArgumentException e) {
            labelGreska.setStyle("-fx-text-fill: red;");
            labelGreska.setText(e.getMessage());
        }
    }

    private void otvoriGlavniProzor(Korisnik korisnik) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/glavni_prozor.fxml"));
            Parent root = loader.load();

            GlavniProzorController controller = loader.getController();
            controller.postaviKorisnika(korisnik);

            Stage stage = (Stage) gumbPrijava.getScene().getWindow();

            AppConfig config = ConfigUtil.ucitajKonfiguraciju();
            int sirina = config.getEkran() != null ? config.getEkran().getSirina() : 1200;
            int visina = config.getEkran() != null ? config.getEkran().getVisina() : 800;

            stage.setScene(new Scene(root, sirina, visina));
            stage.setTitle("Galerija umjetničkih djela");
        } catch (IOException e) {
            labelGreska.setStyle("-fx-text-fill: red;");
            labelGreska.setText("Greška pri otvaranju glavnog prozora");
            e.printStackTrace();
        }
    }
}