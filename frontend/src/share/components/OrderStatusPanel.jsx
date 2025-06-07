import OrderStatus from "./OrderStatus";

export default function OrderStatusPanel({ orderStatus }) {
  return (
    <div className="w-full">
      <div className="mb-8 text-lg font-medium text-center">
        Trạng thái đơn hàng
      </div>
      <div className="flex flex-row justify-between gap-2">
        <div className="flex-1">
          <div>
            <span className="font-bold">Người gửi:</span>{" "}
            {orderStatus.sender.name} - {orderStatus.sender.phone}
          </div>
          <div>
            <span className="font-bold">Địa chỉ lấy hàng:</span>{" "}
            {orderStatus.sender.pickupAddress}
          </div>

          <div className="mt-4">
            <span className="font-bold">Người nhận:</span>{" "}
            {orderStatus.recipent.name} - {orderStatus.recipent.phone}
          </div>
          <div>
            <span className="font-bold">Địa chỉ nhận hàng:</span>{" "}
            {orderStatus.recipent.deliveryAddress}
          </div>
        </div>
        <div className="flex-1 flex relative h-fit flex-col gap-8 items-center">
          {orderStatus.orderStatus.map((status, index) => (
            <OrderStatus statusInfo={status} key={index} />
          ))}
          <div className="absolute h-full w-[2px] bg-red-400 z-0"></div>
        </div>
      </div>
    </div>
  );
}
