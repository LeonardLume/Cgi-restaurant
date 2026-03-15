# Implementatsiooni otsused

See dokument kirjeldab peamisi realiseerimisotsuseid, nende põhjuseid ja nendega seotud kompromisse.

## Tehnoloogiavalikud

### Java 21

Valitud on Java 21, kuna see on LTS versioon ja sobib Spring Boot 3.2 rea jaoks ilma täiendava ühilduvuse tööta.

### Spring Boot 3.2.3

Spring Boot katab käesoleva projekti vajadused:

- REST API;
- konfiguratsiooniprofiilid;
- JPA integratsioon;
- Bean Validation;
- testitugi.

### H2 arenduskeskkonnas, PostgreSQL tootmiskonfiguratsioonis

Arenduses kasutatakse H2 in-memory andmebaasi, sest see vähendab lokaalse seadistuse hulka ja teeb rakenduse käivitamise lihtsaks. PostgreSQL tugi on lisatud eraldi profiiliga ja Docker Compose konfiguratsiooniga.

Kompromiss on see, et `dev` profiil ei peegelda täielikult PostgreSQL käitumist ega säilita andmeid käivituste vahel.

## Arhitektuurilised otsused

### Eristatud controller, service ja repository kihid

Controllerid piirduvad HTTP sisendi ja väljundiga. Ärireeglid on teenusklassis ja päringud repositooriumites.

Selle jaotuse põhjused:

- äriloogika on lihtsamini testitav;
- HTTP ja persistence detailid ei segune;
- domeenikäitumist saab muuta ilma API kihti ümber ehitamata.

### DTO-de kasutamine API kihis

Entitysid ei tagastata otse kliendile. API kasutab eraldi DTO mudeleid.

Selle otsuse põhjused:

- persistence mudelit ei pea siduma välise API kujuga;
- veamudel ja vastused on kontrollitavamad;
- frontendile saab anda ainult vajalikud väljad.

## Soovituste algoritm

### Kaalutud skoorimudel

Laua soovitamine on realiseeritud deterministliku skoorimudelina.

```text
finalScore = (sizeScore * 40 + preferenceScore * 60) / 100
```

Selle lähenemise põhjused:

- loogika on lihtsasti jälgitav;
- tulemus on reprodutseeritav;
- kaalusid saab muuta ilma arhitektuuri muutmata.

### Suuruse sobivus eraldi komponendina

`sizeScore` eelistab täpsemat sobivust, kuid lubab suuremaid laudu kontrollitud karistusega.

Oluline konstant:

```java
private static final double OPTIMAL_FILL_RATIO = 0.8;
```

See tähendab, et ideaalne laud ei ole ainult täpselt sama suur kui seltskond, vaid ka mõistliku reserviga, kuni täituvus jääb ligikaudu 80% juurde.

### Eelistuste skoor baasväärtusega

`preferenceScore` algab baasväärtusest `50` ja lisab boonuseid ainult siis, kui laua omadused kattuvad kasutaja eelistustega.

Põhjus on vältida olukorda, kus eelistuste puudumine annaks kunstlikult väga madala tulemuse.

## Ajaintervallide konfliktide kontroll

Broneeringute kattuvus on lahendatud repositooriumi tasemel JPQL päringuga.

```text
existing.start < requested.end AND existing.end > requested.start
```

See tingimus käsitleb õigesti tüüpilisi kattuvuse juhtumeid ja hoiab kontrolli andmebaasi lähedal.

Kompromiss: saadavuse kontroll tehakse mitmes kohas laua kaupa, mis väikese demoandmestiku puhul on piisav, kuid suure mahu korral vajaks koondamist.

## Veahaldus

### Exception-põhine domeenivigade käsitlus

Broneeringu ärivead ei tagastata enam edukate DTO vastuste sees, vaid visatakse eranditena:

- `ResourceNotFoundException`
- `ConflictException`

`GlobalExceptionHandler` teisendab need ühtseks `ErrorResponse` kujuks.

Põhjused:

- HTTP staatuskoodid peegeldavad tegelikku tulemust;
- frontend ei pea järeldama viga `null` väljade põhjal;
- API käitumine on prognoositavam.

### `RecommendationController` jääb funktsionaalselt erinevaks

Soovituste puhul ei ole tühi tulemus käsitletud veana. Kui sobivaid laudu ei leita, tagastatakse `200 OK` ja tühi nimekiri.

See otsus on teadlik, sest tegemist ei ole tehnilise ega domeeniveaga, vaid kehtiva päringu tulemusega.

