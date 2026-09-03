export type Role = 'CASHIER'|'MANAGER'|'ADMIN'
export type User = {id:number;employeeId:string;name:string;username:string;role:Role;active:boolean}
export type Category = {id:number;name:string;icon?:string}
export type Product = {id:number;externalId:string;itemCode:string;barcode?:string;name:string;description?:string;category?:Category;sellingPrice:number;costPrice:number;unit:string;currentStock:number;minStock:number;active:boolean;trackInventory:boolean;allowDiscount:boolean;imageUrl?:string;stockStatus:string}
export type SaleItem = {productId:number;itemCode:string;productName:string;quantity:number;unitPrice:number;discount:number;tax:number;lineTotal:number}
export type Sale = {id:number;invoiceNumber:string;externalId:string;orderType:string;tableNumber?:string;customerName?:string;customerPhone?:string;customerEmail?:string;cashierName:string;items:SaleItem[];subtotal:number;discount:number;serviceCharge:number;tax:number;total:number;amountPaid:number;balance:number;paymentMethod:string;status:string;managerOverride:boolean;overrideReason?:string;approvedBy?:string;createdAt:string}

const base=''
export function token(){return localStorage.getItem('smartpos_token')}
export function setToken(t:string|null){if(t)localStorage.setItem('smartpos_token',t);else localStorage.removeItem('smartpos_token')}
export async function api<T=any>(path:string, options:RequestInit={}):Promise<T>{
  const headers=new Headers(options.headers||{}); if(!headers.has('Content-Type') && options.body)headers.set('Content-Type','application/json'); const t=token(); if(t)headers.set('Authorization',`Bearer ${t}`)
  const res=await fetch(base+path,{...options,headers}); if(res.status===401){setToken(null);window.dispatchEvent(new Event('smartpos-auth-lost'))}
  if(!res.ok){let message=`Request failed (${res.status})`;try{const b=await res.json();message=b.message||message;if(b.errors)message+=': '+Object.values(b.errors).join(', ')}catch{}throw new Error(message)}
  const ct=res.headers.get('content-type')||''; if(ct.includes('application/json'))return res.json(); return (await res.text()) as any
}
export async function download(path:string, filename:string){const headers:any={};const t=token();if(t)headers.Authorization=`Bearer ${t}`;const res=await fetch(path,{headers});if(!res.ok)throw new Error('Download failed');const blob=await res.blob();const url=URL.createObjectURL(blob);const a=document.createElement('a');a.href=url;a.download=filename;a.click();URL.revokeObjectURL(url)}
export const money=(n:number|string|undefined|null)=>`Rs. ${Number(n||0).toLocaleString('en-LK',{minimumFractionDigits:2,maximumFractionDigits:2})}`
