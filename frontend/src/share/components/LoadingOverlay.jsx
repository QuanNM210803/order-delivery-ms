import { useLoadingStore } from "../stores/loadingStore";
import { ClockLoader } from "react-spinners";

export default function LoadingOverlay() {
  const isLoading = useLoadingStore((state) => state.isLoading);

  return (
    <>
      {isLoading && (
        <div className="bg-black/30 fixed z-[100] h-full w-full flex justify-center items-center top-0 left-0">
          <ClockLoader color="#EE0033" />
        </div>
      )}
    </>
  );
}
