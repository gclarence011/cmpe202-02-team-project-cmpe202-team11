import { Routes, Route } from "react-router-dom";
import Navbar from "@/components/Navbar";
import Browse from "@/pages/Browse";
import Login from "@/pages/Login";
import ListingDetails from "@/pages/ListingDetails";
import CreateListing from "@/pages/CreateListing";
import Chat from "@/pages/Chat";
import ChatRoom from "@/pages/ChatRoom";
import AdminDashboard from "@/pages/AdminDashboard";
import ReviewQueue from "@/pages/ReviewQueue";
import NotFound from "@/pages/NotFound";
import ProtectedRoute from "@/components/ProtectedRoute";

export default function App(){ // removing ProtectedRoutes</ProtectedRoute>
  return (
    <div className="min-h-screen flex flex-col">
      <Navbar/>
      <main className="container mx-auto p-4 flex-1">
        <Routes>
          <Route path="/" element={<ProtectedRoute><Browse/></ProtectedRoute> } />
          <Route path="/login" element={<Login/>} />
          <Route path="/listings/:id" element={<ProtectedRoute><ListingDetails/></ProtectedRoute> } />
          <Route path="/create" element={<ProtectedRoute><CreateListing/></ProtectedRoute> } />
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
