import type { SearchFilters as Filters } from "../types";

type Props = {
  filters: Filters;
  onChange: (f: Filters) => void;
  onSearch: () => void;
  loading: boolean;
};

export default function SearchFilters({ filters, onChange, onSearch, loading }: Props) {
  const update = (partial: Partial<Filters>) => onChange({ ...filters, ...partial });

  const togglePref = (key: keyof Filters["preferences"]) => {
    onChange({
      ...filters,
      preferences: { ...filters.preferences, [key]: !filters.preferences[key] },
    });
  };

  return (
    <div className="filters-panel">
      <div className="section-head">
        <h3>Otsing</h3>
        <span className="section-meta">Leia kiirelt sobiv laud</span>
      </div>

      <div className="filter-row">
        <label>
          Kuupäev
          <input type="date" value={filters.date} onChange={(e) => update({ date: e.target.value })} />
        </label>
        <label>
          Kellaaeg
          <input type="time" value={filters.time} onChange={(e) => update({ time: e.target.value })} />
        </label>
      </div>

      <div className="filter-row">
        <label>
          Külalisi
          <select value={filters.partySize} onChange={(e) => update({ partySize: +e.target.value })}>
            {[1, 2, 3, 4, 5, 6, 7, 8, 10, 12].map((n) => (
              <option key={n} value={n}>{n}</option>
            ))}
          </select>
        </label>
        <label>
          Tsoon
          <select value={filters.zone} onChange={(e) => update({ zone: e.target.value })}>
            <option value="">Kõik</option>
            <option value="MAIN_HALL">Peasaal</option>
            <option value="TERRACE">Terrass</option>
            <option value="PRIVATE_ROOM">Privaatne</option>
          </select>
        </label>
      </div>

      <div className="preferences">
        <label><input type="checkbox" checked={filters.preferences.quiet} onChange={() => togglePref("quiet")} /> Vaikne</label>
        <label><input type="checkbox" checked={filters.preferences.nearWindow} onChange={() => togglePref("nearWindow")} /> Akna lähedal</label>
        <label><input type="checkbox" checked={filters.preferences.accessible} onChange={() => togglePref("accessible")} /> Ligipääsetav</label>
        <label><input type="checkbox" checked={filters.preferences.nearKidsArea} onChange={() => togglePref("nearKidsArea")} /> Lastenurk</label>
      </div>

      <button className="btn-primary" onClick={onSearch} disabled={loading}>
        {loading ? "Otsin..." : "Otsi laudu"}
      </button>
    </div>
  );
}
