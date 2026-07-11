import { useEffect, useState } from "react";
import api from "../services/api";

import {
  BsDatabaseFill,
  BsCloudCheckFill,
  BsCloudSlashFill
} from "react-icons/bs";

function Header() {

  const [online, setOnline] = useState(false);

  const checkBackend = async () => {

    try {

      await api.get("/hello");

      setOnline(true);

    } catch {

      setOnline(false);

    }

  };

  useEffect(() => {

    checkBackend();

    const interval = setInterval(checkBackend,5000);

    return ()=>clearInterval(interval);

  },[]);

  return (

    <div className="dashboard-header fade-in">

      <div>

        <h1 className="dashboard-title">

          <BsDatabaseFill className="me-3"/>

          Mini Redis Dashboard

        </h1>

        <p className="dashboard-subtitle">

          In-Memory Cache Management System

        </p>

      </div>

      <div className="text-end">

        <div className="status">

          <span
            style={{
              background:online ? "#22c55e" : "#ef4444",
              boxShadow:online
                ? "0 0 15px #22c55e"
                : "0 0 15px #ef4444"
            }}
          ></span>

          {

            online ?

            <>

              <BsCloudCheckFill className="me-2"/>

              Backend Connected

            </>

            :

            <>

              <BsCloudSlashFill className="me-2"/>

              Backend Offline

            </>

          }

        </div>

        <div className="version">

          Version 1.0

        </div>

      </div>

    </div>

  );

}

export default Header;