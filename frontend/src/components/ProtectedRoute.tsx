import { Navigate, useLocation } from "react-router-dom";
import { getAccessToken } from "@/lib/session";

export default function ProtectedRoute({ children }:{ children: JSX.Element }){
  const token = getAccessToken();
  const location = useLocation();
  if(!token) return <Navigate to="/login" state={{from:location}} replace />;
  return children;
}
