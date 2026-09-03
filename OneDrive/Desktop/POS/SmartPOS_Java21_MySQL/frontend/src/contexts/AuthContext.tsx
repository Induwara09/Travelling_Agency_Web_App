import React,{createContext,useContext,useEffect,useState} from 'react'
import {api,setToken,User} from '../services/api'

type Auth={user:User|null;loading:boolean;login:(u:string,p:string)=>Promise<void>;logout:()=>void}
const C=createContext<Auth>({user:null,loading:true,login:async()=>{},logout:()=>{}})
export const useAuth=()=>useContext(C)
export function AuthProvider({children}:{children:React.ReactNode}){
 const [user,setUser]=useState<User|null>(null);const[loading,setLoading]=useState(true)
 const load=async()=>{try{if(localStorage.getItem('smartpos_token'))setUser(await api('/api/auth/me'))}catch{setToken(null)}finally{setLoading(false)}}
 useEffect(()=>{load();const h=()=>{setUser(null);setLoading(false)};window.addEventListener('smartpos-auth-lost',h);return()=>window.removeEventListener('smartpos-auth-lost',h)},[])
 const login=async(username:string,password:string)=>{const r=await api<any>('/api/auth/login',{method:'POST',body:JSON.stringify({username,password})});setToken(r.token);setUser(r.user)}
 const logout=()=>{setToken(null);setUser(null)}
 return <C.Provider value={{user,loading,login,logout}}>{children}</C.Provider>
}
