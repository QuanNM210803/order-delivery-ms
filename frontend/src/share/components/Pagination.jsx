import ChevronLeftIcon from "@mui/icons-material/ChevronLeft";
import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import Dropdown from "./Dropdown";
import { useMemo } from "react";

export default function Pagination({
  total,
  pageSize,
  page,
  handlePageSizeChange,
  handlePageChange,
}) {
  const pageSizeOptions = useMemo(() => {
    return [
      {
        text: "10 bản ghi",
        handler: () => handlePageSizeChange(10),
      },
      {
        text: "20 bản ghi",
        handler: () => handlePageSizeChange(20),
      },
      {
        text: "50 bản ghi",
        handler: () => handlePageSizeChange(50),
      },
    ];
  }, []);

  const handleNext = () => {
    const maxPage = Math.ceil(total / pageSize);
    if (page + 1 > maxPage) {
      return;
    }

    handlePageChange(page + 1);
  };

  const handlePrev = () => {
    if (page - 1 <= 0) {
      return;
    }

    handlePageChange(page - 1);
  };

  return (
    <div className="flex flex-row gap-2 items-center">
      <div className="text-sm text-gray-500">
        <Dropdown text={pageSize + " bản ghi"} optionsList={pageSizeOptions} />
      </div>
      <div className="text-gray-500 text-sm italic">
        Hiển thị {(page - 1) * pageSize + 1} -{" "}
        {Math.min(page * pageSize, total)} trên {total} bản ghi{" "}
      </div>
      <div className="flex flex-row gap-4 justify-between items-center">
        <ChevronLeftIcon
          className="text-gray-500 cursor-pointer"
          onClick={handlePrev}
          style={{
            color: page == 1 ? "#D1D5DC" : "#6A7282"
          }}
        />
        <div className="font-medium text-gray-500">{page}</div>
        <ChevronRightIcon
          className="text-gray-500 cursor-pointer"
          onClick={handleNext}
          style={{
            color: page == Math.ceil(total / pageSize) ? "#D1D5DC" : "#6A7282"
          }}
        />
      </div>
    </div>
  );
}
