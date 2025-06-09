import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { authApi } from "src/share/api";
import { useLoadingStore } from "src/share/stores/loadingStore";
import AdminUserTableFilter from "../components/AdminUserTableFilter";
import Pagination from "src/share/components/Pagination";
import { MoonLoader } from "react-spinners";
import AdminAddUserModal from "../components/AdminAddUserModal";
import AddIcon from "@mui/icons-material/Add";

export default function AdminUserList() {
  const [filter, setFilter] = useState({
    username: "",
    fullName: "",
    phone: "",
    roleNames: [],
  });
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [isLoadingData, setIsLoadingData] = useState(false);
  const [totalRecords, setTotalRecords] = useState(0);
  const [tableData, setTableData] = useState([]);
  const [isAddModalShow, setIsAddModalShow] = useState(false);
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

  // useEffect(() => {
  //   orderApi.getCustomerOrder(filter, page, pageSize);
  // }, [filter]);

  useEffect(() => {
    const url = new URL(window.location.href);
    const params = new URLSearchParams(url.search);
    const result = {};

    for (const [key, value] of params.entries()) {
      result[key] = value;
    }

    const newFilter = {
      username: result?.username ?? "",
      fullName: result?.fullName ?? "",
      phone: result?.phone ?? "",
      roleNames: result?.roleNames?.split(",") ?? [],
    };

    setFilter({
      username: newFilter?.username || "",
      fullName: newFilter?.fullName || "",
      phone: newFilter?.phone || "",
      roleNames: newFilter?.roleNames || [],
    });
    setPage(result.pageIndex);
    setPageSize(result.pageSize);
    fetchData(url.search);
  }, []);

  const fetchData = async (queryString) => {
    try {
      setIsLoadingData(true);
      const response = await authApi.filterUser(queryString);
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
      username: filter.username ? filter.username : null,
      fullName: filter.fullName ? filter.fullName : null,
      phone: filter.phone ? filter.phone : null,
      roleNames:
        !filter?.roleNames ||
        filter.roleNames.length === 0 ||
        filter.roleNames.length === 7
          ? null
          : filter.roleNames.join(","),
    };

    const filteredParams = Object.fromEntries(
      Object.entries(params).filter(([_, v]) => v != null),
    );

    const urlParams = new URLSearchParams(filteredParams);

    return urlParams.toString();
  };

  const handleOnOk = () => {
    setIsAddModalShow(false);
    handleFilter();
  }

  useEffect(() => {
    handleFilter();
  }, [page, pageSize]);

  return (
    <div className="p-8">
      <div className="max-w-fit flex flex-col relative">
        {isLoadingData && (
          <div className="absolute z-[80] flex justify-center items-center h-full w-full top-0 left-0">
            <MoonLoader color="#EE0033" />
          </div>
        )}
        <button
          onClick={() => setIsAddModalShow(true)}
          className="px-4 mt-4 w-fit flex items-center cursor-pointer bg-blue-900 hover:brightness-95 mb-4 duration-100 text-white rounded-sm py-2"
        >
          <AddIcon fontSize="small" />
          <span>Thêm mới</span>
        </button>
        <AdminUserTableFilter
          filters={filter}
          onFilterChange={handleFilterChange}
          onFilter={handleFilter}
        />
        <div className="overflow-x-auto max-w-full mb-2 w-fit shadow-md rounded-sm mt-4">
          <table className="text-sm min-w-fit w-fit">
            <thead className="bg-gray-900 text-white">
              <tr>
                <th className="px-3 py-4 text-left min-w-[80px] max-w-[80px]">
                  STT
                </th>
                <th className="px-3 py-4 text-left min-w-[150px] max-w-[150px]">
                  Username
                </th>
                <th className="px-3 py-4 text-left min-w-[250px] max-w-[250px]">
                  Họ tên
                </th>
                <th className="px-3 py-4 text-left min-w-[250px] max-w-[250px]">
                  Email
                </th>
                <th className="px-3 py-4 text-left min-w-[150px] max-w-[150px]">
                  SĐT
                </th>
                <th className="px-3 py-4 text-left min-w-[250px] max-w-[250px]">
                  Vai trò
                </th>
              </tr>
            </thead>
            <tbody>
              {tableData.length == 0 && (
                <div className="text-gray-500 italic py-4 text-center">
                  Không có dữ liệu...
                </div>
              )}
              {tableData.map((user, index) => (
                <tr
                  key={user.orderCode}
                  style={{
                    backgroundColor: index % 2 == 1 ? "#F1F5F9" : "white",
                  }}
                  className="hover:!bg-red-100/50 cursor-pointer duration-100"
                >
                  <td className="px-2 py-3 truncate overflow-hidden whitespace-nowrap min-w-[80px] max-w-[80px]">
                    {(page - 1) * pageSize + index + 1}
                  </td>
                  <td
                    className="px-2 py-3 truncate overflow-hidden whitespace-nowrap min-w-[150px] max-w-[150px]"
                    title={user.username}
                  >
                    {user.username}
                  </td>
                  <td
                    className="px-2 py-3 truncate overflow-hidden whitespace-nowrap min-w-[250px] max-w-[250px]"
                    title={user.fullName}
                  >
                    {user.fullName}
                  </td>
                  <td
                    className="px-2 py-3 truncate overflow-hidden whitespace-nowrap min-w-[250px] max-w-[250px]"
                    title={user.email}
                  >
                    {user.email}
                  </td>
                  <td
                    className="px-2 py-3 truncate overflow-hidden whitespace-nowrap min-w-[150px] max-w-[150px]"
                    title={user.phone}
                  >
                    {user.phone}
                  </td>
                  <td
                    className="px-2 py-3 truncate overflow-hidden whitespace-nowrap min-w-[250px] max-w-[250px]"
                    title={user.roles}
                  >
                    {user.roles}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {isAddModalShow && (
          <AdminAddUserModal onOK={handleOnOk} onCancel={() => setIsAddModalShow(false)} />
        )}
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
    </div>
  );
}
