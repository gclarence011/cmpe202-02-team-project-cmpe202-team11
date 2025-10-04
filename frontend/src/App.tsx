import React, { useEffect, useMemo, useState } from "react";

/**
 * Campus Marketplace – Lo‑Fi Wireframe (Single‑file React)
 * - Monochrome, boxy blocks, minimal styling via Tailwind
 * - Screens: Login, Browse, Listing Details, Create Listing, Chat Threads, Chat Room, Admin Dashboard, Admin Review Queue
 * - Top nav lets you switch screens quickly for demos
 * - NOTE: purely static wireframe (no real APIs)
 */

const SCREENS = [
  "Login",
  "Browse",
  "Listing Details",
  "Create Listing",
  "Chat Threads",
  "Chat Room",
  "Admin Dashboard",
  "Admin Review Queue",
] as const;

export default function WireframeApp() {
  const [screen, setScreen] = useState<(typeof SCREENS)[number]>("Browse");
  const [role, setRole] = useState<"Buyer" | "Seller" | "Admin">("Buyer");

  return (
    <div className="min-h-screen bg-neutral-100 text-neutral-900">
      <Header screen={screen} setScreen={setScreen} role={role} setRole={setRole} />
      <div className="max-w-7xl mx-auto p-4">
        <ScreenArea screen={screen} role={role} setScreen={setScreen} />
      </div>
    </div>
  );
}

function Header({
  screen,
  setScreen,
  role,
  setRole,
}: {
  screen: string;
  setScreen: (s: (typeof SCREENS)[number]) => void;
  role: "Buyer" | "Seller" | "Admin";
  setRole: (r: "Buyer" | "Seller" | "Admin") => void;
  
}) 
{
  const [message, setMessage] = useState("loading...");
  useEffect(() => {
    fetch("http://localhost:8080/api/hello")
      .then(res => res.json())
      .then(data => setMessage(data.text))
      .catch(() => setMessage("Error"));
  }, []);
  return (
    <div className="sticky top-0 z-10 border-b border-neutral-200 bg-white/90 backdrop-blur supports-[backdrop-filter]:bg-white/60">
      <div className="max-w-7xl mx-auto px-4 py-3 flex items-center gap-3">
        <div className="font-semibold tracking-wide">Campus Marketplace (Wireframe)<br />{message}</div>
        <div className="ml-auto flex items-center gap-2">
          <select
            className="border rounded-md px-2 py-1 text-sm"
            value={role}
            onChange={(e) => setRole(e.target.value as any)}
            aria-label="Role"
          >
            <option>Buyer</option>
            <option>Seller</option>
            <option>Admin</option>
          </select>
          <Nav screen={screen} setScreen={setScreen} />
        </div>
      </div>
    </div>
  );
}

function Nav({
  screen,
  setScreen,
}: {
  screen: string;
  setScreen: (s: (typeof SCREENS)[number]) => void;
}) {
  return (
    <div className="flex flex-wrap gap-2">
      {SCREENS.map((s) => (
        <button
          key={s}
          onClick={() => setScreen(s)}
          className={`text-xs px-2.5 py-1.5 rounded-md border ${
            s === screen
              ? "bg-neutral-900 text-white border-neutral-900"
              : "bg-white hover:bg-neutral-50 border-neutral-300"
          }`}
        >
          {s}
        </button>
      ))}
    </div>
  );
}

function ScreenArea({
  screen,
  role,
  setScreen,
}: {
  screen: (typeof SCREENS)[number];
  role: "Buyer" | "Seller" | "Admin";
  setScreen: (s: (typeof SCREENS)[number]) => void;
}) {
  switch (screen) {
    case "Login":
      return <Login />;
    case "Browse":
      return <Browse role={role} setScreen={setScreen} />;
    case "Listing Details":
      return <ListingDetails role={role} setScreen={setScreen} />;
    case "Create Listing":
      return <CreateListing />;
    case "Chat Threads":
      return <ChatThreads setScreen={setScreen} />;
    case "Chat Room":
      return <ChatRoom />;
    case "Admin Dashboard":
      return <AdminDashboard setScreen={setScreen} />;
    case "Admin Review Queue":
      return <AdminQueue />;
    default:
      return null;
  }
}

