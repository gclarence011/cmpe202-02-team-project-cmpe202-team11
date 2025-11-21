import { useEffect, useMemo, useState } from "react";
import Filters, { FilterState } from "@/components/Filters";
import ListingCard from "@/components/ListingCard";
import { SearchAgent } from "@/components/SearchAgent";
import { api, routes } from "@/lib/api";

type Listing = {
  id: string;
  title: string;
  price: number;
  photos?: string[];
  badge?: string;
  category?: string;
};

function SkeletonCard() {
  return (
    <div className="rounded-2xl overflow-hidden border bg-white shadow-sm">
      <div className="animate-pulse bg-gray-200 aspect-[4/3]" />
      <div className="p-3 space-y-2">
        <div className="h-4 bg-gray-200 rounded w-2/3 animate-pulse" />
        <div className="h-4 bg-gray-200 rounded w-1/3 animate-pulse" />
      </div>
    </div>
  );
}

function Section({
  title,
  items,
  loading,
}: {
  title: string;
  items: Listing[];
  loading: boolean;
}) {
  return (
    <section className="space-y-3">
      <div className="flex items-center justify-between">
        <h2 className="text-xl font-semibold">{title}</h2>
      </div>
      {loading ? (
        <div className="grid sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
          {Array.from({ length: 8 }).map((_, i) => (
            <SkeletonCard key={i} />
          ))}
        </div>
      ) : items.length === 0 ? (
        <div className="text-center text-gray-600 py-10">
          Nothing here yet. Try changing filters or add your first listing.
        </div>
      ) : (
        <ul className="grid sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
          {items.map((it) => (
            <li key={it.id}>
              <ListingCard {...it} />
            </li>
          ))}
        </ul>
      )}
    </section>
  );
}

const DEMO: Listing[] = [
  {
    id: "demo-1",
    title: "Graphing Calculator (TI-84)",
    price: 25,
    badge: "Popular",
    category: "Electronics",
  },
  {
    id: "demo-2",
    title: "CS 146 Textbook",
    price: 15,
    category: "Books",
  },
  {
    id: "demo-3",
    title: "Mechanical Keyboard",
    price: 49,
    badge: "New",
    category: "Electronics",
  },
  {
    id: "demo-4",
    title: "Dorm Lamp",
    price: 12,
    category: "Home",
  },
  {
    id: "demo-5",
    title: "Backpack (Laptop Sleeve)",
    price: 18,
    category: "Accessories",
  },
  {
    id: "demo-6",
    title: "Noise-Canceling Headphones",
    price: 95,
    badge: "Deal",
    category: "Electronics",
  },
  {
    id: "demo-7",
    title: "Whiteboard + Markers",
    price: 10,
    category: "Home",
  },
  {
    id: "demo-8",
    title: "iPad (9th Gen)",
    price: 180,
    badge: "Hot",
    category: "Electronics",
  },
];
const CATEGORIES = ["ELECTRONICS", "BOOKS", "FURNITURE", "OTHER"];

export default function Browse() {
  const [items, setItems] = useState<Listing[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeCategory, setActiveCategory] = useState<string | undefined>();
  const [nlpResults, setNlpResults] = useState<Listing[] | null>(null);
  const [nlpLoading, setNlpLoading] = useState(false);

  const showDemo = useMemo(
    () => new URLSearchParams(location.search).get("demo") === "1",
    []
  );

  async function fetchData(f?: FilterState) {
    setLoading(true);
    try {
      const res = await api.get(routes.listings.list, {
        params: {
          keyWord: f?.q ?? "",
          min: f?.min,
          max: f?.max,
          category: f?.category || activeCategory,
        },
      });
      const data = Array.isArray(res.data.listings) ? (res.data.listings as Listing[]) : [];

      if (data.length === 0 && !showDemo) {
        setItems([]); // real empty
      } else if (data.length === 0 && showDemo) {
        // demo mode: http://localhost:5173/?demo=1
        setItems(DEMO);
      } else {
        setItems(data);
      }
    } catch {
      // API down? Use demo so the page still looks great.
      setItems(DEMO);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [activeCategory]);

  const handleNLPSearchResults = (results: any) => {
    if (results && results.listings) {
      setNlpResults(results.listings as Listing[]);
    }
  };

  const handleNLPLoading = (loading: boolean) => {
    setNlpLoading(loading);
  };

  const clearNLPResults = () => {
    setNlpResults(null);
  };

  return (
    <div className="space-y-8">
      {/* Hero */}
      <div className="rounded-2xl bg-gradient-to-r from-gray-900 to-gray-700 text-white p-6">
        <h1 className="text-2xl md:text-3xl font-bold">Marketplace</h1>
        <p className="text-white/80 mt-1">
          Buy & sell with classmates. Safe, local, and fast.
        </p>
        <div className="mt-4">
          <a
            href="/create"
            className="inline-flex items-center gap-2 rounded-lg bg-white text-gray-900 px-4 py-2 font-medium hover:bg-gray-100 transition"
          >
            + Create Listing
          </a>
        </div>
      </div>

      {/* Filters */}
      <div className="bg-white border rounded-2xl p-4 shadow-sm">
        <Filters onChange={fetchData} />
        <div className="mt-3 flex flex-wrap gap-2">
          {CATEGORIES.map((c) => (
            <button
              key={c}
              onClick={() =>
                setActiveCategory((prev) => (prev === c ? undefined : c))
              }
              className={`px-3 py-1 rounded-full text-sm border transition ${
                activeCategory === c
                  ? "bg-gray-900 text-white border-gray-900"
                  : "bg-white hover:bg-gray-100"
              }`}
            >
              {c}
            </button>
          ))}
          <button
            onClick={() => setActiveCategory(undefined)}
            className="px-3 py-1 rounded-full text-sm border bg-white hover:bg-gray-100"
          >
            Clear
          </button>
        </div>
      </div>

      {/* Natural Language Search */}
      <div className="bg-gradient-to-r from-blue-50 to-indigo-50 border-2 border-blue-200 rounded-2xl p-6 shadow-sm">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-xl font-semibold text-gray-800">Smart Search</h2>
          {nlpResults && (
            <button
              onClick={clearNLPResults}
              className="text-sm text-blue-600 hover:text-blue-800 underline"
            >
              Clear Results
            </button>
          )}
        </div>
        <SearchAgent
          onSearchResults={handleNLPSearchResults}
          onLoading={handleNLPLoading}
        />
      </div>

      {/* NLP Search Results */}
      {nlpResults && (
        <Section
          title={`Smart Search Results (${nlpResults.length} found)`}
          items={nlpResults}
          loading={nlpLoading}
        />
      )}

      {/* Featured */}
      {!nlpResults && (
        <>
          <Section
            title="Featured picks"
            items={items.slice(0, 4)}
            loading={loading}
          />

          {/* Trending */}
          <Section
            title="Trending near you"
            items={items.slice(4, 12)}
            loading={loading}
          />
        </>
      )}
    </div>
  );
}