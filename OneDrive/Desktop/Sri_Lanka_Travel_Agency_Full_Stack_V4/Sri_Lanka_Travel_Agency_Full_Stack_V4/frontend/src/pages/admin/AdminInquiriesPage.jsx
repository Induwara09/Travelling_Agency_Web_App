import { useCallback, useEffect, useState } from 'react';
import { Mail, MessageSquareText, Phone, Trash2 } from 'lucide-react';
import toast from 'react-hot-toast';
import AdminPageHeader from '../../components/AdminPageHeader';
import { EmptyState, ErrorState, LoadingState } from '../../components/StateViews';
import { getApiError, inquiryApi } from '../../services/api';

const statuses = ['NEW', 'CONTACTED', 'PLANNING', 'CLOSED'];

export default function AdminInquiriesPage() {
  const [items, setItems] = useState([]); const [loading, setLoading] = useState(true); const [error, setError] = useState('');
  const load = useCallback(async () => { setLoading(true); try { setItems((await inquiryApi.all()).data); setError(''); } catch (err) { setError(getApiError(err)); } finally { setLoading(false); } }, []);
  useEffect(() => { load(); }, [load]);
  const changeStatus = async (id, status) => { try { await inquiryApi.updateStatus(id, status); setItems((list) => list.map((item) => item.id === id ? { ...item, status } : item)); toast.success('Inquiry status updated.'); } catch (err) { toast.error(getApiError(err)); } };
  const remove = async (id) => { if (!window.confirm('Permanently delete this inquiry?')) return; try { await inquiryApi.remove(id); setItems((list) => list.filter((item) => item.id !== id)); toast.success('Inquiry deleted.'); } catch (err) { toast.error(getApiError(err)); } };
  return <><AdminPageHeader title="Journey inquiries" text="Follow each custom trip request from first contact to active planning." />{loading ? <LoadingState /> : error ? <ErrorState message={error} onRetry={load} /> : items.length ? <div className="inquiry-list">{items.map((item) => <article key={item.id} className="inquiry-card"><div className="inquiry-card__top"><div><span className={`status status--${item.status.toLowerCase()}`}>{item.status}</span><h3>{item.name}</h3><small>{new Date(item.createdAt).toLocaleString()}</small></div><button className="icon-danger" onClick={() => remove(item.id)} aria-label="Delete inquiry"><Trash2 /></button></div><div className="inquiry-card__contact"><a href={`mailto:${item.email}`}><Mail /> {item.email}</a>{item.phone && <a href={`tel:${item.phone}`}><Phone /> {item.phone}</a>}<span>{item.country || 'Country not given'} • {item.guests} traveller(s) • {item.travelMonth || 'Flexible dates'}</span></div>{item.interests && <p><strong>Interests:</strong> {item.interests}</p>}<blockquote><MessageSquareText /> <span>{item.message}</span></blockquote><label className="field"><span>Planning status</span><select value={item.status} onChange={(event) => changeStatus(item.id, event.target.value)}>{statuses.map((status) => <option key={status}>{status}</option>)}</select></label></article>)}</div> : <EmptyState title="No journey inquiries yet" text="Requests from the public Plan Your Trip form will appear here." />}</>;
}
