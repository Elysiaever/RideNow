export enum DriverStatus {
  OFFLINE = 'OFFLINE',       // 休车
  IDLE = 'IDLE',             // 出车/听单中 (无订单)
  PICKUP = 'PICKUP',         // 去接乘客
  IN_TRIP = 'IN_TRIP'        // 行程中 (已接到乘客)
}

export interface OrderInfo {
  id: string;
  passengerLoc: { lng: number; lat: number; address: string }; // 乘客位置
  destLoc: { lng: number; lat: number; address: string };      // 目的地
  startTime?: number; // 行程开始时间
  price?: number;     // 预估/实时金额
}