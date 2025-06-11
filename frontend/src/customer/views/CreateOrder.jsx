import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router";
import { toast } from "react-toastify";
import { orderApi, thirdPartyApi } from "src/share/api";
import RequiredMark from "src/share/components/RequiredMark";
import { useLoadingStore } from "src/share/stores/loadingStore";
import { Stepper, Step, StepLabel } from "@mui/material";

export default function CreateOrder() {
  const {
    register,
    handleSubmit,
    watch,
    trigger,
    getValues,
    resetField,
    formState: { errors },
  } = useForm();
  const navigate = useNavigate();
  const [pickupDistricts, setPickupDistricts] = useState([]);
  const [pickupWards, setPickupWards] = useState([]);
  const [shippingDistricts, setShippingDistricts] = useState([]);
  const [shippingWards, setShippingWards] = useState([]);
  const [estimation, setEstimation] = useState(null);
  const [activeStep, setActiveStep] = useState(0);
  const setLoading = useLoadingStore((state) => state.setLoading);
  const selectedPickupDistrictCode = watch("pickupDistrict");
  const selectedDeliveryDistrictCode = watch("shippingDistrict");

  const steps = ["Ước tính chi phí", "Hoàn thành đơn hàng"];

  useEffect(() => {
    resetField("pickupWard");
    const district = pickupDistricts.find(
      (district) => district.name === selectedPickupDistrictCode,
    );
    if (district) {
      setPickupWards(district.wards);
    }
  }, [selectedPickupDistrictCode, pickupDistricts, resetField]);

  useEffect(() => {
    resetField("shippingWard");
    const district = shippingDistricts.find(
      (district) => district.name === selectedDeliveryDistrictCode,
    );
    if (district) {
      setShippingWards(district.wards);
    }
  }, [selectedDeliveryDistrictCode, shippingDistricts, resetField]);

  useEffect(() => {
    setLoading(true);
    thirdPartyApi
      .getDistricts()
      .then((res) => {
        setShippingDistricts(res.data.districts);
        setPickupDistricts(res.data.districts);
      })
      .catch((err) => {
        toast.error("Có lỗi xảy ra trong quá trình lấy thông tin quận");
      })
      .finally(() => {
        setLoading(false);
      });
  }, [setLoading]);

  const handleEstimate = async () => {
    const res = await trigger([
      "shippingDistrict",
      "shippingWard",
      "shippingStreet",
      "pickupDistrict",
      "pickupWard",
      "pickupStreet",
      "packageWeigth",
      "packageSize",
    ]);
    if (res) {
      const data = getValues([
        "shippingDistrict",
        "shippingWard",
        "shippingStreet",
        "pickupDistrict",
        "pickupWard",
        "pickupStreet",
        "packageWeigth",
        "packageSize",
      ]);
      setLoading(true);
      try {
        const payload = {
          pickupAddress: `${data[5]}, ${data[4]}, ${data[3]}`,
          deliveryAddress: `${data[2]}, ${data[1]}, ${data[0]}`,
          weight: data[6],
          size: data[7],
        };
        const res = await orderApi.estimateShippingFee(payload);
        setEstimation(res.data.data);
      } catch (err) {
        toast.error(err?.response?.data?.message || "Có lỗi xảy ra");
        setLoading(false);
        return false;
      } finally {
        setLoading(false);
      }
      return true;
    }
    return false;
  };

  const onSubmit = async (data) => {
    const payload = {
      receiverName: data.recipentName,
      receiverPhone: data.recipentPhone,
      deliveryAddress: `${data.shippingStreet}, ${data.shippingWard}, ${data.shippingDistrict}`,
      pickupAddress: `${data.pickupStreet}, ${data.pickupWard}, ${data.pickupDistrict}`,
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

  const handleNext = async () => {
    if (activeStep === 0) {
      const success = await handleEstimate();
      if (!success) return;
    }
    setActiveStep((prev) => prev + 1);
  };

  const handleBack = () => {
    setActiveStep((prev) => prev - 1);
  };

  const getStepContent = (step) => {
    switch (step) {
      case 0:
        return (
          <div className="flex flex-row gap-2 gap-y-8">
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
                  <option value="">Chọn quận</option>
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
                  <option value="">Chọn phường</option>
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
                  Trọng lượng kiện hàng (Kg) <RequiredMark />
                </label>
                <input
                  {...register("packageWeigth", {
                    required: "Vui lòng nhập trọng lượng kiện hàng",
                    valueAsNumber: true,
                    min: {
                      value: 0,
                      message: "Trọng lượng phải là số không âm",
                    },
                    validate: (value) =>
                      !isNaN(value) || "Giá trị phải là một số hợp lệ",
                  })}
                  className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
                />
                {errors.packageWeigth && (
                  <span className="text-xs text-red-500 mt-0.5">
                    {errors.packageWeigth.message}
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
                  <option value="">Chọn quận</option>
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
                  <option value="">Chọn phường</option>
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
            </div>
          </div>
        );
      case 1:
        return (
          <div>
            <div className="flex flex-row gap-2 gap-y-8">
              <div className="flex-1 min-w-[420px] max-w-[420px]">
                <div className="flex flex-col gap-0.5 mt-1">
                  <label className="text-sm">
                    Địa chỉ lấy hàng <RequiredMark />
                  </label>
                  <select
                    {...register("pickupDistrict")}
                    className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none mt-1"
                    disabled
                  >
                    <option value={getValues("pickupDistrict")}>
                      {getValues("pickupDistrict") || "Chọn quận"}
                    </option>
                  </select>
                  <select
                    {...register("pickupWard")}
                    className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none mt-1"
                    disabled
                  >
                    <option value={getValues("pickupWard")}>
                      {getValues("pickupWard") || "Chọn phường"}
                    </option>
                  </select>
                  <input
                    {...register("pickupStreet")}
                    className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none mt-1"
                    disabled
                  />
                </div>
                <div className="flex flex-col gap-0.5 mt-1">
                  <label className="text-sm">
                    Trọng lượng kiện hàng (Kg) <RequiredMark />
                  </label>
                  <input
                    {...register("packageWeigth")}
                    className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
                    disabled
                  />
                </div>
                <div className="flex flex-col gap-0.5 mt-1">
                  <label className="text-sm">Kích thước kiện hàng</label>
                  <input
                    {...register("packageSize")}
                    className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
                  />
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
                    className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
                  />
                  {errors.packagePrice && (
                    <span className="text-xs text-red-500 mt-0.5">
                      {errors.packagePrice.message}
                    </span>
                  )}
                </div>
                <div className="flex flex-col gap-0.5 mt-1">
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
                    {...register("note")}
                    className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none hover:border-red-500/40 focus:border-red-500/40 duration-200"
                  />
                </div>
              </div>
              <div className="flex-1 min-w-[420px] max-w-[420px]">
                <div className="flex flex-col gap-0.5 mt-1">
                  <label className="text-sm">
                    Địa chỉ nhận hàng <RequiredMark />
                  </label>
                  <select
                    {...register("shippingDistrict")}
                    className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none mt-1"
                    disabled
                  >
                    <option value={getValues("shippingDistrict")}>
                      {getValues("shippingDistrict") || "Chọn quận"}
                    </option>
                  </select>
                  <select
                    {...register("shippingWard")}
                    className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none mt-1"
                    disabled
                  >
                    <option value={getValues("shippingWard")}>
                      {getValues("shippingWard") || "Chọn phường"}
                    </option>
                  </select>
                  <input
                    {...register("shippingStreet")}
                    className="bg-gray-100 border border-gray-300 px-2 py-1 rounded-md outline-none mt-1"
                    disabled
                  />
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
            {estimation && (
              <div className="mt-4 border border-red-300 w-fit rounded-md p-2 bg-red-300/20">
                <div>
                  <span className="font-medium text-red-700">
                    Quãng đường ước tính:{" "}
                  </span>
                  {estimation.estimateDistance}
                </div>
                <div>
                  <span className="font-medium text-red-700">
                    Cước phí ước tính:{" "}
                  </span>
                  {estimation.estimateFee}
                </div>
              </div>
            )}
          </div>
        );
      default:
        return null;
    }
  };

  return (
    <div className="p-8">
      <div className="mb-4 text-lg font-medium text-left">Tạo đơn hàng mới</div>
      <form onSubmit={handleSubmit(onSubmit)}>
        <div className="max-w-[900px]">
          <Stepper activeStep={activeStep} sx={{ mb: 4 }}>
            {steps.map((label) => (
              <Step key={label}>
                <StepLabel>{label}</StepLabel>
              </Step>
            ))}
          </Stepper>
        </div>
        {getStepContent(activeStep)}
        <div className="flex gap-2 max-w-[480px] mt-4">
          {activeStep > 0 && (
            <button
              type="button"
              onClick={handleBack}
              className="bg-red-700 text-white w-[200px] py-2 rounded-sm hover:brightness-95 duration-100 cursor-pointer"
            >
              Quay lại
            </button>
          )}
          <button
            type={activeStep === steps.length - 1 ? "submit" : "button"}
            onClick={activeStep === steps.length - 1 ? undefined : handleNext}
            className="bg-red-700 text-white flex-1 max-w-[420px] py-2 rounded-sm hover:brightness-95 duration-100 cursor-pointer"
          >
            {activeStep === steps.length - 1 ? "Tạo mới" : "Tiếp theo"}
          </button>
        </div>
      </form>
    </div>
  );
}
