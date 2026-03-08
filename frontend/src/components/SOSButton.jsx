import api from "../api/axios";

export default function SOSButton({ rideId }) {

  const triggerSOS = async () => {

    const confirm = window.confirm(
      "Send emergency SOS alert?"
    );

    if (!confirm) return;

    try {

      await api.post(`/sos/${rideId}`);

      alert("SOS alert sent");

    } catch (err) {

      console.error("SOS failed", err);
      alert("Failed to send SOS");

    }

  };

  return (
    <button
      onClick={triggerSOS}
      className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-lg font-semibold"
    >
      SOS
    </button>
  );
}
