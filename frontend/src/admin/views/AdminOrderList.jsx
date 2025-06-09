import AdminOrderTable from "../components/AdminOrderTable";

export default function AdminOrderList() {
  return (
    <div className="p-8 bg-white">
      <div className="mb-4 w-full text-lg font-medium text-left">
        Quản lí đơn hàng
      </div>
      <AdminOrderTable />
    </div>
  );
}
