import { useEffect, useState } from "react";
import api from "../api/axios";

export default function AdminHome() {
  const [stats, setStats] = useState(null);

  useEffect(() => {
    api.get("/admin/stats")
      .then(res => setStats(res.data))
      .catch(() => alert("Unauthorized or failed to load admin data"));
  }, []);

  if (!stats) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-sky-50">
        Loading admin dashboard...
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-sky-50 px-4 py-6">
      <div className="max-w-3xl mx-auto space-y-6">
        <h1 className="text-2xl font-bold text-center">
          Admin Dashboard
        </h1>

        <div className="grid grid-cols-4 gap-4">
          <div className="bg-white p-4 rounded-xl shadow">
            <p className="text-gray-500">Total Users</p>
            <p className="text-2xl font-semibold">{stats.users}</p>
          </div>

          <div className="bg-white p-4 rounded-xl shadow">
            <p className="text-gray-500">Total Rides</p>
            <p className="text-2xl font-semibold">{stats.rides}</p>
          </div>

          <div className="bg-white p-4 rounded-xl shadow">
            <p className="text-gray-500">Total Reports</p>
            <p className="text-2xl font-semibold">{stats.reports}</p>
          </div>

          <div className="bg-white p-4 rounded-xl shadow">
            <p className="text-gray-500">Open Reports</p>
            <p className="text-2xl font-semibold">{stats.openReports}</p>
          </div>
        </div>
      </div>
    </div>
  );
}
