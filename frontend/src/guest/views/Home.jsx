import { useState } from "react";
import LookupPanel from "../components/LookupPanel";
import EstimatePanel from "../components/EstimatePanel";

export default function Home() {
  // "Lookup" | "Estimate"
  const [currentTab, setCurrentTab] = useState("Lookup");

  return (
    <div className="min-h-[100vh] pt-16">
      <div className="w-full flex flex-col items-center">
        <div className="flex flex-row">
          <div
            onClick={() => setCurrentTab("Lookup")}
            className="w-54 bg-white shadow-md cursor-pointer text-center py-4 text-lg"
            style={{
              backgroundColor: currentTab === "Lookup" ? "#E7000B" : "white",
              color: currentTab === "Lookup" ? "white" : "black",
            }}
          >
            Tra cứu
          </div>
          <div
            onClick={() => setCurrentTab("Estimate")}
            className="w-54 bg-white shadow-md cursor-pointer text-center py-4 text-lg"
            style={{
              backgroundColor: currentTab === "Estimate" ? "#E7000B" : "white",
              color: currentTab === "Estimate" ? "white" : "black",
            }}
          >
            Ước tính
          </div>
        </div>
      </div>
      {currentTab === "Lookup" && <LookupPanel />}
      {currentTab === "Estimate" && <EstimatePanel />}
    </div>
  );
}
