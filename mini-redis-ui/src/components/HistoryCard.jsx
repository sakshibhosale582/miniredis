import { useEffect, useState } from "react";
import api from "../services/api";

function HistoryCard() {

    const [history, setHistory] = useState([]);

    const loadHistory = async () => {

        try {

            const response = await api.get("/history");

            setHistory(response.data.data);

        } catch (error) {

            console.log(error);

        }

    };

    useEffect(() => {
        loadHistory();
    }, []);

    return (

        <div className="card shadow mt-4">

            <div className="card-header bg-warning">

                <h4 className="mb-0">
                    📜 Command History
                </h4>

            </div>

            <div className="card-body">

                {
                    history.length === 0 ?

                        <p>No History</p>

                        :

                        <ul className="list-group">

                            {

                                history.map((item,index)=>(

                                    <li
                                        key={index}
                                        className="list-group-item"
                                    >

                                        {item}

                                    </li>

                                ))

                            }

                        </ul>

                }

                <button
                    className="btn btn-outline-warning mt-3"
                    onClick={loadHistory}
                >

                    Refresh

                </button>

            </div>

        </div>

    );

}

export default HistoryCard;