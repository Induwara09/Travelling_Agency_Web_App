import { useCallback, useEffect, useState } from 'react';
import { CalendarDays, MapPin, UsersRound } from 'lucide-react';
import toast from 'react-hot-toast';
import PageHero from '../../components/PageHero';
import StatusBadge from '../../components/StatusBadge';
import { EmptyState, ErrorState, LoadingState } from '../../components/StateViews';
import { bookingApi, getApiError } from '../../services/api';
import { formatCurrency, formatDate } from '../../utils/format';

export default function MyBookingsPage() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const load = useCallback(async () => { setLoading(true); try { setItems((await bookingApi.mine()).data); setError(''); } catch (err) { setError(getApiError(err)); } finally { setLoading(false); } }, []);
  useEffect(() => { load(); }, [load]);
  const cancel = async (id) => { if (!window.confirm('Cancel this booking request?')) return; try { await bookingApi.cancel(id); toast.success('Booking cancelled.'); load(); } catch (err) { toast.error(getApiError(err)); } };
  return <><PageHero compact eyebrow="Your journeys" title="My bookings" text="Follow confirmation and payment status for every booking request." /><section className="section container">{loading ? <LoadingState /> : error ? <ErrorState message={error} onRetry={load} /> : items.length ? <div className="booking-list">{items.map((item) => <article className="booking-item" key={item.id}><div className="booking-item__top"><div><small>Booking #{item.id}</small><h3>{item.packageName}</h3></div><div className="status-group"><StatusBadge value={item.bookingStatus} /><StatusBadge value={item.paymentStatus} /></div></div><div className="booking-item__meta"><span><CalendarDays /> {formatDate(item.travelDate)}</span><span><UsersRound /> {item.numberOfGuests} guests</span><span><MapPin /> Sri Lanka</span></div><div className="booking-item__footer"><div><small>Total</small><strong>{formatCurrency(item.totalAmount)}</strong></div>{!['COMPLETED', 'CANCELLED'].includes(item.bookingStatus) && <button className="button button--danger-outline" onClick={() => cancel(item.id)}>Cancel booking</button>}</div></article>)}</div> : <EmptyState title="No bookings yet" text="Choose a tour package and begin your Sri Lankan story." />}</section></>;
}
