package org.example.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "katalogIzlozbe")
@XmlAccessorType(XmlAccessType.FIELD)
public class IzlozbaKatalogXml {

    @XmlElement
    private String naziv;

    @XmlElement
    private String lokacija;

    @XmlElement
    private String datumPocetka;

    @XmlElement
    private String datumZavrsetka;

    @XmlElementWrapper(name = "djela")
    @XmlElement(name = "djelo")
    private List<DjeloUKatalogXml> djela;

    public IzlozbaKatalogXml() {
    }

    public IzlozbaKatalogXml(String naziv, String lokacija, String datumPocetka,
                             String datumZavrsetka, List<DjeloUKatalogXml> djela) {
        this.naziv = naziv;
        this.lokacija = lokacija;
        this.datumPocetka = datumPocetka;
        this.datumZavrsetka = datumZavrsetka;
        this.djela = djela;
    }
}