export default function Input({ type = "text", placeholder, value, onChange }) {
  return (
    <input
      type={type}
      placeholder={placeholder}
      value={value}
      onChange={onChange}
      className="w-full px-4 py-2 border border-sky-200 rounded-xl bg-white focus:outline-none focus:ring-2 focus:ring-sky-400 focus:border-sky-400 hover:border-sky-400 transition-all duration-200"/>
  );
}
