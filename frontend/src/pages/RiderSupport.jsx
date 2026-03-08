import { useEffect, useState } from "react";
import api from "../api/axios";
import Card from "../components/Card";
import Button from "../components/Button";

export default function RiderSupport() {

  const [tickets, setTickets] = useState([]);
  const [subject, setSubject] = useState("");
  const [message, setMessage] = useState("");

  const loadTickets = async () => {
    const res = await api.get("/support/my");
    setTickets(res.data);
  };

  useEffect(() => {
    loadTickets();
  }, []);

  const submitTicket = async () => {

    if (!subject || !message) {
      alert("Please fill all fields");
      return;
    }

    await api.post("/support", {
      subject,
      message,
      status: "OPEN"
    });

    setSubject("");
    setMessage("");
    loadTickets();
  };

  return (
    <div className="flex justify-center">
      <div className="w-full max-w-3xl space-y-6 flex flex-col items-center">

        <Card>

          <div className="space-y-3">

            <div className="font-semibold text-lg">
              Create Support Ticket
            </div>

            <input
              className="w-full border p-2 rounded"
              placeholder="Subject"
              value={subject}
              onChange={e => setSubject(e.target.value)}
            />

            <textarea
              className="w-full border p-2 rounded"
              placeholder="Describe your issue..."
              rows="4"
              value={message}
              onChange={e => setMessage(e.target.value)}
            />

            <div className="flex justify-center">
              <Button onClick={submitTicket}>
                Submit Ticket
              </Button>
            </div>

          </div>

        </Card>

        <div className="space-y-4">

          {tickets.map(ticket => (

            <Card key={ticket.id}>

              <div className="space-y-2">

                <div className="font-semibold">
                  {ticket.subject}
                </div>

                <div className="text-sm">
                  {ticket.message}
                </div>

                <div className="text-sm">
                  Status: {ticket.status}
                </div>

              </div>

            </Card>

          ))}

        </div>

      </div>

    </div>
  );
}
