package org.example.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "backupBaze")
@XmlAccessorType(XmlAccessType.FIELD)
public class BackupXml {

    @XmlElement
    private String vrijemeBackupa;

    @XmlElementWrapper(name = "umjetnici")
    @XmlElement(name = "umjetnik")
    private List<UmjetnikPocetniXml> umjetnici;

    @XmlElementWrapper(name = "stilovi")
    @XmlElement(name = "stil")
    private List<StilPocetniXml> stilovi;

    @XmlElementWrapper(name = "materijali")
    @XmlElement(name = "materijal")
    private List<MaterijalPocetniXml> materijali;

    @XmlElementWrapper(name = "izlozbe")
    @XmlElement(name = "izlozba")
    private List<IzlozbaXml> izlozbe;

    @XmlElementWrapper(name = "djela")
    @XmlElement(name = "djelo")
    private List<DjeloXml> djela;

    public BackupXml() {
    }

    public BackupXml(String vrijemeBackupa, List<UmjetnikPocetniXml> umjetnici, List<StilPocetniXml> stilovi,
                     List<MaterijalPocetniXml> materijali, List<IzlozbaXml> izlozbe, List<DjeloXml> djela) {
        this.vrijemeBackupa = vrijemeBackupa;
        this.umjetnici = umjetnici;
        this.stilovi = stilovi;
        this.materijali = materijali;
        this.izlozbe = izlozbe;
        this.djela = djela;
    }
}