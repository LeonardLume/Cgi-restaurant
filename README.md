# Restaurant Table Booking Application

Veebirakendus restorani laudade soovitamiseks ja broneerimiseks. Projekt koosneb Spring Boot backendist ja eraldi Vite frontendist.

## Ülevaade

Rakendus katab järgmised põhistsenaariumid:

- saaliplaani kuvamine koos laudade saadavusega;
- laudade soovitamine seltskonna suuruse, tsooni ja eelistuste alusel;
- broneeringu loomine ja tühistamine;
- kohaliku arenduskeskkonna käivitamine H2 või PostgreSQL andmebaasiga.

Backend kasutab vaikimisi `dev` profiili, mis töötab H2 in-memory andmebaasiga. PostgreSQL on toetatud eraldi `prod` profiiliga ja `docker-compose.yml` kaudu.

## Tehnoloogiad

Backend:

- Java 21
- Spring Boot 3.2.3
- Spring Data JPA
- H2
- PostgreSQL
- Maven
- JUnit 5 ja Mockito

Frontend:

- React 19
- TypeScript
- Vite 8
- Axios

## Projekti struktuur

```text
.
├── src/main/java/com/example/restaurantbooking
│   ├── config
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── exception
│   ├── repository
│   └── service
├── src/main/resources
│   ├── application.properties
│   ├── application-dev.properties
│   └── application-prod.properties
├── src/test/java/com/example/restaurantbooking/service
├── frontend
│   ├── src
│   └── package.json
├── API.md
├── ARCHITECTURE.md
├── IMPLEMENTATION.md
├── QUICKSTART.md
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

## Implementitud funktsionaalsus

- REST API saaliplaani, soovituste ja broneeringute jaoks;
- kaalutud skoorimismudel laudade järjestamiseks;
- ajavahemike konfliktide kontroll JPA päringuga;
- Bean Validation päringute valideerimiseks;
- tsentraalne veakäsitlus `@RestControllerAdvice` kaudu;
- eraldi `dev` ja `prod` Spring profiilid;
- React-põhine kasutajaliides saaliplaani, filtrite ja broneerimisvooga;
- unit-testid `ScoringService`, `AvailabilityService` ja `ReservationService` jaoks.

## Käivitamine kohapeal

Eeldused:

- Java 21;
- Maven 3.8 või uuem;
- Node.js 18 või uuem.

Maven wrapper ei ole repoga kaasas. Käskude jaoks tuleb kasutada süsteemi paigaldatud `mvn` binaari.

Backend:

```bash
mvn spring-boot:run
```

Frontend:

```bash
cd frontend
npm install
npm run dev
```

Vaikimisi aadressid:

- backend: `http://localhost:8080`
- frontend: `http://localhost:5173`

Frontend kasutab Vite proxy seadistust ja suunab `/api` päringud backendile.

## Profiilid ja andmebaas

`application.properties` sisaldab ühist konfiguratsiooni. Aktiivne vaikeprofiil on `dev`.

`dev` profiil:

- H2 in-memory andmebaas;
- `ddl-auto=create-drop`;
- H2 console aadressil `http://localhost:8080/h2-console`.

`prod` profiil:

- PostgreSQL;
- ühendusandmed `DB_URL`, `DB_USERNAME` ja `DB_PASSWORD` keskkonnamuutujatest;
- `ddl-auto=update`.

## Docker

`docker-compose.yml` käivitab backendi ja PostgreSQL andmebaasi.

```bash
docker-compose up --build
```

Docker Compose ei käivita frontendi. Frontend tuleb käivitada eraldi `frontend` kataloogist.

## Testid

Backend testide käivitamine:

```bash
mvn test
```

Praegu on repoes 13 unit-testi:

- `ScoringServiceTests`
- `AvailabilityServiceTests`
- `ReservationServiceTests`

Frontendile automaatteste praegu lisatud ei ole.

## API kokkuvõte

| Meetod | Endpoint | Kirjeldus |
|--------|----------|-----------|
| GET | /api/floor-plan | Saaliplaan koos laudade saadavusega |
| GET | /api/floor-plan/all | Kõik lauad ilma saadavuse kontrollita |
| POST | /api/recommendations | Laudade soovitused |
| POST | /api/reservations | Broneeringu loomine |
| DELETE | /api/reservations/{id} | Broneeringu tühistamine |

Detailne kirjeldus on failis [API.md](API.md).

## Piirangud

- autentimine ja autoriseerimine puuduvad;
- controller- ja integratsiooniteste ei ole lisatud;
- frontendil ei ole automaatteste;
- CORS on arenduskeskkonna jaoks avatud kõigile allikatele;
- dokumenteeritud API on sisekasutuse tasemel, versioonihaldust ega OpenAPI spetsifikatsiooni ei ole.

## Lisadokumendid

- [QUICKSTART.md](QUICKSTART.md)
- [API.md](API.md)
- [ARCHITECTURE.md](ARCHITECTURE.md)
- [IMPLEMENTATION.md](IMPLEMENTATION.md)
