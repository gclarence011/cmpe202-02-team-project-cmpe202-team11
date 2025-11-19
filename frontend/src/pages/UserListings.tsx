import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api, routes } from "@/lib/api";

type Listing = { id:string; title:string; price:number; imageUrl?:string };

export default function UserListings(){
  const [items,setItems]=useState<Listing[]>([]);
  const [loading,setLoading]=useState(true);
  const [err,setErr]=useState<string|null>(null);

  useEffect(()=>{ (async()=>{
    try{
      // assume backend supports ?mine=true to fetch current user's listings
      const r = await api.get(routes.listings.list, { params: { mine: true } });
      setItems(r.data || []);
    }catch(e:any){
      console.error(e);
      // Axios error handling: if response is present, show status; if request made but no response -> network issue
      if(e?.response){
        if(e.response.status === 401){
          setErr("You are not logged in. Please sign in to view your listings.");
        }else{
          const msg = e.response.data?.message || `Server error (${e.response.status})`;
          setErr(msg);
        }
      }else if(e?.request){
        setErr("Cannot reach the API. Is the backend server running and CORS configured? (check VITE_API_BASE_URL)");
      }else{
        setErr(e?.message || "Failed to fetch");
      }
    }finally{ setLoading(false); }
  })(); },[]);

  if(loading) return <p>Loading...</p>;
  if(err) return <p className="text-red-600">{err}</p>;

  return (
    <div>
      <h1 className="text-2xl font-semibold mb-4">My Listings</h1>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {items.length === 0 && <p>No listings yet. <Link to="/create" className="underline">Create one</Link>.</p>}
        {items.map(it=> (
          <div key={it.id} className="border rounded-lg p-3 flex gap-3 items-center">
            <img src={it.imageUrl} alt={it.title} className="w-24 h-24 object-cover bg-gray-100 rounded"/>
            <div className="flex-1">
              <h2 className="font-semibold">{it.title}</h2>
              <div>${it.price?.toFixed(2)}</div>
              <div className="mt-2 flex gap-2">
                <Link to={`/listings/${it.id}`} className="px-3 py-1 border rounded">View</Link>
                <Link to={`/listings/${it.id}/edit`} className="px-3 py-1 border rounded">Edit</Link>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
