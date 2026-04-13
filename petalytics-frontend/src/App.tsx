import { useState } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import Layout from './components/Layout';
import DashboardPage from './pages/DashboardPage';
import DataImportPage from './pages/DataImportPage';
import type { Screen } from './types';

export default function App() {
    const [currentScreen, setCurrentScreen] = useState<Screen>('dashboard');
    const [direction, setDirection] = useState(0); // 1 for forward (push), -1 for backward (push_back)

    const handleNavigate = (screen: Screen) => {
        if (screen === currentScreen) return;

        if (screen === 'data-import') {
            setDirection(1); // push
        } else {
            setDirection(-1); // push_back
        }

        setCurrentScreen(screen);
    };

    const variants = {
        enter: (direction: number) => ({
            x: direction > 0 ? 500 : -500,
            opacity: 0,
        }),
        center: {
            zIndex: 1,
            x: 0,
            opacity: 1,
        },
        exit: (direction: number) => ({
            zIndex: 0,
            x: direction < 0 ? 500 : -500,
            opacity: 0,
        }),
    };

    return (
        <Layout currentScreen={currentScreen} onNavigate={handleNavigate}>
            <AnimatePresence mode="wait" custom={direction}>
                <motion.div
                    key={currentScreen}
                    custom={direction}
                    variants={variants}
                    initial="enter"
                    animate="center"
                    exit="exit"
                    transition={{
                        x: { type: 'spring', stiffness: 300, damping: 30 },
                        opacity: { duration: 0.2 },
                    }}
                >
                    {currentScreen === 'dashboard' ? <DashboardPage /> : <DataImportPage />}
                </motion.div>
            </AnimatePresence>
        </Layout>
    );
}
