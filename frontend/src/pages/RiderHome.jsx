import { useState } from "react";
import Navbar from "../components/Navbar";
import RiderBookRide from "./RiderBookRide";
import RiderRideHistory from "./RiderRideHistory";
import RiderSupport from "./RiderSupport";

export default function RiderHome() {
  const [view, setView] = useState("book");

  return (
    <>
      <Navbar />

      <div className="flex justify-center items-center my-6">
        <div className="flex gap-2 bg-white px-3 py-2 rounded-full shadow-sm border border-primaryLight">
          {["book", "history", "support"].map((tab) => {
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
                {tab === "book"
                  ? "Book Ride"
                  : tab === "history"
                  ? "Ride History"
                  : "Support"}
              </button>
            );
          })}
        </div>
      </div>

      {view === "book" && <RiderBookRide />}
      {view === "history" && <RiderRideHistory />}
      {view === "support" && <RiderSupport />}
    </>
  );
}
