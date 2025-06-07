import { useEffect, useRef, useState } from "react";
import orderStatus from "src/share/constants/orderStatus";

export default function StatusDropdownFilter({
  selectedStatuses = [],
  onChange,
}) {
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const dropdownRef = useRef(null);

  const toggleDropdown = () => setDropdownOpen((prev) => !prev);

  const handleStatusToggle = (status) => {
    const updated = selectedStatuses.includes(status)
      ? selectedStatuses.filter((s) => s !== status)
      : [...selectedStatuses, status];
    onChange(updated);
  };

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setDropdownOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <div className="flex flex-col gap-1 relative" ref={dropdownRef}>
      <label className="text-sm">Trạng thái</label>
      <div
        onClick={toggleDropdown}
        className="bg-gray-100 border border-gray-300 px-2 py-1 min-w-36 w-fit flex flex-row rounded-md cursor-pointer hover:border-red-500/40 focus-within:border-red-500/40 duration-200 gap-1 items-center"
      >
        {selectedStatuses.length === 0 ? (
          <span className="text-[#797A7B] text-base font-[350]">Chọn trạng thái</span>
        ) : (
          <>
            {selectedStatuses.slice(0, 3).map((status) => (
              <span
                key={status}
                className="bg-red-100 text-red-700 text-sm font-medium px-2 py-0.5 rounded"
              >
                {status}
              </span>
            ))}
            {selectedStatuses.length > 3 && (
              <span className="bg-gray-200 text-gray-700 text-sm font-medium px-2 py-0.5 rounded">
                +{selectedStatuses.length - 3}
              </span>
            )}
          </>
        )}
      </div>

      {dropdownOpen && (
        <div className="absolute top-14 z-10 mt-1 bg-white border border-gray-300 rounded-md shadow-md p-2 w-40">
          {Object.values(orderStatus).map((status) => (
            <label
              key={status}
              className="flex items-center gap-2 text-sm py-1 cursor-pointer"
            >
              <input
                type="checkbox"
                checked={selectedStatuses.includes(status)}
                onChange={() => handleStatusToggle(status)}
                className="accent-red-500"
              />
              {status}
            </label>
          ))}
        </div>
      )}
    </div>
  );
}
