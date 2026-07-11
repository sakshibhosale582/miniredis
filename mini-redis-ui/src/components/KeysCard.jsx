import { useEffect, useState } from "react";
import api from "../services/api";
import { toast } from "react-toastify";

import {
  BsKeyFill,
  BsArrowClockwise,
  BsClipboard
} from "react-icons/bs";

function KeysCard() {

  const [keys, setKeys] = useState([]);
  const [search, setSearch] = useState("");

  const loadKeys = async () => {

    try {

      const res = await api.get("/keys");

      setKeys(res.data.data || []);

    } catch {

      toast.error("Unable to load keys");

    }

  };

  useEffect(() => {

    loadKeys();

    const interval = setInterval(loadKeys,3000);

    return ()=>clearInterval(interval);

  },[]);

  const copyKey = (key)=>{

    navigator.clipboard.writeText(key);

    toast.success("Key copied");

  };

  const filteredKeys = keys.filter((key)=>

      key.toLowerCase().includes(search.toLowerCase())

  );

  return (

    <div className="card fade-in">

      <div className="card-header bg-primary text-white d-flex justify-content-between align-items-center">

        <div>

          <BsKeyFill className="me-2"/>

          Current Keys

        </div>

        <button
          className="btn btn-light btn-sm"
          onClick={loadKeys}
        >
          <BsArrowClockwise/>
        </button>

      </div>

      <div className="card-body">

        <input
          className="form-control mb-3"
          placeholder="Search key..."
          value={search}
          onChange={(e)=>setSearch(e.target.value)}
        />

        <div className="scroll-area">

          {

            filteredKeys.length===0 ?

            <div className="text-center text-secondary mt-5">

              No Keys Found

            </div>

            :

            <ul className="list-group keys-list">

              {

                filteredKeys.map((key,index)=>(

                  <li
                    key={index}
                    className="d-flex justify-content-between align-items-center"
                  >

                    <span>

                      <BsKeyFill className="me-2 text-primary"/>

                      {key}

                    </span>

                    <button
                      className="btn btn-outline-primary btn-sm"
                      onClick={()=>copyKey(key)}
                    >

                      <BsClipboard/>

                    </button>

                  </li>

                ))

              }

            </ul>

          }

        </div>

      </div>

    </div>

  );

}

export default KeysCard;