function Card({ title, children, badge }: { title: string; children?: React.ReactNode; badge?: string }) {
  return (
    <div className="rounded-2xl border border-neutral-300 bg-white shadow-sm">
      <div className="flex items-center justify-between border-b border-neutral-200 px-4 py-2">
        <div className="text-sm font-semibold tracking-wide text-neutral-700">{title}</div>
        {badge ? (
          <span className="text-[10px] uppercase tracking-wider px-2 py-1 rounded bg-neutral-200 text-neutral-700">
            {badge}
          </span>
        ) : null}
      </div>
      <div className="p-4 text-sm text-neutral-700">{children}</div>
    </div>
  );
}

function Login() {
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
      <Card title="Login (Campus Only)">
        <div className="space-y-3">
          <Field label="Email (@campus.edu)" placeholder="name@campus.edu" />
          <Field label="Password" placeholder="••••••••" type="password" />
          <div className="flex gap-2">
            <button className="px-3 py-2 rounded-md border border-neutral-300 bg-white hover:bg-neutral-50">Login</button>
            <button className="px-3 py-2 rounded-md border border-neutral-300 bg-white hover:bg-neutral-50">Sign up</button>
          </div>
          <p className="text-xs text-neutral-500">Note: non-campus emails show an inline error.</p>
        </div>
      </Card>
      <Card title="What this page covers" badge="WF-01">
        <ul className="list-disc pl-5 space-y-1 text-sm">
          <li>Campus email validation</li>
          <li>Error states</li>
          <li>Redirect to Browse on success</li>
        </ul>
      </Card>
    </div>
  );
}

function Browse({ role, setScreen }: { role: string; setScreen: (s: (typeof SCREENS)[number]) => void }) {
  return (
    <div className="grid grid-cols-12 gap-4">
      <div className="col-span-12 lg:col-span-3">
        <Card title="Filters" badge="WF-02">
          <div className="space-y-3">
            <Field label="Search" placeholder="Search items…" />
            <Field label="Category" placeholder="Textbooks, Gadgets…" />
            <div className="grid grid-cols-2 gap-2">
              <Field label="Min Price" placeholder="10" />
              <Field label="Max Price" placeholder="100" />
            </div>
            <button className="w-full px-3 py-2 rounded-md border border-neutral-300 bg-white hover:bg-neutral-50">Apply</button>
          </div>
        </Card>
      </div>
      <div className="col-span-12 lg:col-span-9 space-y-4">
        <div className="flex items-center justify-between">
          <div className="text-sm text-neutral-600">Newest listings</div>
          {role === "Seller" && (
            <button
              onClick={() => setScreen("Create Listing")}
              className="px-3 py-2 rounded-md border border-neutral-800 bg-neutral-900 text-white"
            >
              Sell an item
            </button>
          )}
        </div>
        <GridOfCards onOpen={() => setScreen("Listing Details")} />
      </div>
    </div>
  );
}

function ListingDetails({ role, setScreen }: { role: string; setScreen: (s: (typeof SCREENS)[number]) => void }) {
  return (
    <div className="grid grid-cols-12 gap-4">
      <div className="col-span-12 md:col-span-7">
        <Card title="Gallery" badge="WF-04">
          <div className="aspect-[16/10] w-full rounded-md border-2 border-dashed border-neutral-300 bg-neutral-100"></div>
          <div className="mt-3 grid grid-cols-5 gap-2">
            {Array.from({ length: 5 }).map((_, i) => (
              <div key={i} className="h-16 rounded-md border-2 border-dashed border-neutral-300 bg-neutral-100" />
            ))}
          </div>
        </Card>
      </div>
      <div className="col-span-12 md:col-span-5 space-y-4">
        <Card title="Listing Info">
          <InfoRow label="Title" value="Calculus Textbook (CMPE 202)" />
          <InfoRow label="Price" value="$25" />
          <InfoRow label="Condition" value="Good" />
          <InfoRow label="Seller" value="@alice" />
          <div className="mt-3 flex gap-2">
            <button onClick={() => setScreen("Chat Room")} className="px-3 py-2 rounded-md border border-neutral-300 bg-white hover:bg-neutral-50">
              Message Seller
            </button>
            <button className="px-3 py-2 rounded-md border border-neutral-300 bg-white hover:bg-neutral-50">Report</button>
          </div>
        </Card>
        {role === "Seller" && (
          <Card title="Seller Actions">
            <div className="flex gap-2 flex-wrap">
              <button className="px-3 py-2 rounded-md border border-neutral-300 bg-white">Edit</button>
              <button className="px-3 py-2 rounded-md border border-neutral-300 bg-white">Delete</button>
              <button className="px-3 py-2 rounded-md border border-neutral-800 bg-neutral-900 text-white">Mark SOLD</button>
            </div>
          </Card>
        )}
      </div>
    </div>
  );
}

