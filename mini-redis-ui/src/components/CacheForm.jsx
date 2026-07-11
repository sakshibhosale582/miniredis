import { useState } from "react";
import api from "../services/api";
import { toast } from "react-toastify";

import {
  BsDatabaseAdd,
  BsSearch,
  BsTrash,
  BsArrowClockwise
} from "react-icons/bs";

function CacheForm() {

  const [key, setKey] = useState("");
  const [value, setValue] = useState("");
  const [loading, setLoading] = useState(false);

  const store = async () => {

    if (!key || !value) {
      toast.warning("Please enter both Key and Value");
      return;
    }

    try {

      setLoading(true);

      const res = await api.post("/set", {
        key,
        value
      });

      toast.success(res.data.message);

      setKey("");
      setValue("");

    } catch (err) {

      toast.error(err.response?.data?.message || "Store failed");

    } finally {

      setLoading(false);

    }

  };

  const get = async () => {

    if (!key) {
      toast.warning("Please enter a key");
      return;
    }

    try {

      setLoading(true);

      const res = await api.get(`/get/${key}`);

      setValue(res.data.data);

      toast.success("Value retrieved");

    } catch (err) {

      toast.error(err.response?.data?.message || "Key not found");

    } finally {

      setLoading(false);

    }

  };

  const remove = async () => {

    if (!key) {
      toast.warning("Please enter a key");
      return;
    }

    try {

      setLoading(true);

      const res = await api.delete(`/delete/${key}`);

      toast.success(res.data.message);

      setValue("");

    } catch (err) {

      toast.error(err.response?.data?.message || "Delete failed");

    } finally {

      setLoading(false);

    }

  };

  const clear = async () => {

    try {

      setLoading(true);

      const res = await api.post("/clear");

      setKey("");
      setValue("");

      toast.success(res.data.message);

    } catch (err) {

      toast.error(err.response?.data?.message || "Clear failed");

    } finally {

      setLoading(false);

    }

  };

  return (

    <div className="card fade-in">

      <div className="card-header bg-dark text-white">

        Cache Operations

      </div>

      <div className="card-body">

        <div className="mb-3">

          <label className="form-label fw-semibold">

            Key

          </label>

          <input
            className="form-control"
            placeholder="Enter Cache Key"
            value={key}
            onChange={(e)=>setKey(e.target.value)}
          />

        </div>

        <div className="mb-4">

          <label className="form-label fw-semibold">

            Value

          </label>

          <input
            className="form-control"
            placeholder="Enter Cache Value"
            value={value}
            onChange={(e)=>setValue(e.target.value)}
          />

        </div>

        <div className="row g-2">

          <div className="col-6">

            <button
              className="btn btn-success w-100"
              disabled={loading}
              onClick={store}
            >
              {loading ? (
                <span
                  className="spinner-border spinner-border-sm me-2"
                ></span>
              ) : (
                <BsDatabaseAdd className="me-2"/>
              )}

              Store

            </button>

          </div>

          <div className="col-6">

            <button
              className="btn btn-primary w-100"
              disabled={loading}
              onClick={get}
            >
              <BsSearch className="me-2"/>

              Get

            </button>

          </div>

          <div className="col-6">

            <button
              className="btn btn-danger w-100"
              disabled={loading}
              onClick={remove}
            >
              <BsTrash className="me-2"/>

              Delete

            </button>

          </div>

          <div className="col-6">

            <button
              className="btn btn-warning w-100"
              disabled={loading}
              onClick={clear}
            >
              <BsArrowClockwise className="me-2"/>

              Clear

            </button>

          </div>

        </div>

      </div>

    </div>

  );

}

export default CacheForm;