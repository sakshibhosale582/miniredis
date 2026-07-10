import { useState } from "react";
import api from "../services/api";
import {
  BsDatabaseAdd,
  BsSearch,
  BsTrash,
  BsArrowClockwise
} from "react-icons/bs";

function CacheForm() {
  const [key, setKey] = useState("");
  const [value, setValue] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const store = async () => {
    if (!key || !value) {
      setMessage("Please enter Key and Value.");
      return;
    }

    try {
      setLoading(true);

      const response = await api.post("/set", {
        key,
        value,
      });

      setMessage(response.data.message);
    } catch (error) {
      setMessage(error.response?.data?.message || "Store failed");
    } finally {
      setLoading(false);
    }
  };

  const get = async () => {
    if (!key) {
      setMessage("Enter a Key.");
      return;
    }

    try {
      setLoading(true);

      const response = await api.get(`/get/${key}`);

      setValue(response.data.data);
      setMessage(response.data.message);
    } catch (error) {
      setValue("");
      setMessage(error.response?.data?.message || "Key not found");
    } finally {
      setLoading(false);
    }
  };

  const remove = async () => {
    if (!key) {
      setMessage("Enter a Key.");
      return;
    }

    try {
      setLoading(true);

      const response = await api.delete(`/delete/${key}`);

      setMessage(response.data.message);
      setValue("");
    } catch (error) {
      setMessage(error.response?.data?.message || "Delete failed");
    } finally {
      setLoading(false);
    }
  };

  const clear = async () => {
    try {
      setLoading(true);

      const response = await api.post("/clear");

      setKey("");
      setValue("");

      setMessage(response.data.message);
    } catch (error) {
      setMessage(error.response?.data?.message || "Clear failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="card shadow-lg border-0">

      <div className="card-header bg-primary text-white">
        <h4 className="mb-0">
          Cache Operations
        </h4>
      </div>

      <div className="card-body">

        <div className="mb-3">
          <label className="form-label fw-semibold">
            Key
          </label>

          <input
            className="form-control"
            placeholder="Enter Key"
            value={key}
            onChange={(e) => setKey(e.target.value)}
          />
        </div>

        <div className="mb-4">
          <label className="form-label fw-semibold">
            Value
          </label>

          <input
            className="form-control"
            placeholder="Enter Value"
            value={value}
            onChange={(e) => setValue(e.target.value)}
          />
        </div>

        <div className="d-grid gap-2">

          <button
            className="btn btn-success"
            onClick={store}
            disabled={loading}
          >
            <BsDatabaseAdd className="me-2" />
            Store
          </button>

          <button
            className="btn btn-primary"
            onClick={get}
            disabled={loading}
          >
            <BsSearch className="me-2" />
            Get
          </button>

          <button
            className="btn btn-danger"
            onClick={remove}
            disabled={loading}
          >
            <BsTrash className="me-2" />
            Delete
          </button>

          <button
            className="btn btn-warning"
            onClick={clear}
            disabled={loading}
          >
            <BsArrowClockwise className="me-2" />
            Clear Cache
          </button>

        </div>

        {message && (
          <div className="alert alert-info mt-4 mb-0">
            {message}
          </div>
        )}

      </div>

    </div>
  );
}

export default CacheForm;