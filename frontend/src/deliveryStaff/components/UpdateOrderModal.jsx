import { useState } from "react";
import { toast } from "react-toastify";
import { deliveryApi } from "src/share/api";
import orderStatus from "src/share/constants/orderStatus";
import { useLoadingStore } from "src/share/stores/loadingStore";

const cancelReasons = {
  UNREASONABLE_WEIGHT_REPORTED_BY_CUSTOMER:
    "Trọng lượng đơn hàng do khách hàng khai báo không hợp lý",
  RECIPIENT_REJECTED_PACKAGE: "Người nhận từ chối nhận hàng",
};

export default function UpdateOrderModal({ onCancel, onOK, orderCode }) {
  const [status, setStatus] = useState("PICKED_UP");
  const [cancelReason, setCancelReason] = useState("");
  const [note, setNote] = useState("");
  const [error, setError] = useState("");
  const setLoading = useLoadingStore((state) => state.setLoading);

  const isCancelled = status === orderStatus.CANCELLED;

  const handleConfirm = () => {
    // Validate required cancel reason
    if (isCancelled && !cancelReason) {
      setError("Vui lòng chọn lý do hủy.");
      return;
    }

    setError("");

    const payload = {
      orderCode,
      status,
      ...(isCancelled && {
        reasonCancel: cancelReason,
        noteCancel: note,
      }),
    };

    setLoading(true);
    deliveryApi
      .updateOrderStatus(payload)
      .then((res) => {
        onOK();
        toast.success(res.data.message);
      })
      .catch((err) => {
        toast.error(err?.response?.data?.message || "Có lỗi xảy ra");
      })
      .finally(() => {
        setLoading(false);
      });

    // Uncomment when ready to proceed:
    // onOK();
  };

  return (
    <div
      onClick={onCancel}
      className="bg-black/40 z-[99] fixed flex justify-center items-center w-full h-full top-0 left-0"
    >
      <div
        onClick={(e) => e.stopPropagation()}
        className="bg-white w-[500px] min-h-[220px] flex flex-col rounded-sm overflow-clip"
      >
        <div className="border-t-4 text-lg font-medium border-green-700 text-green-700 bg-gray-100 px-4 py-2">
          Cập nhật trạng thái đơn hàng
        </div>
        <div className="px-4 flex-1 py-3 space-y-4">
          <div>
            <label className="pr-4 font-medium">Trạng thái</label>
            <select
              className="border rounded px-2 py-1 w-full mt-1"
              value={status}
              onChange={(e) => {
                setStatus(e.target.value);
                // Reset cancelReason & error if status changes
                setCancelReason("");
                setError("");
              }}
            >
              {Object.values(orderStatus)
                .slice(Object.values(orderStatus).indexOf("PICKED_UP"))
                .map((s) => (
                  <option key={s} value={s}>
                    {s}
                  </option>
                ))}
            </select>
          </div>

          {isCancelled && (
            <>
              <div>
                <label className="pr-4 font-medium">Lý do hủy</label>
                <select
                  className="border rounded px-2 py-1 w-full mt-1"
                  value={cancelReason}
                  onChange={(e) => {
                    setCancelReason(e.target.value);
                    setError("");
                  }}
                >
                  <option value="" disabled>
                    -- Chọn lý do --
                  </option>
                  {Object.entries(cancelReasons).map(([key, label]) => (
                    <option key={key} value={key}>
                      {label}
                    </option>
                  ))}
                </select>
                {error && (
                  <div className="text-red-500 text-sm mt-1">{error}</div>
                )}
              </div>

              <div>
                <label className="pr-4 font-medium">Ghi chú</label>
                <textarea
                  rows="3"
                  className="border rounded px-2 py-1 w-full mt-1"
                  placeholder="Nhập ghi chú (nếu có)..."
                  value={note}
                  onChange={(e) => setNote(e.target.value)}
                />
              </div>
            </>
          )}
        </div>
        <div className="px-4 py-2 flex justify-end gap-2 bg-gray-100">
          <button
            onClick={handleConfirm}
            className="bg-green-700 text-white px-4 py-2 rounded-sm cursor-pointer hover:brightness-95 duration-100"
          >
            Xác nhận
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
