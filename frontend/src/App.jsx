import { useEffect, useState } from "react";

function App() {
  const [message, setMessage] = useState("loading...");

  useEffect(() => {
    fetch("http://localhost:8080/api/hello")
      .then(res => res.json())
      .then(data => setMessage(data.text))
      .catch(() => setMessage("Error"));
  }, []);

  return (
    <div style={{ fontFamily: "sans-serif", padding: 24 }}>
      <h1>React + Java</h1>
      <p>{message}</p>
    </div>
  );
}

export default App;