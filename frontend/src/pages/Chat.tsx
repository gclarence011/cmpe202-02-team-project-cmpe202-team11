import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api, routes } from "@/lib/api";

type Chat = { id:string; listingTitle?:string; lastMessage?:string };

export default function Chat(){
  const [chats,setChats]=useState<Chat[]>([]);
  useEffect(()=>{ (async()=>{ const r=await api.get(routes.chats.mine); setChats(r.data||[]); })(); },[]);
  return (
    <section className="space-y-3">
      <h1 className="text-2xl font-semibold">Your Chats</h1>
      <ul className="divide-y">
        {chats.map(c=>(
          <li key={c.id} className="py-3">
            <Link to={`/chats/${c.id}`} className="flex justify-between">
              <span className="font-medium">{c.listingTitle||c.id}</span>
              <span className="text-gray-500 text-sm line-clamp-1">{c.lastMessage}</span>
            </Link>
          </li>
        ))}
      </ul>
    </section>
  );
}
