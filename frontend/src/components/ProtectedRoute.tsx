import { Navigate, useLocation } from "react-router-dom";
import { useEffect } from "react";
import { useAuth} from "@/lib/AuthProvider";

export default function ProtectedRoute({ children }: any) {
  const { user, login } = useAuth();
  useEffect(()=>{
    if (!user) login();
  },[user, login])

  return children;
}
