import OrderTable from "../components/OrderTable";

export default function OrderList() {
  return (
    <div className="p-8 bg-white">
      <div className="mb-4 text-lg font-medium text-left">
        Đơn hàng của tôi
      </div>
      <OrderTable />
    </div>
  );
}
