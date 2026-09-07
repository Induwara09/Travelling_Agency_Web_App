import { useEffect, useMemo, useState } from 'react';
import { Banknote, CalendarCheck2, Compass, Map, MapPinned, MessageSquareText, Sparkles, TrendingUp, Users } from 'lucide-react';
import { Area, AreaChart, Bar, BarChart, CartesianGrid, Cell, Pie, PieChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import AdminPageHeader from '../../components/AdminPageHeader';
import { ErrorState, LoadingState } from '../../components/StateViews';
import { adminApi, destinationApi, getApiError } from '../../services/api';
import { formatCurrency } from '../../utils/format';

const COLORS = ['#0b7a68', '#e6a63c', '#2489a4', '#7b5fb2', '#d66f53', '#4f9c5d', '#b67cbd', '#5f7f95'];
const bookingData = (data) => [
  { name: 'Pending', bookings: data.pendingBookings || 0 },
  { name: 'Confirmed', bookings: data.confirmedBookings || 0 },
  { name: 'Other', bookings: Math.max(0, (data.totalBookings || 0) - (data.pendingBookings || 0) - (data.confirmedBookings || 0)) }
];

export default function DashboardPage() {
  const [data, setData] = useState(null);
  const [destinations, setDestinations] = useState([]);
  const [error, setError] = useState('');
  useEffect(() => {
    Promise.all([adminApi.dashboard(), destinationApi.list()])
      .then(([dashboardResult, destinationResult]) => { setData(dashboardResult.data); setDestinations(destinationResult.data); })
      .catch((err) => setError(getApiError(err)));
  }, []);

  const categoryData = useMemo(() => Object.entries(destinations.reduce((all, item) => ({ ...all, [item.category || 'Other']: (all[item.category || 'Other'] || 0) + 1 }), {})).map(([name, value]) => ({ name, value })).sort((a, b) => b.value - a.value), [destinations]);
  const districtData = useMemo(() => Object.entries(destinations.reduce((all, item) => ({ ...all, [item.district || 'Other']: (all[item.district || 'Other'] || 0) + 1 }), {})).map(([name, places]) => ({ name, places })).sort((a, b) => b.places - a.places).slice(0, 10), [destinations]);

  if (error) return <ErrorState message={error} />;
  if (!data) return <LoadingState label="Loading dashboard..." />;
  const featured = destinations.filter((item) => item.featured).length;
  const cards = [
    ['Total users', data.totalUsers, Users, 'blue'], ['Destinations', data.totalDestinations, Map, 'green'],
    ['Tour packages', data.totalPackages, Compass, 'gold'], ['Featured places', featured, Sparkles, 'violet'],
    ['New inquiries', data.newInquiries, MessageSquareText, 'gold'], ['Bookings', data.totalBookings, CalendarCheck2, 'teal'],
    ['Districts covered', new Set(destinations.map((item) => item.district)).size, MapPinned, 'blue'], ['Paid revenue', formatCurrency(data.totalRevenue), Banknote, 'emerald']
  ];

  return <>
    <AdminPageHeader eyebrow="Live management intelligence" title="Island overview" text="Destinations, travellers, bookings and revenue—presented in one professional command centre." />
    <div className="admin-stat-grid">{cards.map(([label, value, Icon, color], index) => <article key={label} className={`admin-stat admin-stat--${color}`} style={{ animationDelay: `${index * 55}ms` }}><span><Icon /></span><div><small>{label}</small><strong>{value}</strong></div></article>)}</div>
    <div className="admin-dashboard-grid admin-dashboard-grid--v3">
      <article className="admin-panel admin-chart"><div className="admin-panel__title"><div><span className="eyebrow">Destination intelligence</span><h2>Top districts by places</h2></div><MapPinned /></div><ResponsiveContainer width="100%" height={310}><BarChart data={districtData} margin={{ left: -18, right: 8 }}><CartesianGrid strokeDasharray="3 3" vertical={false} /><XAxis dataKey="name" angle={-25} textAnchor="end" height={74} fontSize={11} /><YAxis allowDecimals={false} /><Tooltip cursor={{ fill: 'rgba(15,163,127,.08)' }} /><Bar dataKey="places" fill="#0b7a68" radius={[8, 8, 0, 0]} animationDuration={900} /></BarChart></ResponsiveContainer></article>
      <article className="admin-panel admin-chart admin-chart--pie"><div className="admin-panel__title"><div><span className="eyebrow">Catalogue balance</span><h2>Places by category</h2></div><Sparkles /></div><ResponsiveContainer width="100%" height={310}><PieChart><Pie data={categoryData} dataKey="value" nameKey="name" innerRadius={72} outerRadius={112} paddingAngle={2} animationDuration={1000}>{categoryData.map((entry, index) => <Cell key={entry.name} fill={COLORS[index % COLORS.length]} />)}</Pie><Tooltip /></PieChart></ResponsiveContainer><div className="chart-legend">{categoryData.map((entry, index) => <span key={entry.name}><i style={{ background: COLORS[index % COLORS.length] }} />{entry.name} <strong>{entry.value}</strong></span>)}</div></article>
      <article className="admin-panel admin-chart"><div className="admin-panel__title"><div><span className="eyebrow">Booking pipeline</span><h2>Current booking status</h2></div><TrendingUp /></div><ResponsiveContainer width="100%" height={280}><AreaChart data={bookingData(data)}><defs><linearGradient id="bookingGradient" x1="0" y1="0" x2="0" y2="1"><stop offset="5%" stopColor="#2489a4" stopOpacity={.5}/><stop offset="95%" stopColor="#2489a4" stopOpacity={0}/></linearGradient></defs><CartesianGrid strokeDasharray="3 3" vertical={false} /><XAxis dataKey="name" /><YAxis allowDecimals={false} /><Tooltip /><Area type="monotone" dataKey="bookings" stroke="#2489a4" strokeWidth={3} fill="url(#bookingGradient)" animationDuration={900} /></AreaChart></ResponsiveContainer></article>
      <article className="admin-panel status-summary"><span className="eyebrow">Today’s operations</span><h2>Management pulse</h2><div><span><i className="dot dot--pending" /> Pending review</span><strong>{data.pendingBookings}</strong></div><div><span><i className="dot dot--confirmed" /> Confirmed</span><strong>{data.confirmedBookings}</strong></div><div><span><i className="dot dot--paid" /> New inquiries</span><strong>{data.newInquiries}</strong></div><div><span><i className="dot dot--paid" /> Paid revenue</span><strong>{formatCurrency(data.totalRevenue)}</strong></div><p>Destination charts update automatically whenever an administrator adds, edits or removes a place.</p></article>
    </div>
  </>;
}
