package org.example.service;

import org.example.dao.DjeloRepository;
import org.example.dao.DjeloRepositoryImpl;
import org.example.model.Djelo;
import org.example.model.Izlozba;
import org.example.model.Umjetnik;
import org.example.util.SlikeUtil;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DjeloService implements Validator<Djelo> {

    private final DjeloRepository djeloRepository = new DjeloRepositoryImpl();
    private final IzlozbaService izlozbaService = new IzlozbaService();

    public Djelo dodajDjelo(Djelo djelo) {
        validiraj(djelo);
        validirajIliBaci(djelo, "Djelo nije ispravno popunjeno");
        return djeloRepository.save(djelo);
    }

    public void azurirajDjelo(Djelo djelo) {
        validiraj(djelo);
        djeloRepository.update(djelo);
    }

    public void postaviSliku(Long djeloId, java.io.File novaSlikaDatoteka) {
        djeloRepository.findById(djeloId).ifPresent(djelo -> {
            if (djelo.getPutanjaSlike() != null) {
                SlikeUtil.obrisiSliku(djelo.getPutanjaSlike());
            }
            String novaPutanja = SlikeUtil.spremiSliku(novaSlikaDatoteka);
            djelo.setPutanjaSlike(novaPutanja);
            djeloRepository.update(djelo);
        });
    }

    public void obrisiDjelo(Long id) {
        djeloRepository.findById(id).ifPresent(djelo -> SlikeUtil.obrisiSliku(djelo.getPutanjaSlike()));
        djeloRepository.deleteById(id);
    }

    public Optional<Djelo> pronadiPoId(Long id) {
        return djeloRepository.findById(id);
    }

    public List<Djelo> dohvatiSva() {
        return djeloRepository.findAll();
    }

    public List<Djelo> dohvatiPoUmjetniku(Long umjetnikId) {
        return djeloRepository.findByUmjetnik(umjetnikId);
    }

    public List<Djelo> dohvatiPoStilu(Long stilId) {
        return djeloRepository.findByStil(stilId);
    }

    public List<Djelo> dohvatiPoMaterijalu(Long materijalId) {
        return djeloRepository.findByMaterijal(materijalId);
    }

    public List<Djelo> dohvatiPoIzlozbi(Long izlozbaId) {
        return djeloRepository.findByIzlozba(izlozbaId);
    }

    public void dodajDjeloNaIzlozbu(Long djeloId, Long izlozbaId) throws DjeloVecNaIzlozbiException {
        Izlozba izlozba = izlozbaService.pronadiPoId(izlozbaId)
                .orElseThrow(() -> new IllegalArgumentException("Izložba s id " + izlozbaId + " ne postoji"));

        boolean vecPostoji = djeloRepository.findByIzlozba(izlozbaId).stream()
                .anyMatch(d -> d.getId().equals(djeloId));

        if (vecPostoji) {
            throw new DjeloVecNaIzlozbiException(
                    "Djelo je već dodano na izložbu '" + izlozba.getNaziv() + "'."
            );
        }

        djeloRepository.dodajNaIzlozbu(djeloId, izlozbaId);
    }

    public void ukloniDjeloSIzlozbe(Long djeloId, Long izlozbaId) {
        djeloRepository.ukloniSIzlozbe(djeloId, izlozbaId);
    }

    public List<Djelo> pretraziPoUvjetu(List<Djelo> djela, Predicate<Djelo> uvjet) {
        return djela.stream()
                .filter(uvjet)
                .collect(Collectors.toList());
    }

    public List<Djelo> pretraziPoNazivu(List<Djelo> djela, String tekst) {
        Predicate<Djelo> sadrziTekst = d -> d.getNaziv().toLowerCase().contains(tekst.toLowerCase());
        return pretraziPoUvjetu(djela, sadrziTekst);
    }

    public <R> List<R> transformirajListu(List<Djelo> djela, Function<Djelo, R> mapper) {
        return djela.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    public void zaSvako(List<Djelo> djela, Consumer<Djelo> akcija) {
        djela.forEach(akcija);
    }

    @Override
    public boolean jeIspravno(Djelo djelo) {
        return djelo.getNaziv() != null && !djelo.getNaziv().isBlank()
                && djelo.getUmjetnik() != null
                && djelo.getGodinaNastanka() >= 0 && djelo.getGodinaNastanka() <= 2100;
    }

    private void validiraj(Djelo djelo) {
        // default metoda iz Validator sučelja (I2 kriterij) - brza provjera prije detaljne
        if (jeIspravno(djelo)) {
            return;
        }

        if (djelo.getNaziv() == null || djelo.getNaziv().isBlank()) {
            throw new IllegalArgumentException("Naziv djela ne smije biti prazan");
        }
        if (djelo.getUmjetnik() == null) {
            throw new IllegalArgumentException("Djelo mora imati umjetnika");
        }
        if (djelo.getGodinaNastanka() < 0 || djelo.getGodinaNastanka() > 2100) {
            throw new IllegalArgumentException("Godina nastanka nije valjana");
        }
    }

    public GalerijaStatistika izracunajStatistiku() {
        List<Djelo> djela = djeloRepository.findAll();

        int ukupno = djela.size();

        java.util.Set<Umjetnik> jedinstveniUmjetnici = new java.util.TreeSet<>(
                djela.stream().map(Djelo::getUmjetnik).collect(Collectors.toList())
        );
        long brojRazlicitihUmjetnika = jedinstveniUmjetnici.size();

        String najstarije = djela.stream()
                .min(Comparator.comparingInt(Djelo::getGodinaNastanka))
                .map(d -> d.getNaziv() + " (" + d.getGodinaNastanka() + ")")
                .orElse("nema podataka");

        String najnovije = djela.stream()
                .max(Comparator.comparingInt(Djelo::getGodinaNastanka))
                .map(d -> d.getNaziv() + " (" + d.getGodinaNastanka() + ")")
                .orElse("nema podataka");

        boolean imaPrije1900 = djela.stream()
                .anyMatch(d -> d.getGodinaNastanka() < 1900);

        boolean svaImajuStil = djela.stream()
                .noneMatch(d -> d.getStil() == null);

        Map<String, Long> brojPoStilu = djela.stream()
                .filter(d -> d.getStil() != null)
                .collect(Collectors.groupingBy(d -> d.getStil().getNaziv(), java.util.TreeMap::new, Collectors.counting()));

        List<String> tri = djela.stream()
                .sorted(Comparator.comparingInt(Djelo::getGodinaNastanka).reversed())
                .skip(0)
                .limit(3)
                .map(Djelo::getNaziv)
                .collect(Collectors.toList());

        return new GalerijaStatistika(ukupno, (int) brojRazlicitihUmjetnika, najstarije, najnovije,
                imaPrije1900, svaImajuStil, brojPoStilu, tri);
    }
}