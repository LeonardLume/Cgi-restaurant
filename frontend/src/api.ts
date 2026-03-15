import axios from "axios";
import type { SearchFilters, RecommendationResponse, ReservationRequest, ReservationResponse, TableDto } from "./types";

function toErrorMessage(error: unknown, fallback: string): string {
  if (axios.isAxiosError(error)) {
    const message = error.response?.data?.message;
    if (typeof message === "string" && message.trim()) {
      return message;
    }
  }

  if (error instanceof Error && error.message.trim()) {
    return error.message;
  }

  return fallback;
}

// convert date + time strings to the array format that Spring Boot expects for LocalDateTime
function toDateTimeArray(date: string, time: string): number[] {
  const [y, m, d] = date.split("-").map(Number);
  const [h, min] = time.split(":").map(Number);
  return [y, m, d, h, min, 0];
}

export async function getFloorPlan(): Promise<TableDto[]> {
  const res = await axios.get("/api/floor-plan");
  return res.data.tables;
}

export async function getRecommendations(filters: SearchFilters): Promise<RecommendationResponse> {
  try {
    const res = await axios.post("/api/recommendations", {
      dateTime: toDateTimeArray(filters.date, filters.time),
      partySize: filters.partySize,
      zone: filters.zone || null,
      preferences: filters.preferences,
      durationMinutes: filters.durationMinutes,
    });
    return res.data;
  } catch (error) {
    throw new Error(toErrorMessage(error, "Soovituste laadimine ebaõnnestus"));
  }
}

export async function bookTable(payload: ReservationRequest): Promise<ReservationResponse> {
  try {
    const res = await axios.post("/api/reservations", payload);
    return res.data;
  } catch (error) {
    throw new Error(toErrorMessage(error, "Broneerimine ebaõnnestus"));
  }
}
