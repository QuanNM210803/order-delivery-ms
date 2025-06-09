import { useEffect, useState } from "react";
import AdminOrderTableFilter from "./AdminOrderTableFilter";
import Pagination from "src/share/components/Pagination";
import { MoonLoader } from "react-spinners";
import AssignmentAddIcon from "@mui/icons-material/AssignmentAdd";
import AdminOrderAssignModal from "./AdminOrderAssignModal";
import { deliveryApi, orderApi } from "src/share/api";
import { toast } from "react-toastify";
import AssignmentIcon from "@mui/icons-material/Assignment";
import { useLoadingStore } from "src/share/stores/loadingStore";
import orderStatus from "src/share/constants/orderStatus";

export default function AdminOrderTable() {
  const [filter, setFilter] = useState({
    code: "",
    sender_name: "",
    description: "",
    statuses: [],
    startDate: "",
    endDate: "",
  });
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [isAssignModalShow, setIsAssignModalShow] = useState(false);
  const [assigningId, setAssigningId] = useState(null);
  const [isLoadingData, setIsLoadingData] = useState(false);
  const [totalRecords, setTotalRecords] = useState(0);
  const [tableData, setTableData] = useState([]);
  const setLoading = useLoadingStore((state) => state.setLoading);

  const handleFilterChange = (filter) => {
    setFilter(filter);
  };

  const handlePageSizeChange = (size) => {
    setPageSize(size);
    setPage(1);
  };

  const handlePageChange = (page) => {
    setPage(page);
  };

  const handleRowClick = (id) => {
    window.open(`/admin/order/detail/${id}`, "_blank");
  };

  const handleAssignClick = (id, status) => {
    if (status != "Tạo đơn") {
      return;
    }
    setAssigningId(id);
    setIsAssignModalShow(true);
  };

  useEffect(() => {
    const url = new URL(window.location.href);
    const params = new URLSearchParams(url.search);
    const result = {};

    for (const [key, value] of params.entries()) {
      result[key] = value;
    }

    const newFilter = {
      code: result.orderCode ?? "",
      sender_name: result.senderName ?? "",
      description: result.description ?? "",
      statuses: result?.orderStatuses?.split(",") ?? [],
      startDate: result.startDate ?? "",
      endDate: result.endDate ?? "",
    };

    setFilter({
      code: newFilter.code || "",
      sender_name: newFilter.sender_name || "",
      description: newFilter.description || "",
      statuses: newFilter.statuses || [],
      startDate: newFilter.startDate || "",
      endDate: newFilter.endDate || "",
    });
    setPage(result.pageIndex);
    setPageSize(result.pageSize);
    fetchData(url.search);
  }, []);

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
      const queryString = convertToQueryString();
      fetchData(queryString);
    } catch (err) {
      toast.error(err?.response?.data?.message || "Có lỗi xảy ra");
    } finally {
      setLoading(false);
    }
    console.log(driverId);
  };

  const convertToQueryString = () => {
    const params = {
      pageIndex: page,
      pageSize: pageSize,
      orderCode: filter.code ? filter.code : null,
      senderName: filter.sender_name ? filter.sender_name : null,
      description: filter.description ? filter.description : null,
      orderStatuses:
        !filter?.statuses ||
        filter.statuses.length === 0 ||
        filter.statuses.length === 7
          ? null
          : filter.statuses.join(","),
      startDate: filter.startDate ? filter.startDate : null,
      endDate: filter.endDate ? filter.endDate : null,
    };

    const filteredParams = Object.fromEntries(
      Object.entries(params).filter(([_, v]) => v != null),
    );

    const urlParams = new URLSearchParams(filteredParams);

    return urlParams.toString();
  };

  const fetchData = async (queryString) => {
    try {
      setIsLoadingData(true);
      const response = await orderApi.getAdminOrder(queryString);
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

  useEffect(() => {
    handleFilter();
  }, [page, pageSize]);

  const handleFilter = async () => {
    const url = new URL(window.location.href);
    const queryString = convertToQueryString();
    url.search = queryString;
    window.history.replaceState(null, "", url.toString());
    fetchData(queryString);
  };

  return (
    <div className="max-w-fit flex flex-col relative">
      {isLoadingData && (
        <div className="absolute z-[80] flex justify-center items-center h-full w-full top-0 left-0">
          <MoonLoader color="#EE0033" />
        </div>
      )}
      <AdminOrderTableFilter
        filters={filter}
        onFilterChange={handleFilterChange}
        onFilter={handleFilter}
      />
      <div className="overflow-x-auto max-w-full mb-2 w-fit shadow-md rounded-sm mt-4">
        <table className="text-sm min-w-fit w-fit">
          <thead className="bg-gray-900 text-white">
            <tr>
              <th className="px-3 py-4 text-left min-w-[200px] max-w-[200px]">
                Mã đơn
              </th>
              <th className="px-3 py-4 text-left min-w-[150px] max-w-[150px]">
                Người gửi
              </th>
              <th className="px-3 py-4 text-left min-w-[150px] max-w-[150px]">
                Địa chỉ lấy
              </th>
              <th className="px-3 py-4 text-left min-w-[150px] max-w-[150px]">
                Địa chỉ nhận
              </th>
              <th className="px-3 py-4 text-left min-w-[380px] max-w-[380px]">
                Mô tả
              </th>
              <th className="px-3 py-4 text-left min-w-[120px] max-w-[120px]">
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
                  className="px-2 py-3 truncate overflow-hidden whitespace-nowrap min-w-[200px] max-w-[200px]"
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
                  className="px-2 py-3 truncate overflow-hidden whitespace-nowrap min-w-[150px] max-w-[150px]"
                  title={order.pickupAddress}
                >
                  {order.pickupAddress}
                </td>
                <td
                  className="px-2 py-3 truncate overflow-hidden whitespace-nowrap min-w-[150px] max-w-[150px]"
                  title={order.deliveryAddress}
                >
                  {order.deliveryAddress}
                </td>
                <td
                  className="px-2 py-3 truncate overflow-hidden whitespace-nowrap min-w-[380px] max-w-[380px]"
                  title={order.description}
                >
                  {order.description}
                </td>
                <td
                  className="px-2 py-3 truncate overflow-hidden whitespace-nowrap min-w-[120px] max-w-[120px]"
                  title={order.orderStatus}
                >
                  {order.orderStatus}
                </td>
                <td className="px-2 py-3 max-w-[250px] min-w-[250px]">
                  <div className="flex gap-2 items-center">
                    <button
                      onClick={() =>
                        handleAssignClick(order.orderCode, order.orderStatus)
                      }
                      className="px-4 flex items-center cursor-pointer bg-green-700 hover:brightness-95 duration-100 text-white rounded-sm py-2"
                      style={{
                        backgroundColor:
                          order.orderStatus !== "Tạo đơn" ? "gray" : "",
                        cursor:
                          order.orderStatus !== "Tạo đơn" ? "default" : "",
                      }}
                    >
                      <AssignmentAddIcon fontSize="small" />
                      <span>Giao đơn</span>
                    </button>
                    <button
                      onClick={() => handleRowClick(order.orderCode)}
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
      {isAssignModalShow && (
        <AdminOrderAssignModal
          onCancel={handleAssignModalClose}
          onOK={handleAssignOK}
          cancellingId={assigningId}
        />
      )}
    </div>
  );
}
