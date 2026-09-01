package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.model.MaterijalTehnika;
import org.example.service.MaterijalTehnikaService;

public class MaterijalTehnikaController {

    @FXML
    private TableView<MaterijalTehnika> tablicaMaterijala;
    @FXML
    private TableColumn<MaterijalTehnika, String> kolonaNaziv;
    @FXML
    private TableColumn<MaterijalTehnika, String> kolonaOpis;

    @FXML
    private TextField poljeNaziv;
    @FXML
    private TextArea poljeOpis;

    @FXML
    private Label labelPoruka;

    private final MaterijalTehnikaService materijalTehnikaService = new MaterijalTehnikaService();
    private final ObservableList<MaterijalTehnika> podaci = FXCollections.observableArrayList();

    private MaterijalTehnika odabraniMaterijal;

    @FXML
    public void initialize() {
        kolonaNaziv.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNaziv()));
        kolonaOpis.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOpis()));

        tablicaMaterijala.setItems(podaci);
        ucitajPodatke();

        tablicaMaterijala.getSelectionModel().selectedItemProperty().addListener((obs, staro, novo) -> {
            if (novo != null) popuniFormu(novo);
        });
    }

    private void ucitajPodatke() {
        podaci.setAll(materijalTehnikaService.dohvatiSve());
    }

    private void popuniFormu(MaterijalTehnika materijal) {
        odabraniMaterijal = materijal;
        poljeNaziv.setText(materijal.getNaziv());
        poljeOpis.setText(materijal.getOpis());
    }

    @FXML
    private void onDodaj() {
        try {
            MaterijalTehnika novi = new MaterijalTehnika(null, poljeNaziv.getText(), poljeOpis.getText());
            materijalTehnikaService.dodajMaterijal(novi);
            ucitajPodatke();
            onOcisti();
            prikaziPoruku("Materijal/tehnika dodan.", false);
        } catch (IllegalArgumentException e) {
            prikaziPoruku(e.getMessage(), true);
        }
    }

    @FXML
    private void onAzuriraj() {
        if (odabraniMaterijal == null) {
            prikaziPoruku("Prvo odaberi materijal iz tablice.", true);
            return;
        }
        try {
            odabraniMaterijal.setNaziv(poljeNaziv.getText());
            odabraniMaterijal.setOpis(poljeOpis.getText());

            materijalTehnikaService.azurirajMaterijal(odabraniMaterijal);
            ucitajPodatke();
            onOcisti();
            prikaziPoruku("Materijal/tehnika ažuriran.", false);
        } catch (IllegalArgumentException e) {
            prikaziPoruku(e.getMessage(), true);
        }
    }

    @FXML
    private void onObrisi() {
        if (odabraniMaterijal == null) {
            prikaziPoruku("Prvo odaberi materijal iz tablice.", true);
            return;
        }
        materijalTehnikaService.obrisiMaterijal(odabraniMaterijal.getId());
        ucitajPodatke();
        onOcisti();
        prikaziPoruku("Materijal/tehnika obrisan.", false);
    }

    @FXML
    private void onOcisti() {
        odabraniMaterijal = null;
        poljeNaziv.clear();
        poljeOpis.clear();
        tablicaMaterijala.getSelectionModel().clearSelection();
    }

    private void prikaziPoruku(String tekst, boolean greska) {
        labelPoruka.setStyle(greska ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
        labelPoruka.setText(tekst);
    }
}