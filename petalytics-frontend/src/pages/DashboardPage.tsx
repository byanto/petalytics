// import React from 'react';
import {
    RefreshCw,
    Calendar,
    CheckCircle2,
    Search,
    MoreHorizontal,
    Maximize2,
    BarChart3
} from 'lucide-react';

export default function DashboardPage() {
    return (
        <div className="space-y-8">
            {/* Filter Bar */}
            <section className="p-6 bg-surface-container-low rounded-xl flex flex-wrap gap-4 items-end">
                <div className="space-y-2">
                    <label className="block text-[10px] font-inter font-bold text-on-surface-variant uppercase tracking-widest px-1">Marketplace</label>
                    <div className="flex gap-2">
                        <button className="px-4 py-2 bg-secondary-container text-on-secondary-container rounded-full text-sm font-medium flex items-center gap-2">
                            <CheckCircle2 size={14} />
                            All Marketplaces
                        </button>
                        <button className="px-4 py-2 bg-surface-container-lowest text-on-surface-variant rounded-full text-sm font-medium border border-outline-variant/20 hover:bg-surface-container-low transition-colors">Shopee</button>
                        <button className="px-4 py-2 bg-surface-container-lowest text-on-surface-variant rounded-full text-sm font-medium border border-outline-variant/20 hover:bg-surface-container-low transition-colors">TikTok</button>
                    </div>
                </div>
                <div className="space-y-2">
                    <label className="block text-[10px] font-inter font-bold text-on-surface-variant uppercase tracking-widest px-1">Time Range</label>
                    <div className="flex items-center gap-2 bg-surface-container-lowest px-4 py-2 rounded-md shadow-sm border border-outline-variant/10">
                        <Calendar size={14} className="text-on-surface-variant" />
                        <span className="text-sm font-medium">Last 30 Days (Oct 1 - Oct 30)</span>
                    </div>
                </div>
                <div className="ml-auto">
                    <button className="flex items-center gap-2 px-6 py-2.5 glass-gradient text-white rounded-full font-bold text-sm shadow-md active:scale-95 transition-all">
                        <RefreshCw size={14} />
                        Update Data
                    </button>
                </div>
            </section>

            {/* Main Grid */}
            <div className="grid grid-cols-12 gap-6">
                {/* Left Column: Platform Ranking */}
                <div className="col-span-12 lg:col-span-3">
                    <div className="glass-panel rounded-xl p-6 h-full flex flex-col">
                        <h3 className="font-bold text-lg mb-4">Platform Ranking</h3>
                        <div className="flex p-1 bg-surface-container-low rounded-full mb-6 w-fit mx-auto">
                            <button className="px-6 py-1.5 text-xs font-bold rounded-full transition-all text-on-surface-variant hover:text-primary">
                                Sales
                            </button>
                            <button className="px-6 py-1.5 text-xs font-bold rounded-full transition-all bg-surface-container-lowest text-primary shadow-sm">
                                Orders
                            </button>
                        </div>

                        <div className="flex flex-col items-center justify-center flex-1">
                            <div className="relative w-48 h-48 mb-8">
                                <svg className="w-full h-full transform -rotate-90" viewBox="0 0 100 100">
                                    <circle cx="50" cy="50" fill="transparent" r="40" stroke="var(--color-surface-container-low)" strokeWidth="12"></circle>
                                    <circle cx="50" cy="50" fill="transparent" r="40" stroke="var(--color-primary)" strokeDasharray="175 251" strokeLinecap="round" strokeWidth="12"></circle>
                                    <circle cx="50" cy="50" fill="transparent" r="40" stroke="var(--color-surface-tint)" strokeDasharray="60 251" strokeDashoffset="-175" strokeLinecap="round" strokeWidth="12"></circle>
                                </svg>
                                <div className="absolute inset-0 flex flex-col items-center justify-center text-center">
                                    <span className="text-[10px] font-inter text-on-surface-variant uppercase">Top Tier</span>
                                    <span className="text-2xl font-bold text-primary">68%</span>
                                </div>
                            </div>

                            <div className="w-full space-y-4">
                                <div className="p-3 rounded-lg bg-surface-container-low border border-outline-variant/10">
                                    <div className="flex items-center justify-between">
                                        <div className="flex items-center gap-2">
                                            <div className="w-3 h-3 rounded-full bg-primary"></div>
                                            <span className="text-xs font-bold text-primary">Tier 1</span>
                                        </div>
                                        <span className="text-xs font-extrabold text-primary">68% (99,810)</span>
                                    </div>
                                </div>
                                <div className="p-3 rounded-lg bg-surface-container-low border border-outline-variant/10">
                                    <div className="flex items-center justify-between">
                                        <div className="flex items-center gap-2">
                                            <div className="w-3 h-3 rounded-full bg-surface-tint"></div>
                                            <span className="text-xs font-bold text-primary">Tier 2</span>
                                        </div>
                                        <span className="text-xs font-extrabold text-primary">24% (35,227)</span>
                                    </div>
                                </div>
                                <div className="p-3 rounded-lg bg-surface-container-low border border-outline-variant/10">
                                    <div className="flex items-center justify-between">
                                        <div className="flex items-center gap-2">
                                            <div className="w-3 h-3 rounded-full bg-outline-variant"></div>
                                            <span className="text-xs font-bold text-primary">Others</span>
                                        </div>
                                        <span className="text-xs font-extrabold text-primary">8% (11,742)</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Center Column: KPIs and Map */}
                <div className="col-span-12 lg:col-span-6 space-y-6">
                    {/* KPI Card */}
                    <div className="glass-panel rounded-xl p-8 relative overflow-hidden group">
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-8 relative z-10">
                            <div className="flex flex-col justify-center">
                                <p className="text-on-surface-variant font-inter text-sm font-medium mb-1 uppercase tracking-wider">Sales | IDR</p>
                                <h2 className="text-5xl font-bold text-primary tracking-tight">1,336,355</h2>
                            </div>
                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                <div className="p-4 rounded-lg bg-surface-container-low border border-outline-variant/10">
                                    <p className="text-on-surface-variant font-inter text-xs font-medium mb-1 uppercase tracking-wider">Total Orders</p>
                                    <p className="text-2xl font-bold text-primary">150</p>
                                </div>
                                <div className="p-4 rounded-lg bg-surface-container-low border border-outline-variant/10">
                                    <p className="text-on-surface-variant font-inter text-xs font-medium mb-1 uppercase tracking-wider">Product Sales</p>
                                    <p className="text-2xl font-bold text-primary">1,450</p>
                                </div>
                            </div>
                        </div>
                        <div className="absolute -right-12 -bottom-12 w-48 h-48 bg-primary/5 rounded-full blur-3xl"></div>
                    </div>

                    {/* Top Provinces */}
                    <div className="glass-panel rounded-xl p-8">
                        <div className="flex flex-wrap justify-between items-center gap-4 mb-8">
                            <h3 className="font-bold text-lg">Top 5 Province Distribution</h3>
                            <div className="flex items-center gap-4">
                                <div className="flex p-1 bg-surface-container-low rounded-lg">
                                    <button className="px-4 py-1.5 text-[10px] font-bold rounded-md transition-all bg-surface-container-lowest text-primary shadow-sm uppercase tracking-wider">
                                        Sales
                                    </button>
                                    <button className="px-4 py-1.5 text-[10px] font-bold rounded-md transition-all text-on-surface-variant hover:text-primary uppercase tracking-wider">
                                        Orders
                                    </button>
                                </div>
                                <button className="text-on-surface-variant hover:text-primary transition-colors">
                                    <MoreHorizontal size={20} />
                                </button>
                            </div>
                        </div>
                        <div className="space-y-6">
                            {[
                                { name: 'Jawa Barat', value: '42,501', percent: 85 },
                                { name: 'DKI Jakarta', value: '38,122', percent: 76 },
                                { name: 'Jawa Timur', value: '28,456', percent: 55 },
                                { name: 'Banten', value: '15,890', percent: 32 },
                                { name: 'Jawa Tengah', value: '12,400', percent: 25 },
                            ].map((province) => (
                                <div key={province.name} className="space-y-2">
                                    <div className="flex justify-between text-xs font-inter font-semibold">
                                        <span className="text-on-surface-variant">{province.name}</span>
                                        <span className="text-primary">{province.value}</span>
                                    </div>
                                    <div className="h-2 bg-surface-container-low rounded-full overflow-hidden">
                                        <div
                                            className="h-full bg-primary rounded-full transition-all duration-1000"
                                            style={{ width: `${province.percent}%` }}
                                        ></div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>

                    {/* Geographic Distribution */}
                    <div className="glass-panel rounded-xl p-8">
                        <div className="flex flex-wrap justify-between items-center gap-4 mb-6">
                            <h3 className="font-bold text-lg">Geographic Distribution</h3>
                            <div className="flex items-center gap-2 bg-surface-container-low px-3 py-1.5 rounded-lg border border-outline-variant/10">
                                <div className="w-2.5 h-2.5 rounded-full bg-primary"></div>
                                <span className="text-[10px] font-bold text-primary uppercase tracking-wider">High Density</span>
                            </div>
                        </div>
                        <div className="relative w-full aspect-[2/1] bg-surface-container-low/30 rounded-lg flex items-center justify-center overflow-hidden">
                            {/* Placeholder for Map */}
                            <div className="text-on-surface-variant/20 flex flex-col items-center gap-2">
                                <BarChart3 size={48} />
                                <span className="text-xs font-medium">Interactive Map Visualization</span>
                            </div>

                            <div className="absolute bottom-4 right-4 flex flex-col gap-1 bg-surface-container-lowest/50 backdrop-blur-sm p-2 rounded-lg border border-outline-variant/10">
                                <div className="flex items-center gap-2">
                                    <div className="w-3 h-3 bg-primary rounded-sm"></div>
                                    <span className="text-[9px] font-bold text-on-surface-variant uppercase">Tier 1 (&gt;3k)</span>
                                </div>
                                <div className="flex items-center gap-2">
                                    <div className="w-3 h-3 bg-surface-tint rounded-sm"></div>
                                    <span className="text-[9px] font-bold text-on-surface-variant uppercase">Tier 2 (&gt;1k)</span>
                                </div>
                                <div className="flex items-center gap-2">
                                    <div className="w-3 h-3 bg-secondary-container rounded-sm"></div>
                                    <span className="text-[9px] font-bold text-on-surface-variant uppercase">Other</span>
                                </div>
                            </div>
                        </div>
                        <div className="mt-4 flex justify-between items-center text-[10px] font-medium text-on-surface-variant">
                            <p>Total Coverage: 34 Provinces</p>
                            <button className="flex items-center gap-1 text-primary font-bold hover:underline">
                                Expand Map <Maximize2 size={12} />
                            </button>
                        </div>
                    </div>
                </div>

                {/* Right Column: Performance Table */}
                <div className="col-span-12 lg:col-span-3">
                    <div className="glass-panel rounded-xl overflow-hidden flex flex-col h-full">
                        <div className="p-6 border-b border-outline-variant/10">
                            <h3 className="font-bold text-lg mb-4">Performance</h3>
                            <div className="flex p-1 bg-surface-container-low rounded-lg w-full">
                                <button className="flex-1 py-1.5 text-[10px] font-bold rounded-md transition-all bg-surface-container-lowest text-primary shadow-sm uppercase tracking-wider">
                                    Province
                                </button>
                                <button className="flex-1 py-1.5 text-[10px] font-bold rounded-md transition-all text-on-surface-variant hover:text-primary uppercase tracking-wider">
                                    City
                                </button>
                                <button className="flex-1 py-1.5 text-[10px] font-bold rounded-md transition-all text-on-surface-variant hover:text-primary uppercase tracking-wider">
                                    Product
                                </button>
                            </div>
                            <div className="relative mt-4">
                                <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-on-surface-variant/50" />
                                <input
                                    type="text"
                                    placeholder="Search Province..."
                                    className="w-full pl-10 pr-4 py-2 bg-surface-container-low border border-outline-variant/20 rounded-lg text-xs focus:ring-2 focus:ring-primary/20 focus:border-primary/40 transition-all outline-none text-on-surface font-medium"
                                />
                            </div>
                        </div>
                        <div className="overflow-x-auto flex-1">
                            <table className="w-full text-left">
                                <thead>
                                <tr className="bg-surface-container-low/50">
                                    <th className="px-4 py-3 font-inter text-[10px] uppercase tracking-widest text-on-surface-variant font-bold">Province</th>
                                    <th className="px-4 py-2 font-inter text-[10px] uppercase tracking-widest text-on-surface-variant font-bold text-right">Orders</th>
                                    <th className="px-4 py-2 font-inter text-[10px] uppercase tracking-widest text-on-surface-variant font-bold text-right">Sales</th>
                                </tr>
                                </thead>
                                <tbody className="divide-y divide-outline-variant/10">
                                {[
                                    { name: 'Jawa Barat', orders: '4,250', sales: '45.2M' },
                                    { name: 'DKI Jakarta', orders: '3,812', sales: '42.1M' },
                                    { name: 'Jawa Timur', orders: '2,845', sales: '31.8M' },
                                    { name: 'Banten', orders: '1,589', sales: '18.5M' },
                                    { name: 'Jawa Tengah', orders: '1,240', sales: '14.2M' },
                                    { name: 'Bali', orders: '980', sales: '11.9M' },
                                    { name: 'Sumatera Utara', orders: '850', sales: '9.8M' },
                                    { name: 'Riau', orders: '620', sales: '7.5M' },
                                ].map((row) => (
                                    <tr key={row.name} className="hover:bg-surface-container-low transition-colors">
                                        <td className="px-4 py-3.5 text-xs font-medium text-primary">{row.name}</td>
                                        <td className="px-4 py-3.5 text-xs font-bold text-right text-on-surface-variant">{row.orders}</td>
                                        <td className="px-4 py-3.5 text-xs font-bold text-right text-primary">IDR {row.sales}</td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                        <div className="p-4 bg-surface-container-low/30 border-t border-outline-variant/10 flex justify-center">
                            <button className="text-[10px] font-bold text-primary uppercase tracking-widest hover:underline">View Full Report</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
