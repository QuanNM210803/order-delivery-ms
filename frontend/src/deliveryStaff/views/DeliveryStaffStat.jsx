import Highcharts from "highcharts";
import HighchartsReact from "highcharts-react-official";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { orderApi } from "src/share/api";
import { useLoadingStore } from "src/share/stores/loadingStore";

export default function DeliveryStaffStat() {
  const [filter, setFilter] = useState({
    endDate: "",
    startDate: "",
  });
  const [statData, setStatData] = useState(null);

  const setLoading = useLoadingStore((state) => state.setLoading);

  function getFirstDayOfCurrentMonth() {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, "0"); // months are 0-indexed
    const day = "01"; // first day of the month
    return `${year}-${month}-${day}`;
  }

  useEffect(() => {
    const url = new URL(window.location.href);
    const params = new URLSearchParams(url.search);
    const result = {};

    for (const [key, value] of params.entries()) {
      result[key] = value;
    }

    setFilter({
      startDate: result.startDate ?? getFirstDayOfCurrentMonth(),
      endDate: result.endDate || "",
    });
  }, []);

  const convertToQueryString = () => {
    const params = {
      startDate: filter.startDate ? filter.startDate : null,
      endDate: filter.endDate ? filter.endDate : null,
    };

    const filteredParams = Object.fromEntries(
      Object.entries(params).filter(([_, v]) => v != null),
    );

    const urlParams = new URLSearchParams(filteredParams);

    return urlParams.toString();
  };

  const fetchData = (queryString) => {
    setLoading(true);
    orderApi
      .getOrderStat(queryString)
      .then((res) => {
        setStatData(res.data);
      })
      .catch((err) => {
        toast.error(err?.response?.data?.message || "Có lỗi xảy ra");
      })
      .finally(() => {
        setLoading(false);
      });
  };

  const handleFilter = () => {
    const url = new URL(window.location.href);
    const queryString = convertToQueryString();
    url.search = queryString;
    window.history.replaceState(null, "", url.toString());
    fetchData(queryString);
  };

  useEffect(() => {
    handleFilter();
  }, [filter]);

  const handleFieldChange = (fieldName, value) => {
    setFilter({ ...filter, [fieldName]: value });
  };

  // const statData = {
  //   data: {
  //     orderCompleted: 2,
  //     orderCancelled: 3,
  //     orderPending: 0,
  //     orderTotal: 5,
  //     shippingFeeTotal: 604195.2,
  //     commission: 362517.12,
  //     ranking: 1,
  //     rankBonus: 1000000.0,
  //     revenueBonus: 0.0,
  //     earnings: 1362517.12,
  //   },
  //   code: 1000,
  // };
  //
  const statusLabels = {
    orderCompleted: "Đã hoàn thành",
    orderCancelled: "Đã hủy",
    orderPending: "Đang xử lý",
    orderTotal: "Tổng đơn hàng",
  };

  const earningsLabels = {
    shippingFeeTotal: "Tổng phí giao hàng",
    commission: "Hoa hồng",
    ranking: "Hạng",
    rankBonus: "Thưởng xếp hạng",
    revenueBonus: "Thưởng doanh thu",
    earnings: "Tổng thu nhập",
  };

  const formatNumber = (num) =>
    typeof num === "number"
      ? num.toLocaleString("vi-VN", { style: "currency", currency: "VND" })
      : num;

  const orderChartData = [
    {
      name: "Đã hoàn thành",
      y: statData?.data.orderCompleted,
      color: "#28a745",
    },
    {
      name: "Đã hủy",
      y: statData?.data.orderCancelled,
      color: "#dc3545",
    },
    {
      name: "Đang xử lý",
      y: statData?.data.orderPending,
      color: "#ffc107",
    },
  ];

  const orderOptions = {
    chart: {
      type: "pie",
    },
    title: {
      text: "Phân bố trạng thái đơn hàng",
    },
    credits: {
      enabled: false,
    },
    series: [
      {
        name: "Số lượng",
        colorByPoint: true,
        data: orderChartData,
      },
    ],
    tooltip: {
      pointFormat: "{series.name}: <b>{point.y}</b>",
    },
  };

  const revenueChartData = [
    { name: "Thưởng doanh thu", key: "revenueBonus", color: "#28B463" },
    { name: "Thưởng xếp hạng", key: "rankBonus", color: "#FFC300" },
    { name: "Hoa hồng", key: "commission", color: "#FF5733" },
  ];

  const totalValue = revenueChartData.reduce(
    (sum, item) => sum + statData?.data[item.key],
    0,
  );

  const seriesData = revenueChartData.map((item) => ({
    name: item.name,
    data: [statData?.data[item.key]],
    color: item.color,
  }));

  const revenueOptions = {
    credits: {
      enabled: false,
    },
    chart: {
      type: "bar",
      width: 800,
    },
    title: {
      text: "Chi tiết doanh thu (Hoa hồng & Thưởng)",
    },
    xAxis: {
      categories: ["Tổng doanh thu"],
    },
    yAxis: {
      min: 0,
      title: {
        text: "Giá trị (VND)",
      },
      labels: {
        formatter() {
          return this.value.toLocaleString("vi-VN") + " VND";
        },
      },
    },
    tooltip: {
      formatter() {
        const value = this.y;
        const percent = ((value / totalValue) * 100).toFixed(2);
        return `<b>${this.series.name}</b><br/>Giá trị: <b>${value.toLocaleString("vi-VN")} VND</b><br/>Tỷ lệ: <b>${percent}%</b>`;
      },
      shared: false,
    },
    plotOptions: {
      series: {
        stacking: "normal",
        dataLabels: {
          enabled: true,
          formatter() {
            const percent = ((this.y / totalValue) * 100).toFixed(1);
            return `${this.y.toLocaleString("vi-VN")} VND\n(${percent}%)`;
          },
          style: {
            fontWeight: "bold",
            color: "white",
            textOutline: "1px contrast",
          },
        },
      },
    },
    series: seriesData,
  };

  return (
    <div className="p-8 space-y-12">
      <div className="flex flex-row gap-2">
        <div className="flex flex-col gap-1">
          <label className="text-sm">Ngày bắt đầu</label>
          <input
            type="date"
            value={filter.startDate}
            onChange={(e) => handleFieldChange("startDate", e.target.value)}
            placeholder="Mô tả"
            className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
          />
        </div>
        <div className="flex flex-col gap-1">
          <label className="text-sm">Ngày kết thúc</label>
          <input
            type="date"
            value={filter.endDate}
            onChange={(e) => handleFieldChange("endDate", e.target.value)}
            placeholder="Mô tả"
            className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
          />
        </div>
      </div>
      {statData && statData.data && (
        <div>
          <div className="p-4 shadow-md bg-white">
            <div className="font-bold text-lg mb-4">1. Đơn hàng</div>
            <div className="flex gap-8 flex-wrap justify-around items-center">
              <table className="table-auto border border-gray-300 w-fit h-fit rounded shadow-sm">
                <thead className="bg-gray-100">
                  <tr>
                    <th className="px-4 py-2 border border-gray-300 text-left">
                      Trạng thái
                    </th>
                    <th className="px-4 py-2 border border-gray-300 text-center">
                      Số lượng đơn
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {Object.entries(statData.data)
                    .slice(0, 4)
                    .map(([key, value]) => (
                      <tr key={key}>
                        <td className="px-4 py-2 border border-gray-200">
                          {statusLabels[key]}
                        </td>
                        <td className="px-4 py-2 border border-gray-200 text-center">
                          {value}
                        </td>
                      </tr>
                    ))}
                </tbody>
              </table>

              <div className="">
                <HighchartsReact
                  highcharts={Highcharts}
                  options={orderOptions}
                />
              </div>
            </div>
          </div>

          <div className="p-4 shadow-md bg-white">
            <div className="font-bold text-lg mb-4">2. Doanh thu</div>
            <div className="flex gap-8 flex-wrap justify-around items-center">
              <table className="table-auto border border-gray-300 w-fit h-fit rounded shadow-sm">
                <thead className="bg-gray-100">
                  <tr>
                    <th className="px-4 py-2 border border-gray-300 text-left">
                      Khoản mục
                    </th>
                    <th className="px-4 py-2 border border-gray-300 text-right">
                      Giá trị
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {Object.entries(statData.data)
                    .filter(([key]) => earningsLabels[key])
                    .map(([key, value]) => (
                      <tr key={key}>
                        <td className="px-4 py-2 border border-gray-200">
                          {earningsLabels[key]}
                        </td>
                        <td className="px-4 py-2 border border-gray-200 text-right">
                          {earningsLabels[key] !== "Hạng"
                            ? formatNumber(value)
                            : value}
                        </td>
                      </tr>
                    ))}
                </tbody>
              </table>

              <div className="">
                <HighchartsReact
                  highcharts={Highcharts}
                  options={revenueOptions}
                />
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
