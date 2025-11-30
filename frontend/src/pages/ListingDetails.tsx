import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { api, routes } from "@/lib/api";

import placeholder from "../assets/default-item.png";

type Listing = { id:string; title:string; price:number; description?:string; photos?:string[]; ownerId?:string; };

export default function ListingDetails(){
  const { id } = useParams<{id:string}>();
  const [item,setItem]=useState<Listing|null>(null);
  const [loading,setLoading]=useState(true);
  const [msg,setMsg]=useState<string>("");
  const navigate = useNavigate();

  useEffect(()=>{ (async()=>{
    try{ const r = await api.get(routes.listings.detail(id!)); setItem(r.data); }
    finally{ setLoading(false); }
  })(); },[id]);

  async function startChat(){
    if(!id) return;
    const r = await api.post(routes.chats.start(id));
    const chatId = r.data?.id || r.data?.chatId;
    if(chatId) navigate(`/chats/${chatId}`);
  }

  async function markReport(){
    if(!id) return;
    // Simplified: send a report without reason and show inline confirmation (no prompts)
    try{
      await api.post(routes.listings.report(id), {});
      setMsg('Report submitted — thank you.');
    }catch(err:any){
      setMsg(err?.response?.data || err.message || 'Failed to submit report');
    }
  }

  if(loading) return <p>Loading...</p>;
  if(!item) return <p>Listing not found.</p>;

  return (
    <section className="grid md:grid-cols-2 gap-6">
      <img src={item.photos?.[0]|| placeholder}alt={item.title} className="w-full aspect-square object-cover bg-gray-100 rounded-2xl"/>
      <div className="space-y-3">
        <h2 className="text-2xl font-semibold">{item.title}</h2>
        <div className="text-xl">${item.price?.toFixed(2)}</div>
        <p className="text-gray-700">{item.description}</p>
        {msg && <div className="text-sm text-green-600">{msg}</div>}
        <div className="flex gap-2">
          <button onClick={startChat} className="rounded-lg px-3 py-2 border bg-black text-white">Chat with Seller</button>
          <button onClick={markReport} className="rounded-lg px-3 py-2 border">Report</button>
        </div>
      </div>
    </section>
  );
}
