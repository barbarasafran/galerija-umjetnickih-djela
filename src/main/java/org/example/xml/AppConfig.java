package org.example.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "config")
@XmlAccessorType(XmlAccessType.FIELD)
public class AppConfig {

    @XmlElement
    private EkranConfig ekran;

    @XmlElement
    private BazaConfig baza;

    @XmlElement
    private String pocetniPodaciUrl;

    public EkranConfig getEkran() {
        return ekran;
    }

    public BazaConfig getBaza() {
        return baza;
    }

    public String getPocetniPodaciUrl() {
        return pocetniPodaciUrl;
    }
}