package org.example.service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.example.model.Djelo;
import org.example.model.Izlozba;
import org.example.model.MaterijalTehnika;
import org.example.model.StilPokret;
import org.example.model.Umjetnik;
import org.example.util.ConfigUtil;
import org.example.util.DatabaseConnection;
import org.example.xml.AppConfig;
import org.example.xml.BackupXml;
import org.example.xml.DjeloXml;
import org.example.xml.IzlozbaXml;
import org.example.xml.MaterijalPocetniXml;
import org.example.xml.PocetniPodaciXml;
import org.example.xml.StilPocetniXml;
import org.example.xml.UmjetnikPocetniXml;
import org.example.xml.UmjetnikXml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AdminService {

    private final UmjetnikService umjetnikService = new UmjetnikService();
    private final StilPokretService stilPokretService = new StilPokretService();
    private final MaterijalTehnikaService materijalTehnikaService = new MaterijalTehnikaService();
    private final IzlozbaService izlozbaService = new IzlozbaService();
    private final DjeloService djeloService = new DjeloService();

    private static final String ASSETS_FOLDER =
            System.getProperty("user.home") + File.separator + "galerija-app-assets";

    /**
     * Briše sve podatke iz svih tablica (osim admin korisnika - to bi ga zaključalo iz aplikacije)
     * te briše sve slike iz assets foldera. Poziva se s admin sučelja.
     *
     * @param napredak Consumer koji prima poruke o napretku (za prikaz u GUI-u tijekom rada)
     */
    public void obrisiSvePodatke(Consumer<String> napredak) {
        Connection conn = DatabaseConnection.getInstance().getConnection();

        String[] tablice = {"djelo_izlozba", "djelo", "izlozba", "materijal_tehnika", "stil_pokret", "umjetnik"};

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            for (String tablica : tablice) {
                napredak.accept("Brišem tablicu: " + tablica + "...");
                stmt.execute("TRUNCATE TABLE " + tablica);
            }
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri brisanju podataka iz baze", e);
        }

        napredak.accept("Brišem slike iz assets foldera...");
        obrisiSveSlike();

        napredak.accept("Brisanje završeno.");
    }

    private void obrisiSveSlike() {
        Path assetsDir = Paths.get(ASSETS_FOLDER);
        if (!Files.exists(assetsDir)) {
            return;
        }
        try (Stream<Path> datoteke = Files.walk(assetsDir)) {
            datoteke.sorted(Comparator.reverseOrder())
                    .filter(p -> !p.equals(assetsDir))
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            System.err.println("Ne mogu obrisati: " + p);
                        }
                    });
        } catch (IOException e) {
            System.err.println("Greška pri brisanju assets foldera: " + e.getMessage());
        }
    }

    /**
     * Učitava početne podatke iz XML datoteke (bundled resource) - umjetnici, stilovi, materijali.
     * Koristi JAXB Unmarshaller (I8 kriterij).
     *
     * @param napredak Consumer koji prima poruke o napretku
     */
    public void ucitajPocetnePodatke(Consumer<String> napredak) {
        try {
            napredak.accept("Pokušavam preuzeti podatke s online izvora...");
            PocetniPodaciXml podaci = parsirajXml();

            if (podaci.getUmjetnici() != null) {
                for (UmjetnikPocetniXml u : podaci.getUmjetnici()) {
                    napredak.accept("Dodajem umjetnika: " + u.getIme() + " " + u.getPrezime());
                    umjetnikService.dodajUmjetnika(new Umjetnik(null, u.getIme(), u.getPrezime(),
                            null, u.getDrzava(), u.getBiografija()));
                }
            }

            if (podaci.getStilovi() != null) {
                for (StilPocetniXml s : podaci.getStilovi()) {
                    napredak.accept("Dodajem stil: " + s.getNaziv());
                    stilPokretService.dodajStil(new StilPokret(null, s.getNaziv(), s.getOpis(), s.getRazdoblje()));
                }
            }

            if (podaci.getMaterijali() != null) {
                for (MaterijalPocetniXml m : podaci.getMaterijali()) {
                    napredak.accept("Dodajem materijal: " + m.getNaziv());
                    materijalTehnikaService.dodajMaterijal(new MaterijalTehnika(null, m.getNaziv(), m.getOpis()));
                }
            }

            napredak.accept("Učitavanje početnih podataka završeno.");
        } catch (JAXBException e) {
            throw new RuntimeException("Greška pri parsiranju XML početnih podataka", e);
        }
    }

    /**
     * Pokušava dohvatiti XML s online izvora (URL definiran u config.xml preko HttpClienta).
     * Ako to ne uspije (nema URL-a, nema interneta, server ne odgovara...), koristi lokalnu
     * "bundled" kopiju kao pouzdan fallback - aplikacija ostaje funkcionalna i bez interneta.
     */
    private PocetniPodaciXml parsirajXml() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(PocetniPodaciXml.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        String xmlSadrzaj = pokusajDohvatitiOnline();
        if (xmlSadrzaj != null) {
            return (PocetniPodaciXml) unmarshaller.unmarshal(new StringReader(xmlSadrzaj));
        }

        try (InputStream is = getClass().getResourceAsStream("/xml-data/pocetni_podaci.xml")) {
            if (is == null) {
                throw new RuntimeException("Datoteka pocetni_podaci.xml nije pronađena u resources/xml-data/");
            }
            return (PocetniPodaciXml) unmarshaller.unmarshal(is);
        } catch (IOException e) {
            throw new RuntimeException("Greška pri čitanju XML datoteke", e);
        }
    }

    /**
     * Pokušava preuzeti XML sadržaj s URL-a definiranog u config.xml preko java.net.http.HttpClient
     * (standardna Java biblioteka, ne treba dodatni dependency). Vraća null ako preuzimanje
     * ne uspije iz bilo kojeg razloga - poziva se onda lokalni fallback.
     */
    private String pokusajDohvatitiOnline() {
        AppConfig config = ConfigUtil.ucitajKonfiguraciju();
        String url = config.getPocetniPodaciUrl();

        if (url == null || url.isBlank()) {
            return null;
        }

        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(java.time.Duration.ofSeconds(5))
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(java.time.Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            }
            System.err.println("Online izvor vratio status " + response.statusCode() + ", koristim lokalni fallback.");
            return null;
        } catch (Exception e) {
            System.err.println("Neuspjelo preuzimanje s online izvora (" + e.getMessage() + "), koristim lokalni fallback.");
            return null;
        }
    }

    /**
     * Generira backup cijele baze u jedan XML fajl (I8 - "željeni" bod).
     * Koristi Stream API (I3) za mapiranje entiteta u XML DTO objekte.
     */
    public void napraviBackup(File odrediste, Consumer<String> napredak) {
        napredak.accept("Dohvaćam podatke iz baze...");

        List<UmjetnikPocetniXml> umjetnici = umjetnikService.dohvatiSve().stream()
                .map(u -> new UmjetnikPocetniXml(u.getIme(), u.getPrezime(), u.getDrzava(), u.getBiografija()))
                .collect(Collectors.toList());

        List<StilPocetniXml> stilovi = stilPokretService.dohvatiSve().stream()
                .map(s -> new StilPocetniXml(s.getNaziv(), s.getOpis(), s.getRazdoblje()))
                .collect(Collectors.toList());

        List<MaterijalPocetniXml> materijali = materijalTehnikaService.dohvatiSve().stream()
                .map(m -> new MaterijalPocetniXml(m.getNaziv(), m.getOpis()))
                .collect(Collectors.toList());

        List<IzlozbaXml> izlozbe = izlozbaService.dohvatiSve().stream()
                .map(i -> new IzlozbaXml(
                        i.getNaziv(),
                        i.getLokacija(),
                        i.getDatumPocetka() != null ? i.getDatumPocetka().toString() : null,
                        i.getDatumZavrsetka() != null ? i.getDatumZavrsetka().toString() : null
                ))
                .collect(Collectors.toList());

        List<Djelo> sviDjela = djeloService.dohvatiSva();
        List<DjeloXml> djela = sviDjela.stream()
                .map(d -> new DjeloXml(
                        d.getNaziv(),
                        d.getGodinaNastanka(),
                        d.getOpis(),
                        new UmjetnikXml(d.getUmjetnik().getIme(), d.getUmjetnik().getPrezime(), d.getUmjetnik().getDrzava()),
                        d.getStil() != null ? d.getStil().getNaziv() : null,
                        d.getMaterijal() != null ? d.getMaterijal().getNaziv() : null,
                        d.getIzlozbe().stream()
                                .map(i -> new IzlozbaXml(
                                        i.getNaziv(),
                                        i.getLokacija(),
                                        i.getDatumPocetka() != null ? i.getDatumPocetka().toString() : null,
                                        i.getDatumZavrsetka() != null ? i.getDatumZavrsetka().toString() : null
                                ))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        napredak.accept("Generiram XML backup...");
        String vrijeme = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss"));
        BackupXml backup = new BackupXml(vrijeme, umjetnici, stilovi, materijali, izlozbe, djela);

        try {
            JAXBContext context = JAXBContext.newInstance(BackupXml.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(backup, odrediste);
        } catch (JAXBException e) {
            throw new RuntimeException("Greška pri generiranju backupa", e);
        }

        napredak.accept("Backup završen: " + odrediste.getName());
    }
}