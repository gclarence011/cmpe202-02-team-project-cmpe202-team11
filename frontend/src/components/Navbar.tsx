import { Link, NavLink } from "react-router-dom";
import logo from "@/assets/logo-light.svg";
import { useAuth } from "@/lib/AuthProvider";
import { useEffect, useState } from "react";
import { api, routes } from "@/lib/api";

export default function Navbar() {
  const nav = (isActive: boolean) => `px-3 py-2 rounded-lg hover:bg-gray-100 ${isActive ? "bg-gray-200" : ""}`;
  const { user, login, logout } = useAuth();
  const [isAdmin, setIsAdmin] = useState(false);

  async function validateAdmin() {
    try{
      const r = await api.get('/admin/reports');
      setIsAdmin(r.data);
    }catch{
      console.error("Failed to fetch admin status");
      setIsAdmin(false);
    }

  }

  useEffect(() => {
    if (user) {
      validateAdmin()
    }
  }, [user]);

  return (
    <header className="border-b bg-white sticky top-0 z-40">
      <div className="container mx-auto px-4 py-3 flex items-center gap-4">
        <Link to="/" className="flex items-center gap-2">
          <img src={logo} alt="Campus Marketplace" className="h-6" />
          <span className="font-semibold hidden sm:inline">Campus Market</span>
        </Link>
        <nav className="ml-auto flex gap-1 sm:gap-2">
          <NavLink to="/" className={({ isActive }) => nav(isActive)} end>Browse</NavLink>
          <NavLink to="/mylistings" className={({ isActive }) => nav(isActive)}>MyListing</NavLink>
          <NavLink to="/chats" className={({ isActive }) => nav(isActive)}>Chats</NavLink>

          {/* Only show Admin link if user is admin */}
          {isAdmin && (
            <NavLink to="/admin" className={({ isActive }) => nav(isActive)}>Admin</NavLink>
          )}

          {user ? (
            <button onClick={logout}>Logout</button>
          ) : (
            <button onClick={login}>Login</button>
          )}
        </nav>
      </div>
    </header>
  );
}