import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { api, routes } from "@/lib/api";
import UserListings from "@/pages/UserListings";

export default function CreateListing(){
  const navigate = useNavigate();
  const [title,setTitle]=useState("");
  const [price,setPrice]=useState<number|"">("");
  const [images,setImages]=useState<File[]>([]);
  const [previews,setPreviews]=useState<string[]>([]);
  const [msg,setMsg]=useState<string|null>(null);
  const [loading,setLoading]=useState(false);
  const [createdId,setCreatedId]=useState<string|undefined>(undefined);
  const fileRef = useRef<HTMLInputElement|null>(null);

  useEffect(()=>{
    return ()=>{
      // cleanup blob URLs
      previews.forEach(u=>{ if(u.startsWith("blob:")) URL.revokeObjectURL(u); });
    };
  },[previews]);

  function handleFiles(files: FileList | null){
    if(!files) return;
    const arr = Array.from(files).slice(0,10);
    setImages(arr);
    // revoke old blob urls
    previews.forEach(u=>{ if(u.startsWith("blob:")) URL.revokeObjectURL(u); });
    const urls = arr.map(f=>URL.createObjectURL(f));
    setPreviews(urls);
  }

  async function submit(e:React.FormEvent){
    e.preventDefault(); setMsg(null);
    try{
      setLoading(true);
      
      // Step 1: Create an empty listing
      const createRes = await api.post(routes.listings.create, {});
      const newId = createRes.data?.id;
      
      if(!newId){
        setMsg("Failed to create listing (no id returned).");
        return;
      }

      // Step 2: Fill the listing using the edit/update API
      const form = new FormData();
      form.append("title", title);
      form.append("price", String(price || 0));
      if(images && images.length) images.slice(0,10).forEach(f=>form.append("images", f));

      await api.put(routes.listings.update(newId), form, { headers: { 'Content-Type': 'multipart/form-data' } });
      
      setCreatedId(newId);
      navigate(`/listings/${newId}`);
    }catch(err:any){
      console.error(err);
      setMsg(err?.response?.data?.message || err.message || "Failed to create listing");
    }finally{ setLoading(false); }
  }

  return (
    <div className="max-w-5xl mx-auto">
      <div className="bg-white shadow rounded-lg p-6 mb-6">
        <h1 className="text-2xl font-semibold mb-4">Create Listing</h1>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="md:col-span-1">
            <label className="block text-sm font-medium text-gray-700 mb-2">Photos</label>
            <div className="border border-dashed rounded-lg p-4 flex flex-col items-center bg-gray-50">
              <p className="text-sm text-gray-600 mb-2">Add up to 10 photos</p>
              <input ref={fileRef} type="file" accept="image/*" multiple className="hidden" onChange={e=>handleFiles(e.target.files)} />
              <button type="button" onClick={()=>fileRef.current?.click()} className="px-3 py-2 border rounded bg-white">Choose files</button>
            </div>
            <div className="mt-3 grid grid-cols-3 gap-2">
              {previews.length===0 && <div className="col-span-3 text-sm text-gray-500">No photos yet</div>}
              {previews.map((u,idx)=> (
                <div key={idx} className="w-full h-24 bg-gray-100 rounded overflow-hidden">
                  <img src={u} alt={`preview-${idx}`} className="w-full h-full object-cover" />
                </div>
              ))}
            </div>
          </div>

          <div className="md:col-span-2 space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Title</label>
              <input className="w-full border rounded-lg px-3 py-2" placeholder="e.g. MacBook Pro" value={title} onChange={e=>setTitle(e.target.value)} required />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Price (USD)</label>
              <input className="w-full border rounded-lg px-3 py-2" placeholder="0.00" value={price as any} onChange={e=>setPrice(e.target.value as any)} required />
            </div>

            <div className="flex gap-3">
              <button onClick={(e)=>submit(e as any)} className="px-4 py-2 bg-blue-600 text-white rounded" disabled={loading}>
                {loading ? "Publishing..." : "Publish"}
              </button>
              <button type="button" onClick={()=>{ setTitle(""); setPrice(""); setImages([]); setPreviews([]); if(fileRef.current) fileRef.current.value = ""; }} className="px-4 py-2 border rounded">
                Reset
              </button>
            </div>

            {msg && <div className="text-sm text-red-600">{msg}</div>}
          </div>
        </div>
      </div>

      <div>
        <h2 className="text-xl font-semibold mb-3">My Listings</h2>
        <UserListings />
      </div>
    </div>
  );
}
