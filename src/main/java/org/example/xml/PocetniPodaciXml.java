package org.example.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "pocetniPodaci")
@XmlAccessorType(XmlAccessType.FIELD)
public class PocetniPodaciXml {

    @XmlElementWrapper(name = "umjetnici")
    @XmlElement(name = "umjetnik")
    private List<UmjetnikPocetniXml> umjetnici;

    @XmlElementWrapper(name = "stilovi")
    @XmlElement(name = "stil")
    private List<StilPocetniXml> stilovi;

    @XmlElementWrapper(name = "materijali")
    @XmlElement(name = "materijal")
    private List<MaterijalPocetniXml> materijali;

    public List<UmjetnikPocetniXml> getUmjetnici() {
        return umjetnici;
    }

    public List<StilPocetniXml> getStilovi() {
        return stilovi;
    }

    public List<MaterijalPocetniXml> getMaterijali() {
        return materijali;
    }
}