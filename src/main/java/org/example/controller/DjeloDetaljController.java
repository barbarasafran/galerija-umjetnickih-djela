package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import org.example.model.Djelo;
import org.example.model.Entitet;
import org.example.model.Izlozba;
import org.example.service.XmlExportService;
import org.example.model.MaterijalTehnika;
import org.example.model.StilPokret;
import org.example.model.Umjetnik;
import org.example.service.DjeloService;
import org.example.service.DjeloVecNaIzlozbiException;
import org.example.service.IzlozbaService;
import org.example.service.MaterijalTehnikaService;
import org.example.service.StilPokretService;
import org.example.service.UmjetnikService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DjeloDetaljController {

    @FXML
    private ComboBox<Djelo> poljeOdaberiDjelo;

    @FXML
    private HBox panelDjela;

    @FXML
    private ImageView slikaDjela;

    @FXML
    private Label labelNaziv;
    @FXML
    private Label labelGodina;
    @FXML
    private Label labelOpis;
    @FXML
    private Label labelUmjetnik;
    @FXML
    private Label labelStil;
    @FXML
    private Label labelMaterijal;

    @FXML
    private ListView<Izlozba> listaIzlozbi;

    @FXML
    private Button gumbPostaviSliku;

    @FXML
    private ComboBox<String> filterTip;

    @FXML
    private ListView<Entitet> listaZaPovlacenje;

    @FXML
    private Label labelPoruka;

    private final DjeloService djeloService = new DjeloService();
    private final XmlExportService xmlExportService = new XmlExportService();
    private final UmjetnikService umjetnikService = new UmjetnikService();
    private final StilPokretService stilPokretService = new StilPokretService();
    private final MaterijalTehnikaService materijalTehnikaService = new MaterijalTehnikaService();
    private final IzlozbaService izlozbaService = new IzlozbaService();

    private final ObservableList<Entitet> podaciZaPovlacenje = FXCollections.observableArrayList();

    private Djelo odabranoDjelo;

    private static final String TIP_SVE = "Sve";
    private static final String TIP_UMJETNIK = "Umjetnici";
    private static final String TIP_STIL = "Stilovi/Pokreti";
    private static final String TIP_MATERIJAL = "Materijali/Tehnike";
    private static final String TIP_IZLOZBA = "Izložbe";

    @FXML
    public void initialize() {
        postaviOdabirDjela();
        postaviFilterTipova();
        postaviListuZaPovlacenje();
        postaviListuIzlozbi();
        postaviDragIzvor();
        postaviDragCilj();
    }

    private void postaviOdabirDjela() {
        poljeOdaberiDjelo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Djelo d) {
                return d != null ? d.getNaziv() + " (" + d.getGodinaNastanka() + ")" : "";
            }
            @Override
            public Djelo fromString(String s) {
                return null;
            }
        });

        poljeOdaberiDjelo.getSelectionModel().selectedItemProperty().addListener((obs, staro, novo) -> {
            if (novo != null) {
                prikaziDjelo(novo);
            }
        });

        ucitajListuDjela();
    }

    private void ucitajListuDjela() {
        Long trenutnoOdabranId = odabranoDjelo != null ? odabranoDjelo.getId() : null;

        ObservableList<Djelo> sveDjela = FXCollections.observableArrayList(djeloService.dohvatiSva());
        poljeOdaberiDjelo.setItems(sveDjela);

        if (sveDjela.isEmpty()) {
            return;
        }

        Djelo zaOdabrati = sveDjela.stream()
                .filter(d -> d.getId().equals(trenutnoOdabranId))
                .findFirst()
                .orElse(sveDjela.get(0));

        poljeOdaberiDjelo.getSelectionModel().select(zaOdabrati);
    }

    @FXML
    private void onOsvjezi() {
        ucitajListuDjela();
        if (odabranoDjelo != null) {
            prikaziDjelo(odabranoDjelo);
        }
        ucitajListuPoTipu(filterTip.getValue());
        prikaziPoruku("Podaci osvježeni.", false);
    }

    private void prikaziDjelo(Djelo djelo) {
        odabranoDjelo = djeloService.pronadiPoId(djelo.getId()).orElse(djelo);

        labelNaziv.setText(odabranoDjelo.getNaziv());
        labelGodina.setText(String.valueOf(odabranoDjelo.getGodinaNastanka()));
        labelOpis.setText(odabranoDjelo.getOpis() != null ? odabranoDjelo.getOpis() : "");
        labelUmjetnik.setText(odabranoDjelo.getUmjetnik() != null ? odabranoDjelo.getUmjetnik().getImePrezime() : "-");
        labelStil.setText(odabranoDjelo.getStil() != null ? odabranoDjelo.getStil().getNaziv() : "-");
        labelMaterijal.setText(odabranoDjelo.getMaterijal() != null ? odabranoDjelo.getMaterijal().getNaziv() : "-");

        listaIzlozbi.setItems(FXCollections.observableArrayList(odabranoDjelo.getIzlozbe()));

        ucitajSliku(odabranoDjelo.getPutanjaSlike());
    }

    private void ucitajSliku(String putanja) {
        if (putanja != null && new File(putanja).exists()) {
            slikaDjela.setImage(new Image(new File(putanja).toURI().toString()));
        } else {
            slikaDjela.setImage(null);
        }
    }

    private void postaviFilterTipova() {
        filterTip.setItems(FXCollections.observableArrayList(TIP_SVE, TIP_UMJETNIK, TIP_STIL, TIP_MATERIJAL, TIP_IZLOZBA));
        filterTip.getSelectionModel().selectedItemProperty().addListener((obs, staro, novo) -> ucitajListuPoTipu(novo));
        filterTip.getSelectionModel().selectFirst();
    }

    private void ucitajListuPoTipu(String tip) {
        List<Entitet> lista = new ArrayList<>();

        if (TIP_SVE.equals(tip)) {
            lista.addAll(umjetnikService.dohvatiSve());
            lista.addAll(stilPokretService.dohvatiSve());
            lista.addAll(materijalTehnikaService.dohvatiSve());
            lista.addAll(izlozbaService.dohvatiSve());
        } else if (TIP_UMJETNIK.equals(tip)) {
            lista.addAll(umjetnikService.dohvatiSve());
        } else if (TIP_STIL.equals(tip)) {
            lista.addAll(stilPokretService.dohvatiSve());
        } else if (TIP_MATERIJAL.equals(tip)) {
            lista.addAll(materijalTehnikaService.dohvatiSve());
        } else if (TIP_IZLOZBA.equals(tip)) {
            lista.addAll(izlozbaService.dohvatiSve());
        }

        podaciZaPovlacenje.setAll(lista);
    }

    private void postaviListuZaPovlacenje() {
        listaZaPovlacenje.setItems(podaciZaPovlacenje);

        listaZaPovlacenje.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Entitet entitet, boolean prazno) {
                super.updateItem(entitet, prazno);
                if (prazno || entitet == null) {
                    setText(null);
                    return;
                }
                boolean prikaziPrefiks = TIP_SVE.equals(filterTip.getValue());
                String prefiks = prikaziPrefiks ? "[" + oznakaTipa(entitet) + "] " : "";
                setText(prefiks + entitet.opisiSe());
            }
        });
    }

    private void postaviListuIzlozbi() {
        listaIzlozbi.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Izlozba izlozba, boolean prazno) {
                super.updateItem(izlozba, prazno);
                if (prazno || izlozba == null) {
                    setText(null);
                } else {
                    String lokacija = izlozba.getLokacija() != null && !izlozba.getLokacija().isBlank()
                            ? ", " + izlozba.getLokacija() : "";
                    setText(izlozba.getNaziv() + lokacija);
                }
            }
        });
    }

    private String oznakaTipa(Entitet entitet) {
        if (entitet instanceof Umjetnik) return "Umjetnik";
        if (entitet instanceof StilPokret) return "Stil";
        if (entitet instanceof MaterijalTehnika) return "Materijal";
        if (entitet instanceof Izlozba) return "Izložba";
        return "";
    }

    /**
     * DRAG IZVOR: desna lista (bilo koji tip entiteta). Format podataka koje šaljemo
     * kroz Dragboard je "TipKlase:id", npr. "Umjetnik:3" - na cilju to raspetljamo.
     */
    private void postaviDragIzvor() {
        listaZaPovlacenje.setOnDragDetected(event -> {
            Entitet odabrano = listaZaPovlacenje.getSelectionModel().getSelectedItem();
            if (odabrano != null) {
                Dragboard db = listaZaPovlacenje.startDragAndDrop(TransferMode.COPY);
                ClipboardContent content = new ClipboardContent();
                content.putString(odabrano.getClass().getSimpleName() + ":" + odabrano.getId());
                db.setContent(content);
                event.consume();
            }
        });
    }

    /**
     * DRAG CILJ: cijeli lijevi panel s podacima o djelu. Ovisno o tipu entiteta koji je
     * ispušten, radimo drukčiju akciju (postavi umjetnika/stil/materijal, ili dodaj na izložbu).
     */
    private void postaviDragCilj() {
        panelDjela.setOnDragOver(event -> {
            if (event.getGestureSource() != panelDjela && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        panelDjela.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean uspjeh = false;

            if (db.hasString() && odabranoDjelo != null) {
                String[] dijelovi = db.getString().split(":");
                String tipKlase = dijelovi[0];
                Long id = Long.parseLong(dijelovi[1]);

                uspjeh = obradiIspustanje(tipKlase, id);
            }

            event.setDropCompleted(uspjeh);
            event.consume();
        });
    }

    private boolean obradiIspustanje(String tipKlase, Long id) {
        switch (tipKlase) {
            case "Umjetnik" -> {
                umjetnikService.pronadiPoId(id).ifPresent(u -> {
                    odabranoDjelo.setUmjetnik(u);
                    djeloService.azurirajDjelo(odabranoDjelo);
                    prikaziDjelo(odabranoDjelo);
                    prikaziPoruku("Umjetnik postavljen.", false);
                });
                return true;
            }
            case "StilPokret" -> {
                stilPokretService.pronadiPoId(id).ifPresent(s -> {
                    odabranoDjelo.setStil(s);
                    djeloService.azurirajDjelo(odabranoDjelo);
                    prikaziDjelo(odabranoDjelo);
                    prikaziPoruku("Stil/pokret postavljen.", false);
                });
                return true;
            }
            case "MaterijalTehnika" -> {
                materijalTehnikaService.pronadiPoId(id).ifPresent(m -> {
                    odabranoDjelo.setMaterijal(m);
                    djeloService.azurirajDjelo(odabranoDjelo);
                    prikaziDjelo(odabranoDjelo);
                    prikaziPoruku("Materijal/tehnika postavljen.", false);
                });
                return true;
            }
            case "Izlozba" -> {
                try {
                    djeloService.dodajDjeloNaIzlozbu(odabranoDjelo.getId(), id);
                    prikaziDjelo(odabranoDjelo);
                    prikaziPoruku("Djelo dodano na izložbu.", false);
                } catch (DjeloVecNaIzlozbiException e) {
                    prikaziPoruku(e.getMessage(), true);
                }
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    @FXML
    private void onPostaviSliku() {
        if (odabranoDjelo == null) {
            prikaziPoruku("Prvo odaberi djelo.", true);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Odaberi sliku djela");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Slike", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File odabranaDatoteka = fileChooser.showOpenDialog(gumbPostaviSliku.getScene().getWindow());
        if (odabranaDatoteka != null) {
            djeloService.postaviSliku(odabranoDjelo.getId(), odabranaDatoteka);
            prikaziDjelo(odabranoDjelo);
            prikaziPoruku("Slika postavljena.", false);
        }
    }

    @FXML
    private void onUkloniIzlozbu() {
        Izlozba odabrana = listaIzlozbi.getSelectionModel().getSelectedItem();
        if (odabrana == null || odabranoDjelo == null) {
            prikaziPoruku("Odaberi izložbu s liste za uklanjanje.", true);
            return;
        }
        djeloService.ukloniDjeloSIzlozbe(odabranoDjelo.getId(), odabrana.getId());
        prikaziDjelo(odabranoDjelo);
        prikaziPoruku("Djelo uklonjeno s izložbe.", false);
    }

    @FXML
    private void onExportXml() {
        if (odabranoDjelo == null) {
            prikaziPoruku("Prvo odaberi djelo.", true);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Spremi XML export djela");
        fileChooser.setInitialFileName(odabranoDjelo.getNaziv().replaceAll("\\s+", "_") + ".xml");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML datoteke", "*.xml"));

        File odrediste = fileChooser.showSaveDialog(gumbPostaviSliku.getScene().getWindow());
        if (odrediste != null) {
            try {
                xmlExportService.exportDjelo(odabranoDjelo, odrediste);
                prikaziPoruku("XML export uspješan: " + odrediste.getName(), false);
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