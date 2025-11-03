import { useState } from "react";
import { api, routes } from "@/lib/api";

export default function CreateListing(){
  const [title,setTitle]=useState("");
  const [price,setPrice]=useState<number|''>('');
  const [description,setDescription]=useState("");
  const [msg,setMsg]=useState<string|null>(null);

  async function submit(e:React.FormEvent){
    e.preventDefault(); setMsg(null);
    const body:any={ title, price: Number(price), description };
    const r = await api.post(routes.listings.create, body);
    setMsg(`Created listing with id ${r.data?.id || ""}`);
  }

  return (
    <div className="max-w-lg">
      <h1 className="text-2xl font-semibold mb-3">Create Listing</h1>
      <form onSubmit={submit} className="space-y-3">
        <input className="w-full border rounded-lg px-3 py-2" placeholder="Title" value={title} onChange={e=>setTitle(e.target.value)} required/>
        <input className="w-full border rounded-lg px-3 py-2" placeholder="Price" value={price} onChange={e=>setPrice(e.target.value as any)} required/>
        <textarea className="w-full border rounded-lg px-3 py-2" placeholder="Description" value={description} onChange={e=>setDescription(e.target.value)}/>
        <button className="rounded-lg px-3 py-2 border bg-black text-white">Create</button>
      </form>
      {msg && <p className="text-green-700 mt-3">{msg}</p>}
    </div>
  );
}
