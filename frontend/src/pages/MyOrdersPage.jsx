import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getMyOrders } from "../api/orders";

const STATUS_BADGES = {
  PENDING: { label: "İşleniyor", color: "bg-brand-50 text-brand-700 border-brand-200", dot: "bg-brand-500" },
  CONFIRMED: { label: "Onaylandı", color: "bg-green-50 text-green-700 border-green-200", dot: "bg-green-500" },
  CANCELLED: { label: "İptal", color: "bg-red-50 text-red-700 border-red-200", dot: "bg-red-500" },
};

export default function MyOrdersPage() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    getMyOrders()
      .then(setOrders)
      .catch((err) => setError(err.response?.data?.message || "Siparişler yüklenemedi"))
      .finally(() => setLoading(false));
  }, []);

  const formatPrice = (n) => Number(n).toLocaleString("tr-TR", {
    style: "currency", currency: "TRY"
  });

  if (loading) return (
    <div className="max-w-5xl mx-auto px-4 py-8">
      <div className="space-y-3">
        {[...Array(3)].map((_, i) => (
          <div key={i} className="bg-white rounded-2xl border border-gray-200 p-4 animate-pulse">
            <div className="h-4 bg-gray-100 rounded w-1/3 mb-2"></div>
            <div className="h-3 bg-gray-100 rounded w-2/3"></div>
          </div>
        ))}
      </div>
    </div>
  );
  if (error) return <div className="p-8 text-center text-red-600">{error}</div>;

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8 animate-fade-in">
      <div className="mb-6">
        <h1 className="text-3xl font-extrabold tracking-tight">Siparişlerim</h1>
        {orders.length > 0 && (
          <p className="text-sm text-gray-500 mt-1">{orders.length} sipariş</p>
        )}
      </div>

      {orders.length === 0 ? (
        <div className="text-center py-16 bg-white rounded-2xl border border-gray-200">
          <svg className="w-16 h-16 mx-auto text-brand-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5}
              d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
          </svg>
          <p className="text-gray-500 mb-4">Henüz siparişiniz yok</p>
          <Link to="/" className="inline-block px-6 py-3 bg-brand-600 text-white rounded-xl hover:bg-brand-700 transition font-bold">
            Alışverişe Başla
          </Link>
        </div>
      ) : (
        <div className="space-y-3">
          {orders.map((order) => {
            const s = STATUS_BADGES[order.status] || STATUS_BADGES.PENDING;
            return (
              <Link key={order.id} to={`/orders/${order.id}`}
                className="block bg-white rounded-2xl border border-gray-200 p-5 hover:border-brand-300 hover:shadow-md hover:shadow-brand-600/10 transition-all group">
                <div className="flex items-center justify-between gap-4">
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 mb-1.5 flex-wrap">
                      <p className="font-extrabold">Sipariş #{order.id}</p>
                      <span className={`inline-flex items-center gap-1.5 text-xs px-2 py-0.5 rounded-full font-bold border ${s.color}`}>
                        <span className={`w-1.5 h-1.5 rounded-full ${s.dot} ${order.status === "PENDING" ? "animate-pulse" : ""}`}></span>
                        {s.label}
                      </span>
                    </div>
                    <p className="text-sm text-gray-600 truncate">
                      {order.items?.length} ürün · {new Date(order.createdAt).toLocaleString("tr-TR", {
                        day: "2-digit", month: "short", hour: "2-digit", minute: "2-digit"
                      })}
                    </p>
                    <p className="text-[10px] text-gray-400 font-mono mt-1 uppercase tracking-wider">
                      Durum kodu: {order.sagaStatus}
                    </p>
                  </div>
                  <div className="text-right">
                    <p className="font-extrabold text-lg">{formatPrice(order.totalAmount)}</p>
                    <p className="text-xs text-brand-600 group-hover:underline mt-1 font-bold">
                      Detay →
                    </p>
                  </div>
                </div>
              </Link>
            );
          })}
        </div>
      )}
    </div>
  );
}
