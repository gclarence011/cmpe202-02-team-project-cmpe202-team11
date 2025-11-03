import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { api, routes } from "@/lib/api";
import { setTokens } from "@/lib/session";

export default function Login(){
  const [email,setEmail]=useState("");
  const [password,setPassword]=useState("");
  const [loading,setLoading]=useState(false);
  const [error,setError]=useState<string|null>(null);
  const navigate = useNavigate();
  const location = useLocation() as any;
  const from = location.state?.from?.pathname || "/";

  async function onSubmit(e:React.FormEvent){
    e.preventDefault();
    setLoading(true); setError(null);
    try{
      const res = await api.post(routes.auth.login,{ email, password });
      const accessToken = res.data?.accessToken;
      const refreshToken = res.data?.refreshToken ?? null;
      if(!accessToken) throw new Error("Missing accessToken");
      setTokens(accessToken, refreshToken);
      navigate(from, { replace: true });
    }catch(err:any){
      setError(err?.message || "Login failed");
    }finally{ setLoading(false); }
  }

  return (
    <div className="max-w-sm mx-auto mt-10">
      <h1 className="text-2xl font-semibold mb-4">Sign in</h1>
      <form onSubmit={onSubmit} className="space-y-3">
        <input type="email" value={email} onChange={e=>setEmail(e.target.value)} placeholder="Campus email" className="w-full border rounded-lg px-3 py-2" required />
        <input type="password" value={password} onChange={e=>setPassword(e.target.value)} placeholder="Password" className="w-full border rounded-lg px-3 py-2" required />
        {error && <div className="text-red-600 text-sm">{error}</div>}
        <button disabled={loading} className="w-full rounded-lg px-3 py-2 border bg-black text-white disabled:opacity-50">{loading?"Signing in...":"Sign in"}</button>
      </form>
    </div>
  );
}