function CreateListing() {
  return (
    <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
      <div className="lg:col-span-2">
        <Card title="Create Listing (Form)" badge="WF-05">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Field label="Title" placeholder="e.g., iPad 9th Gen" />
            <Field label="Category" placeholder="Gadgets" />
            <Field label="Price" placeholder="150" />
            <Field label="Condition" placeholder="New / Like New / Good / Fair" />
            <div className="md:col-span-2">
              <Field label="Description" placeholder="Short description" textarea />
            </div>
            <div className="md:col-span-2">
              <label className="block text-xs font-semibold text-neutral-600 mb-1">Photos</label>
              <div className="rounded-md border-2 border-dashed border-neutral-300 bg-neutral-50 p-6 text-sm text-neutral-500">
                Drag & drop files here (wireframe)
              </div>
            </div>
          </div>
          <div className="mt-4 flex gap-2">
            <button className="px-3 py-2 rounded-md border border-neutral-300 bg-white">Save Draft</button>
            <button className="px-3 py-2 rounded-md border border-neutral-800 bg-neutral-900 text-white">Publish</button>
          </div>
        </Card>
      </div>
      <div>
        <Card title="Guidance">
          <ul className="list-disc pl-5 space-y-1 text-sm">
            <li>Validate required fields</li>
            <li>Use signed URL uploads</li>
            <li>Store photo URLs in DB</li>
          </ul>
        </Card>
      </div>
    </div>
  );
}

function ChatThreads({ setScreen }: { setScreen: (s: (typeof SCREENS)[number]) => void }) {
  return (
    <div className="grid grid-cols-12 gap-4">
      <div className="col-span-12 md:col-span-4">
        <Card title="Chats" badge="WF-06">
          <div className="space-y-2">
            {Array.from({ length: 6 }).map((_, i) => (
              <button
                key={i}
                onClick={() => setScreen("Chat Room")}
                className="w-full text-left px-3 py-2 rounded-md border border-neutral-300 bg-white hover:bg-neutral-50"
              >
                <div className="font-medium text-sm">Listing #{100 + i}</div>
                <div className="text-xs text-neutral-500">Last message preview…</div>
              </button>
            ))}
          </div>
        </Card>
      </div>
      <div className="col-span-12 md:col-span-8">
        <Card title="Preview">
          <div className="h-64 rounded-md border-2 border-dashed border-neutral-300 bg-neutral-50 flex items-center justify-center text-neutral-500">
            Select a chat to preview
          </div>
        </Card>
      </div>
    </div>
  );
}

function ChatRoom() {
  return (
    <div className="grid grid-cols-12 gap-4">
      <div className="col-span-12 md:col-span-4">
        <Card title="Listing Summary" badge="WF-07">
          <div className="flex gap-3 items-center">
            <div className="h-16 w-20 rounded-md border-2 border-dashed border-neutral-300 bg-neutral-100" />
            <div>
              <div className="font-medium text-sm">Calculus Textbook</div>
              <div className="text-xs text-neutral-500">$25 • Good</div>
            </div>
          </div>
        </Card>
      </div>
      <div className="col-span-12 md:col-span-8">
        <Card title="Messages">
          <div className="h-72 overflow-auto space-y-3">
            {MOCK_MESSAGES.map((m) => (
              <div key={m.id} className={`max-w-[70%] px-3 py-2 rounded-md border ${m.me ? "ml-auto border-neutral-800" : "border-neutral-300"}`}>
                <div className="text-xs text-neutral-500">{m.time}</div>
                <div className="text-sm">{m.text}</div>
              </div>
            ))}
          </div>
          <div className="mt-3 flex gap-2">
            <input className="flex-1 px-3 py-2 rounded-md border border-neutral-300" placeholder="Type a message…" />
            <button className="px-3 py-2 rounded-md border border-neutral-800 bg-neutral-900 text-white">Send</button>
          </div>
        </Card>
      </div>
    </div>
  );
}

