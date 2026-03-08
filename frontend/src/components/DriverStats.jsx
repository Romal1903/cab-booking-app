import { useEffect, useState } from "react";
import api from "../api/axios";

export default function DriverStats() {
  const [stats, setStats] = useState(null);

  useEffect(() => {
    Promise.all([
      api.get("/driver/earnings/summary"),
      api.get("/driver/ratings/summary"),
    ]).then(([earnings, ratings]) => {
      setStats({
        ...earnings.data,
        ...ratings.data,
      });
    });
  }, []);

  if (!stats) return null;

  return (
    <div className="max-w-5xl mx-auto grid grid-cols-4 gap-4 my-4 px-4">
      <Stat label="Today" value={`₹${stats.today}`} />
      <Stat label="Weekly" value={`₹${stats.weekly}`} />
      <Stat label="Total" value={`₹${stats.total}`} />
      <Stat label="Rating" value={`${stats.average} ⭐ (${stats.count})`} />
    </div>
  );
}

function Stat({ label, value }) {
  return (
    <div className="bg-white rounded-xl shadow p-4 text-center">
      <div className="text-sm text-gray-500">{label}</div>
      <div className="text-xl font-semibold">{value}</div>
    </div>
  );
}
