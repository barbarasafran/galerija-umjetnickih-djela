package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.model.Umjetnik;
import org.example.service.UmjetnikService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UmjetnikController {

    @FXML
    private TableView<Umjetnik> tablicaUmjetnika;
    @FXML
    private TableColumn<Umjetnik, String> kolonaIme;
    @FXML
    private TableColumn<Umjetnik, String> kolonaPrezime;
    @FXML
    private TableColumn<Umjetnik, String> kolonaDatumRodjenja;
    @FXML
    private TableColumn<Umjetnik, String> kolonaDrzava;

    @FXML
    private TextField poljeIme;
    @FXML
    private TextField poljePrezime;
    @FXML
    private DatePicker poljeDatumRodjenja;
    @FXML
    private TextField poljeDrzava;
    @FXML
    private TextArea poljeBiografija;

    @FXML
    private Label labelPoruka;

    private final UmjetnikService umjetnikService = new UmjetnikService();
    private final ObservableList<Umjetnik> podaci = FXCollections.observableArrayList();

    private Umjetnik odabraniUmjetnik;

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy.");

    @FXML
    public void initialize() {
        postaviKolone();
        ucitajPodatke();

        tablicaUmjetnika.getSelectionModel().selectedItemProperty().addListener((obs, staro, novo) -> {
            if (novo != null) {
                popuniFormu(novo);
            }
        });
    }

    private void postaviKolone() {
        kolonaIme.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIme()));
        kolonaPrezime.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrezime()));
        kolonaDrzava.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDrzava()));
        kolonaDatumRodjenja.setCellValueFactory(cellData -> {
            LocalDate datum = cellData.getValue().getDatumRodjenja();
            return new SimpleStringProperty(datum != null ? datum.format(FORMAT) : "");
        });

        tablicaUmjetnika.setItems(podaci);
    }

    private void ucitajPodatke() {
        podaci.setAll(umjetnikService.dohvatiSve());
    }

    private void popuniFormu(Umjetnik umjetnik) {
        odabraniUmjetnik = umjetnik;
        poljeIme.setText(umjetnik.getIme());
        poljePrezime.setText(umjetnik.getPrezime());
        poljeDatumRodjenja.setValue(umjetnik.getDatumRodjenja());
        poljeDrzava.setText(umjetnik.getDrzava());
        poljeBiografija.setText(umjetnik.getBiografija());
    }

    @FXML
    private void onDodaj() {
        try {
            Umjetnik novi = new Umjetnik(null, poljeIme.getText(), poljePrezime.getText(),
                    poljeDatumRodjenja.getValue(), poljeDrzava.getText(), poljeBiografija.getText());
            umjetnikService.dodajUmjetnika(novi);
            ucitajPodatke();
            onOcisti();
            prikaziPoruku("Umjetnik dodan.", false);
        } catch (IllegalArgumentException e) {
            prikaziPoruku(e.getMessage(), true);
        }
    }

    @FXML
    private void onAzuriraj() {
        if (odabraniUmjetnik == null) {
            prikaziPoruku("Prvo odaberi umjetnika iz tablice.", true);
            return;
        }
        try {
            odabraniUmjetnik.setIme(poljeIme.getText());
            odabraniUmjetnik.setPrezime(poljePrezime.getText());
            odabraniUmjetnik.setDatumRodjenja(poljeDatumRodjenja.getValue());
            odabraniUmjetnik.setDrzava(poljeDrzava.getText());
            odabraniUmjetnik.setBiografija(poljeBiografija.getText());

            umjetnikService.azurirajUmjetnika(odabraniUmjetnik);
            ucitajPodatke();
            onOcisti();
            prikaziPoruku("Umjetnik ažuriran.", false);
        } catch (IllegalArgumentException e) {
            prikaziPoruku(e.getMessage(), true);
        }
    }

    @FXML
    private void onObrisi() {
        if (odabraniUmjetnik == null) {
            prikaziPoruku("Prvo odaberi umjetnika iz tablice.", true);
            return;
        }
        umjetnikService.obrisiUmjetnika(odabraniUmjetnik.getId());
        ucitajPodatke();
        onOcisti();
        prikaziPoruku("Umjetnik obrisan.", false);
    }

    @FXML
    private void onOcisti() {
        odabraniUmjetnik = null;
        poljeIme.clear();
        poljePrezime.clear();
        poljeDatumRodjenja.setValue(null);
        poljeDrzava.clear();
        poljeBiografija.clear();
        tablicaUmjetnika.getSelectionModel().clearSelection();
    }

    private void prikaziPoruku(String tekst, boolean greska) {
        labelPoruka.setStyle(greska ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
        labelPoruka.setText(tekst);
    }
}