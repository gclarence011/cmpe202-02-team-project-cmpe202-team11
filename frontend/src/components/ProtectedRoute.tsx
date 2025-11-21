import { useEffect } from "react";
import { useAuth } from "@/lib/AuthProvider";

export default function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { tokens, loading, login } = useAuth();

  // Only trigger login if loading is done and no tokens exist
  useEffect(() => {
    if (!loading && !tokens) {
      login();
    }
  }, [loading, tokens, login]);

  // Render nothing if still waiting or logging in
  if (loading || !tokens) {
    return null;
  }

  return children;
}