import { useState } from "react";
export type FilterState = { q: string; min?: number; max?: number; category?: string; };
export default function Filters({ onChange }:{ onChange:(f:FilterState)=>void }){
  const [q,setQ]=useState(""); const [min,setMin]=useState(""); const [max,setMax]=useState(""); const [cat,setCat]=useState("");
  const categories = ["ELECTRONICS", "BOOKS", "FURNITURE", "OTHER"];

  return (
    <div className="grid sm:grid-cols-4 gap-2">
      <input value={q} onChange={e=>{setQ(e.target.value); onChange({q:e.target.value, min: Number(min)||undefined, max: Number(max)||undefined, category: cat||undefined});}} placeholder="Search..." className="border rounded-lg px-3 py-2"/>
      <input value={min} onChange={e=>{setMin(e.target.value); onChange({q, min:Number(e.target.value)||undefined, max:Number(max)||undefined, category:cat||undefined});}} placeholder="Min $" className="border rounded-lg px-3 py-2"/>
      <input value={max} onChange={e=>{setMax(e.target.value); onChange({q, min:Number(min)||undefined, max:Number(e.target.value)||undefined, category:cat||undefined});}} placeholder="Max $" className="border rounded-lg px-3 py-2"/>
      <select
        value={cat}
        onChange={e => {
          setCat(e.target.value);
          onChange({
            q,
            min: Number(min) || undefined,
            max: Number(max) || undefined,
            category: e.target.value || undefined
          });
        }}
        className="border rounded-lg px-3 py-2"
      >
        <option value="">All Categories</option>
        {categories.map(c => (
          <option key={c} value={c.toUpperCase()}>{c}</option>
        ))}
      </select>
    </div>
  );
}
