import { useParams } from "react-router";
import { useEffect, useState } from "react";
import { deliveryApi, trackingApi } from "src/share/api";
import { toast } from "react-toastify";
import { useLoadingStore } from "src/share/stores/loadingStore";
import OrderStatusPanel from "src/share/components/OrderStatusPanel";
import UpdateOrderModal from "../components/UpdateOrderModal";
import SecurityUpdateGoodIcon from "@mui/icons-material/SecurityUpdateGood";

export default function DeliveryStaffOrderDetail() {
  const { orderCode } = useParams();
  const setLoading = useLoadingStore((state) => state.setLoading);
  const [orderDetail, setOrderDetail] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingCode, setEditingCode] = useState(null);

  const handleEditClick = (code) => {
    setEditingCode(code);
    setIsModalOpen(true);
  };

  const handleOK = () => {
    setEditingCode(null);
    setIsModalOpen(false);
    setTimeout(() => {
      fetchData();
    }, 1000);
  };

  const handleCloseModal = () => {
    setEditingCode(null);
    setIsModalOpen(false);
  };

  const fetchData = () => {
    trackingApi
      .getOrderDetail(orderCode)
      .then((res) => {
        setOrderDetail(res.data.data);
      })
      .catch((err) => {
        toast.error(err?.response?.data?.message || "Có lỗi xảy ra");
      })
      .finally(() => {
        setLoading(false);
      });
  };

  useEffect(() => {
    setLoading(true);
    fetchData();
  }, []);

  return (
    <div className="p-8">
      <div className="mb-8 text-lg font-medium text-left">
        Thông tin đơn hàng
      </div>
      <div className="w-[75%]">
        {orderDetail && (
          <>
            <OrderStatusPanel orderStatus={orderDetail} />
            <button
              onClick={() => handleEditClick(orderDetail.orderCode)}
              className="px-4 flex items-center cursor-pointer bg-amber-500 hover:brightness-95 duration-100 text-white rounded-sm py-2 mt-4"
            >
              <SecurityUpdateGoodIcon fontSize="small" />
              <span>Cập nhật</span>
            </button>
          </>
        )}
      </div>
      {isModalOpen && (
        <UpdateOrderModal
          onOK={handleOK}
          onCancel={handleCloseModal}
          orderCode={editingCode}
        />
      )}
    </div>
  );
}
