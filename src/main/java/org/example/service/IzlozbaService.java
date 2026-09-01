package org.example.service;

import org.example.dao.IzlozbaRepository;
import org.example.dao.IzlozbaRepositoryImpl;
import org.example.model.Izlozba;

import java.util.List;
import java.util.Optional;

public class IzlozbaService {

    private final IzlozbaRepository izlozbaRepository = new IzlozbaRepositoryImpl();

    public Izlozba dodajIzlozbu(Izlozba izlozba) {
        validiraj(izlozba);
        return izlozbaRepository.save(izlozba);
    }

    public void azurirajIzlozbu(Izlozba izlozba) {
        validiraj(izlozba);
        izlozbaRepository.update(izlozba);
    }

    public void obrisiIzlozbu(Long id) {
        izlozbaRepository.deleteById(id);
    }

    public Optional<Izlozba> pronadiPoId(Long id) {
        return izlozbaRepository.findById(id);
    }

    public List<Izlozba> dohvatiSve() {
        return izlozbaRepository.findAll();
    }

    private void validiraj(Izlozba izlozba) {
        if (izlozba.getNaziv() == null || izlozba.getNaziv().isBlank()) {
            throw new IllegalArgumentException("Naziv izložbe ne smije biti prazan");
        }
        if (izlozba.getDatumPocetka() != null && izlozba.getDatumZavrsetka() != null
                && izlozba.getDatumPocetka().isAfter(izlozba.getDatumZavrsetka())) {
            throw new IllegalArgumentException("Datum početka ne smije biti nakon datuma završetka");
        }
    }
}