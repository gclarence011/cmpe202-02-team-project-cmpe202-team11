import { useState, useCallback } from "react";
import { api } from "@/lib/api";

type SearchAgentProps = {
  onSearchResults?: (results: any) => void;
  onLoading?: (loading: boolean) => void;
};

/**
 * Simple Search Agent Component
 * Single editable search query (no history tracking)
 * Let the AI model handle understanding what the user wants
 */
export function SearchAgent({ onSearchResults, onLoading }: SearchAgentProps) {
  const [currentQuery, setCurrentQuery] = useState("");
  const [isSearching, setIsSearching] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [lastSearchQuery, setLastSearchQuery] = useState<string>("");

  const performSearch = useCallback(
    async (query: string) => {
      if (!query.trim()) {
        setError("Please enter a search query");
        return;
      }

      try {
        setIsSearching(true);
        setError(null);
        onLoading?.(true);

        // Send search to backend AI service
        const response = await api.post("/api/search/nlp", {
          query: query,
          searchHistory: [], // No history context - let AI understand from query alone
        });

        onSearchResults?.(response.data);
        setLastSearchQuery(query);
        setError(null);
      } catch (err: any) {
        const errorMessage =
          err.response?.data?.message ||
          err.message ||
          "Failed to perform search";
        setError(errorMessage);
        console.error("Search error:", err);
      } finally {
        setIsSearching(false);
        onLoading?.(false);
      }
    },
    [onSearchResults, onLoading]
  );

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentQuery.trim()) return;
    
    await performSearch(currentQuery);
    // Keep the query in input for editing
  };

  const handleEditSearch = () => {
    // Just focus on the input - it's already there for editing
    // The query stays in the input field
    setCurrentQuery(lastSearchQuery);
  };

  const handleClear = () => {
    setCurrentQuery("");
    setLastSearchQuery("");
    setError(null);
    onSearchResults?.(null);
  };

  return (
    <div className="space-y-4 mb-6">
      {/* Search Input */}
      <form onSubmit={handleSearch} className="flex gap-2">
        <input
          type="text"
          value={currentQuery}
          onChange={(e) => setCurrentQuery(e.target.value)}
          placeholder="What are you looking for? e.g., 'laptop under 100', 'textbooks', 'electronics under 50'"
          className="flex-1 px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          disabled={isSearching}
        />
        <button
          type="submit"
          disabled={isSearching || !currentQuery.trim()}
          className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-gray-400 transition"
        >
          {isSearching ? "Searching..." : "Search"}
        </button>
      </form>

      {/* Error Message */}
      {error && (
        <div className="p-3 bg-red-100 text-red-700 rounded-lg text-sm">
          {error}
        </div>
      )}

      {/* Last Search Display */}
      {lastSearchQuery && (
        <div className="p-3 bg-blue-50 rounded-lg text-sm">
          <div className="text-gray-600">
            Last search: <span className="font-semibold text-blue-600">{lastSearchQuery}</span>
          </div>
          <div className="flex gap-2 mt-2">
            <button
              onClick={() => {
                setCurrentQuery(lastSearchQuery);
              }}
              className="text-xs px-3 py-1 bg-blue-200 hover:bg-blue-300 rounded text-blue-700 transition"
            >
              ✎ Edit Search
            </button>
            <button
              onClick={handleClear}
              className="text-xs px-3 py-1 bg-gray-200 hover:bg-gray-300 rounded text-gray-700 transition"
            >
              ✕ Clear
            </button>
          </div>
        </div>
      )}

      {/* Help Text */}
      <div className="text-xs text-gray-500 px-1">
        💡 The AI understands natural language - try "under 30", "laptops between 50 and 200", or just "books"
      </div>
    </div>
  );
}
