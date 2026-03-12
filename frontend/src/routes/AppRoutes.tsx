import { BrowserRouter, Routes, Route } from "react-router-dom";
import JobListing from "../pages/JobListing";

export default function AppRoutes() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<JobListing />} />
      </Routes>
    </BrowserRouter>
  );
}
