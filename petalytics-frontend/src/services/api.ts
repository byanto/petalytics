import axios from 'axios';
import type { LocationSummary, ChannelSummary } from '../types/analytics';

// 1. Create a base Axios instance
export const apiClient = axios.create({
    baseURL: 'http://localhost:8080/api',
    headers: {
        'Content-Type': 'application/json',
    },
});

// 2. Define our specific API calls
export const analyticsService = {
    getLocationSummary: async (marketplace?: string | null, startDate?: string | null, endDate?: string | null): Promise<LocationSummary[]> => {
        const params: Record<string, string> = {};
        if (marketplace) params.marketplace = marketplace;
        if (startDate) params.startDate = startDate;
        if (endDate) params.endDate = endDate;

        const response = await apiClient.get<LocationSummary[]>('/analytics/location-summary', { params });
        return response.data;
    },
    getChannelSummary: async (marketplace?: string | null, startDate?: string | null, endDate?: string | null): Promise<ChannelSummary[]> => {
        const params: Record<string, string> = {};
        if (marketplace) params.marketplace = marketplace;
        if (startDate) params.startDate = startDate;
        if (endDate) params.endDate = endDate;

        const response = await apiClient.get<ChannelSummary[]>('/analytics/channel-summary', { params });
        return response.data;
    }
};

export const ingestionService = {
    uploadCsv: async (file: File, marketplace: string): Promise<void> => {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('marketplace', marketplace);

        await apiClient.post('/ingestion/upload', formData, {
            headers: {'Content-Type': 'multipart/form-data'}
        });
    }
};