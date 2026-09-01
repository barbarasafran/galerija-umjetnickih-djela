package org.example.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.example.dao.KorisnikRepository;
import org.example.dao.KorisnikRepositoryImpl;
import org.example.model.Korisnik;
import org.example.model.Uloga;
import org.example.util.LogUtil;

import java.util.Optional;

public class KorisnikService {

    private final KorisnikRepository korisnikRepository = new KorisnikRepositoryImpl();

    public Korisnik registrirajKorisnika(String korisnickoIme, String lozinka) {
        if (korisnickoIme == null || korisnickoIme.isBlank()) {
            throw new IllegalArgumentException("Korisničko ime ne smije biti prazno");
        }
        if (lozinka == null || lozinka.length() < 6) {
            throw new IllegalArgumentException("Lozinka mora imati barem 6 znakova");
        }

        Optional<Korisnik> postojeci = korisnikRepository.findByKorisnickoIme(korisnickoIme);
        if (postojeci.isPresent()) {
            throw new IllegalArgumentException("Korisničko ime već postoji");
        }

        String hash = BCrypt.withDefaults().hashToString(12, lozinka.toCharArray());

        Korisnik noviKorisnik = new Korisnik(null, korisnickoIme, hash, Uloga.KORISNIK);
        Korisnik spremljeni = korisnikRepository.save(noviKorisnik);
        LogUtil.zabiljezi(korisnickoIme, "REGISTRACIJA", "Novi korisnik registriran");
        return spremljeni;
    }

    public Optional<Korisnik> prijava(String korisnickoIme, String lozinka) {
        Optional<Korisnik> korisnikOpt = korisnikRepository.findByKorisnickoIme(korisnickoIme);

        Optional<Korisnik> rezultat = korisnikOpt.filter(korisnik ->
                BCrypt.verifyer().verify(lozinka.toCharArray(), korisnik.getLozinkaHash()).verified
        );

        rezultat.ifPresent(k -> LogUtil.zabiljezi(k.getKorisnickoIme(), "PRIJAVA", "Uspješna prijava u sustav"));

        return rezultat;
    }
}