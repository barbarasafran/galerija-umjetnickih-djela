package org.example.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "izlozba")
@XmlAccessorType(XmlAccessType.FIELD)
public class IzlozbaXml {

    @XmlElement
    private String naziv;

    @XmlElement
    private String lokacija;

    @XmlElement
    private String datumPocetka;

    @XmlElement
    private String datumZavrsetka;

    public IzlozbaXml() {
    }

    public IzlozbaXml(String naziv, String lokacija, String datumPocetka, String datumZavrsetka) {
        this.naziv = naziv;
        this.lokacija = lokacija;
        this.datumPocetka = datumPocetka;
        this.datumZavrsetka = datumZavrsetka;
    }

    public String getNaziv() {
        return naziv;
    }

    public String getLokacija() {
        return lokacija;
    }
}