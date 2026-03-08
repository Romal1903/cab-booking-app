import { useEffect, useState } from "react";
import { getDrivers, toggleUserStatus } from "../api/admin";
import Card from "../components/Card";
import ToggleSwitch from "../components/ToggleSwitch";

export default function AdminDrivers() {
  const [drivers, setDrivers] = useState([]);
  const [loadingIds, setLoadingIds] = useState([]);

  const loadDrivers = () => {
    getDrivers()
      .then(res => setDrivers(res.data))
      .catch(console.error);
  };

  useEffect(() => {
    loadDrivers();
  }, []);

  const toggle = async (driver) => {
    setDrivers(prev =>
      prev.map(d =>
        d.id === driver.id ? { ...d, enabled: !d.enabled } : d
      )
    );
    setLoadingIds(prev => [...prev, driver.id]);

    try {
      await toggleUserStatus(driver.id, !driver.enabled);
    } catch (e) {
      setDrivers(prev =>
        prev.map(d =>
          d.id === driver.id ? { ...d, enabled: driver.enabled } : d
        )
      );
      alert(e.response?.data || "Action not allowed");
    } finally {
      setLoadingIds(prev => prev.filter(id => id !== driver.id));
    }
  };

  return (
    <div className="min-h-screen bg-sky-50">
      <div className="flex justify-center px-4 py-6">
        <div className="w-full max-w-3xl flex flex-col items-center space-y-6">

          {drivers.length === 0 && (
            <p className="text-slate-500">No drivers found</p>
          )}

          {drivers.map(driver => (
            <Card key={driver.id}>
              <div className="flex justify-between items-center">
                <div className="space-y-1 text-left">
                  <div>
                    Name: <b>{driver.firstName}</b>
                  </div>

                  <div className="text-sm text-slate-500">
                    Email: {driver.email}
                  </div>

                  <div className="text-sm font-medium">
                    Status:{" "}
                    {driver.enabled ? (
                      <span className="text-green-600">Enabled</span>
                    ) : (
                      <span className="text-red-500">Disabled</span>
                    )}
                  </div>
                </div>

                <ToggleSwitch
                  checked={driver.enabled}
                  onChange={() => toggle(driver)}
                  disabled={loadingIds.includes(driver.id)}
                />
              </div>
            </Card>
          ))}
        </div>
      </div>
    </div>
  );
}
