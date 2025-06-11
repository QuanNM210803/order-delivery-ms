import { useParams } from "react-router";
import { useEffect, useState } from "react";
import { deliveryApi, trackingApi } from "src/share/api";
import { toast } from "react-toastify";
import { useLoadingStore } from "src/share/stores/loadingStore";
import OrderStatusPanel from "src/share/components/OrderStatusPanel";
import orderStatus from "src/share/constants/orderStatus";
import AssignmentAddIcon from "@mui/icons-material/AssignmentAdd";
import AdminOrderAssignModal from "../components/AdminOrderAssignModal";

export default function AdminOrderDetail() {
  const { orderCode } = useParams();
  const setLoading = useLoadingStore((state) => state.setLoading);
  const [orderDetail, setOrderDetail] = useState(null);
  const [isAssignModalShow, setIsAssignModalShow] = useState(false);
  const [assigningId, setAssigningId] = useState(null);

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

  const handleAssignClick = (id, status) => {
    if (status != "Tạo đơn") {
      return;
    }
    setAssigningId(id);
    setIsAssignModalShow(true);
  };

  const handleAssignModalClose = () => {
    setAssigningId(null);
    setIsAssignModalShow(false);
  };

  const handleAssignOK = async (driverId) => {
    setLoading(true);
    try {
      const res = await deliveryApi.updateOrderStatus({
        orderCode: assigningId,
        status: orderStatus.ASSIGNED,
        deliveryStaffId: driverId,
      });
      toast.success(res.data.message);
      setIsAssignModalShow(false);
    } catch (err) {
      toast.error(err?.response?.data?.message || "Có lỗi xảy ra");
    } finally {
      setLoading(false);
    }
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
              onClick={() =>
                handleAssignClick(
                  orderDetail.orderCode,
                  orderDetail.statusHistory[
                    orderDetail.statusHistory.length - 1
                  ].status,
                )
              }
              className="px-4 mt-4 flex items-center cursor-pointer bg-green-700 hover:brightness-95 duration-100 text-white rounded-sm py-2"
              style={{
                backgroundColor:
                  orderDetail.statusHistory[
                    orderDetail.statusHistory.length - 1
                  ].status !== "Tạo đơn"
                    ? "gray"
                    : "",
                cursor:
                  orderDetail.statusHistory[
                    orderDetail.statusHistory.length - 1
                  ].status !== "Tạo đơn"
                    ? "default"
                    : "",
              }}
            >
              <AssignmentAddIcon fontSize="small" />
              <span>Giao đơn</span>
            </button>
          </>
        )}
      </div>
      {isAssignModalShow && (
        <AdminOrderAssignModal
          onCancel={handleAssignModalClose}
          onOK={() => {}}
          cancellingId={assigningId}
        />
      )}
    </div>
  );
}
