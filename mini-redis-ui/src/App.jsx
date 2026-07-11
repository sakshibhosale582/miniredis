import "bootstrap/dist/css/bootstrap.min.css";
import "react-toastify/dist/ReactToastify.css";

import { ToastContainer } from "react-toastify";

import Header from "./components/Header";
import CacheForm from "./components/CacheForm";
import StatsCard from "./components/StatsCard";
import KeysCard from "./components/KeysCard";
import HistoryCard from "./components/HistoryCard";

function App() {
  return (
    <div className="container-fluid">

      <ToastContainer
        position="top-right"
        autoClose={2000}
        hideProgressBar={false}
        newestOnTop
        closeOnClick
        pauseOnHover
        theme="colored"
      />

      <Header />

      <div className="row g-4">

        <div className="col-lg-4">
          <CacheForm />
        </div>

        <div className="col-lg-8">
          <StatsCard />
        </div>

      </div>

      <div className="row g-4 mt-1">

        <div className="col-lg-5">
          <KeysCard />
        </div>

        <div className="col-lg-7">
          <HistoryCard />
        </div>

      </div>

      <footer className="footer">

        <strong>Mini Redis Dashboard</strong>

        <br />

        MCA Final Project • Spring Boot • React

      </footer>

    </div>
  );
}

export default App;