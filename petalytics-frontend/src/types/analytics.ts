export interface LocationSummary {
    province: string;
    city: string;
    totalOrders: number;
    totalRevenue: number;
}

export interface ChannelSummary {
    marketplace: string;
    totalOrders: number;
    totalRevenue: number;
}