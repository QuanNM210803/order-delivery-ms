import { useEffect, useRef, useState } from "react";
import roles from "../constants/roles";

export default function RolesDropdownFilter({
  selectedRoles = [],
  onChange,
}) {
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const dropdownRef = useRef(null);

  const toggleDropdown = () => setDropdownOpen((prev) => !prev);

  const handleStatusToggle = (role) => {
    const updated = selectedRoles.includes(role)
      ? selectedRoles.filter((s) => s !== role)
      : [...selectedRoles, role];
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
      <label className="text-sm">Vai trò</label>
      <div
        onClick={toggleDropdown}
        className="bg-gray-100 border border-gray-300 px-2 py-1 min-w-36 w-fit flex flex-row rounded-md cursor-pointer hover:border-red-500/40 focus-within:border-red-500/40 duration-200 gap-1 items-center"
      >
        {selectedRoles.length === 0 ? (
          <span className="text-[#797A7B] text-base font-[350]">Chọn vai trò</span>
        ) : (
          <>
            {selectedRoles.slice(0, 3).map((status) => (
              <span
                key={status}
                className="bg-red-100 text-red-700 text-sm font-medium px-2 py-0.5 rounded"
              >
                {status}
              </span>
            ))}
            {selectedRoles.length > 3 && (
              <span className="bg-gray-200 text-gray-700 text-sm font-medium px-2 py-0.5 rounded">
                +{selectedRoles.length - 3}
              </span>
            )}
          </>
        )}
      </div>

      {dropdownOpen && (
        <div className="absolute top-14 z-10 mt-1 bg-white border border-gray-300 rounded-md shadow-md p-2 w-40">
          {Object.values(roles).map((status) => (
            <label
              key={status}
              className="flex items-center gap-2 text-sm py-1 cursor-pointer"
            >
              <input
                type="checkbox"
                checked={selectedRoles.includes(status)}
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
