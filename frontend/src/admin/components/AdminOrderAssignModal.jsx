import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { deliveryApi } from "src/share/api";
import { useLoadingStore } from "src/share/stores/loadingStore";

export default function AdminOrderAssignModal({ onCancel, onOK }) {
  const [selectedDriver, setSelectedDriver] = useState(null);
  const [drivers, setDrivers] = useState([]);
  const setLoading = useLoadingStore((state) => state.setLoading);

  useEffect(() => {
    setLoading(true);
    deliveryApi
      .getDrivers()
      .then((res) => {
        console.log(res);
        setDrivers(res.data.data);
      })
      .catch((err) => {
        toast.error(err?.resposne?.data?.message || "Có lỗi xảy ra");
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  const handleAssign = () => {
    if (!selectedDriver) {
      toast.error("Vui lòng chọn tài xế");
      return;
    }
    onOK(selectedDriver);
  };

  return (
    <div
      onClick={onCancel}
      className="bg-black/40 z-[99] fixed flex justify-center items-center w-full h-full top-0 left-0"
    >
      <div
        onClick={(e) => e.stopPropagation()}
        className="bg-white w-[450px] h-[220px] flex flex-col rounded-sm overflow-clip"
      >
        <div className="border-t-4 text-lg font-medium border-green-700 text-green-700 bg-gray-100 px-4 py-2">
          Giao đơn hàng cho tài xế
        </div>
        <div className="px-4 flex-1 py-4 flex flex-col gap-2">
          <label className="text-sm text-gray-800 font-medium">
            Chọn tài xế:
          </label>
          <select
            value={selectedDriver}
            onChange={(e) => setSelectedDriver(e.target.value)}
            className="border rounded-sm px-2 py-1 text-sm"
          >
            <option value="">Chọn tài xế</option>
            {drivers.map((driver) => (
              <option key={driver.userId} value={driver.userId}>
                {driver.fullName}
              </option>
            ))}
          </select>
        </div>
        <div className="px-4 py-2 flex flex-row justify-end gap-2 bg-gray-100">
          <button
            onClick={handleAssign}
            className="bg-green-700 text-white px-4 py-2 rounded-sm cursor-pointer hover:brightness-95 duration-100"
          >
            Giao đơn
          </button>
          <button
            onClick={onCancel}
            className="bg-white text-red-500 px-4 py-2 rounded-sm cursor-pointer border"
          >
            Đóng
          </button>
        </div>
      </div>
    </div>
  );
}
