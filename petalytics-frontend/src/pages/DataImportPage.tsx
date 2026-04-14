import { useState, useRef } from 'react';
import { ingestionService } from '../services/api';
import {
    CloudUpload,
    CheckCircle2,
    FileText,
    Trash2
} from 'lucide-react';

// Define the shape of our history data
interface UploadJob {
    id: string;
    fileName: string;
    date: string;
    status: 'Processing' | 'Completed' | 'Failed';
    marketplace: string;
}

export default function DataImportPage() {
    // 1. React State Management
    const [file, setFile] = useState<File | null>(null);
    const [marketplace, setMarketplace] = useState<string>('SHOPEE');
    const [status, setStatus] = useState<'idle' | 'loading' | 'success' | 'error'>('idle');
    const [message, setMessage] = useState<string>('');
    
    const [uploadHistory, setUploadHistory] = useState<UploadJob[]>([]);

    // 2. Reference to the hidden file input
    const fileInputRef = useRef<HTMLInputElement>(null);

    // 3. Handlers
    const handleMarketplaceChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        setMarketplace(e.target.value);
        // Defensive UX: Clear the file if the source changes, because the required file extension (.csv vs .xlsx) changes!
        setFile(null);
        setStatus('idle');
        setMessage('');
        if (fileInputRef.current) fileInputRef.current.value = '';
    };

    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files && e.target.files.length > 0) {
            setFile(e.target.files[0]);
            setStatus('idle');
            setMessage('');
        }
    };

    const handleRemoveHistory = (jobId: string) => {
        // Filters out the job with the matching ID, effectively deleting it from the UI
        setUploadHistory((prev) => prev.filter(job => job.id !== jobId));
    };

    const handleUpload = async () => {
        if (!file) {
            fileInputRef.current?.click(); // Open file browser if no file selected
            return;
        }

        setStatus('loading');
        
        // Create a new history entry as 'Processing'
        const jobId = Date.now().toString();
        const newJob: UploadJob = {
            id: jobId,
            fileName: file.name,
            date: new Date().toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }),
            status: 'Processing',
            marketplace: marketplace
        };
        
        // Add the new job to the top of the history list
        setUploadHistory((prev) => [newJob, ...prev]);

        try {
            await ingestionService.uploadCsv(file, marketplace);
            setStatus('success');
            setMessage('Data successfully normalized and ingested!');
            
            // Update the specific job in history to 'Completed'
            setUploadHistory((prev) => prev.map(job => job.id === jobId ? { ...job, status: 'Completed' } : job));
            
            setFile(null); // Reset for the next upload
            if (fileInputRef.current) fileInputRef.current.value = '';
        } catch (error: any) {
            setStatus('error');
            setMessage(error.response?.data?.message || 'An unexpected error occurred.');
            
            // Update the specific job in history to 'Failed'
            setUploadHistory((prev) => prev.map(job => job.id === jobId ? { ...job, status: 'Failed' } : job));
        }
    };

    return (
        <div className="max-w-5xl mx-auto space-y-8">
            {/* Page Header */}
            <div className="flex flex-col gap-1">
                <h2 className="text-3xl font-extrabold text-on-surface tracking-tight">Data Import</h2>
                <p className="text-on-surface-variant font-inter text-sm">Ingest your enterprise data into the Petalytics ecosystem. Supports Shopee (.xlsx) and TikTok (.csv) exports.</p>
            </div>

            {/* Marketplace Selector (Required by Backend) */}
            <div className="flex flex-col gap-2">
                <label className="text-sm font-bold text-on-surface uppercase tracking-widest">Data Source</label>
                <select 
                    value={marketplace}
                    onChange={handleMarketplaceChange}
                    className="w-full md:w-64 p-3 bg-surface-container-lowest border-2 border-outline-variant/30 rounded-lg text-on-surface font-medium outline-none focus:border-primary transition-colors"
                >
                    <option value="SHOPEE">Shopee</option>
                    <option value="TIKTOK">TikTok</option>
                </select>
            </div>

            {/* Upload Zone */}
            <section 
                onClick={() => !file && fileInputRef.current?.click()}
                className={`glass-panel rounded-xl p-16 flex flex-col items-center justify-center text-center border-2 border-dashed transition-all group bg-surface-container-lowest
                    ${file ? 'border-primary/60' : 'border-primary/20 hover:border-primary/40 cursor-pointer'}
                `}
            >
                {/* Hidden File Input */}
                <input 
                    type="file" 
                    accept={marketplace === 'SHOPEE' ? '.xlsx' : '.csv'}
                    className="hidden" 
                    ref={fileInputRef} 
                    onChange={handleFileChange} 
                />

                <div className="w-16 h-16 rounded-xl bg-primary/5 flex items-center justify-center mb-6 group-hover:scale-110 transition-transform">
                    <CloudUpload size={40} className="text-primary" />
                </div>
                
                <h3 className="text-2xl font-bold text-on-surface mb-2">
                    {file ? 'File Selected' : 'Ready to ingest your data?'}
                </h3>
                <p className="text-on-surface-variant font-inter mb-8">
                    {file ? <span className="font-bold text-primary">{file.name}</span> : 'Upload or drag and drop your file here'}
                </p>
                
                <button 
                    onClick={(e) => { e.stopPropagation(); handleUpload(); }}
                    disabled={status === 'loading'}
                    className={`px-10 py-3 text-white rounded-lg font-bold shadow-md hover:shadow-lg transition-all 
                        ${status === 'loading' ? 'bg-primary/50 cursor-not-allowed' : 'bg-primary hover:bg-primary-container'}
                    `}
                >
                    {status === 'loading' ? 'Ingesting...' : file ? 'Confirm & Ingest Data' : 'Browse Files'}
                </button>

                <div className="mt-8 flex gap-8">
                    <div className="flex items-center gap-2 text-[10px] font-bold text-on-surface-variant uppercase tracking-widest">
                        <CheckCircle2 size={14} className="text-primary" />
                        Max 50MB
                    </div>
                    <div className="flex items-center gap-2 text-[10px] font-bold text-on-surface-variant uppercase tracking-widest">
                        <CheckCircle2 size={14} className="text-primary" />
                        Auto-mapping
                    </div>
                </div>
            </section>

            {/* Status Messages */}
            {status === 'success' && (
                <div className="p-4 bg-green-50 border border-green-200 rounded-lg text-green-700 font-medium">
                    {message}
                </div>
            )}
            {status === 'error' && (
                <div className="p-4 bg-red-50 border border-red-200 rounded-lg text-red-700 font-medium">
                    {message}
                </div>
            )}

            {/* Recent Uploads */}
            <section className="space-y-4">
                <div className="flex justify-between items-end">
                    <h3 className="text-lg font-bold text-on-surface">Recent Uploads</h3>
                    <button className="text-[11px] font-bold text-primary hover:text-primary-container uppercase tracking-widest">View All History</button>
                </div>

                <div className="glass-panel rounded-xl overflow-hidden">
                    <table className="w-full text-left">
                        <thead>
                        <tr className="bg-surface-container-low border-b border-outline-variant/30">
                            <th className="px-6 py-4 text-[10px] font-bold text-on-surface-variant uppercase tracking-widest font-inter">File Name</th>
                            <th className="px-6 py-4 text-[10px] font-bold text-on-surface-variant uppercase tracking-widest font-inter">Date</th>
                            <th className="px-6 py-4 text-[10px] font-bold text-on-surface-variant uppercase tracking-widest font-inter">Status</th>
                            <th className="px-6 py-4 text-[10px] font-bold text-on-surface-variant uppercase tracking-widest font-inter">Marketplace</th>
                            <th className="px-6 py-4 text-[10px] font-bold text-on-surface-variant uppercase tracking-widest font-inter text-right">Action</th>
                        </tr>
                        </thead>
                        <tbody className="divide-y divide-outline-variant/10">
                        {uploadHistory.length === 0 ? (
                            <tr>
                                <td colSpan={4} className="px-6 py-8 text-center text-sm text-on-surface-variant italic">
                                    No recent uploads in this session.
                                </td>
                            </tr>
                        ) : (
                            uploadHistory.map((job) => (
                                <tr key={job.id} className="hover:bg-surface-container-low transition-colors group">
                                    <td className="px-6 py-5">
                                        <div className="flex items-center gap-3">
                                            <FileText size={18} className="text-primary" />
                                            <span className="text-sm font-medium text-on-surface">{job.fileName}</span>
                                        </div>
                                    </td>
                                    <td className="px-6 py-5 text-sm text-on-surface-variant">{job.date}</td>
                                    <td className="px-6 py-5">
                                        {job.status === 'Completed' && (
                                            <span className="px-3 py-1 bg-green-100 text-green-700 text-[10px] font-black rounded uppercase tracking-tighter">Completed</span>
                                        )}
                                        {job.status === 'Failed' && (
                                            <span className="px-3 py-1 bg-red-100 text-red-700 text-[10px] font-black rounded uppercase tracking-tighter">Failed</span>
                                        )}
                                        {job.status === 'Processing' && (
                                            <span className="px-3 py-1 bg-blue-100 text-blue-700 text-[10px] font-black rounded uppercase tracking-tighter animate-pulse">Processing...</span>
                                        )}
                                    </td>
                                    <td className="px-6 py-5 text-sm text-on-surface-variant">{job.marketplace}</td>
                                    <td className="px-6 py-5 text-right">
                                        <button 
                                            onClick={() => handleRemoveHistory(job.id)}
                                            title="Clear from history"
                                            className="text-outline-variant hover:text-red-500 transition-colors"
                                        >
                                            <Trash2 size={18} />
                                        </button>
                                    </td>
                                </tr>
                            ))
                        )}
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    );
}
