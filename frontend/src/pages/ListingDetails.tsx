import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { api, routes } from "@/lib/api";

import placeholder from "../assets/default-item.png";

type Listing = { id:string; title:string; price:number; description?:string; photos?:string[]; ownerId?:string; };

export default function ListingDetails(){
  const { id } = useParams<{id:string}>();
  const { ownerId } = useParams<{ownerId:string}>();
  const [item,setItem]=useState<Listing|null>(null);
  const [loading,setLoading]=useState(true);
  const navigate = useNavigate();

  useEffect(()=>{ (async()=>{
    try{ const r = await api.get(routes.listings.detail(id!)); setItem(r.data); }
    finally{ setLoading(false); }
  })(); },[id]);

  async function startChat(){
    if(!id) return;
    console.log(ownerId, id);
    navigate("/chats", {
      state: {
        prefill: {
          recipient: ownerId,
          listing: id,
          message: "",
        },
      },
    })
  }

  async function markReport(){
    if(!id) return;
    await api.patch(routes.listings.markSold(id)); 
    navigate("/");
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
        <div className="flex gap-2">
          <button onClick={startChat} className="rounded-lg px-3 py-2 border bg-black text-white">Chat with Seller</button>
          <button onClick={markReport} className="rounded-lg px-3 py-2 border">Report</button>
        </div>
      </div>
    </section>
  );
}
