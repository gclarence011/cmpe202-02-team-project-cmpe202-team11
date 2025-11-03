import { useEffect, useState } from "react";
import { api, routes } from "@/lib/api";

export default function AdminDashboard(){
  const [stats,setStats]=useState<any>(null);
  useEffect(()=>{ (async()=>{ try{ const r = await api.get('/admin/stats'); setStats(r.data); }catch{ setStats({ users:0, listings:0, chats:0 }); } })(); },[]);
  return (
    <section className="space-y-3">
      <h1 className="text-2xl font-semibold">Admin Dashboard</h1>
      <div className="grid sm:grid-cols-3 gap-3">
        <div className="border rounded-2xl p-4"><div className="text-gray-500 text-sm">Users</div><div className="text-2xl">{stats?.users ?? '-'}</div></div>
        <div className="border rounded-2xl p-4"><div className="text-gray-500 text-sm">Listings</div><div className="text-2xl">{stats?.listings ?? '-'}</div></div>
        <div className="border rounded-2xl p-4"><div className="text-gray-500 text-sm">Chats</div><div className="text-2xl">{stats?.chats ?? '-'}</div></div>
      </div>
    </section>
  );
}
