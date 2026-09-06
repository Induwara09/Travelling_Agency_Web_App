import { useCallback, useEffect, useMemo, useState } from 'react';
import { Edit3, MapPinned, Plus, RotateCcw, Search, Star, Trash2 } from 'lucide-react';
import toast from 'react-hot-toast';
import AdminPageHeader from '../../components/AdminPageHeader';
import ImageWithFallback from '../../components/ImageWithFallback';
import Modal from '../../components/Modal';
import { EmptyState, ErrorState, LoadingState } from '../../components/StateViews';
import { destinationCategories, districts, images } from '../../data/siteData';
import { destinationApi, getApiError } from '../../services/api';

const emptyForm = { name: '', location: '', district: 'Colombo', category: 'City & Culture', shortDescription: '', description: '', imageUrl: '', imageSourceUrl: '', tags: '', featured: false };
const PAGE_SIZE = 15;

export default function AdminDestinationsPage() {
  const [items, setItems] = useState([]);
  const [search, setSearch] = useState('');
  const [district, setDistrict] = useState('');
  const [category, setCategory] = useState('');
  const [form, setForm] = useState(emptyForm);
  const [editing, setEditing] = useState(null);
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [page, setPage] = useState(1);

  const load = useCallback(async () => {
    setLoading(true);
    try { setItems((await destinationApi.list({ search: search || undefined, district: district || undefined, category: category || undefined })).data); setError(''); }
    catch (err) { setError(getApiError(err)); }
    finally { setLoading(false); }
  }, [search, district, category]);

  useEffect(() => { const timer = setTimeout(load, 220); return () => clearTimeout(timer); }, [load]);
  useEffect(() => setPage(1), [search, district, category]);
  const visible = useMemo(() => items.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE), [items, page]);
  const pageCount = Math.max(1, Math.ceil(items.length / PAGE_SIZE));
  const showCreate = () => { setEditing(null); setForm(emptyForm); setOpen(true); };
  const showEdit = (item) => { setEditing(item.id); setForm({ ...emptyForm, ...item, featured: Boolean(item.featured) }); setOpen(true); };
  const change = (key) => (event) => setForm({ ...form, [key]: event.target.type === 'checkbox' ? event.target.checked : event.target.value });
  const submit = async (event) => {
    event.preventDefault();
    try {
      editing ? await destinationApi.update(editing, form) : await destinationApi.create(form);
      toast.success(`Destination ${editing ? 'updated' : 'created'}.`); setOpen(false); load();
    } catch (err) { toast.error(getApiError(err)); }
  };
  const remove = async (id) => {
    if (!window.confirm('Delete this destination? Packages using it will prevent deletion.')) return;
    try { await destinationApi.remove(id); toast.success('Destination deleted.'); load(); }
    catch (err) { toast.error(getApiError(err)); }
  };

  return <>
    <AdminPageHeader eyebrow="25-district catalogue" title="Destinations" text="Create, edit and curate every place shown on the public website." action={<button className="button button--primary" onClick={showCreate}><Plus /> Add destination</button>} />
    <div className="admin-mini-stats"><article><MapPinned /><div><strong>{items.length}</strong><span>filtered places</span></div></article><article><Star /><div><strong>{items.filter((item) => item.featured).length}</strong><span>featured</span></div></article><article><span className="admin-mini-stats__number">25</span><div><strong>Islandwide</strong><span>district coverage</span></div></article></div>
    <div className="admin-toolbar admin-toolbar--filters"><label className="field field--search"><Search /><input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Search destinations..." /></label><label className="field"><select aria-label="Filter district" value={district} onChange={(event) => setDistrict(event.target.value)}><option value="">All districts</option>{districts.map((item) => <option key={item}>{item}</option>)}</select></label><label className="field"><select aria-label="Filter category" value={category} onChange={(event) => setCategory(event.target.value)}><option value="">All categories</option>{destinationCategories.map((item) => <option key={item}>{item}</option>)}</select></label><button className="icon-button" aria-label="Reset filters" onClick={() => { setSearch(''); setDistrict(''); setCategory(''); }}><RotateCcw /></button><span>{items.length} records</span></div>
    {loading ? <LoadingState /> : error ? <ErrorState message={error} onRetry={load} /> : visible.length ? <><div className="admin-table-wrap"><table className="admin-table admin-table--destinations"><thead><tr><th>Destination</th><th>District / category</th><th>Summary</th><th>Visibility</th><th>Actions</th></tr></thead><tbody>{visible.map((item) => <tr key={item.id}><td><div className="table-entity"><ImageWithFallback src={item.imageUrl} fallback={images.destination(item.name, item.district, item.category)} alt="" /><div><strong>{item.name}</strong><small>#{item.id}</small></div></div></td><td><strong>{item.district}</strong><small className="table-category">{item.category}</small></td><td className="description-cell">{item.shortDescription || item.description || '—'}</td><td>{item.featured ? <span className="featured-status"><Star fill="currentColor" /> Featured</span> : <span className="muted-copy">Standard</span>}</td><td><div className="table-actions"><button aria-label={`Edit ${item.name}`} onClick={() => showEdit(item)}><Edit3 /></button><button aria-label={`Delete ${item.name}`} className="danger" onClick={() => remove(item.id)}><Trash2 /></button></div></td></tr>)}</tbody></table></div><div className="admin-pagination"><button disabled={page === 1} onClick={() => setPage((value) => value - 1)}>Previous</button><span>Page {page} of {pageCount}</span><button disabled={page === pageCount} onClick={() => setPage((value) => value + 1)}>Next</button></div></> : <EmptyState title="No destinations matched" />}
    <Modal open={open} title={editing ? 'Edit destination' : 'Add destination'} onClose={() => setOpen(false)} wide><form className="admin-form" onSubmit={submit}>
      <label className="field"><span>Name</span><input value={form.name} onChange={change('name')} required maxLength="100" /></label>
      <label className="field"><span>Location</span><input value={form.location} onChange={change('location')} required maxLength="150" /></label>
      <label className="field"><span>District</span><select value={form.district} onChange={change('district')} required>{districts.map((item) => <option key={item}>{item}</option>)}</select></label>
      <label className="field"><span>Category</span><select value={form.category} onChange={change('category')} required>{destinationCategories.map((item) => <option key={item}>{item}</option>)}</select></label>
      <label className="field field--full"><span>Card summary</span><textarea rows="3" value={form.shortDescription} onChange={change('shortDescription')} maxLength="500" /></label>
      <label className="field field--full"><span>Full description</span><textarea rows="5" value={form.description} onChange={change('description')} maxLength="2000" /></label>
      <label className="field field--full"><span>Image URL</span><input type="url" value={form.imageUrl || ''} onChange={change('imageUrl')} placeholder="https://..." maxLength="500" /></label>
      <label className="field field--full"><span>Image credit/source URL</span><input type="url" value={form.imageSourceUrl || ''} onChange={change('imageSourceUrl')} placeholder="https://commons.wikimedia.org/..." maxLength="500" /></label>
      <label className="field field--full"><span>Tags (comma separated)</span><input value={form.tags || ''} onChange={change('tags')} placeholder="Photography, History, Popular" maxLength="300" /></label>
      <label className="admin-featured-check field--full"><input type="checkbox" checked={form.featured} onChange={change('featured')} /><span><strong>Feature on homepage</strong><small>Shows this place in the curated homepage collection.</small></span></label>
      <div className="form-actions field--full"><button type="button" className="button button--secondary" onClick={() => setOpen(false)}>Cancel</button><button className="button button--primary">{editing ? 'Save changes' : 'Create destination'}</button></div>
    </form></Modal>
  </>;
}
