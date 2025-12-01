import { useEffect, useState } from "react";
import { api, routes } from "@/lib/api";

type Report = { id:string; listingId:string; reason?:string; listingStatus?:string; listingTitle?:string; };

export default function ReviewQueue(){
  const [items,setItems]=useState<Report[]>([]);
  async function load(){
    try{ const r = await api.get('/admin/reports'); setItems(r.data||[]); }catch{ setItems([]); }
  }
  useEffect(()=>{ load(); },[]);

  async function flagListing(listingId:string){
    if(!confirm('Flag this listing? It will be removed from public browse.')) return;
    try{ await api.patch(routes.listings.flag(listingId)); alert('Listing flagged'); await load(); }catch(err:any){ alert(err?.response?.data || err.message || 'Failed to flag listing'); }
  }

  return (
    <section className="space-y-3">
      <h1 className="text-2xl font-semibold">Reports Review</h1>
      <ul className="divide-y">
        {items.map(r=> {
          const bg = r.listingStatus === 'FLAGED' ? 'bg-red-100' : 'bg-orange-100';
          return (
            <li key={r.id} className={`${bg} py-3 px-3 rounded my-2 flex justify-between items-center`}>
              <div>
                <div className="font-medium">{r.listingTitle || `Listing #${r.listingId}`}</div>
                <div className="text-sm text-gray-600">{r.reason}</div>
              </div>
              <div className="flex gap-2">
                {r.listingStatus !== 'FLAGED' && <button onClick={()=>flagListing(r.listingId)} className="px-3 py-1 bg-red-50 border rounded">Flag</button>}
              </div>
            </li>
          );
        })}
      </ul>
    </section>
  );
}
