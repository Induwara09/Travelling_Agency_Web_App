import {useEffect,useState} from 'react'
import {useAuth} from './contexts/AuthContext'
import LoginPage from './pages/LoginPage'
import Layout from './components/Layout'
import POSPage from './pages/POSPage'
import DashboardPage from './pages/DashboardPage'
import InventoryPage from './pages/InventoryPage'
import ProductsPage from './pages/ProductsPage'
import SalesPage from './pages/SalesPage'
import ReportsPage from './pages/ReportsPage'
import UsersPage from './pages/UsersPage'
import PurchasesPage from './pages/PurchasesPage'
import ShiftsPage from './pages/ShiftsPage'
import AdminToolsPage from './pages/AdminToolsPage'
import CustomersPage from './pages/CustomersPage'
import RecipesPage from './pages/RecipesPage'

export type Page='pos'|'dashboard'|'inventory'|'products'|'sales'|'reports'|'users'|'purchases'|'shifts'|'admin-tools'|'customers'|'recipes'
export default function App(){
 const {user,loading}=useAuth(); const [page,setPage]=useState<Page>('pos')
 useEffect(()=>{if(user?.role!=='CASHIER'&&page==='pos')setPage('dashboard')},[user])
 if(loading)return <div className="center-screen"><div className="loader"/><p>Starting SmartPOS...</p></div>
 if(!user)return <LoginPage/>
 const content=page==='pos'?<POSPage/>:page==='dashboard'?<DashboardPage/>:page==='inventory'?<InventoryPage/>:page==='products'?<ProductsPage/>:page==='sales'?<SalesPage/>:page==='reports'?<ReportsPage/>:page==='users'?<UsersPage/>:page==='purchases'?<PurchasesPage/>:page==='shifts'?<ShiftsPage/>:page==='customers'?<CustomersPage/>:page==='recipes'?<RecipesPage/>:<AdminToolsPage/>
 return <Layout page={page} setPage={setPage}>{content}</Layout>
}
