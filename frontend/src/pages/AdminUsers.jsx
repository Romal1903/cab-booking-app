import { useEffect, useState } from "react";
import { getAllUsers } from "../api/admin";
import Card from "../components/Card";

export default function AdminUsers() {
  const [users, setUsers] = useState([]);

  useEffect(() => {
    getAllUsers()
      .then(res => setUsers(res.data))
      .catch(console.error);
  }, []);

  return (
    <div className="min-h-screen bg-sky-50">
      <div className="flex justify-center px-4 py-6">
        <div className="w-full max-w-3xl flex flex-col items-center space-y-6">

          {users.length === 0 && (
            <p className="text-slate-500">No users found</p>
          )}

          {users.map(user => (
            <Card key={user.id}>
              <div className="space-y-3 text-left">
                <div>
                  <p className="text-sm text-slate-500">Name</p>
                  <p className="font-semibold">{user.firstName}</p>
                </div>

                <div>
                  <p className="text-sm text-slate-500">Email</p>
                  <p className="break-all">{user.email}</p>
                </div>

                <div className="flex justify-between items-center pt-2">
                  <span className="text-sm font-medium">
                    Role: {user.role}
                  </span>

                  {user.enabled ? (
                    <span className="px-3 py-1 text-xs font-medium text-green-700 bg-green-100 rounded-full">
                      Enabled
                    </span>
                  ) : (
                    <span className="px-3 py-1 text-xs font-medium text-red-600 bg-red-100 rounded-full">
                      Disabled
                    </span>
                  )}
                </div>
              </div>
            </Card>
          ))}
        </div>
      </div>
    </div>
  );
}
