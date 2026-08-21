import { useCallback, useEffect, useState } from 'react';
import { Edit3, Plus, Search, Trash2 } from 'lucide-react';
import toast from 'react-hot-toast';
import AdminPageHeader from '../../components/AdminPageHeader';
import ImageWithFallback from '../../components/ImageWithFallback';
import Modal from '../../components/Modal';
import { EmptyState, ErrorState, LoadingState } from '../../components/StateViews';
import { images } from '../../data/siteData';
import { destinationApi, getApiError } from '../../services/api';

const emptyForm = { name: '', location: '', description: '', imageUrl: '' };

export default function AdminDestinationsPage() {
  const [items, setItems] = useState([]);
  const [search, setSearch] = useState('');
  const [form, setForm] = useState(emptyForm);
  const [editing, setEditing] = useState(null);
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const load = useCallback(async () => { setLoading(true); try { setItems((await destinationApi.list(search)).data); setError(''); } catch (err) { setError(getApiError(err)); } finally { setLoading(false); } }, [search]);
  useEffect(() => { const timer = setTimeout(load, 220); return () => clearTimeout(timer); }, [load]);
  const showCreate = () => { setEditing(null); setForm(emptyForm); setOpen(true); };
  const showEdit = (item) => { setEditing(item.id); setForm({ name: item.name, location: item.location, description: item.description || '', imageUrl: item.imageUrl || '' }); setOpen(true); };
  const submit = async (event) => { event.preventDefault(); try { editing ? await destinationApi.update(editing, form) : await destinationApi.create(form); toast.success(`Destination ${editing ? 'updated' : 'created'}.`); setOpen(false); load(); } catch (err) { toast.error(getApiError(err)); } };
  const remove = async (id) => { if (!window.confirm('Delete this destination? Packages using it will prevent deletion.')) return; try { await destinationApi.remove(id); toast.success('Destination deleted.'); load(); } catch (err) { toast.error(getApiError(err)); } };
  return <><AdminPageHeader title="Destinations" text="Create and curate the places shown across the customer experience." action={<button className="button button--primary" onClick={showCreate}><Plus /> Add destination</button>} /><div className="admin-toolbar"><label className="field field--search"><Search /><input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Search destinations..." /></label><span>{items.length} records</span></div>{loading ? <LoadingState /> : error ? <ErrorState message={error} onRetry={load} /> : items.length ? <div className="admin-table-wrap"><table className="admin-table"><thead><tr><th>Destination</th><th>Location</th><th>Description</th><th>Actions</th></tr></thead><tbody>{items.map((item) => <tr key={item.id}><td><div className="table-entity"><ImageWithFallback src={item.imageUrl} fallback={images.destination(item.name)} alt="" /><div><strong>{item.name}</strong><small>#{item.id}</small></div></div></td><td>{item.location}</td><td className="description-cell">{item.description || '—'}</td><td><div className="table-actions"><button onClick={() => showEdit(item)}><Edit3 /></button><button className="danger" onClick={() => remove(item.id)}><Trash2 /></button></div></td></tr>)}</tbody></table></div> : <EmptyState title="No destinations yet" />}<Modal open={open} title={editing ? 'Edit destination' : 'Add destination'} onClose={() => setOpen(false)}><form className="admin-form" onSubmit={submit}><label className="field"><span>Name</span><input value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} required maxLength="100" /></label><label className="field"><span>Location</span><input value={form.location} onChange={(event) => setForm({ ...form, location: event.target.value })} required maxLength="150" /></label><label className="field field--full"><span>Description</span><textarea rows="5" value={form.description} onChange={(event) => setForm({ ...form, description: event.target.value })} maxLength="2000" /></label><label className="field field--full"><span>Image URL</span><input type="url" value={form.imageUrl} onChange={(event) => setForm({ ...form, imageUrl: event.target.value })} placeholder="https://..." maxLength="500" /></label><div className="form-actions field--full"><button type="button" className="button button--secondary" onClick={() => setOpen(false)}>Cancel</button><button className="button button--primary">{editing ? 'Save changes' : 'Create destination'}</button></div></form></Modal></>;
}
