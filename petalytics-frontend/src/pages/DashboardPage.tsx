import { useState, useEffect, useMemo } from 'react';
import { PieChart, Pie, ResponsiveContainer, Tooltip } from 'recharts';
import { analyticsService } from '../services/api';
import type { LocationSummary, ChannelSummary } from '../types/analytics';
import {
    RefreshCw,
    Calendar,
    CheckCircle2,
    Search,
    ArrowUp,
    ArrowDown,
    ChevronLeft,
    ChevronRight
} from 'lucide-react';

export default function DashboardPage() {
    // 1. Data State
    const [locationData, setLocationData] = useState<LocationSummary[]>([]);
    const [channelData, setChannelData] = useState<ChannelSummary[]>([]);
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    // 2. UI State for Platform Ranking Toggle
    const [platformMetric, setPlatformMetric] = useState<'sales' | 'orders'>('sales');
    const [topProvinceMetric, setTopProvinceMetric] = useState<'sales' | 'orders'>('sales');
    const [performanceTab, setPerformanceTab] = useState<'province' | 'city'>('province');
    
    // Add Sorting State for the Performance Table
    const [sortConfig, setSortConfig] = useState<{ key: 'name' | 'orders' | 'sales', direction: 'asc' | 'desc' }>({ key: 'sales', direction: 'desc' });
    const [currentPage, setCurrentPage] = useState(1);
    const [searchQuery, setSearchQuery] = useState('');
    
    // Filter State (Draft vs Applied Pattern)
    interface FilterState {
        marketplace: string | null;
        startDate: string;
        endDate: string;
    }
    const defaultFilters: FilterState = { marketplace: null, startDate: '', endDate: '' };
    const [draftFilters, setDraftFilters] = useState<FilterState>(defaultFilters);
    const [appliedFilters, setAppliedFilters] = useState<FilterState>(defaultFilters);
    const ROWS_PER_PAGE = 10;

    const handleSort = (key: 'name' | 'orders' | 'sales') => {
        let direction: 'asc' | 'desc' = 'desc';
        // If clicking the same column that is already descending, flip it to ascending
        if (sortConfig.key === key && sortConfig.direction === 'desc') {
            direction = 'asc';
        }
        setSortConfig({ key, direction });
    };

    // Reset pagination to page 1 whenever the user changes tabs, re-sorts, or types in the search box
    useEffect(() => {
        setCurrentPage(1);
    }, [performanceTab, sortConfig, searchQuery]);

    // 3. Fetch Data on Load
    useEffect(() => {
        const fetchAnalytics = async () => {
            setIsLoading(true); // Re-trigger loading state when switching filters
            
            // Format dates for Spring Boot LocalDateTime (Start of Day / End of Day)
            const formattedStart = appliedFilters.startDate ? `${appliedFilters.startDate}T00:00:00` : null;
            const formattedEnd = appliedFilters.endDate ? `${appliedFilters.endDate}T23:59:59` : null;
            
            try {
                const [locationRes, channelRes] = await Promise.all([
                    analyticsService.getLocationSummary(appliedFilters.marketplace, formattedStart, formattedEnd),
                    analyticsService.getChannelSummary(appliedFilters.marketplace, formattedStart, formattedEnd)
                ]);
                setLocationData(locationRes);
                setChannelData(channelRes);
                setIsLoading(false);
            } catch (err) {
                console.error("Failed to fetch analytics", err);
                setError("Unable to load dashboard data. Is the backend running?");
                setIsLoading(false);
            }
        };
        fetchAnalytics();
    }, [appliedFilters]); // <-- Clean React Pattern! Only fetches when the globally applied filters change.

    const CHART_COLORS = ['#2563EB', '#64748B', '#94A3B8']; // Tailwind Blue-600, Slate-500, Slate-400

    // 4. Data Transformation for Recharts
    const platformChartData = useMemo(() => {
        const sortedData = channelData.map(c => ({
            name: c.marketplace === 'SHOPEE' ? 'Shopee' : 'TikTok',
            value: platformMetric === 'sales' ? c.totalRevenue : c.totalOrders,
        })).sort((a, b) => b.value - a.value); // Sort highest to lowest

        // Recharts 4.0 Standard: Inject 'fill' directly into the data object!
        return sortedData.map((item, index) => ({
            ...item,
            fill: CHART_COLORS[index % CHART_COLORS.length]
        }));
    }, [channelData, platformMetric]);

    const totalPlatformValue = platformChartData.reduce((sum, item) => sum + item.value, 0);

    // 5. Center Column KPI Calculations
    const kpiTotals = useMemo(() => {
        return locationData.reduce((acc, loc) => ({
            sales: acc.sales + loc.totalRevenue,
            orders: acc.orders + loc.totalOrders
        }), {sales: 0, orders: 0})
    }, [locationData]);

    const uniqueCitiesCount = locationData.length;

    // 6. Center Column: Top 5 Provinces Aggregation
    const topProvinces = useMemo(() => {
        // 1. Group the city-level data into Province-level totals
        const provinceMap = new Map<string, { sales: number; orders: number}>();
        locationData.forEach(loc => {
            const current = provinceMap.get(loc.province) || { sales: 0, orders: 0};
            provinceMap.set(loc.province, {
                sales: current.sales + loc.totalRevenue,
                orders: current.orders + loc.totalOrders
            });
        });

        // 2. Convert Map to Array, format, and sort by the selected metric
        const aggregated = Array.from(provinceMap.entries()).map(([name, data]) => ({
            name,
            value: topProvinceMetric === 'sales' ? data.sales : data.orders,
        })).sort((a, b) => b.value - a.value);

        // 3. Take the Top 5 and calculate their percentage relative to the #1 province
        const top5 = aggregated.slice(0, 5);
        const maxVal = top5.length > 0 ? top5[0].value : 1;

        return top5.map(p => ({ ...p, percent: Math.round((p.value / maxVal) * 100)}));

    }, [locationData, topProvinceMetric]);

    // 7. Right Column: Performance Table Data
    const performanceTableData = useMemo(() => {
        const dataMap = new Map<string, { sales: number; orders: number}>();
        locationData.forEach(loc => {
            // Dynamically choose the grouping key based on the active tab!
            const key = performanceTab === 'province' ? loc.province : loc.city;
            const current = dataMap.get(key) || { sales: 0, orders: 0};
            dataMap.set(key, {
                sales: current.sales + loc.totalRevenue,
                orders: current.orders + loc.totalOrders
            });
        });

        return Array.from(dataMap.entries()).map(([name, data]) => ({
            name,
            sales: data.sales,
            orders: data.orders
        }))
        .filter(row => row.name.toLowerCase().includes(searchQuery.toLowerCase())) // <-- The Search Filter!
        .sort((a, b) => {
            // Dynamic sorting logic based on state
            if (a[sortConfig.key] < b[sortConfig.key]) return sortConfig.direction === 'asc' ? -1 : 1;
            if (a[sortConfig.key] > b[sortConfig.key]) return sortConfig.direction === 'asc' ? 1 : -1;
            return 0;
        });
    }, [locationData, performanceTab, sortConfig, searchQuery]); // <-- Added searchQuery dependency!

    // 8. Pagination Math
    const totalPages = Math.ceil(performanceTableData.length / ROWS_PER_PAGE);
    const paginatedData = performanceTableData.slice((currentPage - 1) * ROWS_PER_PAGE, currentPage * ROWS_PER_PAGE);

    if (isLoading) return <div className="p-8 text-center text-gray-500">Loading analytics...</div>;
    if (error) return <div className="p-8 text-center text-red-500">{error}</div>;

    return (
        <div className="space-y-8">
            {/* Filter Bar */}
            <section className="p-6 bg-surface-container-low rounded-xl flex flex-wrap gap-4 items-end">
                <div className="space-y-2">
                    <label className="block text-[10px] font-inter font-bold text-on-surface-variant uppercase tracking-widest px-1">Marketplace</label>
                    <div className="flex gap-2">
                        <button 
                            onClick={() => setDraftFilters(prev => ({ ...prev, marketplace: null }))}
                            className={`px-4 py-2 rounded-full text-sm font-medium flex items-center gap-2 transition-colors ${draftFilters.marketplace === null ? 'bg-secondary-container text-on-secondary-container' : 'bg-surface-container-lowest text-on-surface-variant border border-outline-variant/20 hover:bg-surface-container-low'}`}
                        >
                            {draftFilters.marketplace === null && <CheckCircle2 size={14} />}
                            All Marketplaces
                        </button>
                        <button 
                            onClick={() => setDraftFilters(prev => ({ ...prev, marketplace: 'SHOPEE' }))}
                            className={`px-4 py-2 rounded-full text-sm font-medium flex items-center gap-2 transition-colors ${draftFilters.marketplace === 'SHOPEE' ? 'bg-secondary-container text-on-secondary-container' : 'bg-surface-container-lowest text-on-surface-variant border border-outline-variant/20 hover:bg-surface-container-low'}`}
                        >
                            {draftFilters.marketplace === 'SHOPEE' && <CheckCircle2 size={14} />}
                            Shopee
                        </button>
                        <button 
                            onClick={() => setDraftFilters(prev => ({ ...prev, marketplace: 'TIKTOK' }))}
                            className={`px-4 py-2 rounded-full text-sm font-medium flex items-center gap-2 transition-colors ${draftFilters.marketplace === 'TIKTOK' ? 'bg-secondary-container text-on-secondary-container' : 'bg-surface-container-lowest text-on-surface-variant border border-outline-variant/20 hover:bg-surface-container-low'}`}
                        >
                            {draftFilters.marketplace === 'TIKTOK' && <CheckCircle2 size={14} />}
                            TikTok
                        </button>
                    </div>
                </div>
                <div className="space-y-2">
                    <label className="block text-[10px] font-inter font-bold text-on-surface-variant uppercase tracking-widest px-1">Time Range</label>
                    <div className="flex items-center gap-2">
                        <div className="flex items-center gap-2 bg-surface-container-lowest px-3 py-1.5 rounded-full shadow-sm border border-outline-variant/20 focus-within:border-primary/50 focus-within:ring-2 focus-within:ring-primary/20 transition-all">
                            <Calendar size={14} className="text-on-surface-variant" />
                            <input 
                                type="date" 
                                value={draftFilters.startDate}
                                onChange={(e) => setDraftFilters(prev => ({ ...prev, startDate: e.target.value }))}
                                className="text-sm font-medium bg-transparent outline-none text-on-surface w-28 cursor-pointer"
                            />
                        </div>
                        <span className="text-on-surface-variant text-sm font-bold">to</span>
                        <div className="flex items-center gap-2 bg-surface-container-lowest px-3 py-1.5 rounded-full shadow-sm border border-outline-variant/20 focus-within:border-primary/50 focus-within:ring-2 focus-within:ring-primary/20 transition-all">
                            <Calendar size={14} className="text-on-surface-variant" />
                            <input 
                                type="date" 
                                value={draftFilters.endDate}
                                onChange={(e) => setDraftFilters(prev => ({ ...prev, endDate: e.target.value }))}
                                className="text-sm font-medium bg-transparent outline-none text-on-surface w-28 cursor-pointer"
                            />
                        </div>
                    </div>
                </div>
                <div className="ml-auto flex items-center gap-4">
                    {/* Global Reset Button */}
                    {(draftFilters.marketplace !== null || draftFilters.startDate !== '' || draftFilters.endDate !== '') && (
                        <button 
                            onClick={() => { setDraftFilters(defaultFilters); setAppliedFilters(defaultFilters); }}
                            className="text-xs font-bold text-on-surface-variant hover:text-primary transition-colors"
                        >
                            Reset All
                        </button>
                    )}
                    {/* Master Apply Button */}
                    <button 
                        onClick={() => setAppliedFilters(draftFilters)}
                        className="flex items-center gap-2 px-6 py-2.5 bg-primary hover:bg-primary/90 text-white rounded-full font-bold text-sm shadow-md active:scale-95 transition-all"
                    >
                        <RefreshCw size={14} />
                        Apply Filters
                    </button>
                </div>
            </section>

            {/* Row 1: KPI Cards */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="glass-panel rounded-xl p-6 relative overflow-hidden group">
                    <div className="relative z-10">
                        <p className="text-on-surface-variant font-inter text-sm font-medium mb-1 uppercase tracking-wider">Total Sales | IDR</p>
                        <h2 className="text-4xl font-bold text-primary tracking-tight mt-2">
                            {new Intl.NumberFormat('id-ID', { maximumFractionDigits: 0 }).format(kpiTotals.sales)}
                        </h2>
                    </div>
                    <div className="absolute -right-8 -bottom-8 w-32 h-32 bg-primary/5 rounded-full blur-2xl"></div>
                </div>
                <div className="glass-panel rounded-xl p-6 relative overflow-hidden group">
                    <div className="relative z-10">
                        <p className="text-on-surface-variant font-inter text-sm font-medium mb-1 uppercase tracking-wider">Total Orders</p>
                        <h2 className="text-4xl font-bold text-primary tracking-tight mt-2">
                            {new Intl.NumberFormat('id-ID').format(kpiTotals.orders)}
                        </h2>
                    </div>
                    <div className="absolute -right-8 -bottom-8 w-32 h-32 bg-primary/5 rounded-full blur-2xl"></div>
                </div>
                <div className="glass-panel rounded-xl p-6 relative overflow-hidden group">
                    <div className="relative z-10">
                        <p className="text-on-surface-variant font-inter text-sm font-medium mb-1 uppercase tracking-wider">Cities Reached</p>
                        <h2 className="text-4xl font-bold text-primary tracking-tight mt-2">
                            {uniqueCitiesCount}
                        </h2>
                    </div>
                    <div className="absolute -right-8 -bottom-8 w-32 h-32 bg-primary/5 rounded-full blur-2xl"></div>
                </div>
            </div>

            {/* Row 2: Charts Split */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                
                {/* Left Chart: Platform Ranking */}
                <div className="glass-panel rounded-xl p-6 lg:col-span-1 flex flex-col">
                    <h3 className="font-bold text-lg mb-4">Platform Ranking</h3>
                    <div className="flex p-1 bg-surface-container-low rounded-full mb-6 w-fit mx-auto">
                        <button 
                            onClick={() => setPlatformMetric('sales')}
                            className={`px-6 py-1.5 text-xs font-bold rounded-full transition-all ${platformMetric === 'sales' ? 'bg-surface-container-lowest text-primary shadow-sm' : 'text-on-surface-variant hover:text-primary'}`}>
                            Sales
                        </button>
                        <button 
                            onClick={() => setPlatformMetric('orders')}
                            className={`px-6 py-1.5 text-xs font-bold rounded-full transition-all ${platformMetric === 'orders' ? 'bg-surface-container-lowest text-primary shadow-sm' : 'text-on-surface-variant hover:text-primary'}`}>
                            Orders
                        </button>
                    </div>

                    <div className="flex flex-col items-center justify-center flex-1">
                        <div className="relative w-48 h-48 mb-8">
                            <ResponsiveContainer width="100%" height="100%">
                                <PieChart>
                                    <Pie
                                        data={platformChartData}
                                        cx="50%"
                                        cy="50%"
                                        innerRadius={65}
                                        outerRadius={85}
                                        paddingAngle={5}
                                        dataKey="value"
                                        nameKey="name"
                                        stroke="none"
                                    />
                                    <Tooltip formatter={(value: any) => platformMetric === 'sales' ? `IDR ${Number(value).toLocaleString('id-ID')}` : Number(value).toLocaleString('id-ID')} />
                                </PieChart>
                            </ResponsiveContainer>
                            <div className="absolute inset-0 flex flex-col items-center justify-center text-center pointer-events-none">
                                <span className="text-[10px] font-inter text-on-surface-variant uppercase">{platformChartData[0]?.name || 'No Data'}</span>
                                <span className="text-2xl font-bold text-primary">
                                    {totalPlatformValue > 0 && platformChartData[0] ? Math.round((platformChartData[0].value / totalPlatformValue) * 100) : 0}%
                                </span>
                            </div>
                        </div>

                        <div className="w-full space-y-3">
                            {platformChartData.map((item, index) => (
                                <div key={item.name} className="p-3 rounded-lg bg-surface-container-low border border-outline-variant/10">
                                    <div className="flex items-center justify-between">
                                        <div className="flex items-center gap-2">
                                            <div className="w-3 h-3 rounded-full" style={{ backgroundColor: CHART_COLORS[index % CHART_COLORS.length] }}></div>
                                            <span className="text-xs font-bold text-primary">{item.name}</span>
                                        </div>
                                        <span className="text-xs font-extrabold text-primary">
                                            {totalPlatformValue > 0 ? Math.round((item.value / totalPlatformValue) * 100) : 0}% ({platformMetric === 'sales' ? `IDR ${item.value.toLocaleString()}` : item.value.toLocaleString()})
                                        </span>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>

                {/* Right Chart: Top 5 Provinces */}
                <div className="glass-panel rounded-xl p-8 lg:col-span-2 flex flex-col">
                    <div className="flex flex-wrap justify-between items-center gap-4 mb-8">
                        <h3 className="font-bold text-lg">Top 5 Province Distribution</h3>
                        <div className="flex items-center gap-4">
                            <div className="flex p-1 bg-surface-container-low rounded-lg">
                                <button 
                                    onClick={() => setTopProvinceMetric('sales')}
                                    className={`px-4 py-1.5 text-[10px] font-bold rounded-md transition-all uppercase tracking-wider ${topProvinceMetric === 'sales' ? 'bg-surface-container-lowest text-primary shadow-sm' : 'text-on-surface-variant hover:text-primary'}`}>
                                    Sales
                                </button>
                                <button 
                                    onClick={() => setTopProvinceMetric('orders')}
                                    className={`px-4 py-1.5 text-[10px] font-bold rounded-md transition-all uppercase tracking-wider ${topProvinceMetric === 'orders' ? 'bg-surface-container-lowest text-primary shadow-sm' : 'text-on-surface-variant hover:text-primary'}`}>
                                    Orders
                                </button>
                            </div>
                        </div>
                    </div>
                    
                    <div className="space-y-8 flex-1 justify-center flex flex-col">
                        {topProvinces.map((province) => (
                            <div key={province.name} className="space-y-2">
                                <div className="flex justify-between text-xs font-inter font-semibold">
                                    <span className="text-on-surface-variant">{province.name}</span>
                                    <span className="text-primary">
                                        {topProvinceMetric === 'sales' ? `IDR ${province.value.toLocaleString('id-ID')}` : province.value.toLocaleString('id-ID')}
                                    </span>
                                </div>
                                <div className="h-2.5 bg-surface-container-low rounded-full overflow-hidden">
                                    <div
                                        className="h-full bg-primary rounded-full transition-all duration-1000"
                                        style={{ width: `${province.percent}%` }}
                                    ></div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>

            {/* Row 3: Performance Table */}
            <div className="glass-panel rounded-xl overflow-hidden flex flex-col">
                <div className="p-6 border-b border-outline-variant/10 flex flex-col md:flex-row md:items-center justify-between gap-4">
                    <h3 className="font-bold text-lg">Performance Ledger</h3>
                    <div className="flex flex-col sm:flex-row gap-4 items-center w-full md:w-auto">
                        <div className="flex p-1 bg-surface-container-low rounded-lg w-full sm:w-64">
                            <button 
                                onClick={() => setPerformanceTab('province')}
                                className={`flex-1 py-1.5 text-[10px] font-bold rounded-md transition-all uppercase tracking-wider ${performanceTab === 'province' ? 'bg-surface-container-lowest text-primary shadow-sm' : 'text-on-surface-variant hover:text-primary'}`}>
                                Province
                            </button>
                            <button 
                                onClick={() => setPerformanceTab('city')}
                                className={`flex-1 py-1.5 text-[10px] font-bold rounded-md transition-all uppercase tracking-wider ${performanceTab === 'city' ? 'bg-surface-container-lowest text-primary shadow-sm' : 'text-on-surface-variant hover:text-primary'}`}>
                                City
                            </button>
                        </div>
                        <div className="relative w-full sm:w-64">
                            <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-on-surface-variant/50" />
                            <input
                                type="text"
                                placeholder={`Search ${performanceTab === 'province' ? 'Province' : 'City'}...`}
                                value={searchQuery}
                                onChange={(e) => setSearchQuery(e.target.value)}
                                className="w-full pl-10 pr-4 py-2 bg-surface-container-low border border-outline-variant/20 rounded-lg text-xs focus:ring-2 focus:ring-primary/20 focus:border-primary/40 transition-all outline-none text-on-surface font-medium"
                            />
                        </div>
                    </div>
                </div>
                <div className="overflow-x-auto w-full">
                    <table className="w-full text-left whitespace-nowrap">
                        <thead>
                        <tr className="bg-surface-container-low/50">
                            <th 
                                className="px-6 py-4 font-inter text-[10px] uppercase tracking-widest text-on-surface-variant font-bold cursor-pointer hover:bg-surface-container-low transition-colors"
                                onClick={() => handleSort('name')}
                            >
                                <div className="flex items-center gap-1">
                                    {performanceTab === 'province' ? 'Province' : 'City'}
                                    {sortConfig.key === 'name' && (sortConfig.direction === 'asc' ? <ArrowUp size={12} /> : <ArrowDown size={12} />)}
                                </div>
                            </th>
                            <th 
                                className="px-6 py-4 font-inter text-[10px] uppercase tracking-widest text-on-surface-variant font-bold text-right cursor-pointer hover:bg-surface-container-low transition-colors"
                                onClick={() => handleSort('orders')}
                            >
                                <div className="flex items-center justify-end gap-1">
                                    Orders
                                    {sortConfig.key === 'orders' && (sortConfig.direction === 'asc' ? <ArrowUp size={12} /> : <ArrowDown size={12} />)}
                                </div>
                            </th>
                            <th 
                                className="px-6 py-4 font-inter text-[10px] uppercase tracking-widest text-on-surface-variant font-bold text-right cursor-pointer hover:bg-surface-container-low transition-colors"
                                onClick={() => handleSort('sales')}
                            >
                                <div className="flex items-center justify-end gap-1">
                                    Sales
                                    {sortConfig.key === 'sales' && (sortConfig.direction === 'asc' ? <ArrowUp size={12} /> : <ArrowDown size={12} />)}
                                </div>
                            </th>
                        </tr>
                        </thead>
                        <tbody className="divide-y divide-outline-variant/10">
                        {paginatedData.map((row) => (
                            <tr key={row.name} className="hover:bg-surface-container-low transition-colors">
                                <td className="px-6 py-4 text-sm font-medium text-primary">{row.name}</td>
                                <td className="px-6 py-4 text-sm font-bold text-right text-on-surface-variant">
                                    {new Intl.NumberFormat('id-ID').format(row.orders)}
                                </td>
                                <td className="px-6 py-4 text-sm font-bold text-right text-primary">
                                    IDR {new Intl.NumberFormat('id-ID').format(row.sales)}
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
                <div className="p-4 bg-surface-container-low/30 border-t border-outline-variant/10 flex items-center justify-between">
                    <span className="text-[10px] font-bold text-on-surface-variant uppercase tracking-wider">
                        Page {currentPage} of {totalPages || 1}
                    </span>
                    <div className="flex gap-2">
                        <button 
                            onClick={() => setCurrentPage(p => Math.max(1, p - 1))}
                            disabled={currentPage === 1}
                            className="p-1.5 rounded bg-surface-container-lowest border border-outline-variant/20 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-surface-container-low transition-colors"
                        >
                            <ChevronLeft size={16} className="text-on-surface-variant" />
                        </button>
                        <button 
                            onClick={() => setCurrentPage(p => Math.min(totalPages, p + 1))}
                            disabled={currentPage === totalPages || totalPages === 0}
                            className="p-1.5 rounded bg-surface-container-lowest border border-outline-variant/20 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-surface-container-low transition-colors"
                        >
                            <ChevronRight size={16} className="text-on-surface-variant" />
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}
