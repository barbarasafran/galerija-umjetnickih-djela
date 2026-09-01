package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.example.model.Djelo;
import org.example.model.Izlozba;
import org.example.service.DjeloService;
import org.example.service.IzlozbaService;
import org.example.service.XmlExportService;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class IzlozbaController {

    @FXML
    private TableView<Izlozba> tablicaIzlozbi;
    @FXML
    private TableColumn<Izlozba, String> kolonaNaziv;
    @FXML
    private TableColumn<Izlozba, String> kolonaLokacija;
    @FXML
    private TableColumn<Izlozba, String> kolonaPocetak;
    @FXML
    private TableColumn<Izlozba, String> kolonaZavrsetak;
    @FXML
    private TableColumn<Izlozba, String> kolonaOpis;

    @FXML
    private TextField poljeNaziv;
    @FXML
    private TextField poljeLokacija;
    @FXML
    private DatePicker poljeDatumPocetka;
    @FXML
    private DatePicker poljeDatumZavrsetka;
    @FXML
    private TextArea poljeOpis;

    @FXML
    private Label labelPoruka;

    private final IzlozbaService izlozbaService = new IzlozbaService();
    private final DjeloService djeloService = new DjeloService();
    private final XmlExportService xmlExportService = new XmlExportService();
    private final ObservableList<Izlozba> podaci = FXCollections.observableArrayList();

    private Izlozba odabranaIzlozba;

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy.");

    @FXML
    public void initialize() {
        kolonaNaziv.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNaziv()));
        kolonaLokacija.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLokacija()));
        kolonaPocetak.setCellValueFactory(cellData -> new SimpleStringProperty(formatiraj(cellData.getValue().getDatumPocetka())));
        kolonaZavrsetak.setCellValueFactory(cellData -> new SimpleStringProperty(formatiraj(cellData.getValue().getDatumZavrsetka())));
        kolonaOpis.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOpis()));

        tablicaIzlozbi.setItems(podaci);
        ucitajPodatke();

        tablicaIzlozbi.getSelectionModel().selectedItemProperty().addListener((obs, staro, novo) -> {
            if (novo != null) popuniFormu(novo);
        });
    }

    private String formatiraj(LocalDate datum) {
        return datum != null ? datum.format(FORMAT) : "";
    }

    private void ucitajPodatke() {
        podaci.setAll(izlozbaService.dohvatiSve());
    }

    private void popuniFormu(Izlozba izlozba) {
        odabranaIzlozba = izlozba;
        poljeNaziv.setText(izlozba.getNaziv());
        poljeLokacija.setText(izlozba.getLokacija());
        poljeDatumPocetka.setValue(izlozba.getDatumPocetka());
        poljeDatumZavrsetka.setValue(izlozba.getDatumZavrsetka());
        poljeOpis.setText(izlozba.getOpis());
    }

    @FXML
    private void onDodaj() {
        try {
            Izlozba nova = new Izlozba(null, poljeNaziv.getText(), poljeLokacija.getText(),
                    poljeDatumPocetka.getValue(), poljeDatumZavrsetka.getValue(), poljeOpis.getText());
            izlozbaService.dodajIzlozbu(nova);
            ucitajPodatke();
            onOcisti();
            prikaziPoruku("Izložba dodana.", false);
        } catch (IllegalArgumentException e) {
            prikaziPoruku(e.getMessage(), true);
        }
    }

    @FXML
    private void onAzuriraj() {
        if (odabranaIzlozba == null) {
            prikaziPoruku("Prvo odaberi izložbu iz tablice.", true);
            return;
        }
        try {
            odabranaIzlozba.setNaziv(poljeNaziv.getText());
            odabranaIzlozba.setLokacija(poljeLokacija.getText());
            odabranaIzlozba.setDatumPocetka(poljeDatumPocetka.getValue());
            odabranaIzlozba.setDatumZavrsetka(poljeDatumZavrsetka.getValue());
            odabranaIzlozba.setOpis(poljeOpis.getText());

            izlozbaService.azurirajIzlozbu(odabranaIzlozba);
            ucitajPodatke();
            onOcisti();
            prikaziPoruku("Izložba ažurirana.", false);
        } catch (IllegalArgumentException e) {
            prikaziPoruku(e.getMessage(), true);
        }
    }

    @FXML
    private void onObrisi() {
        if (odabranaIzlozba == null) {
            prikaziPoruku("Prvo odaberi izložbu iz tablice.", true);
            return;
        }
        izlozbaService.obrisiIzlozbu(odabranaIzlozba.getId());
        ucitajPodatke();
        onOcisti();
        prikaziPoruku("Izložba obrisana.", false);
    }

    @FXML
    private void onOcisti() {
        odabranaIzlozba = null;
        poljeNaziv.clear();
        poljeLokacija.clear();
        poljeDatumPocetka.setValue(null);
        poljeDatumZavrsetka.setValue(null);
        poljeOpis.clear();
        tablicaIzlozbi.getSelectionModel().clearSelection();
    }

    @FXML
    private void onExportKatalog() {
        if (odabranaIzlozba == null) {
            prikaziPoruku("Prvo odaberi izložbu iz tablice.", true);
            return;
        }

        List<Djelo> djelaNaIzlozbi = djeloService.dohvatiPoIzlozbi(odabranaIzlozba.getId());

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Spremi katalog izložbe (XML)");
        fileChooser.setInitialFileName(odabranaIzlozba.getNaziv().replaceAll("\\s+", "_") + "_katalog.xml");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML datoteke", "*.xml"));

        File odrediste = fileChooser.showSaveDialog(tablicaIzlozbi.getScene().getWindow());
        if (odrediste != null) {
            try {
                xmlExportService.exportKatalogIzlozbe(odabranaIzlozba, djelaNaIzlozbi, odrediste);
                prikaziPoruku("Katalog izvezen: " + odrediste.getName()
                        + " (" + djelaNaIzlozbi.size() + " djela)", false);
            } catch (RuntimeException e) {
                prikaziPoruku("Greška pri exportu: " + e.getMessage(), true);
            }
        }
    }

    private void prikaziPoruku(String tekst, boolean greska) {
        labelPoruka.setStyle(greska ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
        labelPoruka.setText(tekst);
    }
}