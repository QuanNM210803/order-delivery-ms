import { useEffect, useState } from "react";
import { deliveryApi } from "src/share/api";
import { MoonLoader } from "react-spinners";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import CancelIcon from "@mui/icons-material/Cancel";
import { useAuthStore } from "src/share/stores/authStore";
import roles from "src/share/constants/roles";

export default function StatusToggler() {
  const [isVisible, setIsVisible] = useState(false);
  const [status, setStatus] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const user = useAuthStore((state) => state.user);

  const syncStatus = () => {
    setIsLoading(true);
    if (isVisible) {
      deliveryApi
        .getDriverStatus()
        .then((res) => {
          setStatus(res.data.data);
        })
        .catch((err) => {
          // Void
        })
        .finally(() => {
          setIsLoading(false);
        });
    }
  };

  useEffect(() => {
    syncStatus();
  }, [isVisible]);

  if (!user || user?.roles[0] != roles.DELIVERY_STAFF) {
    return null;
  }

  const handleToggle = () => {
    setIsLoading(true);
    deliveryApi
      .updateDriverStatus()
      .then((res) => {
        syncStatus();
      })
      .catch((err) => {
        //void
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  return (
    <div className="fixed right-12 bottom-72">
      <div className="cursor-pointer">
        {isVisible && (
          <div className="flex justify-center items-center h-16 bg-white shadow-md rounded-md mb-2">
            {isLoading ? (
              <MoonLoader size={36} />
            ) : (
              <>
                {status ? (
                  <div className="text-green-700">
                    <CheckCircleIcon onClick={handleToggle} fontSize="large" />
                  </div>
                ) : (
                  <div className="text-red-700">
                    <CancelIcon onClick={handleToggle} fontSize="large" />
                  </div>
                )}
              </>
            )}
          </div>
        )}
        <div
          className=" bg-red-800 px-2 py-1 text-white rounded-full"
          onClick={() => setIsVisible((state) => !state)}
        >
          Tìm kiếm đơn?
        </div>
      </div>
    </div>
  );
}
