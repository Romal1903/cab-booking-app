import { useState } from "react";
import Navbar from "../components/Navbar";
import AdminHome from "./AdminHome";
import AdminUsers from "./AdminUsers";
import AdminDrivers from "./AdminDrivers";
import AdminRides from "./AdminRides";
import AdminSupport from "./AdminSupport";

export default function AdminLayout() {
  const [view, setView] = useState("dashboard");

  return (
    <>
      <Navbar />

      <div className="min-h-screen bg-sky-50">

        <div className="flex justify-center my-6">
          <div className="flex gap-2 bg-white px-3 py-2 rounded-full shadow-sm border border-sky-100">
            {["dashboard", "users", "drivers", "rides", "support"].map((tab) => {
              const isActive = view === tab;

              return (
                <button
                  key={tab}
                  onClick={() => setView(tab)}
                  className={`px-4 py-1.5 rounded-full text-sm font-medium transition
                    ${
                      isActive
                        ? "bg-gray-900 text-white shadow"
                        : "text-gray-600 hover:bg-gray-800 hover:text-white"
                    }`}
                >
                  {tab.charAt(0).toUpperCase() + tab.slice(1)}
                </button>
              );
            })}
          </div>
        </div>

        <div className="flex justify-center px-4 pb-10">
          <div className="w-full max-w-5xl">
            {view === "dashboard" && <AdminHome />}
            {view === "users" && <AdminUsers />}
            {view === "drivers" && <AdminDrivers />}
            {view === "rides" && <AdminRides />}
            {view === "support" && <AdminSupport />}
          </div>
        </div>
      </div>
    </>
  );
}
