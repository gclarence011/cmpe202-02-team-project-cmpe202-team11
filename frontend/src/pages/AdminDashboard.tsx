import { useEffect, useState } from "react";
import { api, routes } from "@/lib/api";
import ListingCard from "@/components/ListingCard";

type Report = { id:string; listingId:string; reason?:string; listingStatus?:string; listingTitle?:string; };

export default function AdminDashboard(){
  const [reports,setReports] = useState<Report[]>([]);
  const [listingMap, setListingMap] = useState<Record<string, any>>({})

  async function loadReports(){
    try{
      const r = await api.get('/admin/reports');
      const repList: Report[] = Array.isArray(r.data)? r.data : [];
      setReports(repList);

      // fetch full listing details for each reported listing (concurrent)
      const ids = Array.from(new Set(repList.map(x=>String(x.listingId))));
      const fetches = ids.map(id => api.get(routes.listings.detail(id)).then(res=> ({ id, data: res.data })).catch(()=> ({ id, data: null })) );
      const results = await Promise.all(fetches);
      const map: Record<string, any> = {};
      results.forEach(r2 => { if (r2 && r2.id) map[r2.id] = r2.data; });
      setListingMap(map);
    }catch{ setReports([]); setListingMap({}); }
  }

  useEffect(()=>{ loadReports(); },[]);

  async function flagListing(listingId:string){
    try{ await api.patch(routes.listings.flag(listingId)); await loadReports(); }catch(err:any){ alert(err?.response?.data || err.message || 'Failed to flag listing'); }
  }

  async function unflagListing(listingId:string){
    try{ await api.patch(routes.listings.unflag(listingId)); await loadReports(); }catch(err:any){ alert(err?.response?.data || err.message || 'Failed to unflag listing'); }
  }

  return (
    <section className="space-y-3">
      <h1 className="text-2xl font-semibold">Admin Dashboard</h1>
      <div className="grid sm:grid-cols-5 gap-3">
        <div className="border rounded-2xl p-4 bg-orange-50"><div className="text-gray-500 text-sm">Reported</div><div className="text-2xl">{(() => {
          const flaggedSet = new Set<string>();
          const reportedMap: Record<string, Report[]> = {};
          reports.forEach(r => {
            const lid = String(r.listingId);
            if (r.listingStatus === 'FLAGED' || (r.id && String(r.id).startsWith('flag-'))) flaggedSet.add(lid);
            if (String(r.id).startsWith('report-')) {
              if (!reportedMap[lid]) reportedMap[lid] = [];
              reportedMap[lid].push(r);
            }
          });
          return Object.keys(reportedMap).filter(lid => !flaggedSet.has(lid)).length;
        })()}</div></div>
        <div className="border rounded-2xl p-4 bg-red-50"><div className="text-gray-500 text-sm">Flagged</div><div className="text-2xl">{(() => {
          const flaggedSet = new Set<string>();
          reports.forEach(r => {
            const lid = String(r.listingId);
            if (r.listingStatus === 'FLAGED' || (r.id && String(r.id).startsWith('flag-'))) flaggedSet.add(lid);
          });
          return flaggedSet.size;
        })()}</div></div>
      </div>

      <div className="mt-4">
        <h2 className="text-xl font-semibold">Reports</h2>
        <div className="space-y-3 mt-2">
              {reports.length === 0 && <div className="text-sm text-gray-500">No reports or flagged listings yet.</div>}

              {/* Build sets/maps: flaggedSet contains listingIds that are flagged (from either flagged-only entries or reported entries with FLAGED status) */}
              {(() => {
                const flaggedSet = new Set<string>();
                const reportedMap: Record<string, Report[]> = {};
                reports.forEach(r => {
                  const lid = String(r.listingId);
                  if (r.listingStatus === 'FLAGED' || (r.id && String(r.id).startsWith('flag-'))) flaggedSet.add(lid);
                  if (String(r.id).startsWith('report-')) {
                    if (!reportedMap[lid]) reportedMap[lid] = [];
                    reportedMap[lid].push(r);
                  }
                });

                // Prepare arrays
                const reportedListings = Object.keys(reportedMap).filter(lid => !flaggedSet.has(lid));
                const flaggedListings = Array.from(flaggedSet);

                return (
                  <div className="space-y-6">
                    <div>
                      <div className="flex items-center justify-between mb-2">
                        <h3 className="text-lg font-medium">Reported ({reportedListings.length})</h3>
                      </div>
                      {reportedListings.length === 0 && <div className="text-sm text-gray-500">No reported listings.</div>}
                      <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-3">
                        {reportedListings.map(lid => {
                          const reportsFor = reportedMap[lid] || [];
                          const sample = reportsFor[0];
                          const listing = listingMap[lid];
                          return (
                            <div key={`reported-${lid}`} className={`bg-orange-50 border rounded-lg p-3`}>
                              <div className="w-full">
                                {listing ? (
                                  <ListingCard id={String(listing.id)} title={listing.title} price={Number(listing.price||0)} photos={listing.photos} badge={'REPORTED'} />
                                ) : (
                                  <div className="p-3 border rounded text-sm">{sample.listingTitle || `Listing #${lid}`}</div>
                                )}
                              </div>
                              <div className="mt-2 flex items-center justify-between">
                                <div className="text-xs text-gray-600">{reportsFor.length} report{reportsFor.length !== 1 ? 's' : ''}</div>
                                <button onClick={()=>flagListing(lid)} className="px-3 py-1 bg-red-500 text-white rounded text-xs hover:bg-red-600">Flag</button>
                              </div>
                            </div>
                          );
                        })}
                      </div>
                    </div>

                    <div>
                      <div className="flex items-center justify-between mb-2">
                        <h3 className="text-lg font-medium">Flagged ({flaggedListings.length})</h3>
                      </div>
                      {flaggedListings.length === 0 && <div className="text-sm text-gray-500">No flagged listings.</div>}
                      <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-3">
                        {flaggedListings.map(lid => {
                          const listing = listingMap[lid];
                          return (
                            <div key={`flagged-${lid}`} className={`bg-red-50 border rounded-lg p-3`}>
                              <div className="w-full">
                                {listing ? (
                                  <ListingCard id={String(listing.id)} title={listing.title} price={Number(listing.price||0)} photos={listing.photos} badge={'FLAGGED'} />
                                ) : (
                                  <div className="p-3 border rounded text-sm">Listing #{lid}</div>
                                )}
                              </div>
                              <div className="mt-2 flex items-center justify-between">
                                <div className="text-xs text-gray-600">Removed from public view</div>
                                <button onClick={()=>unflagListing(lid)} className="px-3 py-1 bg-green-500 text-white rounded text-xs hover:bg-green-600">Unflag</button>
                              </div>
                            </div>
                          );
                        })}
                      </div>
                    </div>
                  </div>
                );
              })()}
        </div>
      </div>
    </section>
  );
}
