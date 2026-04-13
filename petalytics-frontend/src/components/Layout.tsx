import React, { useState } from 'react';
import {
    LayoutDashboard,
    CloudUpload,
    HelpCircle,
    LogOut,
    Bell,
    Settings,
    ChevronLeft,
    ChevronRight,
    BarChart3
} from 'lucide-react';
import type { Screen } from '../types';
// import { motion, AnimatePresence } from 'motion/react';

interface LayoutProps {
    children: React.ReactNode;
    currentScreen: Screen;
    onNavigate: (screen: Screen) => void;
}

export default function Layout({ children, currentScreen, onNavigate }: LayoutProps) {
    const [isCollapsed, setIsCollapsed] = useState(false);

    return (
        <div className="flex min-h-screen bg-background font-manrope">
            {/* Sidebar */}
            <aside
                className={`fixed left-0 top-0 h-full bg-surface-container-low border-r border-outline-variant/10 flex flex-col py-6 z-50 transition-all duration-300 ${isCollapsed ? 'w-20' : 'w-64'}`}
            >
                <button
                    onClick={() => setIsCollapsed(!isCollapsed)}
                    className="absolute -right-3 top-10 bg-surface-container-lowest border border-outline-variant/20 rounded-full p-1 shadow-md z-50 hover:bg-surface-container-low transition-all"
                >
                    {isCollapsed ? <ChevronRight size={14} className="text-on-surface-variant" /> : <ChevronLeft size={14} className="text-on-surface-variant" />}
                </button>

                <div className="px-6 mb-8">
                    <div className="flex items-center gap-3">
                        <div className="w-10 h-10 rounded-xl bg-primary flex items-center justify-center text-white shrink-0">
                            <BarChart3 size={20} />
                        </div>
                        {!isCollapsed && (
                            <div className="sidebar-text">
                                <h1 className="text-lg font-extrabold text-primary leading-none">Petalytics</h1>
                                <p className="text-[10px] font-inter text-on-surface-variant uppercase tracking-wider">Analytics Suite</p>
                            </div>
                        )}
                    </div>
                </div>

                <nav className="flex-1 px-4 space-y-1">
                    <button
                        onClick={() => onNavigate('dashboard')}
                        className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all duration-200 ${
                            currentScreen === 'dashboard'
                                ? 'text-primary font-bold border-r-2 border-primary bg-primary/5'
                                : 'text-on-surface-variant hover:text-primary hover:translate-x-1'
                        }`}
                    >
                        <LayoutDashboard size={20} />
                        {!isCollapsed && <span className="text-sm font-medium">Dashboard</span>}
                    </button>
                    <button
                        onClick={() => onNavigate('data-import')}
                        className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all duration-200 ${
                            currentScreen === 'data-import'
                                ? 'text-primary font-bold border-r-2 border-primary bg-primary/5'
                                : 'text-on-surface-variant hover:text-primary hover:translate-x-1'
                        }`}
                    >
                        <CloudUpload size={20} />
                        {!isCollapsed && <span className="text-sm font-medium">Data Import</span>}
                    </button>
                </nav>

                <div className="px-4 mt-auto space-y-1">
                    <button className="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-on-surface-variant hover:text-primary transition-colors">
                        <HelpCircle size={20} />
                        {!isCollapsed && <span className="text-sm font-medium">Help Center</span>}
                    </button>
                    <button className="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-on-surface-variant hover:text-primary transition-colors">
                        <LogOut size={20} />
                        {!isCollapsed && <span className="text-sm font-medium">Log Out</span>}
                    </button>
                </div>
            </aside>

            {/* Main Content */}
            <div className={`flex-1 flex flex-col transition-all duration-300 ${isCollapsed ? 'ml-20' : 'ml-64'}`}>
                <header className="sticky top-0 z-40 flex justify-between items-center px-8 py-4 w-full bg-surface-container-lowest/80 backdrop-blur-xl border-b border-outline-variant/10">
                    <div className="flex items-center gap-8">
                        {/* Breadcrumbs or search could go here */}
                    </div>
                    <div className="flex items-center gap-4">
                        <button className="p-2 text-on-surface-variant hover:bg-surface-container-low rounded-full transition-colors">
                            <Bell size={20} />
                        </button>
                        <button className="p-2 text-on-surface-variant hover:bg-surface-container-low rounded-full transition-colors">
                            <Settings size={20} />
                        </button>
                        <div className="flex items-center gap-3 pl-4 border-l border-outline-variant/20">
                            <div className="text-right hidden sm:block">
                                <p className="text-xs font-bold text-on-surface leading-none">Alex Sterling</p>
                                <p className="text-[10px] text-on-surface-variant font-inter">Admin</p>
                            </div>
                            <div className="w-8 h-8 rounded-full overflow-hidden border-2 border-surface-container-low">
                                <img
                                    src="https://picsum.photos/seed/user/100/100"
                                    alt="User profile"
                                    className="w-full h-full object-cover"
                                    referrerPolicy="no-referrer"
                                />
                            </div>
                        </div>
                    </div>
                </header>

                <main className="p-8 flex-1 overflow-x-hidden">
                    {children}
                </main>
            </div>
        </div>
    );
}
