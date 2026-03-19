import { Layout, Menu } from "antd";
import { useNavigate, useLocation, Outlet } from "react-router-dom";

const { Content, Sider } = Layout;

function AppLayout() {
  const navigate = useNavigate();
  const location = useLocation();

  return (
    <Layout style={{ minHeight: "100vh" }}>
      <Sider>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          onClick={({ key }) => navigate(key)}
          items={[
            {
              key: "/",
              label: "About",
            },
            {
              key: "/upload-image",
              label: "Upload Image",
            },
            {
              key: "/job-listing",
              label: "Job History",
            },
            {
              key: "/file-listing",
              label: "Processed Files"
            }
          ]}
        />
      </Sider>

      <Layout>
        <Content style={{ margin: "16px" }}>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}

export default AppLayout;