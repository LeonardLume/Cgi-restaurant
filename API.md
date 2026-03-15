# API dokumentatsioon

Dokument kirjeldab backend teenuse HTTP liidest seisuga käesolevas repoes.

## Üldtingimused

- baas-URL: `http://localhost:8080`
- sisuformaat: `application/json`
- autentimine puudub
- CORS on lubatud kõigile allikatele teekonnal `/api/**`

Kuupäeva ja kellaaja väljad on dokumenteeritud ISO 8601 `LocalDateTime` formaadis, näiteks `2026-03-20T19:00:00`.

## Veamudel

Valideerimis- ja domeenivead tagastatakse ühtses vormingus.

```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Laud sellel ajal ei ole saadaval",
  "timestamp": "2026-03-15T12:00:00",
  "path": "/api/reservations"
}
```

Veakoodid:

- `400 Bad Request` vigane päring või valideerimisviga;
- `404 Not Found` ressurssi ei leitud;
- `409 Conflict` broneeringu konflikt või sobimatu mahutavus;
- `500 Internal Server Error` ootamatu serveriviga.

## GET /api/floor-plan

Tagastab saaliplaani koos laudade staatustega valitud ajavahemikus.

Päringuparameetrid:

| Parameeter | Tüüp | Kohustuslik | Vaikeväärtus |
|------------|------|-------------|--------------|
| `startTime` | `LocalDateTime` | ei | serveri praegune aeg |
| `endTime` | `LocalDateTime` | ei | `startTime + 2h` |

Näidispäring:

```bash
curl "http://localhost:8080/api/floor-plan?startTime=2026-03-20T19:00:00&endTime=2026-03-20T21:00:00"
```

Näidisvastus `200 OK`:

```json
{
  "tables": [
    {
      "id": 1,
      "name": "M1",
      "capacity": 2,
      "zone": "MAIN_HALL",
      "x": 50,
      "y": 100,
      "width": 80,
      "height": 60,
      "nearWindow": false,
      "quiet": true,
      "accessible": false,
      "nearKidsArea": false,
      "combinableGroupId": null,
      "status": "AVAILABLE"
    }
  ],
  "totalTables": 14,
  "availableTables": 12
}
```

Laua staatuse võimalikud väärtused:

- `AVAILABLE`
- `OCCUPIED`

## GET /api/floor-plan/all

Tagastab kõik lauad ilma saadavuse kontrollita.

Näidispäring:

```bash
curl "http://localhost:8080/api/floor-plan/all"
```

Näidisvastus `200 OK`:

```json
[
  {
    "id": 1,
    "name": "M1",
    "capacity": 2,
    "zone": "MAIN_HALL",
    "x": 50,
    "y": 100,
    "width": 80,
    "height": 60,
    "nearWindow": false,
    "quiet": true,
    "accessible": false,
    "nearKidsArea": false,
    "combinableGroupId": null,
    "status": "AVAILABLE"
  }
]
```

## POST /api/recommendations

Tagastab soovitatud lauad antud sisendi alusel.

Päringu keha:

```json
{
  "dateTime": "2026-03-20T19:00:00",
  "partySize": 4,
  "zone": "MAIN_HALL",
  "preferences": {
    "quiet": true,
    "nearWindow": true,
    "accessible": false,
    "nearKidsArea": false
  },
  "durationMinutes": 120
}
```

Väljad:

| Väli | Tüüp | Kohustuslik | Märkused |
|------|------|-------------|----------|
| `dateTime` | `LocalDateTime` | jah | külastuse algus |
| `partySize` | `Integer` | jah | minimaalne väärtus `1` |
| `zone` | `Zone` | ei | `MAIN_HALL`, `TERRACE`, `PRIVATE_ROOM` |
| `preferences` | objekt | ei | kõik väljad on boolean tüüpi |
| `durationMinutes` | `Integer` | ei | vahemik `15..480`, vaikimisi `120` |

Näidisvastus `200 OK`:

