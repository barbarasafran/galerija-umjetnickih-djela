package org.example.service;

import java.util.List;
import java.util.Map;

public class GalerijaStatistika {

    private final int ukupnoDjela;
    private final int brojRazlicitihUmjetnika;
    private final String najstarijeDjelo;
    private final String najnovijeDjelo;
    private final boolean imaDjelaPrije1900;
    private final boolean svaDjelaImajuStil;
    private final Map<String, Long> brojDjelaPoStilu;
    private final List<String> nazivi3NajnovijaDjela;

    public GalerijaStatistika(int ukupnoDjela, int brojRazlicitihUmjetnika, String najstarijeDjelo,
                              String najnovijeDjelo, boolean imaDjelaPrije1900, boolean svaDjelaImajuStil,
                              Map<String, Long> brojDjelaPoStilu, List<String> nazivi3NajnovijaDjela) {
        this.ukupnoDjela = ukupnoDjela;
        this.brojRazlicitihUmjetnika = brojRazlicitihUmjetnika;
        this.najstarijeDjelo = najstarijeDjelo;
        this.najnovijeDjelo = najnovijeDjelo;
        this.imaDjelaPrije1900 = imaDjelaPrije1900;
        this.svaDjelaImajuStil = svaDjelaImajuStil;
        this.brojDjelaPoStilu = brojDjelaPoStilu;
        this.nazivi3NajnovijaDjela = nazivi3NajnovijaDjela;
    }

    public int getUkupnoDjela() {
        return ukupnoDjela;
    }

    public int getBrojRazlicitihUmjetnika() {
        return brojRazlicitihUmjetnika;
    }

    public String getNajstarijeDjelo() {
        return najstarijeDjelo;
    }

    public String getNajnovijeDjelo() {
        return najnovijeDjelo;
    }

    public boolean isImaDjelaPrije1900() {
        return imaDjelaPrije1900;
    }

    public boolean isSvaDjelaImajuStil() {
        return svaDjelaImajuStil;
    }

    public Map<String, Long> getBrojDjelaPoStilu() {
        return brojDjelaPoStilu;
    }

    public List<String> getNazivi3NajnovijaDjela() {
        return nazivi3NajnovijaDjela;
    }
}