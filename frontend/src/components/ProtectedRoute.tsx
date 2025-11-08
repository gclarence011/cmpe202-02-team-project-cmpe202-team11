import { useEffect } from "react";
import { useAuth } from "@/lib/AuthProvider";
import { Navigate } from "react-router-dom";

export default function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { user, tokens, loading, login } = useAuth();

  // Show loading while AuthProvider is initializing
  if (loading) {
    return <div>Loading authentication...</div>;
  }

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