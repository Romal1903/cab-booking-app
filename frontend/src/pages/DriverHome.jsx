import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import DriverAvailableRides from "./DriverAvailableRides";
import DriverActiveRides from "./DriverActiveRides";
import DriverCompletedRides from "./DriverCompletedRides";
import DriverCancelledRides from "./DriverCancelledRides";
import DriverStats from "../components/DriverStats";

export default function DriverHome() {
  const navigate = useNavigate();
  const [view, setView] = useState("available");

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) return;

    const payload = JSON.parse(atob(token.split(".")[1]));

    if (payload.role !== "DRIVER") {
      navigate("/select-role");
    }
  }, []);

  return (
    <>
      <Navbar />
      <DriverStats />
      
      <div className="flex justify-center items-center my-6">
        <div className="flex gap-2 bg-white px-3 py-2 rounded-full shadow-sm border border-primaryLight">
          {["available", "active", "completed", "cancelled"].map((tab) => {
            const isActive = view === tab;

            return (
              <button
                key={tab}
                onClick={() => setView(tab)}
                aria-current={isActive ? "true" : undefined}
                className={`relative px-4 py-1.5 rounded-full text-sm font-medium transition-all duration-150
                  ${isActive
                    ? "bg-gray-900 text-white shadow-lg z-10 hover:scale-105 hover:shadow-xl"
                    : "text-primary hover:bg-gray-800 hover:text-white hover:scale-105"}
                  focus:outline-none focus:ring-4 focus:ring-gray-700/30`}
              >
                {tab.charAt(0).toUpperCase() + tab.slice(1)}
              </button>
            );
          })}
        </div>
      </div>

      {view === "available" && <DriverAvailableRides />}
      {view === "active" && <DriverActiveRides />}
      {view === "completed" && <DriverCompletedRides />}
      {view === "cancelled" && <DriverCancelledRides />}
    </>
  );
}
