import { useEffect, useState } from "react";
import Pagination from "src/share/components/Pagination";
import { MoonLoader } from "react-spinners";
import { deliveryApi, orderApi } from "src/share/api";
import { toast } from "react-toastify";
import AssignmentIcon from "@mui/icons-material/Assignment";
import UpdateOrderModal from "../components/UpdateOrderModal";
import SecurityUpdateGoodIcon from "@mui/icons-material/SecurityUpdateGood";

export default function DeliveryStaffOngoingOrders() {
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [isLoadingData, setIsLoadingData] = useState(false);
  const [totalRecords, setTotalRecords] = useState(0);
  const [tableData, setTableData] = useState([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingCode, setEditingCode] = useState(null);

  const handlePageSizeChange = (size) => {
    setPageSize(size);
    setPage(1);
  };

  const handlePageChange = (page) => {
    setPage(page);
  };

  const handleRowClick = (id) => {
    window.open(`/delivery/order/detail/${id}`, "_blank");
  };

  useEffect(() => {
    const url = new URL(window.location.href);
    const params = new URLSearchParams(url.search);
    const result = {};

    for (const [key, value] of params.entries()) {
      result[key] = value;
    }

    setPage(result.pageIndex);
    setPageSize(result.pageSize);
    fetchData(url.search);
  }, []);

  const fetchData = async (queryString) => {
    try {
      setIsLoadingData(true);
      const response = await orderApi.getDeliveryStaffOngoingOrder(queryString);
      setPage(response.data.data.pageInfo.pageIndex);
      setPageSize(response.data.data.pageInfo.pageSize);
      setTotalRecords(response.data.data.pageInfo.totalElements);
      setTableData(response.data.data.data);
    } catch (err) {
      toast.error(err?.response?.data?.message || "Có lỗi xảy ra");
    } finally {
      setIsLoadingData(false);
    }
  };

  const handleFilter = () => {
    const url = new URL(window.location.href);
    const queryString = convertToQueryString();
    url.search = queryString;
    window.history.replaceState(null, "", url.toString());
    fetchData(queryString);
  };

  const convertToQueryString = () => {
    const params = {
      pageIndex: page,
      pageSize: pageSize,
    };

    const filteredParams = Object.fromEntries(
      Object.entries(params).filter(([_, v]) => v != null),
    );

    const urlParams = new URLSearchParams(filteredParams);

    return urlParams.toString();
  };

  const handleEditClick = (code) => {
    setEditingCode(code);
    setIsModalOpen(true);
  };

  const handleOK = (res) => {
    setEditingCode(null);
    setIsModalOpen(false);
    setTableData((state) => {
      const newState = state.map((data) => {
        if (data.orderCode == res.data.data.orderCode) {
          return {
            ...data,
            orderStatus: res.data.data.status,
          };
        } else {
          return data;
        }
      });
      return newState;
    });
  };

  const handleCloseModal = () => {
    setEditingCode(null);
    setIsModalOpen(false);
  };

  useEffect(() => {
    handleFilter();
  }, [page, pageSize]);

  return (
    <div className="p-8">
      <div className="mb-4 text-lg font-medium text-left">
        Đơn hàng đang thực hiện
      </div>
      <div className="max-w-fit flex flex-col relative">
        {isLoadingData && (
          <div className="absolute z-[80] flex justify-center items-center h-full w-full top-0 left-0">
            <MoonLoader color="#EE0033" />
          </div>
        )}
        <div className="overflow-x-auto max-w-full mb-2 w-fit shadow-md rounded-sm mt-4">
          <table className="text-sm min-w-fit w-fit">
            <thead className="bg-gray-900 text-white">
              <tr>
                <th className="px-3 py-4 text-left min-w-[180px] max-w-[180px]">
                  Mã đơn
                </th>
                <th className="px-3 py-4 text-left min-w-[150px] max-w-[150px]">
                  Tên người gửi
                </th>
                <th className="px-3 py-4 text-left min-w-[180px] max-w-[180px]">
                  Địa điểm lấy hàng
                </th>
                <th className="px-3 py-4 text-left min-w-[180px] max-w-[180px]">
                  Địa điểm nhận hàng
                </th>
                <th className="px-3 py-4 text-left min-w-[260px] max-w-[260px]">
                  Mô tả
                </th>
                <th className="px-3 py-4 text-left min-w-[180px] max-w-[180px]">
                  Trạng thái
                </th>
                <th className="px-3 py-4 text-left max-w-[250px] min-w-[250px]">
                  Hành động
                </th>
              </tr>
            </thead>
            <tbody>
              {tableData.length == 0 && (
                <div className="text-gray-500 italic py-4 text-center">
                  Không có dữ liệu...
                </div>
              )}
              {tableData.map((order, index) => (
                <tr
                  key={order.orderCode}
                  style={{
                    backgroundColor: index % 2 == 1 ? "#F1F5F9" : "white",
                  }}
                  className="hover:!bg-red-100/50 cursor-pointer duration-100"
                >
                  <td
                    className="px-2 py-3 truncate overflow-hidden whitespace-nowrap min-w-[180px] max-w-[180px]"
                    title={order.orderCode}
                  >
                    {order.orderCode}
                  </td>
                  <td
                    className="px-2 py-3 truncate overflow-hidden whitespace-nowrap min-w-[150px] max-w-[150px]"
                    title={order.senderName}
                  >
                    {order.senderName}
                  </td>
                  <td
                    className="px-2 py-3 truncate overflow-hidden whitespace-nowrap min-w-[180px] max-w-[180px]"
                    title={order.pickupAddress}
                  >
                    {order.pickupAddress}
                  </td>
                  <td
                    className="px-2 py-3 truncate overflow-hidden whitespace-nowrap min-w-[180px] max-w-[180px]"
                    title={order.deliveryAddress}
                  >
                    {order.deliveryAddress}
                  </td>
                  <td
                    className="px-2 py-3 truncate overflow-hidden whitespace-nowrap min-w-[260px] max-w-[260px]"
                    title={order.description}
                  >
                    {order.description}
                  </td>
                  <td
                    className="px-2 py-3 truncate overflow-hidden whitespace-nowrap min-w-[180px] max-w-[180px]"
                    title={order.orderStatus}
                  >
                    {order.orderStatus}
                  </td>
                  <td className="px-2 py-3 max-w-[250px] min-w-[250px]">
                    <div className="flex gap-2 items-center">
                      <button
                        onClick={() => handleEditClick(order.orderCode)}
                        className="px-4 flex items-center cursor-pointer bg-amber-500 hover:brightness-95 duration-100 text-white rounded-sm py-2"
                      >
                        <SecurityUpdateGoodIcon fontSize="small" />
                        <span>Cập nhật</span>
                      </button>
                      <button
                        onClick={(e) => handleRowClick(order.orderCode)}
                        className="px-4 flex items-center cursor-pointer bg-blue-800 hover:brightness-95 duration-100 text-white rounded-sm py-2"
                      >
                        <AssignmentIcon fontSize="small" />
                        <span>Chi tiết</span>
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <div className="self-end">
          <Pagination
            page={page}
            pageSize={pageSize}
            total={totalRecords}
            handlePageChange={(page) => handlePageChange(page)}
            handlePageSizeChange={(size) => handlePageSizeChange(size)}
          />
        </div>
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
