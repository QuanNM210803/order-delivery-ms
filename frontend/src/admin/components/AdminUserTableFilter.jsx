import RolesDropdownFilter from "src/share/components/RolesDropdownFilter";

export default function AdminUserTableFilter({
  onFilterChange,
  filters,
  onFilter,
}) {
  const resetFilter = () => {
    onFilterChange({
      username: "",
      fullName: "",
      phone: "",
      roleNames: [],
    });
  };
  const handleRolesChange = (roles) => {
    onFilterChange({ ...filters, roleNames: roles });
  };

  const handleFieldChange = (fieldName, value) => {
    onFilterChange({ ...filters, [fieldName]: value });
  };

  return (
    <div className="flex flex-row gap-2 flex-wrap">
      <div className="flex flex-col gap-1">
        <label className="text-sm">Username</label>
        <input
          type="text"
          value={filters.username}
          onChange={(e) => handleFieldChange("username", e.target.value)}
          placeholder="Username"
          className="bg-gray-100 border border-gray-300 w-32 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
        />
      </div>
      <div className="flex flex-col gap-1">
        <label className="text-sm">Họ tên</label>
        <input
          type="text"
          value={filters.fullName}
          onChange={(e) => handleFieldChange("fullName", e.target.value)}
          placeholder="Họ tên"
          className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
        />
      </div>
      <div className="flex flex-col gap-1">
        <label className="text-sm">SĐT</label>
        <input
          type="text"
          value={filters.phone}
          onChange={(e) => handleFieldChange("phone", e.target.value)}
          placeholder="SĐT"
          className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
        />
      </div>
      <div className="self-start text-sm">
        <RolesDropdownFilter
          selectedRoles={filters.roleNames}
          onChange={handleRolesChange}
        />
      </div>
      <div
        onClick={resetFilter}
        className="h-fit self-end mb-[1px] w-18 text-center text-base bg-red-700 rounded-sm cursor-pointer hover:brightness-95 text-white px-2 py-1"
      >
        Đặt lại
      </div>
      <div
        onClick={onFilter}
        className="h-fit self-end mb-[1px] w-18 text-center text-base bg-red-700 rounded-sm cursor-pointer hover:brightness-95 text-white px-2 py-1"
      >
        Lọc
      </div>
    </div>
  );
}
