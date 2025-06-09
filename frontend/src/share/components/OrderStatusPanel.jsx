import OrderStatus from "./OrderStatus";
import ArrowDownwardIcon from "@mui/icons-material/ArrowDownward";

export default function OrderStatusPanel({ orderStatus }) {
  return (
    <div className="w-full border border-gray-400 p-4 rounded-md">
      <div className="flex flex-row justify-between gap-2">
        <div className="flex-1">
          <div className="mb-4">
            <span className="font-bold">Mã đơn hàng:</span>{" "}
            {orderStatus.orderCode}
          </div>
          <div>
            <span className="font-bold">Người gửi:</span>{" "}
            {orderStatus.senderName} - {orderStatus.senderPhone}
          </div>
          <div>
            <span className="font-bold">Địa chỉ lấy hàng:</span>{" "}
            {orderStatus.pickupAddress}
          </div>
          <div className="mt-4">
            <span className="font-bold">Người nhận:</span>{" "}
            {orderStatus.receiverName} - {orderStatus.receiverPhone}
          </div>
          <div>
            <span className="font-bold">Địa chỉ nhận hàng:</span>{" "}
            {orderStatus.deliveryAddress}
          </div>
          <div className="mt-4">
            <span className="font-bold">Giá kiện hàng:</span>{" "}
            {orderStatus.price}
          </div>
          <div>
            <span className="font-bold">Phí vận chuyển:</span>{" "}
            {orderStatus.shippingFee}
          </div>
          <div>
            <span className="font-bold">Mô tả:</span>{" "}
            <div className="max-h-[100px] p-2 max-w-[320px] break-all border border-gray-400 rounded-md overflow-auto">
              {orderStatus.description}
            </div>
          </div>
          <div className="mt-2">
            <span className="font-bold">Ghi chú:</span>{" "}
            <div className="max-h-[100px] p-2 max-w-[320px] break-all border border-gray-400 rounded-md overflow-auto">
              {orderStatus.note}
            </div>
          </div>
          <div className="mt-4">
            <span className="font-bold">Khối lượng:</span> {orderStatus.weight}
          </div>
          <div>
            <span className="font-bold">Kích thước:</span> {orderStatus.size}
          </div>
          <div>
            <span className="font-bold">Khoảng cách (ước lượng):</span>{" "}
            {orderStatus.distance}
          </div>
        </div>
        <div className="flex-1 flex h-fit flex-col gap-2 items-center">
          <div className="mb-4 w-full text-lg font-medium text-center">
            Trạng thái đơn hàng
          </div>
          {orderStatus.statusHistory.map((status, index) => (
            <>
              <OrderStatus statusInfo={status} key={index} />
              {index != orderStatus.statusHistory.length - 1 && (
                <ArrowDownwardIcon />
              )}
            </>
          ))}
        </div>
      </div>
    </div>
  );
}
