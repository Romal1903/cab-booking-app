export default function Navbar() {
  const logout = () => {
    localStorage.removeItem("token");
    window.location.href = "/";
  };

  return (
    <div className="flex justify-between items-center px-6 py-4 bg-white border-b border-primaryLight shadow-sm">
      <h1 className="text-xl font-semibold text-primary">
        Cab Booking
      </h1>
      <button
        onClick={logout}
        className="text-sm bg-red-500 text-white px-3 py-1 rounded-md hover:bg-red-600 hover:scale-105 transition">
        Logout
      </button>
    </div>
  );
}
