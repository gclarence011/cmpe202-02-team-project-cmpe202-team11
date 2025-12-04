import { useEffect, useRef, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { api, routes } from "@/lib/api";

type Listing = {
  id: string;
  title: string;
  price: number;
  photos?: string[];
  badge?: string;
  category?: string;
  description?: string;
  status?: "draft" | "sold" | "open";
};

export default function EditListing(){
  const navigate = useNavigate();
  const [title,setTitle]=useState("");
  const [price,setPrice]=useState<number|"">("");
  const [description,setDescription] = useState("");
  const [status,setStatus] = useState<"draft"|"sold"|"open">("draft");
  const [category,setCategory] = useState("");
  const [images,setImages]=useState<File[]>([]);
  const [previews,setPreviews]=useState<string[]>([]);
  const [msg,setMsg]=useState<string|null>(null);
  const [loading,setLoading]=useState(true);
  const fileRef = useRef<HTMLInputElement|null>(null);
  const { id } = useParams<{id:string}>();
  const [item,setItem]=useState<Listing|null>(null);

  useEffect(()=>{ (async()=>{
    try{ const r = await api.get(routes.listings.detail(id!)); setItem(r.data); }
    finally{ setLoading(false); }
  })(); },[]);

  useEffect(()=>{
    if(item){
      setTitle(item.title || "");
      setPrice(item.price || "");
      setPreviews(item.photos || []);
      setDescription(item.description || "");
      setStatus((item.status as any) || "draft");
      setCategory(item.category || "");
    }
  }, [item]);

  useEffect(()=>{
    return ()=>{
      // cleanup blob URLs
      previews.forEach(u=>{ if(u.startsWith("blob:")) URL.revokeObjectURL(u); });
    };
  },[previews]);

  async function handleFiles(files: FileList | null){
    if (!files) return;

    const arr = Array.from(files).slice(0, 10);

    // revoke old blob URLs
    previews.forEach(u => { if (u.startsWith("blob:")) URL.revokeObjectURL(u); });

    for (const file of arr) {
      const formData = new FormData();
      formData.append("file", file);

      try {
        setLoading(true)
        const res = await api.post(routes.listings.upload(id!), formData);
        setPreviews(prev=>[...prev,res.data])
        navigate(`/listings/${id}/edit`);
      } catch (err) {
        console.error("Upload failed for file:", file.name, err);
      } finally{
        setLoading(false)
      }
    }
  }

  function removePreview(index: number){
    // revoke blob URL if needed
    const url = previews[index];
    if(url.startsWith("blob:")) URL.revokeObjectURL(url);

    // remove from previews list
    const updated = previews.filter((_,i)=>i!==index);
    setPreviews(updated);
  }

  async function submit(e:React.FormEvent){
    e.preventDefault(); setMsg(null);
    try{
      setLoading(true);
      
      // Update the existing listing using PATCH
      await api.patch(routes.listings.update(id!), {
        title,
        price,
        description,
        status,
        category,
        photos: previews   // send list of URL strings
      });

      navigate(`/mylistings`);
    }catch(err:any){
      console.error(err);
      setMsg(err?.response?.data?.message || err.message || "Failed to edit listing");
    }finally{ setLoading(false); }
  }

  async function deleteListing() {
    try{
      setLoading(true)
      await api.delete(routes.listings.remove(id!))
      navigate(`/mylistings`);
    }catch(err:any){
      console.error(err);
      setMsg(err?.response?.data?.message || err.message || "Failed to delete listing");
    }finally{ setLoading(false); }
  }

  if(loading) return <p>Loading...</p>;
  if(!item) return <p>Listing not found.</p>;

  return (
    <div className="max-w-5xl mx-auto">
      <div className="bg-white shadow rounded-lg p-6 mb-6">
        <h1 className="text-2xl font-semibold mb-4">Edit Listing</h1>
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
                <div key={idx} className="relative w-full h-24 bg-gray-100 rounded overflow-hidden">
                  <img src={u} alt={`preview-${idx}`} className="w-full h-full object-cover" />
                  <button
                    type="button"
                    onClick={() => removePreview(idx)}
                    className="absolute top-1 right-1 bg-white bg-opacity-80 rounded-full p-1 text-xs border"
                  >
                    ✕
                  </button>
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
              <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
              <textarea
                className="w-full border rounded-lg px-3 py-2"
                placeholder="Describe your item..."
                value={description}
                onChange={e=>setDescription(e.target.value)}
                rows={4}
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Price (USD)</label>
              <input className="w-full border rounded-lg px-3 py-2" placeholder="0.00" value={price as any} onChange={e=>setPrice(e.target.value as any)} required />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Status</label>
              <select
                className="w-full border rounded-lg px-3 py-2"
                value={status}
                onChange={e=>setStatus(e.target.value as any)}
              >
                <option value="DRAFT">Draft</option>
                <option value="OPEN">Open</option>
                <option value="SOLD">Sold</option>
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Category</label>
              <select
                className="w-full border rounded-lg px-3 py-2"
                value={category}
                onChange={e=>setCategory(e.target.value)}
              >
                <option value="">Select category</option>
                <option value="ELECTRONICS">Electronics</option>
                <option value="BOOKS">Books</option>
                <option value="FURNITURE">Furniture</option>
                <option value="other">Other</option>
              </select>
            </div>

            <div className="flex gap-3">
              <button onClick={(e)=>submit(e as any)} className="px-4 py-2 bg-blue-600 text-white rounded" disabled={loading}>
                {loading ? "Publishing..." : "Publish"}
              </button>
              <button type="button" onClick={()=>{
                  setTitle(item.title || "");
                  setPrice(item.price || "");
                  setPreviews(item.photos || []);
                  setDescription(item.description || "");
                  setStatus((item.status as any) || "draft");
                  setCategory(item.category || "");
                }} 
                className="px-4 py-2 border rounded">
                Reset
              </button>
              <button type="button" onClick={()=>{
                deleteListing()
                }} 
                className="px-4 py-2 border rounded">
                delete
              </button>
            </div>

            {msg && <div className="text-sm text-red-600">{msg}</div>}
          </div>
        </div>
      </div>
    </div>
  );
}
