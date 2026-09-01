package org.example.dao;

import org.example.model.Korisnik;

import java.util.Optional;

public interface KorisnikRepository extends Repository<Korisnik, Long> {

    Optional<Korisnik> findByKorisnickoIme(String korisnickoIme);
}