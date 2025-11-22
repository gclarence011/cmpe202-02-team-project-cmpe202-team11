import axios, { AxiosError } from "axios";
import { getAccessToken, getRefreshToken, setTokens, clearTokens } from "./session";

let getToken: (() => string | null) | null = null;

export function setAuthTokenGetter(fn: () => string | null) {
  getToken = fn;
}

const API_BASE = (typeof window !== "undefined" && window.__ENV?.API_BASE_URL) || import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
export const api = axios.create({ baseURL: API_BASE.replace(/\/$/, ""), timeout: 15000 });

export const routes = {
  auth: { signup:"/auth/signup", login:"/auth/login", logout:"/auth/logout", me:"/auth/me", refresh:"/auth/refresh" }, // Not implemented
  listings: { mylist:"api/listings/user", list:"api/listings/search", detail:(id:string)=>`api/listings/${id}`, create:"api/listings/create", update:(id:string)=>`api/listings/${id}`, upload:(id:string)=>`api/listings/${id}/upload`, remove:(id:string)=>`api/listings/delete/${id}`, markSold:(id:string)=>`/listings/${id}/mark` },
  chats: { list:"/chats"/*, start:(listingId:string)=>`/chats/listing/${listingId}`, send:`/chats`*/ }
} as const;

api.interceptors.request.use((config) => {
  const token = getToken ? getToken() : null;
  
  if (token) {
    config.headers = config.headers || {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (res) => res,
  async (error) => {
    const originalRequest = error.config;
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      const refreshToken = getRefreshToken();
      if (!refreshToken) {
        clearTokens();
        throw error;
      }
      try {
        const refreshRes = await api.post(routes.auth.refresh, { refreshToken });
        const newTokens = refreshRes.data;
        if (!newTokens?.accessToken) {
          clearTokens();
          throw error;
        }
        setTokens(newTokens.accessToken, newTokens.refreshToken || refreshToken);
        originalRequest.headers = originalRequest.headers || {};
        originalRequest.headers.Authorization = `Bearer ${newTokens.accessToken}`;
        return api(originalRequest);
      } catch (e) {
        clearTokens();
        throw e;
      }
    }
    throw error;
  }
);
