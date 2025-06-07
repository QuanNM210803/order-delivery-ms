import { useMemo, useState } from "react";
import StatusDropdownFilter from "./StatusDropdownFilter";
import DeleteOutlineIcon from "@mui/icons-material/DeleteOutline";
import { useNavigate } from "react-router";

export default function OrderTable() {
  const [filter, setFilter] = useState({});

  const resetFilter = () => {
    setFilter({});
  };

  const handleRowClick = (id) => {
    window.open(`/order/${id}`, "_blank");
  };

  const tableData = [
    {
      id: 1,
      code: "D1",
      recipent: "Duong TranDuong TranDuong TranDuong TranDuong TranDuong Tran",
      recipentPhone: "123456123456123456123456123456123456",
      description:
        "blah blah this is a deliveryblah blah this is a deliveryblah blah this is a delivery",
      status: "IN_TRANSIT",
    },
    {
      id: 2,
      code: "D2",
      recipent: "Duong TranDuong TranDuong TranDuong TranDuong TranDuong Tran",
      recipentPhone: "123456123456123456123456123456123456",
      description:
        "blah blah this is a deliveryblah blah this is a deliveryblah blah this is a delivery",
      status: "IN_TRANSIT",
    },
    {
      id: 3,
      code: "D3",
      recipent: "Duong TranDuong TranDuong TranDuong TranDuong TranDuong Tran",
      recipentPhone: "123456123456123456123456123456123456",
      description:
        "blah blah this is a deliveryblah blah this is a deliveryblah blah this is a delivery",
      status: "IN_TRANSIT",
    },
  ];

  const handleStatusChange = (statuses) => {
    setFilter((prev) => ({ ...prev, statuses }));
  };
  return (
    <div>
      <div className="flex flex-row gap-2">
        <div className="flex flex-col gap-1">
          <label className="text-sm">Mã đơn</label>
          <input
            type="text"
            placeholder="Mã đơn"
            className="bg-gray-100 border border-gray-300 w-32 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
          />
        </div>
        <div className="flex flex-col gap-1">
          <label className="text-sm">Tên người nhận</label>
          <input
            type="text"
            placeholder="Tên người nhận"
            className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
          />
        </div>
        <div className="flex flex-col gap-1">
          <label className="text-sm">SĐT người nhận</label>
          <input
            type="text"
            placeholder="SĐT người nhận"
            className="bg-gray-100 border border-gray-300 w-44 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
          />
        </div>
        <div className="flex flex-col gap-1">
          <label className="text-sm">Mô tả</label>
          <input
            type="text"
            placeholder="Mô tả"
            className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
          />
        </div>
        <div className="self-start text-sm">
          <StatusDropdownFilter
            selectedStatuses={filter.statuses}
            onChange={handleStatusChange}
          />
        </div>
        <div
          onClick={resetFilter}
          className="h-fit self-end mb-[1px] w-18 text-center ml-2 text-base bg-red-700 rounded-sm cursor-pointer hover:brightness-95 text-white px-2 py-1"
        >
          Đặt lại
        </div>
      </div>
      <div className="overflow-x-auto shadow-md rounded-sm mt-4">
        <table className="text-sm min-w-[1145px] w-full">
          <thead className="bg-gray-900 text-white">
            <tr>
              <th className="px-3 py-4 text-left max-w-[60px]">Mã đơn</th>
              <th className="px-3 py-4 text-left max-w-[100px]">Người nhận</th>
              <th className="px-3 py-4 text-left max-w-[100px]">SĐT</th>
              <th className="px-3 py-4 text-left max-w-[300px]">Mô tả</th>
              <th className="px-3 py-4 text-left max-w-[160px]">Trạng thái</th>
              <th className="px-3 py-4 text-left max-w-[80px] min-w-[80px]">
                Hành động
              </th>
            </tr>
          </thead>
          <tbody>
            {tableData.map((order, index) => (
              <tr
                key={order.id}
                onClick={() => handleRowClick(order.id)}
                style={{
                  backgroundColor: index % 2 == 1 ? "#F1F5F9" : "white",
                }}
                className="hover:!bg-red-100/50 cursor-pointer duration-100"
              >
                <td
                  className="px-2 py-3 truncate overflow-hidden whitespace-nowrap max-w-[60px]"
                  title={order.code}
                >
                  {order.code}
                </td>
                <td
                  className="px-2 py-3 truncate overflow-hidden whitespace-nowrap max-w-[100px]"
                  title={order.recipent}
                >
                  {order.recipent}
                </td>
                <td
                  className="px-2 py-3 truncate overflow-hidden whitespace-nowrap max-w-[100px]"
                  title={order.recipentPhone}
                >
                  {order.recipentPhone}
                </td>
                <td
                  className="px-2 py-3 truncate overflow-hidden whitespace-nowrap max-w-[300px]"
                  title={order.description}
                >
                  {order.description}
                </td>
                <td
                  className="px-2 py-3 truncate overflow-hidden whitespace-nowrap max-w-[160px]"
                  title={order.status}
                >
                  {order.status}
                </td>
                <td className="px-2 py-3 max-w-[80px] min-w-[80px]">
                  <div className="flex gap-2 items-center">
                    <button
                      onClick={() => console.log(order.id)} // define this function
                      className="px-4 flex items-center cursor-pointer bg-red-700 hover:brightness-95 duration-100 text-white rounded-sm py-2 min-w-[64px]"
                    >
                      <DeleteOutlineIcon fontSize="small" />
                      <span>Hủy đơn</span>
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
