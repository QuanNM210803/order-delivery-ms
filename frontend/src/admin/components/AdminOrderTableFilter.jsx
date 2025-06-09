import StatusDropdownFilter from "src/share/components/StatusDropdownFilter";

export default function AdminOrderTableFilter({
  onFilterChange,
  filters,
  onFilter,
}) {
  const resetFilter = () => {
    onFilterChange({
      code: "",
      sender_name: "",
      description: "",
      statuses: [],
      startDate: "",
      endDate: "",
    });
  };
  const handleStatusChange = (statuses) => {
    onFilterChange({ ...filters, statuses });
  };

  const handleFieldChange = (fieldName, value) => {
    onFilterChange({ ...filters, [fieldName]: value });
  };

  return (
    <div className="flex flex-row gap-2 flex-wrap">
      <div className="flex flex-col gap-1">
        <label className="text-sm">Mã đơn</label>
        <input
          type="text"
          value={filters.code}
          onChange={(e) => handleFieldChange("code", e.target.value)}
          placeholder="Mã đơn"
          className="bg-gray-100 border border-gray-300 w-32 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
        />
      </div>
      <div className="flex flex-col gap-1">
        <label className="text-sm">Tên người gửi</label>
        <input
          type="text"
          value={filters.sender_name}
          onChange={(e) => handleFieldChange("sender_name", e.target.value)}
          placeholder="Tên người gửi"
          className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
        />
      </div>
      <div className="flex flex-col gap-1">
        <label className="text-sm">Mô tả</label>
        <input
          type="text"
          value={filters.description}
          onChange={(e) => handleFieldChange("description", e.target.value)}
          placeholder="Mô tả"
          className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
        />
      </div>
      <div className="self-start text-sm">
        <StatusDropdownFilter
          selectedStatuses={filters.statuses}
          onChange={handleStatusChange}
        />
      </div>
      <div className="flex flex-col gap-1">
        <label className="text-sm">Ngày bắt đầu</label>
        <input
          type="date"
          value={filters.startDate}
          onChange={(e) => handleFieldChange("startDate", e.target.value)}
          placeholder="Mô tả"
          className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
        />
      </div>
      <div className="flex flex-col gap-1">
        <label className="text-sm">Ngày kết thúc</label>
        <input
          type="date"
          value={filters.endDate}
          onChange={(e) => handleFieldChange("endDate", e.target.value)}
          placeholder="Mô tả"
          className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
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
