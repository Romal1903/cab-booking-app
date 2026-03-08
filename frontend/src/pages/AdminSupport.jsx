import { useEffect, useState } from "react";
import api from "../api/axios";
import Card from "../components/Card";

export default function AdminSupport() {

  const [tickets, setTickets] = useState([]);

  const load = () => {
    api.get("/support/admin")
      .then(res => setTickets(res.data));
  };

  useEffect(() => {
    load();
  }, []);

  const updateStatus = async (id, status) => {
    await api.put(`/support/${id}?status=${status}`);
    load();
  };

  return (
    <div className="flex justify-center">
      <div className="w-full max-w-3xl space-y-6 flex flex-col items-center">

        {tickets.map(ticket => (
          <Card key={ticket.id}>
            <div className="space-y-2">

                <div>
                    <b>{ticket.subject}</b>
                </div>

                <div className="text-sm">
                    {ticket.message}
                </div>

                <div className="text-sm">
                    User: {ticket.userName}
                </div>

                {ticket.driverName && (
                <div className="text-sm">
                    Driver: {ticket.driverName}
                </div>
                )}

                {ticket.rideId && (
                <div className="text-sm">
                    Ride ID: {ticket.rideId}
                </div>
                )}

                {ticket.createdAt && (
                <div className="text-xs text-gray-500">
                    Created: {new Date(ticket.createdAt).toLocaleString()}
                </div>
                )}

                <div className="text-sm">
                Status: {ticket.status}
                </div>

                {ticket.status !== "RESOLVED" && (
                <div className="flex gap-2">
                    {ticket.status === "OPEN" && (
                    <button
                        onClick={() => updateStatus(ticket.id, "IN_PROGRESS")}
                    >
                        In Progress
                    </button>
                    )}

                    <button
                    onClick={() => updateStatus(ticket.id, "RESOLVED")}
                    >
                    Resolve
                    </button>
                </div>
                )}
            </div>
            </Card>
        ))}
      </div>
    </div>
  );
}
