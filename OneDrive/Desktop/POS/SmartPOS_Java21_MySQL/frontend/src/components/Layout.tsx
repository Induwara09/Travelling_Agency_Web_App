import {ReactNode,useEffect,useState} from 'react'
import {Page} from '../App'
import {useAuth} from '../contexts/AuthContext'
import {api} from '../services/api'

export default function Layout({children,page,setPage}:{children:ReactNode;page:Page;setPage:(p:Page)=>void}){
 const{user,logout}=useAuth();const[online,setOnline]=useState(navigator.onLine);const[pending,setPending]=useState({sync:0,email:0})
 useEffect(()=>{const update=()=>setOnline(navigator.onLine);window.addEventListener('online',update);window.addEventListener('offline',update);const poll=async()=>{try{const h=await api<any>('/api/health');setPending({sync:h.pendingSync||0,email:h.pendingEmail||0})}catch{}};poll();const id=setInterval(poll,15000);return()=>{window.removeEventListener('online',update);window.removeEventListener('offline',update);clearInterval(id)}},[])
 const nav=[
  ['pos','▦','POS'],['dashboard','◫','Dashboard'],['inventory','▤','Inventory'],['products','◈','Products'],['recipes','◉','Recipes'],['sales','▧','Sales'],['customers','♧','Customers'],['purchases','▱','Purchases'],['reports','⌁','Reports'],['shifts','◷','Shifts'],['users','♙','Users'],['admin-tools','⚙','System']
 ] as [Page,string,string][]
 const allowed=(p:Page)=>user?.role==='ADMIN'||(user?.role==='MANAGER'?!['users','admin-tools'].includes(p):['pos','sales','shifts','customers'].includes(p))
 return <div className="app-shell"><aside className="sidebar"><div className="logo"><div className="brand-mark small">▣</div><div><strong>SmartPOS</strong><small>Offline Business Suite</small></div></div><nav>{nav.filter(n=>allowed(n[0])).map(([p,icon,label])=><button key={p} className={page===p?'active':''} onClick={()=>setPage(p)}><span>{icon}</span>{label}</button>)}</nav><div className="sidebar-bottom"><div className="user-box"><div className="avatar">{user?.name?.[0]}</div><div><b>{user?.name}</b><small>{user?.role}</small></div></div><button className="logout" onClick={logout}>↪ Logout</button></div></aside><main className="main"><header className="topbar"><div><h2>{nav.find(n=>n[0]===page)?.[2]}</h2><p>{new Date().toLocaleDateString('en-LK',{weekday:'long',year:'numeric',month:'long',day:'numeric'})}</p></div><div className="top-actions"><span className={online?'status online':'status offline'}>{online?'● ONLINE':'● OFFLINE'}</span>{(pending.sync+pending.email)>0&&<span className="queue-chip">{pending.sync+pending.email} queued</span>}<div className="top-avatar">{user?.name?.[0]}</div></div></header><section className="content">{children}</section></main></div>
}
