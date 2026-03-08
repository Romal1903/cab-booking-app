export default function Button({ children, onClick, type = "button" }) {
  return (
    <button
      type={type}
      onClick={onClick}
      className="flex justify-center items-center px-6 py-2.5 bg-sky-500 text-white rounded-xl shadow-md hover:bg-sky-600 hover:scale-[1.02] active:scale-[0.98] hover:shadow-xl transition-all duration-200"
    >
      {children}
    </button>
  );
}
