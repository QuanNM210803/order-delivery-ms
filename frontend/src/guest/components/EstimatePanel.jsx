import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { toast } from "react-toastify";
import { thirdPartyApi } from "src/share/api";
import { useLoadingStore } from "src/share/stores/loadingStore";

export default function EstimatePanel() {
  const setLoading = useLoadingStore((state) => state.setLoading);
  const [districts, setDistricts] = useState([]);
  const [wards, setWards] = useState([]);

  useEffect(() => {
    setLoading(true);
    thirdPartyApi
      .getDistricts()
      .then((res) => {
        setDistricts(res.data.districts);
        console.log(res.data.districts);
      })
      .catch((err) => {
        toast.error("Có lỗi xảy ra trong quá trính lấy thông tin quận");
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  const {
    register,
    handleSubmit,
    resetField,
    watch,
    formState: { errors },
  } = useForm();

  const selectedDistrictCode = watch("district");

  useEffect(() => {
    resetField("ward");
    const district = districts.find(
      (district) => district.code == selectedDistrictCode,
    );
    if (district) {
      setWards(district.wards);
    }
  }, [selectedDistrictCode, districts]);

  const onSubmit = (data) => {
    console.log(data);
  };

  return (
    <div className="shadow-md bg-white mx-64 rounded-sm px-8 py-6 mt-12 pb-24">
      <div className="mb-8 text-lg font-medium text-center">
        Ước tính tiền hàng
      </div>
      <div className="flex flex-row gap-8">
        <div className="flex-1">
          <form onSubmit={handleSubmit(onSubmit)} className="mb-6">
            <div className="mb-4">
              <label className="block mb-1 font-bold">Quận</label>
              <select
                {...register("district", {
                  required: "Vui lòng chọn quận",
                })}
                className="w-full border border-gray-300 rounded px-3 py-2"
              >
                <option className="" value="">
                  Chọn quận
                </option>
                {districts.map((district) => (
                  <option key={district.code} value={district.code}>
                    {district.name}
                  </option>
                ))}
              </select>
              {errors.district && (
                <p className="text-red-500 text-sm mt-1">
                  {errors.district.message}
                </p>
              )}
            </div>
            <div className="mb-4">
              <label className="block mb-1 font-bold">Phường</label>
              <select
                {...register("ward", { required: "Vui lòng chọn phường" })}
                className="w-full border border-gray-300 rounded px-3 py-2"
              >
                <option value="">Chọn phường</option>
                {wards.map((ward) => (
                  <option key={ward.code} value={ward.code}>
                    {ward.name}
                  </option>
                ))}
              </select>
              {errors.ward && (
                <p className="text-red-500 text-sm mt-1">
                  {errors.ward.message}
                </p>
              )}
            </div>
            <div className="mb-4">
              <label className="block mb-1 font-bold">Khối lượng (kg)</label>
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
                <p className="text-red-500 text-sm mt-1">
                  {errors.weight.message}
                </p>
              )}
            </div>

            <button
              type="submit"
              className="bg-red-700 text-white w-full py-2 rounded-sm hover:brightness-95 duration-100 cursor-pointer"
            >
              Ước tính
            </button>
          </form>
        </div>
        <div className="flex-1">
          <div>
            <span className="font-bold">Giá tiền ước tính:</span>{" "}
          </div>
          <div>
            <span className="font-bold">Quãng đường ước tính:</span>{" "}
          </div>
        </div>
      </div>
    </div>
  );
}
