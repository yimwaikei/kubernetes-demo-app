import { BrowserRouter, Routes, Route } from "react-router-dom";
import JobListing from "../pages/JobListing";
import AppLayout from "../layout/AppLayout";
import About from "../pages/About";
import UploadImage from "../pages/UploadImage";

export default function AppRoutes() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<AppLayout />}>
          <Route index element={<About />} />
          <Route path="upload-image" element={<UploadImage />} />
          <Route path="job-listing" element={<JobListing />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
