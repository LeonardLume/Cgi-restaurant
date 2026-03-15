// ---- Table ----

export type Zone = "TERRACE" | "MAIN_HALL" | "PRIVATE_ROOM";

export type TableDto = {
  id: number;
  name: string;
  capacity: number;
  zone: Zone;
  x: number;
  y: number;
  width: number;
  height: number;
  nearWindow: boolean;
  quiet: boolean;
  accessible: boolean;
  nearKidsArea: boolean;
  combinableGroupId: number | null;
  status: string; 
};



export type SearchFilters = {
  date: string;
  time: string;
  partySize: number;
  zone: string;
  durationMinutes: number;
  preferences: {
    quiet: boolean;
    nearWindow: boolean;
    accessible: boolean;
    nearKidsArea: boolean;
  };
};

export type RecommendedTable = {
  tableId: number;
  tableName: string;
  score: number;
  reason: string;
};

export type RecommendationResponse = {
  bestTableId: number | null;
  bestTableName: string | null;
  recommendedTables: RecommendedTable[];
  message: string | null;
};



export type ReservationRequest = {
  customerName: string;
  tableId: number;
  partySize: number;
  startTime: number[];
  endTime: number[];
};

export type ReservationResponse = {
  id: number | null;
  customerName: string;
  tableId: number;
  startTime: number[];
  endTime: number[];
  partySize: number;
  status: string;
  message: string | null;
};