```json
{
  "bestTableId": 4,
  "bestTableName": "M4",
  "recommendedTables": [
    {
      "tableId": 4,
      "tableName": "M4",
      "score": 76,
      "reason": "Laud sobib hästi 4-liikmelisele seltskonnale ja asub vaikses kohas."
    },
    {
      "tableId": 3,
      "tableName": "M3",
      "score": 70,
      "reason": "Laud sobib hästi 4-liikmelisele seltskonnale."
    }
  ],
  "message": "Leitud 2 soovitust"
}
```

Kui sobivaid laudu ei leita, tagastatakse samuti `200 OK`, kuid nimekiri on tühi:

```json
{
  "bestTableId": null,
  "bestTableName": null,
  "recommendedTables": [],
  "message": "Sellel ajal ei ole sobivaid laudu saadaval"
}
```

Valideerimisviga tagastatakse koodiga `400 Bad Request` ühtse veamudeli kujul.

## POST /api/reservations

Loob uue broneeringu.

Päringu keha:

```json
{
  "customerName": "Jaan Tamm",
  "tableId": 4,
  "partySize": 4,
  "startTime": "2026-03-20T19:00:00",
  "endTime": "2026-03-20T21:00:00"
}
```

Väljad:

| Väli | Tüüp | Kohustuslik | Märkused |
|------|------|-------------|----------|
| `customerName` | `String` | jah | ei tohi olla tühi |
| `tableId` | `Long` | jah | olemasoleva laua identifikaator |
| `partySize` | `Integer` | jah | minimaalne väärtus `1` |
| `startTime` | `LocalDateTime` | jah | broneeringu algus |
| `endTime` | `LocalDateTime` | jah | peab olema hilisem kui `startTime` |

Näidisvastus `201 Created`:

```json
{
  "id": 42,
  "customerName": "Jaan Tamm",
  "tableId": 4,
  "startTime": "2026-03-20T19:00:00",
  "endTime": "2026-03-20T21:00:00",
  "partySize": 4,
  "status": "ACTIVE",
  "message": "Broneering edukalt loodud"
}
```

Võimalikud vead:

- `400 Bad Request` puuduv või vigane sisend;
- `404 Not Found` lauda ei leitud;
- `409 Conflict` laud ei ole saadaval või mahutavus ei sobi.

Näide `409 Conflict` vastusest:

```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Laud sellel ajal ei ole saadaval",
  "timestamp": "2026-03-15T12:00:00",
  "path": "/api/reservations"
}
```

## DELETE /api/reservations/{id}

Tühistab olemasoleva broneeringu, määrates staatuseks `CANCELLED`.

Näidispäring:

```bash
curl -X DELETE "http://localhost:8080/api/reservations/42"
```

Näidisvastus `200 OK`:

```json
{
  "id": 42,
  "customerName": "Jaan Tamm",
  "tableId": 4,
  "startTime": "2026-03-20T19:00:00",
  "endTime": "2026-03-20T21:00:00",
  "partySize": 4,
  "status": "CANCELLED",
  "message": "Broneering tühistatud"
}
```

Kui broneeringut ei leita, tagastatakse `404 Not Found`.

## Enum väärtused

`Zone`:

| Väärtus | Kirjeldus |
|---------|-----------|
| `MAIN_HALL` | peasaal |
| `TERRACE` | terrass |
| `PRIVATE_ROOM` | privaatne ala |

`ReservationStatus`:

| Väärtus | Kirjeldus |
|---------|-----------|
| `ACTIVE` | aktiivne broneering |
| `CANCELLED` | tühistatud broneering |
| `COMPLETED` | lõpetatud broneering |

## Näidistöövoog

```bash
# 1. Küsi soovitusi
curl -X POST http://localhost:8080/api/recommendations \
  -H "Content-Type: application/json" \
  -d '{
    "dateTime": "2026-03-20T19:00:00",
    "partySize": 4,
    "zone": "MAIN_HALL",
    "preferences": {"quiet": true, "nearWindow": true},
    "durationMinutes": 120
  }'

# 2. Loo broneering ühe soovitatud laua jaoks
curl -X POST http://localhost:8080/api/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Jaan Tamm",
    "tableId": 4,
    "partySize": 4,
    "startTime": "2026-03-20T19:00:00",
    "endTime": "2026-03-20T21:00:00"
  }'

# 3. Tühista broneering
curl -X DELETE "http://localhost:8080/api/reservations/42"
```
