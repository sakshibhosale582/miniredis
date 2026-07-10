import { useEffect, useState } from "react";
import api from "../services/api";

function KeysCard() {

  const [keys, setKeys] = useState([]);

  const loadKeys = async () => {

    try {

      const response = await api.get("/keys");

      setKeys(response.data.data);

    } catch (error) {

      console.log(error);

    }

  };

  useEffect(() => {
    loadKeys();
  }, []);

  return (

    <div className="card shadow mt-4">

      <div className="card-header bg-success text-white">

        <h4 className="mb-0">
          🔑 Current Keys
        </h4>

      </div>

      <div className="card-body">

        {
          keys.length === 0 ?

          <p>No Keys Found</p>

          :

          <ul className="list-group">

            {
              keys.map((key,index)=>(

                <li
                  key={index}
                  className="list-group-item"
                >

                  {key}

                </li>

              ))
            }

          </ul>

        }

        <button
          className="btn btn-outline-success mt-3"
          onClick={loadKeys}
        >

          Refresh

        </button>

      </div>

    </div>

  );

}

export default KeysCard;