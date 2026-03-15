# Frontend

Frontend on eraldi Vite rakendus, mis kasutab backend teenust teekonna `/api` kaudu.

## Eeldused

- Node.js 18 või uuem
- töötav backend aadressil `http://localhost:8080`

## Käivitamine

```bash
npm install
npm run dev
```

Arenduskeskkonna server kuulab aadressil `http://localhost:5173`.

## Build

```bash
npm run build
```

Build väljund kirjutatakse kataloogi `dist/`.

## Struktuur

```text
src/
├── api.ts
├── App.tsx
├── index.css
├── main.tsx
├── types.ts
└── components/
    ├── FloorPlan.tsx
    ├── ReservationPage.tsx
    └── SearchFilters.tsx
```

## Vastutus

- `ReservationPage.tsx` koondab kasutajaliidese põhivoo;
- `SearchFilters.tsx` haldab otsingusisendi vormi;
- `FloorPlan.tsx` renderdab SVG saaliplaani ja laua seisundid;
- `api.ts` sisaldab HTTP päringuid backendile ning veateadete normaliseerimist;
- `types.ts` sisaldab frontendis kasutatavaid DTO tüüpe.

## Tehnoloogiad

- React 19
- TypeScript
- Vite 8
- Axios

## Sõltuvus backendist

Frontend eeldab, et backend teenus töötab samal masinal pordil `8080`. Vite proxy suunab järgmised päringud backendile:

- `GET /api/floor-plan`
- `POST /api/recommendations`
- `POST /api/reservations`

API detailne kirjeldus on juurkataloogi failis [../API.md](../API.md).
