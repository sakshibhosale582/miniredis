import { useEffect, useState } from "react";
import api from "../services/api";

import {
  BsKeyFill,
  BsGraphUpArrow,
  BsXCircleFill,
  BsLightningChargeFill,
  BsArrowRepeat
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

      const res = await api.get("/stats");

      setStats(res.data.data);

    } catch (e) {

      console.log(e);

    }

  };

  useEffect(() => {

    loadStats();

    const timer = setInterval(loadStats,3000);

    return ()=>clearInterval(timer);

  },[]);

  const cards=[

    {
      title:"Active Keys",
      value:stats.activeKeys,
      color:"primary",
      icon:<BsKeyFill/>
    },

    {
      title:"Cache Hits",
      value:stats.cacheHits,
      color:"success",
      icon:<BsGraphUpArrow/>
    },

    {
      title:"Cache Misses",
      value:stats.cacheMisses,
      color:"danger",
      icon:<BsXCircleFill/>
    },

    {
      title:"Hit Ratio",
      value:stats.hitRatio.toFixed(0)+"%",
      color:"warning",
      icon:<BsLightningChargeFill/>
    }

  ];

  return(

<div className="card">

<div className="card-header bg-white d-flex justify-content-between align-items-center">

<h5 className="mb-0 fw-bold">

System Statistics

</h5>

<button
className="btn btn-outline-primary btn-sm"
onClick={loadStats}
>

<BsArrowRepeat/>

</button>

</div>

<div className="card-body">

<div className="row">

{

cards.map((card,index)=>(

<div
className="col-md-6 col-xl-3 mb-3"
key={index}
>

<div
className={`border border-${card.color} rounded-4 p-4 h-100 shadow-sm`}
>

<div
className={`text-${card.color} fs-2 mb-3`}
>

{card.icon}

</div>

<h2 className="fw-bold">

{card.value}

</h2>

<div className="text-secondary">

{card.title}

</div>

</div>

</div>

))

}

</div>

</div>

</div>

);

}

export default StatsCard;