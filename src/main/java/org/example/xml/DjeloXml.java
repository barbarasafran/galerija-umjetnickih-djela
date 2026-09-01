package org.example.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "djelo")
@XmlAccessorType(XmlAccessType.FIELD)
public class DjeloXml {

    @XmlElement
    private String naziv;

    @XmlElement
    private int godinaNastanka;

    @XmlElement
    private String opis;

    @XmlElement
    private UmjetnikXml umjetnik;

    @XmlElement
    private String stil;

    @XmlElement
    private String materijal;

    @XmlElementWrapper(name = "izlozbe")
    @XmlElement(name = "izlozba")
    private List<IzlozbaXml> izlozbe;

    public DjeloXml() {
    }

    public DjeloXml(String naziv, int godinaNastanka, String opis, UmjetnikXml umjetnik,
                    String stil, String materijal, List<IzlozbaXml> izlozbe) {
        this.naziv = naziv;
        this.godinaNastanka = godinaNastanka;
        this.opis = opis;
        this.umjetnik = umjetnik;
        this.stil = stil;
        this.materijal = materijal;
        this.izlozbe = izlozbe;
    }
}