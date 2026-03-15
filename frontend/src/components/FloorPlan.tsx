import type { TableDto } from "../types";

type Props = {
  tables: TableDto[];
  recommendedId: number | null;
  selectedId: number | null;
  onSelect: (id: number) => void;
};

// colors for table statuses
const COLORS: Record<string, string> = {
  AVAILABLE: "#69b96b",
  OCCUPIED: "#9da3af",
  RECOMMENDED: "#2793ff",
  SELECTED: "#ff9f1c",
};

function getStatus(
  table: TableDto,
  recommendedId: number | null,
  selectedId: number | null
): string {
  if (table.status === "OCCUPIED") return "OCCUPIED";
  if (table.id === selectedId) return "SELECTED";
  if (table.id === recommendedId) return "RECOMMENDED";
  return "AVAILABLE";
}

export default function FloorPlan({ tables, recommendedId, selectedId, onSelect }: Props) {
  return (
    <div className="floorplan-wrapper">
      <svg viewBox="0 0 700 470" className="floorplan-svg">
        <text x="18" y="25" className="zone-label">Peasaal</text>
        <rect x="10" y="10" width="380" height="260" fill="rgba(255,255,255,0.55)" stroke="#c6d0db" strokeDasharray="6 4" rx="12" />

        <text x="418" y="25" className="zone-label">Terrass</text>
        <rect x="410" y="10" width="280" height="260" fill="rgba(247,244,237,0.75)" stroke="#d4c4a8" strokeDasharray="6 4" rx="12" />

        <text x="18" y="305" className="zone-label">Privaatne</text>
        <rect x="10" y="290" width="280" height="160" fill="rgba(250,247,255,0.82)" stroke="#cabede" strokeDasharray="6 4" rx="12" />

        {tables.map((t) => {
          const status = getStatus(t, recommendedId, selectedId);
          const color = COLORS[status] || "#ccc";
          const clickable = status !== "OCCUPIED";
          const stroke = status === "SELECTED" ? "#7a3f00" : status === "RECOMMENDED" ? "#0d4b88" : color;
          const strokeWidth = status === "SELECTED" ? 3 : status === "RECOMMENDED" ? 2.5 : 1.5;
          const labelColor = status === "OCCUPIED" ? "rgba(255,255,255,0.92)" : "#fff";

          return (
            <g
              key={t.id}
              onClick={() => clickable && onSelect(t.id)}
              style={{ cursor: clickable ? "pointer" : "default" }}
            >
              <rect
                x={t.x} y={t.y} width={t.width} height={t.height}
                rx={10} fill={color} stroke={stroke} strokeWidth={strokeWidth}
                opacity={status === "OCCUPIED" ? 0.58 : 0.96}
              />
              <text
                x={t.x + t.width / 2} y={t.y + t.height / 2 - 5}
                textAnchor="middle" fill={labelColor} fontSize="12" fontWeight="700"
              >
                {t.name}
              </text>
              <text
                x={t.x + t.width / 2} y={t.y + t.height / 2 + 10}
                textAnchor="middle" fill="rgba(255,255,255,0.82)" fontSize="10"
              >
                {t.capacity} kht
              </text>
              <title>{t.name} - {t.capacity} kohta</title>
            </g>
          );
        })}
      </svg>

      <div className="legend">
        <span><i style={{ background: COLORS.AVAILABLE }} /> Vaba</span>
        <span><i style={{ background: COLORS.OCCUPIED }} /> Hõivatud</span>
        <span><i style={{ background: COLORS.RECOMMENDED }} /> Soovitatud</span>
        <span><i style={{ background: COLORS.SELECTED }} /> Valitud</span>
      </div>
    </div>
  );
}
