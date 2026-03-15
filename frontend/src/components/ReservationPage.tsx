import { useState, useEffect } from "react";
import type { SearchFilters as FiltersType, TableDto, RecommendationResponse } from "../types";
import { getFloorPlan, getRecommendations, bookTable } from "../api";
import SearchFilters from "./SearchFilters";
import FloorPlan from "./FloorPlan";

const today = new Date().toISOString().slice(0, 10);

const defaultFilters: FiltersType = {
  date: today,
  time: "18:00",
  partySize: 2,
  zone: "",
  durationMinutes: 120,
  preferences: { quiet: false, nearWindow: false, accessible: false, nearKidsArea: false },
};

// helper: adds minutes to a datetime array
function addMinutes(dt: number[], mins: number): number[] {
  const d = new Date(dt[0], dt[1] - 1, dt[2], dt[3], dt[4]);
  d.setMinutes(d.getMinutes() + mins);
  return [d.getFullYear(), d.getMonth() + 1, d.getDate(), d.getHours(), d.getMinutes(), 0];
}

function toDateTimeArray(date: string, time: string): number[] {
  const [y, m, d] = date.split("-").map(Number);
  const [h, min] = time.split(":").map(Number);
  return [y, m, d, h, min, 0];
}

export default function ReservationPage() {
  const [filters, setFilters] = useState(defaultFilters);
  const [tables, setTables] = useState<TableDto[]>([]);
  const [recommendation, setRecommendation] = useState<RecommendationResponse | null>(null);
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [customerName, setCustomerName] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // load floor plan on mount
  useEffect(() => {
    getFloorPlan()
      .then(setTables)
      .catch(() => setError("Saaliplaani laadimine ebaõnnestus"));
  }, []);

  const handleSearch = async () => {
    setLoading(true);
    setError("");
    setSuccess("");
    setSelectedId(null);
    try {
      const rec = await getRecommendations(filters);
      setRecommendation(rec);
      if (rec.bestTableId) setSelectedId(rec.bestTableId);
    } catch (error) {
      setError(error instanceof Error ? error.message : "Soovituste laadimine ebaõnnestus");
    } finally {
      setLoading(false);
    }
  };

  const handleBook = async () => {
    if (!selectedId || !customerName.trim()) return;
    setLoading(true);
    setError("");
    try {
      const startTime = toDateTimeArray(filters.date, filters.time);
      const endTime = addMinutes(startTime, filters.durationMinutes);
      await bookTable({
        customerName: customerName.trim(),
        tableId: selectedId,
        partySize: filters.partySize,
        startTime,
        endTime,
      });
      setSuccess("Broneering kinnitatud!");
    } catch (error) {
      setError(error instanceof Error ? error.message : "Broneerimine ebaõnnestus");
    } finally {
      setLoading(false);
    }
  };

  const selectedTable = tables.find((t) => t.id === selectedId);
  const bestRecommendation = recommendation?.recommendedTables[0] ?? null;

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <p className="eyebrow">Restaurant Booking</p>
          <h1>Laua broneerimine</h1>
          <p className="page-subtitle">Leia sobiv laud, võrdle soovitusi ja kinnita broneering mõne klikiga.</p>
        </div>

        <div className="header-summary">
          <span className="summary-pill">{tables.length} lauda saalis</span>
          <span className="summary-pill">{filters.partySize} külalist</span>
          <span className="summary-pill">{filters.time}</span>
        </div>
      </div>

      <div className="layout">
        <div className="sidebar">
          <SearchFilters filters={filters} onChange={setFilters} onSearch={handleSearch} loading={loading} />

          {bestRecommendation && (
            <div className="card spotlight-card">
              <div className="section-head">
                <h3>Parim valik</h3>
                <span className="spotlight-score">Skoor {bestRecommendation.score}</span>
              </div>
              <div className="spotlight-name">{bestRecommendation.tableName}</div>
              <p className="spotlight-reason">{bestRecommendation.reason}</p>
            </div>
          )}

          {recommendation && recommendation.recommendedTables.length > 0 && (
            <div className="card rec-card">
              <div className="section-head">
                <h3>Soovitused</h3>
                <span className="section-meta">{recommendation.recommendedTables.length} varianti</span>
              </div>
              {recommendation.recommendedTables.map((rec) => (
                <div
                  key={rec.tableId}
                  className={[
                    "rec-item",
                    rec.tableId === recommendation.bestTableId ? "rec-best" : "",
                    rec.tableId === selectedId ? "rec-selected" : "",
                  ].filter(Boolean).join(" ")}
                  onClick={() => setSelectedId(rec.tableId)}
                >
                  <div className="rec-topline">
                    <strong>{rec.tableName}</strong>
                    <div className="rec-badges">
                      {rec.tableId === recommendation.bestTableId && <span className="rec-badge rec-badge-best">Parim</span>}
                      {rec.tableId === selectedId && <span className="rec-badge rec-badge-selected">Valitud</span>}
                      <span className="rec-score">{rec.score}</span>
                    </div>
                  </div>
                  <p className="rec-reason">{rec.reason}</p>
                </div>
              ))}
            </div>
          )}

          {recommendation && recommendation.recommendedTables.length === 0 && (
            <div className="card empty-card">
              <h3>Soovitused</h3>
              <p className="empty-copy">Sobivaid laudu ei leitud. Proovi muuta kellaaega, tsooni või eelistusi.</p>
            </div>
          )}

          {selectedTable && (
            <div className="card booking-card">
              <div className="section-head">
                <h3>Broneerimine</h3>
                <span className="section-meta">Laua valik kinnitatud</span>
              </div>
              <div className="booking-info">
                <p><strong>{selectedTable.name}</strong> • {selectedTable.capacity} kohta</p>
                <p>{filters.date} • kell {filters.time}</p>
                <p>{filters.partySize} külalist • kestus {filters.durationMinutes} min</p>
              </div>
              <label className="name-field">
                Nimi
                <input
                  type="text"
                  value={customerName}
                  onChange={(e) => setCustomerName(e.target.value)}
                  placeholder="Teie nimi"
                />
              </label>
              <button
                className="btn-primary"
                disabled={!customerName.trim() || loading}
                onClick={handleBook}
              >
                {loading ? "Broneerin..." : "Broneeri"}
              </button>
            </div>
          )}

          {success && <div className="msg-success">{success}</div>}
          {error && <div className="msg-error">{error}</div>}
        </div>

        <div className="main-area">
          <div className="card floorplan-card">
            <div className="section-head">
              <h3>Saaliplaan</h3>
              <span className="section-meta">
                {selectedTable ? `Valitud: ${selectedTable.name}` : "Vali laud kaardilt või soovitustest"}
              </span>
            </div>

          {tables.length === 0 ? (
            <p className="loading-copy">Laadimine...</p>
          ) : (
            <FloorPlan
              tables={tables}
              recommendedId={recommendation?.bestTableId ?? null}
              selectedId={selectedId}
              onSelect={(id) => { setSelectedId(id); setSuccess(""); }}
            />
          )}
          </div>
        </div>
      </div>
    </div>
  );
}
