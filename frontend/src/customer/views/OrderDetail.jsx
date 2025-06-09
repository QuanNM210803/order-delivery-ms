import { useParams } from "react-router";
import { useEffect, useState } from "react";
import { deliveryApi, trackingApi } from "src/share/api";
import { toast } from "react-toastify";
import { useLoadingStore } from "src/share/stores/loadingStore";
import OrderStatusPanel from "src/share/components/OrderStatusPanel";
import orderStatus from "src/share/constants/orderStatus";
import DeleteOutlineIcon from "@mui/icons-material/DeleteOutline";
import OrderCancelModal from "../components/OrderCancelModal";

export default function OrderDetail() {
  const { orderCode } = useParams();
  const setLoading = useLoadingStore((state) => state.setLoading);
  const [orderDetail, setOrderDetail] = useState(null);
  const [cancellingId, setCancellingId] = useState(null);
  const [isCancelModalShow, setIsCancelModalShow] = useState(false);

  const handleCancelClick = (id, status) => {
    console.log(id, status);
    if (status != "Tạo đơn") {
      return;
    }
    setCancellingId(id);
    setIsCancelModalShow(true);
  };

  const handleCancelModalClose = () => {
    setCancellingId(null);
    setIsCancelModalShow(false);
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

  const handleCancelOK = async () => {
    setLoading(true);
    try {
      const res = await deliveryApi.updateOrderStatus({
        orderCode: cancellingId,
        status: orderStatus.CANCELLED,
      });
      toast.success(res.data.message);
      setIsCancelModalShow(false);
      fetchData();
    } catch (err) {
      toast.success(err?.response?.data?.message ?? "Có lỗi xảy ra");
    } finally {
      setLoading(false);
    }
  };

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
                handleCancelClick(
                  orderDetail.orderCode,
                  orderDetail.statusHistory[
                    orderDetail.statusHistory.length - 1
                  ].status,
                )
              }
              className="px-4 mt-4 flex items-center cursor-pointer bg-red-700 hover:brightness-95 duration-100 text-white rounded-sm py-2"
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
              <DeleteOutlineIcon fontSize="small" />
              <span>Hủy đơn</span>
            </button>
          </>
        )}
      </div>
      {isCancelModalShow && (
        <OrderCancelModal
          onCancel={handleCancelModalClose}
          onOK={handleCancelOK}
          cancellingId={cancellingId}
        />
      )}
    </div>
  );
}
