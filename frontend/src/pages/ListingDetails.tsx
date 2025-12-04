import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { api, routes } from "@/lib/api";
import { Swiper, SwiperSlide } from "swiper/react";
import { Navigation } from "swiper/modules";
import "swiper/css";
import "swiper/css/navigation";

import placeholder from "../assets/default-item.png";

type User = {name?:string, id?:string}

type Listing = { id:string; title:string; price:number; description?:string; photos?:string[]; seller?:User; };

export default function ListingDetails(){
  const { id } = useParams<{id:string}>();
  const [item,setItem]=useState<Listing|null>(null);
  const [loading,setLoading]=useState(true);
  const [msg,setMsg]=useState<string>("");
  const navigate = useNavigate();

  useEffect(()=>{ (async()=>{
    try{ 
      const r = await api.get(routes.listings.detail(id!)); 
      setItem(r.data); 
    }
    finally{ setLoading(false); }
  })(); },[id]);

  async function startChat(ownerId:string){
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
      <Swiper
        modules={[Navigation]}
        navigation
        spaceBetween={10}
        slidesPerView={1}
        className="w-full aspect-square rounded-2xl"
      >
        {item.photos?.length ? (
          item.photos.map((p, i) => (
            <SwiperSlide key={i}>
              <img
                src={p}
                alt={item.title}
                className="w-full h-full object-cover rounded-2xl bg-gray-100"
              />
            </SwiperSlide>
          ))
        ) : (
          <SwiperSlide>
            <img
              src={placeholder}
              alt="placeholder"
              className="w-full h-full object-cover rounded-2xl bg-gray-100"
            />
          </SwiperSlide>
        )}
      </Swiper>
      <div className="space-y-3">
        <h2 className="text-2xl font-semibold">{item.title}</h2>
        <div className="text-xl">${item.price?.toFixed(2)}</div>
        <p className="text-xl">{"Seller: " + item.seller?.name}</p>
        <p className="text-xl">{"Description: "}</p>
        <p className="text-gray-700">{item.description}</p>
        <p className="text-sm text-green-600">{msg}</p>
        <div className="flex gap-2">
          <button onClick={()=>startChat(item.seller?.id||"")} className="rounded-lg px-3 py-2 border bg-black text-white">Chat with Seller</button>
          <button onClick={markReport} className="rounded-lg px-3 py-2 border">Report</button>
        </div>
      </div>
    </section>
  );
}
