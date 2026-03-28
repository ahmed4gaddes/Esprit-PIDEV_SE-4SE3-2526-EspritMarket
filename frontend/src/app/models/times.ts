export interface Times {
  id?: number;
  type: string;
  startTime: string;   // ISO date string
  endTime?: string;
  duration?: number;   // in minutes
  description?: string;
  liveSessionId?: number;
}
