package org.example.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "log")
@XmlAccessorType(XmlAccessType.FIELD)
public class LogXml {

    @XmlElement(name = "zapis")
    private List<LogZapisXml> zapisi = new ArrayList<>();

    public List<LogZapisXml> getZapisi() {
        return zapisi;
    }

    public void setZapisi(List<LogZapisXml> zapisi) {
        this.zapisi = zapisi;
    }
}