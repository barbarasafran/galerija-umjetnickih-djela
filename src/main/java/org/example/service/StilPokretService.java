package org.example.service;

import org.example.dao.StilPokretRepository;
import org.example.dao.StilPokretRepositoryImpl;
import org.example.model.StilPokret;

import java.util.List;
import java.util.Optional;

public class StilPokretService {

    private final StilPokretRepository stilPokretRepository = new StilPokretRepositoryImpl();

    public StilPokret dodajStil(StilPokret stil) {
        validiraj(stil);
        return stilPokretRepository.save(stil);
    }

    public void azurirajStil(StilPokret stil) {
        validiraj(stil);
        stilPokretRepository.update(stil);
    }

    public void obrisiStil(Long id) {
        stilPokretRepository.deleteById(id);
    }

    public Optional<StilPokret> pronadiPoId(Long id) {
        return stilPokretRepository.findById(id);
    }

    public List<StilPokret> dohvatiSve() {
        return stilPokretRepository.findAll();
    }

    private void validiraj(StilPokret stil) {
        if (stil.getNaziv() == null || stil.getNaziv().isBlank()) {
            throw new IllegalArgumentException("Naziv stila/pokreta ne smije biti prazan");
        }
    }
}