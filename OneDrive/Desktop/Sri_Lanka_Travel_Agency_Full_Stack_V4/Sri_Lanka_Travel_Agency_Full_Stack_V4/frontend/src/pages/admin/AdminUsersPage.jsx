import { useCallback, useEffect, useState } from 'react';
import { ShieldCheck, Trash2, UserRound } from 'lucide-react';
import toast from 'react-hot-toast';
import AdminPageHeader from '../../components/AdminPageHeader';
import { EmptyState, ErrorState, LoadingState } from '../../components/StateViews';
import { adminApi, getApiError } from '../../services/api';
import { formatDate } from '../../utils/format';
import { useAuth } from '../../context/AuthContext';

export default function AdminUsersPage() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const { user: currentUser } = useAuth();
  const load = useCallback(async () => { setLoading(true); try { setItems((await adminApi.users()).data); setError(''); } catch (err) { setError(getApiError(err)); } finally { setLoading(false); } }, []);
  useEffect(() => { load(); }, [load]);
  const changeRole = async (id, role) => { try { await adminApi.updateRole(id, role); toast.success('User role updated.'); load(); } catch (err) { toast.error(getApiError(err)); } };
  const remove = async (id) => { if (!window.confirm('Delete this user? Users with bookings cannot be removed.')) return; try { await adminApi.removeUser(id); toast.success('User deleted.'); load(); } catch (err) { toast.error(getApiError(err)); } };
  return <><AdminPageHeader title="Users" text="View customer accounts and control role-based access." />{loading ? <LoadingState /> : error ? <ErrorState message={error} onRetry={load} /> : items.length ? <div className="admin-table-wrap"><table className="admin-table"><thead><tr><th>User</th><th>Phone</th><th>Joined</th><th>Role</th><th>Actions</th></tr></thead><tbody>{items.map((item) => { const isSelf = item.id === currentUser?.id; return <tr key={item.id}><td><div className="table-entity table-entity--avatar"><span className="avatar">{item.name?.charAt(0)}</span><div><strong>{item.name}</strong><small>{item.email}</small></div></div></td><td>{item.phone || '—'}</td><td>{formatDate(item.createdAt)}</td><td><select className="role-select" value={item.role} disabled={isSelf} onChange={(event) => changeRole(item.id, event.target.value)}><option>CUSTOMER</option><option>ADMIN</option></select></td><td><div className="table-actions">{item.role === 'ADMIN' ? <span title="Administrator"><ShieldCheck /></span> : <span title="Customer"><UserRound /></span>}<button className="danger" disabled={isSelf} onClick={() => remove(item.id)}><Trash2 /></button></div></td></tr>; })}</tbody></table></div> : <EmptyState title="No users found" />}</>;
}