## Sisendivalideerimine

Valideerimine toimub DTO-de peal Bean Validation annotatsioonidega.

Näited:

- `@NotBlank` kliendi nime jaoks;
- `@Min(1)` seltskonna suuruse jaoks;
- `@AssertTrue` kontrollimaks, et `endTime > startTime`;
- `@Max` ja `@Min` soovituse kestuse jaoks.

Põhjus: lihtsamad süntaktilised vead tuleb peatada enne äriloogikasse jõudmist.

## Konfiguratsioon

### Profiilide eraldamine

Konfiguratsioon on jagatud järgmiselt:

- `application.properties` ühised seaded;
- `application-dev.properties` H2 arenduskeskkond;
- `application-prod.properties` PostgreSQL tootmiskonfiguratsioon.

Põhjus on vältida olukorda, kus dokumentatsioon või käivituskäitumine sõltub ainult ühest kesksest H2 konfiguratsioonist.

## CORS

CORS on määratud tsentraalselt `WebConfig` kaudu kõigile `/api/**` teekondadele.

See on eelistatud lahendus võrreldes annotatsioonide dubleerimisega igas kontrolleris.

Kompromiss: konfiguratsioon on arenduskeskkonna jaoks lai ja ei erista usaldatud allikaid.

## Demoandmed

`DemoDataService` loob käivitamisel fikseeritud laudade komplekti ja juhuslikud aktiivsed broneeringud.

Põhjused:

- rakendus on kohe kasutatav pärast käivitamist;
- UI saab kohe mõistlikku sisendit;
- H2 `create-drop` profiiliga ei ole vaja eraldi migratsioone ega seed-faile.

Kompromiss on juhuslikkus: broneeringute täpne seis on igal käivitamisel veidi erinev.

## Frontendi realiseerimine

### Väike, ühevaateline komponentstruktuur

Frontend on teadlikult hoitud väikese komponendipuuna:

- `ReservationPage` koondab põhivoo;
- `SearchFilters` haldab sisendit;
- `FloorPlan` renderdab saaliplaani.

See vähendab hajutatust väikese projekti puhul, kuid ei ole sama hästi skaleeruv suurema kasutajaliidese korral.

### Backend vigade normaliseerimine `api.ts` failis

Axios vastuste töötlus teisendab backendist tuleva `ErrorResponse.message` välja kasutajale kuvatavaks veateateks.

Põhjus: UI ei peaks tundma backend veamudeli kõiki detaile.

## Testimise valik

Praegu on testid suunatud teenuskihile.

Kaetud on:

- skoorimismudel;
- saadavuse kontroll;
- broneeringu loomise ja tühistamise põhisenaariumid.

Jäetud välja on:

- controller-testid;
- integratsioonitestid;
- frontend-testid.

See valik vähendab algset mahtu, kuid tähendab, et HTTP lepingu katvus ei ole automaatselt kontrollitud.

## Teadlikult edasi lükatud teemad

Praegusest realiseeringust on teadlikult välja jäetud:

- autentimine ja autoriseerimine;
- andmebaasimigratsioonid;
- OpenAPI spetsifikatsioon;
- laua ühendamise loogika `combinableGroupId` välja põhjal;
- frontend testautomaatika.

## Kohandamiskohad

| Teema | Asukoht |
|-------|---------|
| soovituse kaalud | `ScoringService` |
| optimaalne täituvus | `ScoringService.OPTIMAL_FILL_RATIO` |
| demoandmed | `DemoDataService` |
| CORS poliitika | `WebConfig` |
| profiilide andmebaasiseaded | `application-dev.properties`, `application-prod.properties` |
| vaikimisi kestus | `RecommendationRequest.durationMinutes` |

## Märkused hindajale

### Käivitamine ja kontroll

Rakenduse käivitamiseks vajalik minimaalne teave on koondatud failidesse `README.md`, `QUICKSTART.md` ja `API.md`.

Praktiline kontrollijada on järgmine:

1. käivitada backend projektijuurest käsuga `mvn spring-boot:run`;
2. käivitada frontend kataloogist `frontend` käsuga `npm run dev`;
3. avada `http://localhost:5173`;
4. teha soovituse päring, luua broneering ja kontrollida konfliktiolukorda;
5. vajadusel kontrollida H2 console kaudu, et broneeringud on andmebaasi kirjutatud.

### Töö käigus tehtu

Käesoleva versiooni juures tehti järgmised sisulised muudatused:

