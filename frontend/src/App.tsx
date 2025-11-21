import { Routes, Route } from "react-router-dom";
import Navbar from "@/components/Navbar";
import Browse from "@/pages/Browse";
import ListingDetails from "@/pages/ListingDetails";
import CreateListing from "@/pages/EditListing";
import Chat from "@/pages/Chat";
import ChatRoom from "@/pages/ChatRoom";
import AdminDashboard from "@/pages/AdminDashboard";
import ReviewQueue from "@/pages/ReviewQueue";
import NotFound from "@/pages/NotFound";
import ProtectedRoute from "@/components/ProtectedRoute";
import UserListings from "./pages/UserListings";
import EditListing from "@/pages/EditListing";

export default function App(){ // removing ProtectedRoutes</ProtectedRoute>
  return (
    <div className="min-h-screen flex flex-col">
      <Navbar/>
      <main className="container mx-auto p-4 flex-1">
        <Routes>
          <Route path="/" element={<ProtectedRoute><Browse/></ProtectedRoute> } />
          <Route path="/listings/:id" element={<ProtectedRoute><ListingDetails/></ProtectedRoute> } />
          <Route path="/listings/:id/edit" element={<ProtectedRoute><EditListing/></ProtectedRoute> } />
          <Route path="/mylistings" element={<ProtectedRoute><UserListings/></ProtectedRoute> } />
          <Route path="/chats" element={ <ProtectedRoute><Chat/></ProtectedRoute>  } />
          <Route path="/chats/:id" element={ <ProtectedRoute><ChatRoom/></ProtectedRoute>  } />
          <Route path="/admin" element={ <ProtectedRoute><AdminDashboard/></ProtectedRoute> } />
          <Route path="/admin/review" element={ <ProtectedRoute><ReviewQueue/></ProtectedRoute>  } />
          <Route path="*" element={<NotFound/>} />
        </Routes>
      </main>
    </div>
  );
}
