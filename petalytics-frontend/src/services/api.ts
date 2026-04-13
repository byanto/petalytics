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
    getLocationSummary: async (): Promise<LocationSummary[]> => {
        const response = await apiClient.get<LocationSummary[]>('/analytics/location-summary');
        return response.data;
    },
    getChannelSummary: async (): Promise<ChannelSummary[]> => {
        const response = await apiClient.get<ChannelSummary[]>('/analytics/channel-summary');
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