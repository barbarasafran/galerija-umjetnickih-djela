package org.example.service;

import org.example.dao.UmjetnikRepository;
import org.example.dao.UmjetnikRepositoryImpl;
import org.example.model.Umjetnik;

import java.util.List;
import java.util.Optional;

public class UmjetnikService {

    private final UmjetnikRepository umjetnikRepository = new UmjetnikRepositoryImpl();

    public Umjetnik dodajUmjetnika(Umjetnik umjetnik) {
        validiraj(umjetnik);
        return umjetnikRepository.save(umjetnik);
    }

    public void azurirajUmjetnika(Umjetnik umjetnik) {
        validiraj(umjetnik);
        umjetnikRepository.update(umjetnik);
    }

    public void obrisiUmjetnika(Long id) {
        umjetnikRepository.deleteById(id);
    }

    public Optional<Umjetnik> pronadiPoId(Long id) {
        return umjetnikRepository.findById(id);
    }

    public List<Umjetnik> dohvatiSve() {
        return umjetnikRepository.findAll();
    }

    private void validiraj(Umjetnik umjetnik) {
        if (umjetnik.getIme() == null || umjetnik.getIme().isBlank()) {
            throw new IllegalArgumentException("Ime umjetnika ne smije biti prazno");
        }
        if (umjetnik.getPrezime() == null || umjetnik.getPrezime().isBlank()) {
            throw new IllegalArgumentException("Prezime umjetnika ne smije biti prazno");
        }
    }
}