import { useCallback, useEffect, useState } from 'react';
import { Edit3, Plus, Search, Trash2 } from 'lucide-react';
import toast from 'react-hot-toast';
import AdminPageHeader from '../../components/AdminPageHeader';
import ImageWithFallback from '../../components/ImageWithFallback';
import Modal from '../../components/Modal';
import StatusBadge from '../../components/StatusBadge';
import { EmptyState, ErrorState, LoadingState } from '../../components/StateViews';
import { categories, images } from '../../data/siteData';
import { destinationApi, getApiError, packageApi } from '../../services/api';
import { formatCurrency } from '../../utils/format';

const emptyForm = { name: '', description: '', price: '', durationDays: '', imageUrl: '', category: 'Adventure', status: 'ACTIVE', destinationId: '' };

export default function AdminPackagesPage() {
  const [items, setItems] = useState([]);
  const [destinations, setDestinations] = useState([]);
  const [search, setSearch] = useState('');
  const [form, setForm] = useState(emptyForm);
  const [editing, setEditing] = useState(null);
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const load = useCallback(async () => { setLoading(true); try { const [packageResult, destinationResult] = await Promise.all([packageApi.list(search ? { search } : {}), destinationApi.list()]); setItems(packageResult.data); setDestinations(destinationResult.data); setError(''); } catch (err) { setError(getApiError(err)); } finally { setLoading(false); } }, [search]);
  useEffect(() => { const timer = setTimeout(load, 220); return () => clearTimeout(timer); }, [load]);
  const showCreate = () => { setEditing(null); setForm({ ...emptyForm, destinationId: destinations[0]?.id || '' }); setOpen(true); };
  const showEdit = (item) => { setEditing(item.id); setForm({ name: item.name, description: item.description || '', price: item.price, durationDays: item.durationDays, imageUrl: item.imageUrl || '', category: item.category, status: item.status, destinationId: item.destinationId }); setOpen(true); };
  const payload = () => ({ ...form, price: Number(form.price), durationDays: Number(form.durationDays), destinationId: Number(form.destinationId) });
  const submit = async (event) => { event.preventDefault(); try { editing ? await packageApi.update(editing, payload()) : await packageApi.create(payload()); toast.success(`Package ${editing ? 'updated' : 'created'}.`); setOpen(false); load(); } catch (err) { toast.error(getApiError(err)); } };
  const remove = async (id) => { if (!window.confirm('Delete this package? Existing bookings will prevent deletion.')) return; try { await packageApi.remove(id); toast.success('Package deleted.'); load(); } catch (err) { toast.error(getApiError(err)); } };
  const update = (event) => setForm({ ...form, [event.target.name]: event.target.value });
  return <><AdminPageHeader title="Tour packages" text="Manage pricing, durations, destinations and package availability." action={<button className="button button--primary" onClick={showCreate} disabled={!destinations.length}><Plus /> Add package</button>} /><div className="admin-toolbar"><label className="field field--search"><Search /><input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Search packages..." /></label><span>{items.length} records</span></div>{!destinations.length && <div className="admin-alert">Create at least one destination before adding a tour package.</div>}{loading ? <LoadingState /> : error ? <ErrorState message={error} onRetry={load} /> : items.length ? <div className="admin-table-wrap"><table className="admin-table"><thead><tr><th>Package</th><th>Destination</th><th>Duration</th><th>Price</th><th>Status</th><th>Actions</th></tr></thead><tbody>{items.map((item) => <tr key={item.id}><td><div className="table-entity"><ImageWithFallback src={item.imageUrl} fallback={images.package} alt="" /><div><strong>{item.name}</strong><small>{item.category}</small></div></div></td><td>{item.destinationName}</td><td>{item.durationDays} days</td><td>{formatCurrency(item.price)}</td><td><StatusBadge value={item.status} /></td><td><div className="table-actions"><button onClick={() => showEdit(item)}><Edit3 /></button><button className="danger" onClick={() => remove(item.id)}><Trash2 /></button></div></td></tr>)}</tbody></table></div> : <EmptyState title="No tour packages yet" />}<Modal open={open} title={editing ? 'Edit tour package' : 'Add tour package'} onClose={() => setOpen(false)} wide><form className="admin-form" onSubmit={submit}><label className="field"><span>Package name</span><input name="name" value={form.name} onChange={update} required maxLength="150" /></label><label className="field"><span>Destination</span><select name="destinationId" value={form.destinationId} onChange={update} required><option value="">Select destination</option>{destinations.map((item) => <option key={item.id} value={item.id}>{item.name}</option>)}</select></label><label className="field"><span>Price (LKR / person)</span><input name="price" type="number" min="1" step="0.01" value={form.price} onChange={update} required /></label><label className="field"><span>Duration (days)</span><input name="durationDays" type="number" min="1" value={form.durationDays} onChange={update} required /></label><label className="field"><span>Category</span><select name="category" value={form.category} onChange={update}>{categories.map((category) => <option key={category}>{category}</option>)}</select></label><label className="field"><span>Status</span><select name="status" value={form.status} onChange={update}><option>ACTIVE</option><option>INACTIVE</option></select></label><label className="field field--full"><span>Description</span><textarea name="description" rows="4" value={form.description} onChange={update} maxLength="3000" /></label><label className="field field--full"><span>Image URL</span><input name="imageUrl" type="url" value={form.imageUrl} onChange={update} placeholder="https://..." maxLength="500" /></label><div className="form-actions field--full"><button type="button" className="button button--secondary" onClick={() => setOpen(false)}>Cancel</button><button className="button button--primary">{editing ? 'Save changes' : 'Create package'}</button></div></form></Modal></>;
}
