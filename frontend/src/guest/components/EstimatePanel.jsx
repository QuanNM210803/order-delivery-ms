import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { toast } from "react-toastify";
import { orderApi, thirdPartyApi } from "src/share/api";
import { useLoadingStore } from "src/share/stores/loadingStore";
import RequiredMark from "src/share/components/RequiredMark";
import PriceTable from "src/share/components/PriceTable";

export default function EstimatePanel() {
  const setLoading = useLoadingStore((state) => state.setLoading);
  const [pickupDistricts, setPickupDistricts] = useState([]);
  const [pickupWards, setPickupWards] = useState([]);
  const [deliveryDistricts, setDeliveryDistricts] = useState([]);
  const [deliveryWards, setDeliveryWards] = useState([]);
  const [estimation, setEstimation] = useState(null);
  const [priceData, setPriceData] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);

        const [matrixRes, districtRes] = await Promise.all([
          orderApi.getMatrix(),
          thirdPartyApi.getDistricts(),
        ]);

        setPriceData(matrixRes.data.data);
        console.log(matrixRes.data.data);
        setPickupDistricts(districtRes.data.districts);
        setDeliveryDistricts(districtRes.data.districts);
      } catch (err) {
        toast.error("Có lỗi xảy ra trong quá trình tải dữ liệu");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const {
    register,
    handleSubmit,
    resetField,
    watch,
    formState: { errors },
  } = useForm();

  const selectedPickupDistrict = watch("pickupDistrict");
  const selectedDeliveryDistrict = watch("deliveryDistrict");

  useEffect(() => {
    resetField("pickupWard");
    const district = pickupDistricts.find(
      (d) => d.name === selectedPickupDistrict,
    );
    setPickupWards(district ? district.wards : []);
  }, [selectedPickupDistrict, pickupDistricts]);

  useEffect(() => {
    resetField("deliveryWard");
    const district = deliveryDistricts.find(
      (d) => d.name === selectedDeliveryDistrict,
    );
    setDeliveryWards(district ? district.wards : []);
  }, [selectedDeliveryDistrict, deliveryDistricts]);

  const onSubmit = async (data) => {
    const payload = {
      pickupAddress: `${data.pickupStreet}, ${data.pickupWard}, ${data.pickupDistrict}`,
      deliveryAddress: `${data.deliveryStreet}, ${data.deliveryWard}, ${data.deliveryDistrict}`,
      weight: data.weight,
    };

    setLoading(true);
    try {
      const res = await orderApi.estimateShippingFee(payload);
      setEstimation(res.data.data);
    } catch (err) {
      toast.error(err?.response?.data?.message || "Có lỗi xảy ra");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="shadow-md bg-white mx-64 rounded-sm px-8 py-6 mt-12 pb-24">
      <div className="mb-8 text-lg font-medium text-center">
        Ước tính tiền hàng
      </div>
      <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-8">
        <div>
          <div className="text-base font-semibold mb-2">Địa chỉ lấy hàng</div>
          <div className="col-span-2">
            <label className="block mb-1">
              Số nhà <RequiredMark />
            </label>
            <input
              type="text"
              placeholder="Nhập số nhà"
              {...register("pickupStreet", {
                required: "Vui lòng nhập số nhà",
              })}
              className="w-full border border-gray-300 rounded px-3 py-2"
            />
            {errors.pickupStreet && (
              <p className="text-red-500 text-sm mt-1">
                {errors.pickupStreet.message}
              </p>
            )}
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block mb-1">
                Quận <RequiredMark />
              </label>
              <select
                {...register("pickupDistrict", {
                  required: "Vui lòng chọn quận",
                })}
                className="w-full border border-gray-300 rounded px-3 py-2"
              >
                <option value="">Chọn quận</option>
                {pickupDistricts.map((district) => (
                  <option key={district.code} value={district.name}>
                    {district.name}
                  </option>
                ))}
              </select>
              {errors.pickupDistrict && (
                <p className="text-red-500 text-sm mt-1">
                  {errors.pickupDistrict.message}
                </p>
              )}
            </div>
            <div>
              <label className="block mb-1">
                Phường <RequiredMark />
              </label>
              <select
                {...register("pickupWard", {
                  required: "Vui lòng chọn phường",
                })}
                className="w-full border border-gray-300 rounded px-3 py-2"
              >
                <option value="">Chọn phường</option>
                {pickupWards.map((ward) => (
                  <option key={ward.code} value={ward.name}>
                    {ward.name}
                  </option>
                ))}
              </select>
              {errors.pickupWard && (
                <p className="text-red-500 text-sm mt-1">
                  {errors.pickupWard.message}
                </p>
              )}
            </div>
          </div>
        </div>

        <div>
          <div className="text-base font-semibold mb-2">Địa chỉ nhận hàng</div>
          <div className="col-span-2">
            <label className="block mb-1">
              Số nhà <RequiredMark />
            </label>
            <input
              type="text"
              placeholder="Nhập số nhà"
              {...register("deliveryStreet", {
                required: "Vui lòng nhập số nhà",
              })}
              className="w-full border border-gray-300 rounded px-3 py-2"
            />
            {errors.deliveryStreet && (
              <p className="text-red-500 text-sm mt-1">
                {errors.deliveryStreet.message}
              </p>
            )}
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block mb-1">
                Quận <RequiredMark />
              </label>
              <select
                {...register("deliveryDistrict", {
                  required: "Vui lòng chọn quận",
                })}
                className="w-full border border-gray-300 rounded px-3 py-2"
              >
                <option value="">Chọn quận</option>
                {deliveryDistricts.map((district) => (
                  <option key={district.code} value={district.name}>
                    {district.name}
                  </option>
                ))}
              </select>
              {errors.deliveryDistrict && (
                <p className="text-red-500 text-sm mt-1">
                  {errors.deliveryDistrict.message}
                </p>
              )}
            </div>
            <div>
              <label className="block mb-1">
                Phường <RequiredMark />
              </label>
              <select
                {...register("deliveryWard", {
                  required: "Vui lòng chọn phường",
                })}
                className="w-full border border-gray-300 rounded px-3 py-2"
              >
                <option value="">Chọn phường</option>
                {deliveryWards.map((ward) => (
                  <option key={ward.code} value={ward.name}>
                    {ward.name}
                  </option>
                ))}
              </select>
              {errors.deliveryWard && (
                <p className="text-red-500 text-sm mt-1">
                  {errors.deliveryWard.message}
                </p>
              )}
            </div>
          </div>
        </div>

        <div className="mb-4">
          <label className="block mb-1">
            Khối lượng (Kg) <RequiredMark />
          </label>
          <input
            type="number"
            step="0.1"
            min="0"
            placeholder="Nhập khối lượng"
            {...register("weight", {
              required: "Vui lòng nhập khối lượng",
              valueAsNumber: true,
              min: { value: 0.01, message: "Khối lượng phải lớn hơn 0" },
            })}
            className="w-full border border-gray-300 rounded px-3 py-2"
          />
          {errors.weight && (
            <p className="text-red-500 text-sm mt-1">{errors.weight.message}</p>
          )}
        </div>

        <button
          type="submit"
          className="bg-red-700 text-white py-2 px-6 w-fit rounded-sm hover:brightness-95 duration-100 cursor-pointer"
        >
          Ước tính
        </button>
      </form>
      {estimation && (
        <div className="p-4 border-gray-400 border rounded-md mt-4">
          <div>
            <div className="font-bold">Quãng đường ước tính: </div>
            <span>{estimation.estimateDistance}</span>
          </div>
          <div>
            <div className="font-bold">Cước phí ước tính: </div>
            <span>{estimation.estimateFee}</span>
          </div>
        </div>
      )}
      {priceData && (
        <div className="mt-4">
          <PriceTable data={priceData} />
        </div>
      )}
    </div>
  );
}
