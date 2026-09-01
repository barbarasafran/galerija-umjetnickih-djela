package org.example.service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.example.model.Djelo;
import org.example.model.Izlozba;
import org.example.xml.DjeloUKatalogXml;
import org.example.xml.DjeloXml;
import org.example.xml.IzlozbaKatalogXml;
import org.example.xml.IzlozbaXml;
import org.example.xml.UmjetnikXml;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class XmlExportService {

    public void exportDjelo(Djelo djelo, File odrediste) {
        UmjetnikXml umjetnikXml = new UmjetnikXml(
                djelo.getUmjetnik().getIme(),
                djelo.getUmjetnik().getPrezime(),
                djelo.getUmjetnik().getDrzava()
        );

        List<IzlozbaXml> izlozbeXml = djelo.getIzlozbe().stream()
                .map(i -> new IzlozbaXml(
                        i.getNaziv(),
                        i.getLokacija(),
                        i.getDatumPocetka() != null ? i.getDatumPocetka().toString() : null,
                        i.getDatumZavrsetka() != null ? i.getDatumZavrsetka().toString() : null
                ))
                .collect(Collectors.toList());

        DjeloXml djeloXml = new DjeloXml(
                djelo.getNaziv(),
                djelo.getGodinaNastanka(),
                djelo.getOpis(),
                umjetnikXml,
                djelo.getStil() != null ? djelo.getStil().getNaziv() : null,
                djelo.getMaterijal() != null ? djelo.getMaterijal().getNaziv() : null,
                izlozbeXml
        );

        marshalirajUFajl(djeloXml, DjeloXml.class, odrediste);
    }

    public void exportKatalogIzlozbe(Izlozba izlozba, List<Djelo> djelaNaIzlozbi, File odrediste) {
        List<DjeloUKatalogXml> djelaXml = djelaNaIzlozbi.stream()
                .map(d -> new DjeloUKatalogXml(
                        d.getNaziv(),
                        d.getGodinaNastanka(),
                        d.getUmjetnik().getImePrezime(),
                        d.getStil() != null ? d.getStil().getNaziv() : null,
                        d.getMaterijal() != null ? d.getMaterijal().getNaziv() : null
                ))
                .collect(Collectors.toList());

        IzlozbaKatalogXml katalog = new IzlozbaKatalogXml(
                izlozba.getNaziv(),
                izlozba.getLokacija(),
                izlozba.getDatumPocetka() != null ? izlozba.getDatumPocetka().toString() : null,
                izlozba.getDatumZavrsetka() != null ? izlozba.getDatumZavrsetka().toString() : null,
                djelaXml
        );

        marshalirajUFajl(katalog, IzlozbaKatalogXml.class, odrediste);
    }

    private <T> void marshalirajUFajl(T objekt, Class<T> klasa, File odrediste) {
        try {
            JAXBContext context = JAXBContext.newInstance(klasa);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(objekt, odrediste);
        } catch (JAXBException e) {
            throw new RuntimeException("Greška pri XML exportu", e);
        }
    }
}