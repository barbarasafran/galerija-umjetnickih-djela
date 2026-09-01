package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.model.StilPokret;
import org.example.service.StilPokretService;

public class StilPokretController {

    @FXML
    private TableView<StilPokret> tablicaStilova;
    @FXML
    private TableColumn<StilPokret, String> kolonaNaziv;
    @FXML
    private TableColumn<StilPokret, String> kolonaRazdoblje;
    @FXML
    private TableColumn<StilPokret, String> kolonaOpis;

    @FXML
    private TextField poljeNaziv;
    @FXML
    private TextField poljeRazdoblje;
    @FXML
    private TextArea poljeOpis;

    @FXML
    private Label labelPoruka;

    private final StilPokretService stilPokretService = new StilPokretService();
    private final ObservableList<StilPokret> podaci = FXCollections.observableArrayList();

    private StilPokret odabraniStil;

    @FXML
    public void initialize() {
        kolonaNaziv.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNaziv()));
        kolonaRazdoblje.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRazdoblje()));
        kolonaOpis.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOpis()));

        tablicaStilova.setItems(podaci);
        ucitajPodatke();

        tablicaStilova.getSelectionModel().selectedItemProperty().addListener((obs, staro, novo) -> {
            if (novo != null) popuniFormu(novo);
        });
    }

    private void ucitajPodatke() {
        podaci.setAll(stilPokretService.dohvatiSve());
    }

    private void popuniFormu(StilPokret stil) {
        odabraniStil = stil;
        poljeNaziv.setText(stil.getNaziv());
        poljeRazdoblje.setText(stil.getRazdoblje());
        poljeOpis.setText(stil.getOpis());
    }

    @FXML
    private void onDodaj() {
        try {
            StilPokret novi = new StilPokret(null, poljeNaziv.getText(), poljeOpis.getText(), poljeRazdoblje.getText());
            stilPokretService.dodajStil(novi);
            ucitajPodatke();
            onOcisti();
            prikaziPoruku("Stil/pokret dodan.", false);
        } catch (IllegalArgumentException e) {
            prikaziPoruku(e.getMessage(), true);
        }
    }

    @FXML
    private void onAzuriraj() {
        if (odabraniStil == null) {
            prikaziPoruku("Prvo odaberi stil iz tablice.", true);
            return;
        }
        try {
            odabraniStil.setNaziv(poljeNaziv.getText());
            odabraniStil.setRazdoblje(poljeRazdoblje.getText());
            odabraniStil.setOpis(poljeOpis.getText());

            stilPokretService.azurirajStil(odabraniStil);
            ucitajPodatke();
            onOcisti();
            prikaziPoruku("Stil/pokret ažuriran.", false);
        } catch (IllegalArgumentException e) {
            prikaziPoruku(e.getMessage(), true);
        }
    }

    @FXML
    private void onObrisi() {
        if (odabraniStil == null) {
            prikaziPoruku("Prvo odaberi stil iz tablice.", true);
            return;
        }
        stilPokretService.obrisiStil(odabraniStil.getId());
        ucitajPodatke();
        onOcisti();
        prikaziPoruku("Stil/pokret obrisan.", false);
    }

    @FXML
    private void onOcisti() {
        odabraniStil = null;
        poljeNaziv.clear();
        poljeRazdoblje.clear();
        poljeOpis.clear();
        tablicaStilova.getSelectionModel().clearSelection();
    }

    private void prikaziPoruku(String tekst, boolean greska) {
        labelPoruka.setStyle(greska ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
        labelPoruka.setText(tekst);
    }
}