- backend konfiguratsioon viidi üle profiilidele `dev` ja `prod`;
- broneeringute veahaldus viidi DTO-põhiselt käsitluselt üle HTTP staatuskoodidele ja ühtsele veamudelile;
- DTO valideerimine viidi Bean Validation peale;
- `ScoringService` puhastati magilistest arvudest ja explanation loogika viidi kooskõlla skoorimisega;
- frontendis viidi error handling vastavusse backendist tuleva `ErrorResponse` mudeliga;
- kasutajaliides korrastati, et soovitused, valitud laud ja saaliplaan oleksid loetavamad;
- dokumentatsioon kirjutati ümber nii, et see vastaks tegelikule koodile, mitte varasemale vaheolekule.

### Tööks kulunud aeg


```text
- backend äriloogika ja API: 8h
- frontend kasutajaliides: 2h
- testid: 4h
- dokumentatsioon: 1h
```

### Keerulisemad kohad

Kõige keerulisemad kohad selle lahenduse juures olid järgmised:

- broneeringu vigade eristamine nii, et API annaks korrektseid HTTP koode `400`, `404` ja `409`, mitte ainult tekstisõnumi edukas vastuses;
- backendi ja frontendi kooskõlastamine pärast veamudeli muutmist, et frontend ei katkeks vana DTO eelduste tõttu;
- soovituste skoorimise selgituse hoidmine samas loogikas, mida kasutab tegelik skooriarvutus;
- dokumentatsiooni viimine vastavusse tegeliku koodibaasiga pärast seda, kui käitumine oli muutunud;
- käivitusjuhendi sõnastamine inimesele, kes projekti varem näinud ei ole.

### Kust abi kasutati

Abi kasutati peamiselt järgmistes kohtades:

- Spring Boot ja Bean Validation dokumentatsioon, et kontrollida `@Valid`, exception handleri ja profiilide korrektset kasutust;
- React, Vite ja Axios dokumentatsioon, et hoida frontend error handling kooskõlas backend vastustega;
- editori veateated ja build väljund, et kinnitada, et refaktor ei lõhkunud tüüpe ega kompileerimist;
- AI tööriist abiandjana keerukamates refaktorites, mitte kogu lahenduse automaatseks genereerimiseks.

### Kus AI kasutamine oli sisuliselt põhjendatud


AI abi kasutati järgmistes keerukamates kohtades:

- ajavahemike kattuvuse loogika üle kontrollimiseks ja selle sõnastamiseks nii päringus kui dokumentatsioonis;
- backend veahalduse ümberstruktureerimisel, et liikuda `null` väljadega vastustelt exception-põhisele lahendusele;
- frontend ja backend veamudelite kooskõlastamisel, et backendist tulev `message` jõuaks kasutajaliideses õigesse kohta;
- dokumentatsiooni ümberkirjutamisel pärast suuremat refaktorit, et vältida vastuolusid eri failide vahel.

AI abi ei kasutatud järgmistes asjades täislahenduse asendajana:

- projekti algse struktuuri loomisel;
- kõigi klasside pimesi genereerimisel;
- testide või dokumentatsiooni lisamisel ilma kohalikku koodi üle kontrollimata.



### Eeldused

Lahenduse tegemisel lähtuti järgmistest eeldustest:

- tühi soovituse tulemus ei ole viga, vaid korrektne `200 OK` vastus;
- arenduskeskkonna jaoks piisab H2 in-memory andmebaasist ja demoandmetest;
- käesoleva töö kontekstis ei ole autentimine ega autoriseerimine nõutud;
- üksiku restorani vaates piisab staatustest `AVAILABLE` ja `OCCUPIED` saaliplaani jaoks;
- frontend ja backend töötavad lokaalselt eri portidel ning seetõttu on arenduskeskkonnas lai CORS lubatud.

### Lahendamata või osaliselt lahendatud teemad

Alljärgnevad teemad on teadlikult lahendamata või viidud ainult minimaalselt töötavale tasemele:

- `FloorPlanService` teeb iga laua kohta eraldi saadavuse kontrolli; suurema andmemahu korral tuleks see asendada koondpäringu või batch-lähenemisega;
- controller- ja integratsioonitestid puuduvad; järgmine loogiline samm oleks lisada MockMvc ja vähemalt üks end-to-end API test;
- frontendil puudub automaattestide kiht;
- `combinableGroupId` väli on andmemudelis olemas, kuid ühendatavate laudade loogikat ei kasutata;
- Docker Compose ei käivita frontendi, vaid ainult backendi ja PostgreSQL andmebaasi.


