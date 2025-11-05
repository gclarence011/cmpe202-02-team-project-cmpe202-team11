import { createContext, useContext, useEffect, useState } from "react";
import { api, setAuthTokenGetter } from "./api";

const domain = "us-west-1rxv8v92ch.auth.us-west-1.amazoncognito.com";
const clientId = "7nr3tc2v2j0ga1jiclbpk9hf1u";
const redirectUri = import.meta.env.VITE_REDIRECT_URI;
const loginUrl =
  `https://${domain}/login/continue?` +
  `client_id=${clientId}` +
  `&redirect_uri=${encodeURIComponent(redirectUri)}` +
  `&response_type=code` +
  `&scope=openid+email+phone`;

const logoutUrl =
  `https://${domain}/logout?` +
  `client_id=${clientId}` +
  `&logout_uri=${encodeURIComponent(redirectUri)}`;

interface Tokens {
  access_token: string;
  id_token: string;
  refresh_token?: string;
  expires_in?: number;
  token_type?: string;
}

interface AuthContextType {
  user: any | null;
  tokens: Tokens | null;
  login: () => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const saved = localStorage.getItem("authTokens");
  const initialTokens: Tokens | null = saved ? JSON.parse(saved) : null;
  const [tokens, setTokens] = useState<Tokens | null>(initialTokens);
  const [user, setUser] = useState<any | null>(null);

  // Set auth token getter immediately after tokens initialized
  useEffect(() => {
    setAuthTokenGetter(() => tokens?.access_token || null);
  }, [tokens]);

  // Decode ID token when tokens change
  useEffect(() => {
    if (tokens?.id_token) {
      try {
        const payload = JSON.parse(atob(tokens.id_token.split(".")[1]));
        setUser(payload);
      } catch (err) {
        console.error("Failed to decode ID token:", err);
      }
    } else {
      setUser(null);
    }
  }, [tokens]);

  // Handle code from Cognito redirect
  useEffect(() => {
    const code = new URLSearchParams(window.location.search).get("code");
    if (!code) return;

    exchangeCode(code);
    window.history.replaceState({}, "", "/"); // remove ?code
  }, []);

  async function exchangeCode(code: string) {
    try {
      const res = await fetch("http://localhost:8080/auth/exchange", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ code }),
      });

      if (!res.ok) throw new Error("Failed to exchange code");

      const data: Tokens = await res.json();
      setTokens(data);
      localStorage.setItem("authTokens", JSON.stringify(data));
    } catch (err) {
      console.error("Error exchanging code:", err);
    }
  }

  function login() {
    window.location.href = loginUrl;
  }

  function logout() {
    localStorage.removeItem("authTokens");
    setTokens(null);
    setUser(null);
    window.location.href = logoutUrl;
  }

  return (
    <AuthContext.Provider value={{ user, tokens, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth must be used within AuthProvider");
  return context;
}