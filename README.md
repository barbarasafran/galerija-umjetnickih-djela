# Galerija umjetničkih djela

JavaFX aplikacija za upravljanje katalogom galerija i umjetničkih djela — projekt iz kolegija vezanog uz Javu. Aplikacija omogućuje administratoru i korisnicima upravljanje umjetničkim djelima te njihovo povezivanje s umjetnicima, stilovima/pokretima, materijalima/tehnikama i izložbama.

## Sadržaj

- [Tehnologije](#tehnologije)
- [Funkcionalnosti](#funkcionalnosti)
- [Struktura projekta](#struktura-projekta)
- [Postavljanje](#postavljanje)
- [Pokretanje](#pokretanje)
- [Zadana prijava](#zadana-prijava)

## Tehnologije

- **Java 21**
- **JavaFX 21** — grafičko sučelje
- **MySQL 8** — baza podataka
- **JDBC** (PreparedStatement / CallableStatement) — pristup bazi
- **Jakarta XML Binding (JAXB)** — XML export/import, konfiguracija, logiranje
- **BCrypt** — hashiranje lozinki
- **Maven** — build alat

## Funkcionalnosti

- **Autentifikacija** — prijava i registracija korisnika (uloge Administrator / Korisnik)
- **CRUD operacije** za sve entitete: Djelo, Umjetnik, Stil/Pokret, Materijal/Tehnika, Izložba
- **Pretraga** djela po nazivu, umjetniku i stilu
- **Drag & Drop** — povezivanje djela s umjetnikom, stilom, materijalom i izložbama povlačenjem
- **Upravljanje slikama** — postavljanje slike djela, automatsko brisanje pri uklanjanju
- **Statistika** — pregled statistike galerije (broj djela, najstarije/najnovije djelo, raspodjela po stilovima...)
- **XML export** — export pojedinog djela (s umjetnikom i izložbama) i kataloga izložbe (sva djela na njoj)
- **Administratorske funkcije**:
    - brisanje svih podataka iz baze
    - učitavanje početnih podataka (s online izvora, uz lokalni fallback)
    - backup cijele baze u XML
- **Konfiguracija** aplikacije putem `config.xml` (veličina prozora, podaci za bazu, izvor početnih podataka)
- **Logiranje** akcija korisnika u `log.xml`

## Struktura projekta

```
src/main/java/org/example/
├── app/          - pokretanje aplikacije (Main, App)
├── model/        - entiteti (Djelo, Umjetnik, StilPokret, MaterijalTehnika, Izlozba, Korisnik...)
├── dao/          - pristup bazi podataka (Repository uzorak)
├── service/      - poslovna logika
├── controller/   - JavaFX kontroleri (MVC)
├── util/         - pomoćne statičke klase (DatabaseConnection, ConfigUtil, LogUtil, SlikeUtil, AlertUtil)
└── xml/          - DTO klase za JAXB (export, konfiguracija, log, backup)

src/main/resources/
├── fxml/         - FXML datoteke (View sloj)
├── config/       - config.xml.example (predložak konfiguracije)
└── xml-data/     - početni podaci (fallback izvor)
```

## Postavljanje

### 1. Baza podataka

Kreiraj MySQL bazu i pokreni inicijalizacijske skripte (nalaze se u `sql/` folderu projekta, ili zatraži od autora):

```sql
CREATE DATABASE galerija_db;
```

Zatim pokreni redom:
1. `01_init.sql` — kreira sve tablice
2. `03_sp_kreiraj_admina.sql` — kreira proceduru za admin korisnika
3. `04_sp_djelo.sql` — kreira procedure za Djelo

### 2. Konfiguracija

Kopiraj predložak konfiguracije i popuni svoje podatke:

```
src/main/resources/config/config.xml.example  →  src/main/resources/config/config.xml
```

U novom `config.xml` upiši svoju MySQL lozinku:

```xml
<lozinka>TVOJA_STVARNA_LOZINKA</lozinka>
```

> `config.xml` je namjerno u `.gitignore` i ne nalazi se u repozitoriju — svatko postavlja svoju lokalnu konfiguraciju.

### 3. Maven dependencyji

Maven će automatski povući sve potrebne dependencyje (JavaFX, MySQL driver, JAXB, BCrypt) prilikom builda projekta.

## Pokretanje

Kroz IntelliJ IDEA:
1. Otvori projekt (File → Open → odaberi folder s `pom.xml`)
2. Pričekaj da se Maven dependencyji povuku
3. Pokreni `Main.java` (`org.example.app.Main`)

Ili preko Mavena:
```
mvn javafx:run
```

## Zadana prijava

Nakon pokretanja aplikacije prvi put, potrebno je kreirati administratorski račun (jednokratno, preko procedure `sp_kreiraj_admina` — vidi upute u dokumentaciji projekta) ili se registrirati kao novi korisnik uloge "Korisnik" putem forme za registraciju unutar aplikacije.

---

Projekt izrađen u sklopu kolegija na fakultetu, akademska godina 2025/2026.