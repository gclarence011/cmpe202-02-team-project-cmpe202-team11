import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { api, routes } from "@/lib/api";
import ChatBubble from "@/components/ChatBubble";

type Msg = { id:string; text:string; mine?:boolean; createdAt?:string };

export default function ChatRoom(){
  const { id } = useParams<{id:string}>();
  const [msgs,setMsgs]=useState<Msg[]>([]);
  const [text,setText]=useState("");

  async function load(){ if(!id) return; const r=await api.get(routes.chats.room(id)); setMsgs(r.data?.messages||r.data||[]); }
  async function send(){ if(!id||!text.trim()) return; const r=await api.post(routes.chats.send(id), { text }); setText(""); await load(); }

  useEffect(()=>{ load(); },[id]);

  return (
    <section className="max-w-2xl mx-auto">
      <div className="border rounded-2xl p-3 h-[60vh] overflow-y-auto bg-white">
        {msgs.map(m=><ChatBubble key={m.id} me={m.mine} text={m.text} time={m.createdAt}/>)}
      </div>
      <div className="flex gap-2 mt-2">
        <input value={text} onChange={e=>setText(e.target.value)} placeholder="Type a message" className="flex-1 border rounded-lg px-3 py-2"/>
        <button onClick={send} className="rounded-lg px-3 py-2 border bg-black text-white">Send</button>
      </div>
    </section>
  );
}