function AdminDashboard({ setScreen }: { setScreen: (s: (typeof SCREENS)[number]) => void }) {
  return (
    <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
      <Card title="KPIs" badge="WF-09">
        <div className="grid grid-cols-3 gap-3">
          {[
            ["Active Listings", "128"],
            ["Open Reports", "7"],
            ["Hidden Items", "12"],
          ].map(([label, val]) => (
            <div key={label} className="rounded-lg border border-neutral-300 p-3">
              <div className="text-xs text-neutral-500">{label}</div>
              <div className="text-xl font-bold">{val}</div>
            </div>
          ))}
        </div>
      </Card>
      <Card title="Shortcuts">
        <div className="flex flex-wrap gap-2">
          <button onClick={() => setScreen("Admin Review Queue")} className="px-3 py-2 rounded-md border border-neutral-300 bg-white">Review Queue</button>
          <button className="px-3 py-2 rounded-md border border-neutral-300 bg-white">Users</button>
        </div>
      </Card>
      <Card title="Recent Activity">
        <ul className="space-y-2 text-sm">
          {Array.from({ length: 4 }).map((_, i) => (
            <li key={i} className="border-b border-neutral-200 pb-2">Report #{200 + i} created</li>
          ))}
        </ul>
      </Card>
    </div>
  );
}

function AdminQueue() {
  return (
    <Card title="Admin Review Queue" badge="WF-10">
      <div className="overflow-auto">
        <table className="w-full text-sm">
          <thead>
            <tr className="text-left text-neutral-500 border-b">
              {"Listing,Seller,Reason,Reported At,Status,Actions".split(",").map((h) => (
                <th key={h} className="py-2 pr-3 font-normal">{h}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {Array.from({ length: 6 }).map((_, i) => (
              <tr key={i} className="border-b last:border-none">
                <td className="py-2 pr-3">Listing #{110 + i}</td>
                <td className="py-2 pr-3">@seller{i}</td>
                <td className="py-2 pr-3">Incomplete</td>
                <td className="py-2 pr-3">2025-09-2{i}</td>
                <td className="py-2 pr-3">OPEN</td>
                <td className="py-2 pr-3">
                  <div className="flex gap-1 flex-wrap">
                    <button className="px-2 py-1 rounded border border-neutral-300 bg-white">Hide</button>
                    <button className="px-2 py-1 rounded border border-neutral-300 bg-white">Remove</button>
                    <button className="px-2 py-1 rounded border border-neutral-300 bg-white">Warn</button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </Card>
  );
}

function Field({ label, placeholder, textarea, type = "text" }: { label: string; placeholder?: string; textarea?: boolean; type?: string }) {
  return (
    <div>
      <label className="block text-xs font-semibold text-neutral-600 mb-1">{label}</label>
      {textarea ? (
        <textarea rows={4} className="w-full px-3 py-2 rounded-md border border-neutral-300" placeholder={placeholder} />
      ) : (
        <input className="w-full px-3 py-2 rounded-md border border-neutral-300" placeholder={placeholder} type={type} />
      )}
    </div>
  );
}

function InfoRow({ label, value }: { label: string; value: string }) {
  return (
    <div className="grid grid-cols-12 gap-2 items-center py-1">
      <div className="col-span-4 text-xs text-neutral-500">{label}</div>
      <div className="col-span-8 text-sm">{value}</div>
    </div>
  );
}

function GridOfCards({ onOpen }: { onOpen?: () => void }) {
  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
      {Array.from({ length: 9 }).map((_, i) => (
        <button
          key={i}
          onClick={onOpen}
          className="text-left rounded-xl border border-neutral-300 bg-white p-3 hover:border-neutral-400"
        >
          <div className="aspect-[4/3] w-full rounded-md border-2 border-dashed border-neutral-300 bg-neutral-100" />
          <div className="mt-2 h-3 w-2/3 rounded bg-neutral-200" />
          <div className="mt-1 h-3 w-1/3 rounded bg-neutral-200" />
        </button>
      ))}
    </div>
  );
}

const MOCK_MESSAGES = [
  { id: 1, me: false, time: "10:00", text: "Hi, is this available?" },
  { id: 2, me: true, time: "10:01", text: "Yes, it's available." },
  { id: 3, me: false, time: "10:02", text: "Can you do $20?" },
  { id: 4, me: true, time: "10:03", text: "$25 is firm, sorry!" },
];
