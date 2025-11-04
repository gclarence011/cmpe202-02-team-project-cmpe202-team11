import axios, { AxiosError } from "axios";
import { getAccessToken, getRefreshToken, setTokens, clearTokens } from "./session";

const API_BASE = (typeof window !== "undefined" && window.__ENV?.API_BASE_URL) || import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
export const api = axios.create({ baseURL: API_BASE.replace(/\/$/, ""), timeout: 15000 });

export const routes = {
  auth: { signup:"/auth/signup", login:"/auth/login", logout:"/auth/logout", me:"/auth/me", refresh:"/auth/refresh" }, // Not implemented
  users: { public:(id:string)=>`/users/${id}`, me:"/users/me", saved:"/users/me/saved", saveToggle:(id:string)=>`/users/me/saved/${id}` }, // Does not exist
  listings: { list:"api/listings/search", detail:(id:string)=>`api/listings/${id}`, create:"/listings", update:(id:string)=>`/listings/${id}`, remove:(id:string)=>`/listings/${id}`, markSold:(id:string)=>`/listings/${id}/mark` },
  search: { q:"/search", categories:"/browse/categories", category:(slug:string)=>`/browse/category/${slug}`, recommended:"/browse/recommend" },
  chats: { mine:"/chats", start:(listingId:string)=>`/chats/listing/${listingId}`, room:(chatId:string)=>`/chats/${chatId}`, send:(chatId:string)=>`/chats/${chatId}/msg` },
  orders: { create:"/orders", detail:(id:string)=>`/orders/${id}`, mine:"/orders" }
} as const;

api.interceptors.request.use(cfg=>{
  const t = getAccessToken();
  if(t){ cfg.headers = cfg.headers || {}; cfg.headers.Authorization = `Bearer ${t}`; }
  return cfg;
});

let refreshing = false;
let queue: Array<(t:string)=>void> = [];

async function refresh(): Promise<string> {
  const r = getRefreshToken();
  if(!r) throw new Error("no-refresh");
  const res = await api.post(routes.auth.refresh, { refreshToken: r });
  const newToken = res.data?.accessToken;
  if(!newToken) throw new Error("no-token");
  setTokens(newToken, r);
  return newToken;
}

api.interceptors.response.use(r=>r, async (error: AxiosError)=>{
  const original: any = error.config || {};
  if(error.response?.status === 401 && !original._retry){
    original._retry = true;
    if(!refreshing){
      refreshing = true;
      try{
        const nt = await refresh();
        queue.forEach(fn=>fn(nt)); queue=[];
        original.headers = original.headers || {};
        original.headers.Authorization = `Bearer ${nt}`;
        return api(original);
      }catch(e){
        clearTokens(); queue=[];
        throw e;
      }finally{ refreshing = false; }
    }
    return new Promise((resolve, reject)=>{
      queue.push((nt:string)=>{
        original.headers = original.headers || {};
        original.headers.Authorization = `Bearer ${nt}`;
        api(original).then(resolve).catch(reject);
      });
    });
  }
  throw error;
});
