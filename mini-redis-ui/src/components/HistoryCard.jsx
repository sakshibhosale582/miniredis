import { useEffect, useState } from "react";
import api from "../services/api";
import { toast } from "react-toastify";

import {
  BsClockHistory,
  BsArrowClockwise
} from "react-icons/bs";

function HistoryCard() {

  const [history, setHistory] = useState([]);

  const loadHistory = async () => {

    try {

      const res = await api.get("/history");

      setHistory(res.data.data || []);

    } catch {

      toast.error("Unable to load history");

    }

  };

  useEffect(() => {

    loadHistory();

    const interval = setInterval(loadHistory,3000);

    return ()=>clearInterval(interval);

  },[]);

  return (

    <div className="card fade-in">

      <div className="card-header bg-success text-white d-flex justify-content-between align-items-center">

        <div>

          <BsClockHistory className="me-2"/>

          Command History

        </div>

        <button
          className="btn btn-light btn-sm"
          onClick={loadHistory}
        >
          <BsArrowClockwise/>
        </button>

      </div>

      <div className="card-body scroll-area">

        {

          history.length===0 ?

          (

            <div
              className="text-center text-secondary mt-5"
            >

              No Commands Executed

            </div>

          )

          :

          history.map((item,index)=>(

            <div
              key={index}
              className="d-flex align-items-center border-bottom py-3"
            >

              <div
                className="bg-success text-white rounded-circle d-flex justify-content-center align-items-center"
                style={{
                  width:"42px",
                  height:"42px",
                  minWidth:"42px"
                }}
              >

                <BsClockHistory/>

              </div>

              <div className="ms-3">

                <div className="fw-semibold">

                  {item}

                </div>

                <small className="text-muted">

                  Cache Operation

                </small>

              </div>

            </div>

          ))

        }

      </div>

    </div>

  );

}

export default HistoryCard;