import { useEffect, useState } from "react";
import api from "../services/api";
import {
  BsKeyFill,
  BsGraphUpArrow,
  BsXCircleFill,
  BsLightningChargeFill
} from "react-icons/bs";

function StatsCard() {

  const [stats, setStats] = useState({
    activeKeys: 0,
    cacheHits: 0,
    cacheMisses: 0,
    totalRequests: 0,
    hitRatio: 0
  });

  const loadStats = async () => {

    try {

      const response = await api.get("/stats");

      setStats(response.data.data);

    } catch (error) {

      console.log(error);

    }

  };

  useEffect(() => {

    loadStats();

  }, []);

  return (

    <>

      <div className="row g-3">

        <div className="col-6">

          <div className="card text-center shadow border-0">

            <div className="card-body">

              <BsKeyFill
                size={35}
                className="text-primary mb-2"
              />

              <h3>{stats.activeKeys}</h3>

              <p className="mb-0">
                Active Keys
              </p>

            </div>

          </div>

        </div>

        <div className="col-6">

          <div className="card text-center shadow border-0">

            <div className="card-body">

              <BsGraphUpArrow
                size={35}
                className="text-success mb-2"
              />

              <h3>{stats.cacheHits}</h3>

              <p className="mb-0">
                Cache Hits
              </p>

            </div>

          </div>

        </div>

        <div className="col-6">

          <div className="card text-center shadow border-0">

            <div className="card-body">

              <BsXCircleFill
                size={35}
                className="text-danger mb-2"
              />

              <h3>{stats.cacheMisses}</h3>

              <p className="mb-0">
                Cache Misses
              </p>

            </div>

          </div>

        </div>

        <div className="col-6">

          <div className="card text-center shadow border-0">

            <div className="card-body">

              <BsLightningChargeFill
                size={35}
                className="text-warning mb-2"
              />

              <h3>{stats.hitRatio.toFixed(0)}%</h3>

              <p className="mb-0">
                Hit Ratio
              </p>

            </div>

          </div>

        </div>

      </div>

    </>

  );

}

export default StatsCard;