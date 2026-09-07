import { useCallback, useEffect, useState } from 'react';
import { Edit3, Trash2 } from 'lucide-react';
import toast from 'react-hot-toast';
import AdminPageHeader from '../../components/AdminPageHeader';
import Modal from '../../components/Modal';
import StatusBadge from '../../components/StatusBadge';
import { EmptyState, ErrorState, LoadingState } from '../../components/StateViews';
import { bookingApi, getApiError } from '../../services/api';
import { formatCurrency, formatDate } from '../../utils/format';

export default function AdminBookingsPage() {
  const [items, setItems] = useState([]);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState({ bookingStatus: 'PENDING', paymentStatus: 'PENDING' });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const load = useCallback(async () => { setLoading(true); try { setItems((await bookingApi.all()).data); setError(''); } catch (err) { setError(getApiError(err)); } finally { setLoading(false); } }, []);
  useEffect(() => { load(); }, [load]);
  const showEdit = (item) => { setEditing(item); setForm({ bookingStatus: item.bookingStatus, paymentStatus: item.paymentStatus }); };
  const submit = async (event) => { event.preventDefault(); try { await bookingApi.updateStatus(editing.id, form); toast.success('Booking status updated.'); setEditing(null); load(); } catch (err) { toast.error(getApiError(err)); } };
  const remove = async (id) => { if (!window.confirm('Permanently delete this booking?')) return; try { await bookingApi.remove(id); toast.success('Booking deleted.'); load(); } catch (err) { toast.error(getApiError(err)); } };
  return <><AdminPageHeader title="Bookings" text="Review customer journeys, booking state and payment verification." />{loading ? <LoadingState /> : error ? <ErrorState message={error} onRetry={load} /> : items.length ? <div className="admin-table-wrap"><table className="admin-table"><thead><tr><th>Booking</th><th>Customer</th><th>Travel date</th><th>Guests</th><th>Total</th><th>Booking</th><th>Payment</th><th>Actions</th></tr></thead><tbody>{items.map((item) => <tr key={item.id}><td><div><strong>#{item.id}</strong><small className="block-small">{item.packageName}</small></div></td><td><div><strong>{item.customerName}</strong><small className="block-small">{item.customerEmail}</small></div></td><td>{formatDate(item.travelDate)}</td><td>{item.numberOfGuests}</td><td>{formatCurrency(item.totalAmount)}</td><td><StatusBadge value={item.bookingStatus} /></td><td><StatusBadge value={item.paymentStatus} /></td><td><div className="table-actions"><button onClick={() => showEdit(item)}><Edit3 /></button><button className="danger" onClick={() => remove(item.id)}><Trash2 /></button></div></td></tr>)}</tbody></table></div> : <EmptyState title="No bookings yet" />}<Modal open={Boolean(editing)} title={`Update booking #${editing?.id || ''}`} onClose={() => setEditing(null)}><form className="admin-form" onSubmit={submit}><label className="field field--full"><span>Booking status</span><select value={form.bookingStatus} onChange={(event) => setForm({ ...form, bookingStatus: event.target.value })}><option>PENDING</option><option>CONFIRMED</option><option>COMPLETED</option><option>CANCELLED</option></select></label><label className="field field--full"><span>Payment status</span><select value={form.paymentStatus} onChange={(event) => setForm({ ...form, paymentStatus: event.target.value })}><option>PENDING</option><option>PAID</option><option>FAILED</option><option>REFUNDED</option></select></label><div className="form-actions field--full"><button type="button" className="button button--secondary" onClick={() => setEditing(null)}>Cancel</button><button className="button button--primary">Update status</button></div></form></Modal></>;
}
