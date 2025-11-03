import { useEffect, useState } from "react";
import { api } from "@/lib/api";

type Report = { id:string; listingId:string; reason?:string; };

export default function ReviewQueue(){
  const [items,setItems]=useState<Report[]>([]);
  useEffect(()=>{ (async()=>{ try{ const r = await api.get('/admin/reports'); setItems(r.data||[]); }catch{ setItems([]); } })(); },[]);
  return (
    <section className="space-y-3">
      <h1 className="text-2xl font-semibold">Reports Review</h1>
      <ul className="divide-y">
        {items.map(r=> <li key={r.id} className="py-3 flex justify-between"><span>Listing #{r.listingId}</span><span className="text-sm text-gray-600">{r.reason}</span></li>)}
      </ul>
    </section>
  );
}
