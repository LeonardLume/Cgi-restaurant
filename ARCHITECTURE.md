# Arhitektuur

## Süsteemi piirid

Rakendus koosneb kahest iseseisvalt käivitatavast osast:

- Spring Boot backend, mis exposeb REST API;
- React ja Vite frontend, mis tarbib backend teenust.

Vaikimisi töötab frontend aadressil `http://localhost:5173` ja backend aadressil `http://localhost:8080`.

```text
Frontend (React, Vite)
                |
                | HTTP /api/*
                v
Backend (Spring Boot)
                |
                v
Database (H2 dev / PostgreSQL prod)
```

## Backend kihistus

Backend järgib klassikalist `controller -> service -> repository` jaotust.

```text
HTTP request
    -> controller
    -> service
    -> repository
    -> database
```

### Controller kiht

Controller kiht tegeleb HTTP sisendi ja väljundiga. CORS ei ole määratud annotatsioonidega kontrollerite peal, vaid tsentraalselt `WebConfig` kaudu.

Peamised kontrollerid:

| Klass | Vastutus |
|-------|----------|
| `FloorPlanController` | saaliplaani ja laudade nimekirja väljastamine |
| `RecommendationController` | laudade soovituste päring |
| `ReservationController` | broneeringu loomine ja tühistamine |

### Service kiht

Service kiht sisaldab äriloogikat.

| Klass | Vastutus |
|-------|----------|
| `RecommendationService` | kandidaatide filtreerimine, skoorimine, järjestamine |
| `ScoringService` | suuruse ja eelistuste põhine skoorimudel |
| `ReservationService` | broneeringu loomine, tühistamine, domeenivead |
| `AvailabilityService` | ajavahemiku konfliktide kontroll |
| `FloorPlanService` | laudade konverteerimine DTO-deks koos staatusega |
| `DemoDataService` | demoandmete genereerimine käivitamisel |

`ReservationService` on märgitud `@Transactional` annotatsiooniga. Sisendivalideerimine toimub DTO tasemel Bean Validation abil ning domeenivead teisendatakse HTTP vastusteks globaalse exception handleri kaudu.

### Repository kiht

Repository kiht kasutab Spring Data JPA-d.

Olulisemad päringud:

- `TableRepository.findByCapacityGreaterThanEqual(...)`
- `TableRepository.findByZoneAndCapacityGreaterThanEqual(...)`
- `ReservationRepository.findConflictingReservations(...)`
- `ReservationRepository.findActiveReservationsInTimeRange(...)`

Ajaintervallide kattuvus põhineb tingimusel:

```text
existing.start < requested.end AND existing.end > requested.start
```

### Exception kiht

`exception/` pakett eraldab domeenivead HTTP esitusest.

Peamised klassid:

- `ResourceNotFoundException`
- `ConflictException`
- `GlobalExceptionHandler`

See kiht määrab `400`, `404`, `409` ja `500` vastused ühtse `ErrorResponse` mudeli kaudu.

## Andmemudel

### `RestaurantTable`

Laua olem sisaldab:

- identifikaatorit ja nime;
- mahutavust;
- tsooni;
- SVG paigutuse koordinaate ja mõõtmeid;
- omadusi `nearWindow`, `quiet`, `accessible`, `nearKidsArea`;
- välja `combinableGroupId`, mida praegune äriloogika ei kasuta.

### `Reservation`

Broneeringu olem sisaldab:

- kliendi nime;
- laua identifikaatorit;
- ajavahemikku;
- seltskonna suurust;
- staatust `ACTIVE`, `CANCELLED` või `COMPLETED`;
- loomisaega `createdAt`.

Broneering ei kasuta JPA seost `RestaurantTable` olemiga. Seos on esitatud ainult `tableId` väljana.

## DTO mudel

API kasutab eraldi DTO klasse, mitte entitysid otse.

Olulisemad DTO-d:

- `FloorPlanResponse`
- `TableDto`
- `RecommendationRequest`
- `RecommendationResponse`
- `TableRecommendationDto`
- `ReservationRequest`
- `ReservationResponse`
- `ErrorResponse`

See eraldus võimaldab muuta API kuju ilma persistence mudelit ümber kujundamata.

## Soovituste voog

Soovituste päringu töötlus jaguneb kolmeks sammuks.

### 1. Kandidaatide leidmine

`RecommendationService` valib lauad järgmiste tingimuste alusel:

- kui tsoon on määratud, arvestatakse tsooni;
- laua mahutavus peab olema vähemalt `partySize`;
- laud peab olema vaba antud ajavahemikus.

### 2. Skoorimine

`ScoringService` arvutab iga kandidaadi skoori kahest komponendist:

```text
finalScore = (sizeScore * 40 + preferenceScore * 60) / 100
```

`sizeScore` arvestab, kui efektiivselt laud seltskonnale sobib.

`preferenceScore` arvestab järgmisi eelistusi:

- `quiet`
- `nearWindow`
- `accessible`
- `nearKidsArea`

### 3. Sorteerimine

Tulemused sorteeritakse skoori järgi kahanevalt. Esimene element tagastatakse kui `bestTableId` ja `bestTableName`.

## Saaliplaani voog

`FloorPlanService` loeb kõik lauad ja määrab igaühele staatuse, tehes saadavuse kontrolli etteantud ajavahemikus.

Praegune realiseering teeb iga laua kohta eraldi konfliktipäringu. Funktsionaalselt on see korrektne, kuid suurema andmemahu korral ei ole see optimaalne.

## Frontend arhitektuur

Frontend on väike ühevaateline rakendus.

Peamised komponendid:

| Komponent | Vastutus |
|-----------|----------|
| `ReservationPage` | põhileht, lokaalne state, API kutsed |
| `SearchFilters` | otsingu ja eelistuste vorm |
| `FloorPlan` | SVG saaliplaan ja laua valik |

`api.ts` koondab HTTP päringud ja teisendab backend veateated frontendile sobivasse vormi.

`types.ts` peegeldab backend DTO-sid TypeScript tüüpidena. Broneeringu aeg saadetakse frontendist massiivina `number[]`, sest kasutajaliides konstrueerib väärtuse Java `LocalDateTime` deserialiseerimiseks sobivasse vormi.

## Konfiguratsioon

Spring konfiguratsioon on jaotatud kolmeks failiks:

- `application.properties` ühised seaded;
- `application-dev.properties` H2 arenduskeskkond;
- `application-prod.properties` PostgreSQL konfiguratsioon.

Vaikeprofiil on `dev`.

## Demoandmed

`DataInitializer` käivitab rakenduse stardil `DemoDataService.initializeDemoData()`.

Praegune loogika:

- kustutab olemasolevad tabelid ja broneeringud;
- loob 14 lauda kolmes tsoonis;
- loob 5 kuni 8 juhuslikku aktiivset broneeringut.

See tähendab, et arenduskeskkonnas ei säilitata andmeid käivituste vahel.

## Testid

Projektis on teenuskihi unit-testid järgmiste klasside jaoks:

- `ScoringServiceTests`
- `AvailabilityServiceTests`
- `ReservationServiceTests`

Controller-, integratsiooni- ja frontend-testid puuduvad.
