import "bootstrap/dist/css/bootstrap.min.css";

import Header from "./components/Header";
import CacheForm from "./components/CacheForm";
import StatsCard from "./components/StatsCard";
import KeysCard from "./components/KeysCard";
import HistoryCard from "./components/HistoryCard";

function App() {
  return (
    <div className="container-fluid bg-light min-vh-100 py-4">

      <div className="container">

        <Header />

        {/* First Row */}
        <div className="row g-4">

          <div className="col-lg-6">
            <CacheForm />
          </div>

          <div className="col-lg-6">
            <StatsCard />
          </div>

        </div>

        {/* Second Row */}
        <div className="row g-4 mt-1">

          <div className="col-lg-6">
            <KeysCard />
          </div>

          <div className="col-lg-6">
            <HistoryCard />
          </div>

        </div>

      </div>

    </div>
  );
}

export default App;