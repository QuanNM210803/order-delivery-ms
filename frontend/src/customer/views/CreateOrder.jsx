import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router";
import { toast } from "react-toastify";
import { orderApi, thirdPartyApi } from "src/share/api";
import RequiredMark from "src/share/components/RequiredMark";
import { useLoadingStore } from "src/share/stores/loadingStore";

export default function CreateOrder() {
  const {
    register,
    handleSubmit,
    watch,
    resetField,
    formState: { errors },
  } = useForm();

  const navigate = useNavigate();

  const [pickupDistricts, setPickupDistricts] = useState([]);
  const [pickupWards, setPickupWards] = useState([]);

  const [shippingDistricts, setShippingDistricts] = useState([]);
  const [shippingWards, setShippingWards] = useState([]);

  const setLoading = useLoadingStore((state) => state.setLoading);
  const selectedPickupDistrictCode = watch("pickupDistrict");
  const selectedDeliveryDistrictCode = watch("shippingDistrict");

  useEffect(() => {
    resetField("pickupWard");
    const district = pickupDistricts.find(
      (district) => district.name == selectedPickupDistrictCode,
    );
    if (district) {
      setPickupWards(district.wards);
    }
  }, [selectedPickupDistrictCode, pickupDistricts]);

  useEffect(() => {
    resetField("shippingWard");
    const district = shippingDistricts.find(
      (district) => district.name == selectedDeliveryDistrictCode,
    );
    if (district) {
      setShippingWards(district.wards);
    }
  }, [selectedDeliveryDistrictCode, shippingDistricts]);

  useEffect(() => {
    setLoading(true);
    thirdPartyApi
      .getDistricts()
      .then((res) => {
        setShippingDistricts(res.data.districts);
        setPickupDistricts(res.data.districts);
      })
      .catch((err) => {
        toast.error("Có lỗi xảy ra trong quá trính lấy thông tin quận");
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  const onSubmit = async (data) => {
    const payload = {
      receiverName: data.recipentName,
      receiverPhone: data.recipentPhone,
      deliveryAddress: `${data.shippingStreet}, ${data.shippingWard}, ${data.shippingDistrict}`,
      pickupAddress: `${data.pickupStreet}, ${data.pickupWard} ${data.pickupDistrict}`,
      description: data.description,
      size: data.packageSize,
      weight: +data.packageWeigth,
      note: data.note,
      price: +data.packagePrice,
    };
    setLoading(true);
    try {
      const response = await orderApi.createOrder(payload);
      toast.success(response.data.message);
      navigate(`/customer/order/detail/${response.data.data.id}`);
    } catch (err) {
      toast.error(err?.response?.data?.message || "Có lỗi xảy ra");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-8">
      <div class="mb-4 text-lg font-medium text-left">Tạo đơn hàng mới</div>
      <form className="flex flex-col" onSubmit={handleSubmit(onSubmit)}>
        <div className="flex flex-row gap-2 flex-wrap gap-y-8">
          <div className="flex-1 min-w-[420px] max-w-[420px]">
            <div className="flex flex-col gap-0.5 mt-1">
              <label className="text-sm">
                Địa chỉ lấy hàng <RequiredMark />
              </label>
              <select
                {...register("pickupDistrict", {
                  required: "Vui lòng chọn quận",
                })}
                className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none mt-1"
              >
                <option className="" value="">
                  Chọn quận
                </option>
                {pickupDistricts.map((district) => (
                  <option key={district.code} value={district.name}>
                    {district.name}
                  </option>
                ))}
              </select>
              {errors.pickupDistrict && (
                <span className="text-xs text-red-500 mt-0.5">
                  {errors.pickupDistrict.message}
                </span>
              )}

              <select
                {...register("pickupWard", {
                  required: "Vui lòng chọn phường",
                })}
                className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none mt-1"
              >
                <option className="" value="">
                  Chọn phường
                </option>
                {pickupWards.map((ward) => (
                  <option key={ward.code} value={ward.name}>
                    {ward.name}
                  </option>
                ))}
              </select>
              {errors.pickupWard && (
                <span className="text-xs text-red-500 mt-0.5">
                  {errors.pickupWard.message}
                </span>
              )}
              <input
                placeholder="Số nhà, tên đường"
                {...register("pickupStreet", {
                  required: "Vui lòng nhập số nhà và tên đường",
                })}
                className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none mt-1"
              />
              {errors.pickupStreet && (
                <span className="text-xs text-red-500 mt-0.5">
                  {errors.pickupStreet.message}
                </span>
              )}
            </div>
            <div className="flex flex-col gap-0.5 mt-1">
              <label className="text-sm">
                Kích thước kiện hàng <RequiredMark />
              </label>
              <input
                type="text"
                {...register("packageSize", {
                  required: "Vui lòng nhập kích thước kiện hàng",
                })}
                className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
              />
              {errors.packageSize && (
                <span className="text-xs text-red-500 mt-0.5">
                  {errors.packageSize.message}
                </span>
              )}
            </div>
            <div className="flex flex-col gap-0.5 mt-1">
              <label className="text-sm">
                Trọng lượng kiện hàng (Kg) <RequiredMark />
              </label>
              <input
                {...register("packageWeigth", {
                  required: "Vui lòng nhập trọng lượng kiện hàng",
                })}
                className="bg-gray-100 border border-gray-300 flex-1 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
              />
              {errors.packageWeigth && (
                <span className="text-xs text-red-500 mt-0.5">
                  {errors.packageWeigth.message}
                </span>
              )}
            </div>
            <div className="flex flex-col gap-0.5 mt-1">
              <label className="text-sm">
                Giá tiền hàng (VNĐ) <RequiredMark />
              </label>
              <input
                type="text"
                {...register("packagePrice", {
                  required: "Vui lòng nhập giá tiền hàng",
                  pattern: {
                    value: /^\d+$/,
                    message: "Giá tiền không hợp lệ",
                  },
                })}
                className="bg-gray-100 border border-gray-300 flex-1 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
              />
              {errors.packagePrice && (
                <span className="text-xs text-red-500 mt-0.5">
                  {errors.packagePrice.message}
                </span>
              )}
            </div>
          </div>
          <div className="flex-1 min-w-[420px] max-w-[420px]">
            <div className="flex flex-col gap-0.5 mt-1">
              <label className="text-sm">
                Địa chỉ nhận hàng <RequiredMark />
              </label>
              <select
                {...register("shippingDistrict", {
                  required: "Vui lòng chọn quận",
                })}
                className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none mt-1"
              >
                <option className="" value="">
                  Chọn quận
                </option>
                {shippingDistricts.map((district) => (
                  <option key={district.code} value={district.name}>
                    {district.name}
                  </option>
                ))}
              </select>
              {errors.shippingDistrict && (
                <span className="text-xs text-red-500 mt-0.5">
                  {errors.shippingDistrict.message}
                </span>
              )}

              <select
                {...register("shippingWard", {
                  required: "Vui lòng chọn phường",
                })}
                className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none mt-1"
              >
                <option className="" value="">
                  Chọn phường
                </option>
                {shippingWards.map((ward) => (
                  <option key={ward.code} value={ward.name}>
                    {ward.name}
                  </option>
                ))}
              </select>
              {errors.shippingWard && (
                <span className="text-xs text-red-500 mt-0.5">
                  {errors.shippingWard.message}
                </span>
              )}
              <input
                placeholder="Số nhà, tên đường"
                {...register("shippingStreet", {
                  required: "Vui lòng nhập số nhà và tên đường",
                })}
                className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none mt-1"
              />
              {errors.shippingStreet && (
                <span className="text-xs text-red-500 mt-0.5">
                  {errors.shippingStreet.message}
                </span>
              )}
            </div>
            <div className="flex flex-col gap-0.5 mt-1">
              <label className="text-sm">
                Tên người nhận <RequiredMark />
              </label>
              <input
                type="text"
                {...register("recipentName", {
                  required: "Vui lòng nhập tên người nhận",
                })}
                className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
              />
              {errors.recipentName && (
                <span className="text-xs text-red-500 mt-0.5">
                  {errors.recipentName.message}
                </span>
              )}
            </div>
            <div className="flex flex-col gap-0.5 mt-1">
              <label className="text-sm">
                SĐT người nhận <RequiredMark />
              </label>
              <input
                type="text"
                {...register("recipentPhone", {
                  required: "Vui lòng nhập số điện thoại người nhận",
                  pattern: {
                    value: /^[0-9]+$/,
                    message: "Số điện thoại không hợp lệ",
                  },
                })}
                className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
              />
              {errors.recipentPhone && (
                <span className="text-xs text-red-500 mt-0.5">
                  {errors.recipentPhone.message}
                </span>
              )}
            </div>
          </div>
        </div>
        <div className="flex-1 min-w-[420px] max-w-[420px] mt-8">
          <div className="flex flex-col gap-0.5">
            <label className="text-sm">
              Mô tả đơn hàng <RequiredMark />
            </label>
            <textarea
              rows={4}
              {...register("description", {
                required: "Vui lòng nhập mô tả",
              })}
              className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
            />
            {errors.description && (
              <span className="text-xs text-red-500 mt-0.5">
                {errors.description.message}
              </span>
            )}
          </div>
          <div className="flex flex-col gap-0.5 mt-1">
            <label className="text-sm">Ghi chú (nếu có)</label>
            <textarea
              rows={4}
              {...register("note", {})}
              className="bg-gray-100 border border-gray-300 flex-1 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
            />
            {errors.note && (
              <span className="text-xs text-red-500 mt-0.5">
                {errors.note.message}
              </span>
            )}
          </div>
        </div>
        <button
          type="submit"
          className="bg-red-700 max-w-[480px] text-white w-[420px] py-2 mt-4 rounded-sm hover:brightness-95 duration-100 cursor-pointer"
        >
          Tạo mới
        </button>
      </form>
    </div>
  );
}
