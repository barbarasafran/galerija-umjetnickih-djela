package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.example.service.DjeloService;
import org.example.service.GalerijaStatistika;

import java.util.Map;
import java.util.stream.Collectors;

public class StatistikaController {

    @FXML
    private Label labelUkupnoDjela;
    @FXML
    private Label labelBrojUmjetnika;
    @FXML
    private Label labelNajstarije;
    @FXML
    private Label labelNajnovije;
    @FXML
    private Label labelImaPrije1900;
    @FXML
    private Label labelSvaImajuStil;
    @FXML
    private ListView<String> listaPoStilu;

    private final DjeloService djeloService = new DjeloService();

    @FXML
    public void initialize() {
        osvjeziStatistiku();
    }

    @FXML
    private void onOsvjezi() {
        osvjeziStatistiku();
    }

    private void osvjeziStatistiku() {
        GalerijaStatistika statistika = djeloService.izracunajStatistiku();

        labelUkupnoDjela.setText(String.valueOf(statistika.getUkupnoDjela()));
        labelBrojUmjetnika.setText(String.valueOf(statistika.getBrojRazlicitihUmjetnika()));
        labelNajstarije.setText(statistika.getNajstarijeDjelo());
        labelNajnovije.setText(statistika.getNajnovijeDjelo());
        labelImaPrije1900.setText(statistika.isImaDjelaPrije1900() ? "Da" : "Ne");
        labelSvaImajuStil.setText(statistika.isSvaDjelaImajuStil() ? "Da" : "Ne");

        Map<String, Long> poStilu = statistika.getBrojDjelaPoStilu();
        ObservableList<String> stavke = FXCollections.observableArrayList(
                poStilu.entrySet().stream()
                        .map(e -> e.getKey() + ": " + e.getValue() + " djela")
                        .collect(Collectors.toList())
        );
        listaPoStilu.setItems(stavke);
    }
}