export default function ToggleSwitch({ checked, onChange, disabled }) {
  return (
    <label className={`inline-flex items-center cursor-pointer ${disabled ? "opacity-50 cursor-not-allowed" : ""}`}>
      <input
        type="checkbox"
        checked={checked}
        onChange={onChange}
        disabled={disabled}
        className="sr-only peer"
      />
      <div
        className={`
          relative w-11 h-6
          bg-gray-300
          rounded-full
          peer-focus:outline-none
          peer-checked:bg-green-600
          transition-all
        `}
      >
        <div
          className={`
            absolute top-[2px] left-[2px]
            bg-white w-5 h-5
            rounded-full
            transition-all
            ${checked ? "translate-x-full" : ""}
            flex items-center justify-center
          `}
        >
          {disabled && (
            <div className="w-3 h-3 border-2 border-white border-t-transparent rounded-full animate-spin" />
          )}
        </div>
      </div>
    </label>
  );
}
