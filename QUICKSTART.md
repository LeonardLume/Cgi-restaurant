# Kiirjuhend

See juhend kirjeldab minimaalseid samme projekti käivitamiseks kohalikus arenduskeskkonnas.

## Eeldused

- Java 21
- Maven 3.8 või uuem
- Node.js 18 või uuem

Maven wrapper faili (`mvnw`) repoes ei ole. Kasutada tuleb süsteemi paigaldatud `mvn` käsku.

## 1. Kontrolli tööriistu

```bash
java -version
mvn -v
node -v
```

Kui `mvn` ei ole leitav, tuleb Maven lisada süsteemi `PATH` muutujasse enne järgmiste sammude käivitamist.

## 2. Käivita backend

Projektijuurest:

```bash
mvn spring-boot:run
```

Backend kuulab aadressil `http://localhost:8080`.

Vaikimisi kasutatakse `dev` profiili ja H2 in-memory andmebaasi.

## 3. Käivita frontend

Uues terminalis:

```bash
cd frontend
npm install
npm run dev
```

Frontend kuulab aadressil `http://localhost:5173`.

## 4. Tee minimaalne kontroll

Soovitatav kontrollijada:

1. Ava `http://localhost:5173`.
2. Veendu, et saaliplaan laeb.
3. Tee soovituste päring.
4. Loo üks broneering.
5. Proovi luua sama ajavahemiku jaoks sama laua teine broneering ja kontrolli, et tagastatakse konflikt.

## 5. Kontrolli H2 andmebaasi

H2 console:

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- kasutaja: `sa`
- parool: tühi

Näidispäringud:

```sql
SELECT * FROM restaurant_tables;
SELECT * FROM reservations;
```

## 6. Käivita testid

```bash
mvn test
```

Praegune backend testikomplekt sisaldab 13 unit-testi.

## 7. PostgreSQL variandi käivitamine

```bash
docker-compose up --build
```

See käivitab backendi ja PostgreSQL andmebaasi. Frontend tuleb endiselt eraldi käivitada `frontend` kataloogist.

## 8. Levinud probleemid

`mvn` ei ole leitav:

- kontrolli, et Maven on paigaldatud;
- kontrolli, et Maveni `bin` kataloog on lisatud `PATH` muutujasse;
- ava pärast muudatust uus terminal.

Port `8080` on kasutusel:

```powershell
Get-NetTCPConnection -LocalPort 8080
```

Vale Java versioon:

```bash
java -version
```

Projekt eeldab Java 21 kasutamist.

## 9. Edasine viide

- [README.md](README.md)
- [API.md](API.md)
- [ARCHITECTURE.md](ARCHITECTURE.md)
- [IMPLEMENTATION.md](IMPLEMENTATION.md)
