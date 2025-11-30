import React, { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { motion } from "framer-motion";
import { useNavigate, useLocation } from "react-router-dom";
import { api, routes } from "@/lib/api";

type Message = {
  id: number;
  sender: {id: string, name: string};
  receiver: {id: string, name: string};
  listing: {id: string, title: string};
  content: string;
  time: string;
};

export default function Chat() {
  const [messages, setMessages] = useState<Message[]>([]);
  const [expandedId, setExpandedId] = useState<number | null>(null);

  const [composeOpen, setComposeOpen] = useState(false);
  const [composeData, setComposeData] = useState<{
    sender?: string;
    recipient: string;
    listing: string;
    message: string;
  }>({
    recipient: "",
    listing: "",
    message: "",
  });

  const location = useLocation();

  useEffect(() => {
    // Handle prefilled compose window when redirected from a listing page
    if (location.state?.prefill) {
      setComposeData(location.state.prefill);
      setComposeOpen(true);
    }
  }, [location.state]);

  useEffect(() => {
    api.get(routes.chats.list)
      //.then((resp) => console.log(resp.data));

      .then((resp) => {
        /* const sorted = resp.data.sort((a: Message, b: Message) =>
          new Date(b.time).getTime() - new Date(a.time).getTime()
        ); */
        //setMessages(sorted);
        setMessages(resp.data);
      });
  }, []);

  const sendMessage = () => {
    api.post(routes.chats.list, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        senderId: composeData.sender,
        receiverId: composeData.recipient,
        listingId: composeData.listing,
        message: composeData.message,
      }),
    }).then((resp) => {
      console.log(resp.data)
      setComposeOpen(false);
      setComposeData({ recipient: "", listing: "", message: "" });
    });
  };

  return (
    <div className="p-6 grid gap-4 max-w-3xl mx-auto">
      <h1 className="text-3xl font-bold mb-4">Inbox</h1>

      {messages.map((msg) => (
        <motion.div key={msg.id} layout>
          <Card
            className="cursor-pointer"
            onClick={() => setExpandedId(expandedId === msg.id ? null : msg.id)}
          >
            <CardContent className="p-4">
              <div className="font-semibold text-xl">{msg.sender.name}</div>
              <div className="font-semibold text-xl">{msg.receiver.name}</div>
              <div className="text-sm opacity-70">{msg.time}</div>
              <div className="mt-2 font-medium">Listing: {msg.listing.title}</div>

              {expandedId === msg.id && (
                <motion.div
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  className="mt-4"
                >
                  <p className="mb-4">{msg.content}</p>
                  <Button
                    onClick={(e) => {
                      e.stopPropagation();
                      setComposeData({ recipient: msg.sender.id, listing: msg.listing.id, message: "" });
                      setComposeOpen(true);
                    }}
                  >
                    Reply
                  </Button>
                </motion.div>
              )}
            </CardContent>
          </Card>
        </motion.div>
      ))}

      {composeOpen && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="fixed inset-0 bg-black/50 flex justify-center items-center p-4"
        >
          <Card className="max-w-lg w-full">
            <CardContent className="p-6 grid gap-4">
              <h2 className="text-2xl font-bold">Compose Message</h2>

              <div>
                <label className="font-semibold">Recipient</label>
                <input
                  className="w-full p-2 border rounded-xl"
                  value={composeData.recipient}
                  onChange={(e) =>
                    setComposeData({ ...composeData, recipient: e.target.value })
                  }
                />
              </div>

              <div>
                <label className="font-semibold">Listing</label>
                <input
                  className="w-full p-2 border rounded-xl"
                  value={composeData.listing}
                  onChange={(e) =>
                    setComposeData({ ...composeData, listing: e.target.value })
                  }
                />
              </div>

              <div>
                <label className="font-semibold">Message</label>
                <textarea
                  className="w-full p-2 border rounded-xl"
                  rows={4}
                  value={composeData.message}
                  onChange={(e) =>
                    setComposeData({ ...composeData, message: e.target.value })
                  }
                />
              </div>

              <div className="flex gap-2 justify-end">
                <Button variant="ghost" onClick={() => setComposeOpen(false)}>
                  Cancel
                </Button>
                <Button onClick={sendMessage}>Send</Button>
              </div>
            </CardContent>
          </Card>
        </motion.div>
      )}
    </div>
  );
}