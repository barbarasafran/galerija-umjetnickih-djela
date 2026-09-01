package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.example.model.Djelo;
import org.example.model.MaterijalTehnika;
import org.example.model.StilPokret;
import org.example.model.Umjetnik;
import org.example.service.DjeloService;
import org.example.service.MaterijalTehnikaService;
import org.example.service.StilPokretService;
import org.example.service.UmjetnikService;

import java.util.List;

public class DjeloController {

    @FXML
    private TextField poljePretraga;
    @FXML
    private ComboBox<StilPokret> filterStil;
    @FXML
    private ComboBox<Umjetnik> filterUmjetnik;

    @FXML
    private TableView<Djelo> tablicaDjela;
    @FXML
    private TableColumn<Djelo, String> kolonaNaziv;
    @FXML
    private TableColumn<Djelo, String> kolonaGodina;
    @FXML
    private TableColumn<Djelo, String> kolonaUmjetnik;
    @FXML
    private TableColumn<Djelo, String> kolonaStil;
    @FXML
    private TableColumn<Djelo, String> kolonaMaterijal;
    @FXML
    private TableColumn<Djelo, String> kolonaOpis;

    @FXML
    private TextField poljeNaziv;
    @FXML
    private TextField poljeGodina;
    @FXML
    private ComboBox<Umjetnik> poljeUmjetnik;
    @FXML
    private ComboBox<StilPokret> poljeStil;
    @FXML
    private ComboBox<MaterijalTehnika> poljeMaterijal;
    @FXML
    private TextArea poljeOpis;

    @FXML
    private Label labelPoruka;

    private final DjeloService djeloService = new DjeloService();
    private final UmjetnikService umjetnikService = new UmjetnikService();
    private final StilPokretService stilPokretService = new StilPokretService();
    private final MaterijalTehnikaService materijalTehnikaService = new MaterijalTehnikaService();

    private final ObservableList<Djelo> podaci = FXCollections.observableArrayList();
    private List<Djelo> svaDjelaCache;

    private Djelo odabranoDjelo;

    @FXML
    public void initialize() {
        postaviKolone();
        ucitajComboBoxove();
        ucitajPodatke();

        tablicaDjela.getSelectionModel().selectedItemProperty().addListener((obs, staro, novo) -> {
            if (novo != null) popuniFormu(novo);
        });
    }

