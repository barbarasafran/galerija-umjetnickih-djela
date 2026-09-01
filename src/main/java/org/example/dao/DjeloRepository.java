package org.example.dao;

import org.example.model.Djelo;

import java.util.List;

public interface DjeloRepository extends Repository<Djelo, Long> {

    List<Djelo> findByUmjetnik(Long umjetnikId);

    List<Djelo> findByStil(Long stilId);

    List<Djelo> findByMaterijal(Long materijalId);

    List<Djelo> findByIzlozba(Long izlozbaId);

    // Ove dvije metode poziva drag & drop logika
    void dodajNaIzlozbu(Long djeloId, Long izlozbaId);

    void ukloniSIzlozbe(Long djeloId, Long izlozbaId);
}