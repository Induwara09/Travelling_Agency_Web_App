import { useEffect, useState } from 'react';
import { Banknote, BedDouble, CalendarCheck2, Compass, Map, MessageSquareText, Sparkles, TrendingUp, Users } from 'lucide-react';
import { Area, AreaChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import AdminPageHeader from '../../components/AdminPageHeader';
import { ErrorState, LoadingState } from '../../components/StateViews';
import { adminApi, getApiError } from '../../services/api';
import { formatCurrency } from '../../utils/format';

const chartData = (data) => [
  { name: 'Pending', bookings: data.pendingBookings || 0 },
  { name: 'Confirmed', bookings: data.confirmedBookings || 0 },
  { name: 'Other', bookings: Math.max(0, (data.totalBookings || 0) - (data.pendingBookings || 0) - (data.confirmedBookings || 0)) }
];

export default function DashboardPage() {
  const [data, setData] = useState(null);
  const [error, setError] = useState('');
  useEffect(() => { adminApi.dashboard().then((response) => setData(response.data)).catch((err) => setError(getApiError(err))); }, []);
  if (error) return <ErrorState message={error} />;
  if (!data) return <LoadingState label="Loading dashboard..." />;
  const cards = [
    ['Total users', data.totalUsers, Users, 'blue'],
    ['Destinations', data.totalDestinations, Map, 'green'],
    ['Tour packages', data.totalPackages, Compass, 'gold'],
    ['Hotels', data.totalHotels, BedDouble, 'violet'],
    ['Experiences', data.totalExperiences, Sparkles, 'teal'],
    ['New inquiries', data.newInquiries, MessageSquareText, 'gold'],
    ['Bookings', data.totalBookings, CalendarCheck2, 'teal'],
    ['Paid revenue', formatCurrency(data.totalRevenue), Banknote, 'emerald']
  ];
  return <><AdminPageHeader eyebrow="Live overview" title="Dashboard" text="A clear view of travellers, inventory, bookings and paid revenue." /><div className="admin-stat-grid">{cards.map(([label, value, Icon, color]) => <article key={label} className={`admin-stat admin-stat--${color}`}><span><Icon /></span><div><small>{label}</small><strong>{value}</strong></div></article>)}</div><div className="admin-dashboard-grid"><article className="admin-panel admin-chart"><div className="admin-panel__title"><div><span className="eyebrow">Booking pipeline</span><h2>Current booking status</h2></div><TrendingUp /></div><ResponsiveContainer width="100%" height={280}><AreaChart data={chartData(data)}><defs><linearGradient id="bookingGradient" x1="0" y1="0" x2="0" y2="1"><stop offset="5%" stopColor="#0fa37f" stopOpacity={.55}/><stop offset="95%" stopColor="#0fa37f" stopOpacity={0}/></linearGradient></defs><CartesianGrid strokeDasharray="3 3" vertical={false} /><XAxis dataKey="name" /><YAxis allowDecimals={false} /><Tooltip /><Area type="monotone" dataKey="bookings" stroke="#0fa37f" strokeWidth={3} fill="url(#bookingGradient)" /></AreaChart></ResponsiveContainer></article><article className="admin-panel status-summary"><span className="eyebrow">Operations</span><h2>Booking health</h2><div><span><i className="dot dot--pending" /> Pending review</span><strong>{data.pendingBookings}</strong></div><div><span><i className="dot dot--confirmed" /> Confirmed</span><strong>{data.confirmedBookings}</strong></div><div><span><i className="dot dot--paid" /> Revenue</span><strong>{formatCurrency(data.totalRevenue)}</strong></div><p>Revenue includes bookings marked as PAID by an administrator.</p></article></div></>;
}