    private void postaviKolone() {
        kolonaNaziv.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNaziv()));
        kolonaGodina.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getGodinaNastanka())));
        kolonaUmjetnik.setCellValueFactory(cellData -> {
            Umjetnik u = cellData.getValue().getUmjetnik();
            return new SimpleStringProperty(u != null ? u.getImePrezime() : "");
        });
        kolonaStil.setCellValueFactory(cellData -> {
            StilPokret s = cellData.getValue().getStil();
            return new SimpleStringProperty(s != null ? s.getNaziv() : "");
        });
        kolonaMaterijal.setCellValueFactory(cellData -> {
            MaterijalTehnika m = cellData.getValue().getMaterijal();
            return new SimpleStringProperty(m != null ? m.getNaziv() : "");
        });
        kolonaOpis.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOpis()));

        tablicaDjela.setItems(podaci);
    }

    private void ucitajComboBoxove() {
        ObservableList<Umjetnik> umjetnici = FXCollections.observableArrayList(umjetnikService.dohvatiSve());
        poljeUmjetnik.setItems(umjetnici);
        filterUmjetnik.setItems(umjetnici);
        StringConverter<Umjetnik> umjetnikConverter = new StringConverter<>() {
            @Override
            public String toString(Umjetnik u) {
                return u != null ? u.getImePrezime() : "";
            }
            @Override
            public Umjetnik fromString(String s) {
                return null;
            }
        };
        poljeUmjetnik.setConverter(umjetnikConverter);
        filterUmjetnik.setConverter(umjetnikConverter);

        // Stil ComboBox
        ObservableList<StilPokret> stilovi = FXCollections.observableArrayList(stilPokretService.dohvatiSve());
        poljeStil.setItems(stilovi);
        filterStil.setItems(stilovi);
        StringConverter<StilPokret> stilConverter = new StringConverter<>() {
            @Override
            public String toString(StilPokret s) {
                return s != null ? s.getNaziv() : "";
            }
            @Override
            public StilPokret fromString(String s) {
                return null;
            }
        };
        poljeStil.setConverter(stilConverter);
        filterStil.setConverter(stilConverter);

        // Materijal ComboBox
        ObservableList<MaterijalTehnika> materijali = FXCollections.observableArrayList(materijalTehnikaService.dohvatiSve());
        poljeMaterijal.setItems(materijali);
        poljeMaterijal.setConverter(new StringConverter<>() {
            @Override
            public String toString(MaterijalTehnika m) {
                return m != null ? m.getNaziv() : "";
            }
            @Override
            public MaterijalTehnika fromString(String s) {
                return null;
            }
        });
    }

    private void ucitajPodatke() {
        svaDjelaCache = djeloService.dohvatiSva();
        podaci.setAll(svaDjelaCache);
    }

    private void popuniFormu(Djelo djelo) {
        odabranoDjelo = djelo;
        poljeNaziv.setText(djelo.getNaziv());
        poljeGodina.setText(String.valueOf(djelo.getGodinaNastanka()));
        poljeUmjetnik.setValue(djelo.getUmjetnik());
        poljeStil.setValue(djelo.getStil());
        poljeMaterijal.setValue(djelo.getMaterijal());
        poljeOpis.setText(djelo.getOpis());
    }

    @FXML
    private void onDodaj() {
        try {
            Djelo novo = izgradiDjeloIzForme(null);
            djeloService.dodajDjelo(novo);
            ucitajPodatke();
            onOcisti();
            prikaziPoruku("Djelo dodano.", false);
        } catch (IllegalArgumentException e) {
            prikaziPoruku(poruka(e), true);
        }
    }

    @FXML
    private void onAzuriraj() {
        if (odabranoDjelo == null) {
            prikaziPoruku("Prvo odaberi djelo iz tablice.", true);
            return;
        }
        try {
            Djelo azurirano = izgradiDjeloIzForme(odabranoDjelo.getId());
            azurirano.setIzlozbe(odabranoDjelo.getIzlozbe());
            djeloService.azurirajDjelo(azurirano);
            ucitajPodatke();
            onOcisti();
            prikaziPoruku("Djelo ažurirano.", false);
        } catch (IllegalArgumentException e) {
            prikaziPoruku(poruka(e), true);
        }
    }

    @FXML
    private void onObrisi() {
        if (odabranoDjelo == null) {
            prikaziPoruku("Prvo odaberi djelo iz tablice.", true);
            return;
        }
        djeloService.obrisiDjelo(odabranoDjelo.getId());
        ucitajPodatke();
        onOcisti();
        prikaziPoruku("Djelo obrisano.", false);
    }

    @FXML
    private void onOcisti() {
        odabranoDjelo = null;
        poljeNaziv.clear();
        poljeGodina.clear();
        poljeUmjetnik.setValue(null);
        poljeStil.setValue(null);
        poljeMaterijal.setValue(null);
        poljeOpis.clear();
        tablicaDjela.getSelectionModel().clearSelection();
    }

    /**
     * Pretraga - koristi DjeloService.pretraziPoUvjetu koji prima Predicate (funkcionalna paradigma).
     * Kombiniramo tri neovisna uvjeta (naziv, stil, umjetnik) u jedan Predicate lancem .and().
     */
    @FXML
    private void onPretrazi() {
        String tekst = poljePretraga.getText();
        StilPokret odabraniStil = filterStil.getValue();
        Umjetnik odabraniUmjetnik = filterUmjetnik.getValue();

        List<Djelo> rezultat = djeloService.pretraziPoUvjetu(svaDjelaCache, djelo -> {
            boolean odgovaraNazivu = tekst == null || tekst.isBlank()
                    || djelo.getNaziv().toLowerCase().contains(tekst.toLowerCase());
            boolean odgovaraStilu = odabraniStil == null
                    || (djelo.getStil() != null && djelo.getStil().getId().equals(odabraniStil.getId()));
            boolean odgovaraUmjetniku = odabraniUmjetnik == null
                    || djelo.getUmjetnik().getId().equals(odabraniUmjetnik.getId());

            return odgovaraNazivu && odgovaraStilu && odgovaraUmjetniku;
        });

        podaci.setAll(rezultat);
    }

    @FXML
    private void onPonistiFiltere() {
        poljePretraga.clear();
        filterStil.setValue(null);
        filterUmjetnik.setValue(null);
        podaci.setAll(svaDjelaCache);
    }

    private Djelo izgradiDjeloIzForme(Long id) {
        if (poljeUmjetnik.getValue() == null) {
            throw new IllegalArgumentException("Odaberi umjetnika");
        }
        int godina = Integer.parseInt(poljeGodina.getText().trim());

        return new Djelo(
                id,
                poljeNaziv.getText(),
                godina,
                null,
                poljeOpis.getText(),
                null,
                poljeUmjetnik.getValue(),
                poljeStil.getValue(),
                poljeMaterijal.getValue()
        );
    }

    private String poruka(RuntimeException e) {
        if (e instanceof NumberFormatException) {
            return "Godina nastanka mora biti broj";
        }
        return e.getMessage();
    }

    private void prikaziPoruku(String tekst, boolean greska) {
        labelPoruka.setStyle(greska ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
        labelPoruka.setText(tekst);
    }
}