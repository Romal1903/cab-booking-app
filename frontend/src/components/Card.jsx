export default function Card({ children }) {
  return (
    <div className="bg-white p-8 rounded-2xl shadow-md border border-sky-100 hover:shadow-xl hover:-translate-y-1 transition-all duration-300 w-full max-w-md">
      {children}
    </div>
  );
}
