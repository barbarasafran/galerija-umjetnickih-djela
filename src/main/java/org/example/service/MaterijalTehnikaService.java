package org.example.service;

import org.example.dao.MaterijalTehnikaRepository;
import org.example.dao.MaterijalTehnikaRepositoryImpl;
import org.example.model.MaterijalTehnika;

import java.util.List;
import java.util.Optional;

public class MaterijalTehnikaService {

    private final MaterijalTehnikaRepository materijalTehnikaRepository = new MaterijalTehnikaRepositoryImpl();

    public MaterijalTehnika dodajMaterijal(MaterijalTehnika materijal) {
        validiraj(materijal);
        return materijalTehnikaRepository.save(materijal);
    }

    public void azurirajMaterijal(MaterijalTehnika materijal) {
        validiraj(materijal);
        materijalTehnikaRepository.update(materijal);
    }

    public void obrisiMaterijal(Long id) {
        materijalTehnikaRepository.deleteById(id);
    }

    public Optional<MaterijalTehnika> pronadiPoId(Long id) {
        return materijalTehnikaRepository.findById(id);
    }

    public List<MaterijalTehnika> dohvatiSve() {
        return materijalTehnikaRepository.findAll();
    }

    private void validiraj(MaterijalTehnika materijal) {
        if (materijal.getNaziv() == null || materijal.getNaziv().isBlank()) {
            throw new IllegalArgumentException("Naziv materijala/tehnike ne smije biti prazan");
        }
    }
}