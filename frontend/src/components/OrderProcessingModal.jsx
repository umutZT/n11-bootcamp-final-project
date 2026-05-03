import { useEffect, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { getOrder } from "../api/orders";

const STEPS = [
  { key: "created",        label: "Sipariş oluşturuldu" },
  { key: "stockReserve",   label: "Stok rezervasyonu" },
  { key: "paymentProcess", label: "Ödeme işlemi" },
  { key: "stockConfirm",   label: "Sipariş hazırlanıyor" },
];

function computeStepStates(order) {
  if (!order) return {};
  const s = order.sagaStatus;
  const states = {
    created: "DONE",
    stockReserve: "PENDING",
    paymentProcess: "PENDING",
    stockConfirm: "PENDING",
  };
  switch (s) {
    case "STARTED":
    case "STOCK_RESERVING":
      states.stockReserve = "ACTIVE"; break;
    case "STOCK_RESERVED":
      states.stockReserve = "DONE"; states.paymentProcess = "ACTIVE"; break;
    case "PAYMENT_PROCESSING":
      states.stockReserve = "DONE"; states.paymentProcess = "ACTIVE"; break;
    case "PAYMENT_COMPLETED":
      states.stockReserve = "DONE"; states.paymentProcess = "DONE"; states.stockConfirm = "ACTIVE"; break;
    case "STOCK_CONFIRMED":
      states.stockReserve = "DONE"; states.paymentProcess = "DONE"; states.stockConfirm = "DONE"; break;
    case "FAILED_AT_STOCK":
      states.stockReserve = "FAILED"; break;
    case "COMPENSATING_STOCK":
      states.stockReserve = "DONE"; states.paymentProcess = "FAILED"; break;
    case "COMPENSATED":
      states.stockReserve = "DONE"; states.paymentProcess = "FAILED"; break;
    default: break;
  }
  return states;
}

function MiniStep({ label, status }) {
  const isDone = status === "DONE";
  const isActive = status === "ACTIVE";
  const isFailed = status === "FAILED";
  const isPending = status === "PENDING" || !status;

  return (
    <div className="flex items-center gap-3 py-1.5">
      <div className="flex-shrink-0">
        {isDone && (
          <div className="w-5 h-5 rounded-full bg-green-500 flex items-center justify-center text-white shadow-sm">
            <svg className="w-3 h-3" fill="none" stroke="currentColor" strokeWidth="3" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
            </svg>
          </div>
        )}
        {isActive && (
          <div className="relative w-5 h-5 rounded-full border-2 border-brand-600 bg-white flex items-center justify-center">
            <div className="absolute inset-0 rounded-full bg-brand-100 animate-pulse-soft"></div>
            <div className="relative w-1.5 h-1.5 bg-brand-600 rounded-full"></div>
          </div>
        )}
        {isFailed && (
          <div className="w-5 h-5 rounded-full bg-red-500 flex items-center justify-center text-white shadow-sm">
            <svg className="w-3 h-3" fill="none" stroke="currentColor" strokeWidth="3" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </div>
        )}
        {isPending && (
          <div className="w-5 h-5 rounded-full border-2 border-gray-300 bg-white"></div>
        )}
      </div>
      <span className={`text-sm ${
        isDone ? "text-gray-900 font-semibold" :
        isActive ? "text-brand-600 font-bold" :
        isFailed ? "text-red-600 font-semibold" :
        "text-gray-400"
      }`}>
        {label}
      </span>
    </div>
  );
}

export default function OrderProcessingModal({ orderId, onClose }) {
  const [order, setOrder] = useState(null);
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const pollingRef = useRef(null);

  useEffect(() => {
    let cancelled = false;
    const fetchOrder = async () => {
      try {
        const data = await getOrder(orderId);
        if (cancelled) return;
        setOrder(data);
        if (["CONFIRMED", "CANCELLED"].includes(data.status) && pollingRef.current) {
          clearInterval(pollingRef.current);
          pollingRef.current = null;
        }
      } catch (err) {
        if (cancelled) return;
        setError(err.response?.data?.message || "Sipariş durumu alınamadı");
      }
    };
    fetchOrder();
    pollingRef.current = setInterval(fetchOrder, 1000);
    return () => {
      cancelled = true;
      if (pollingRef.current) clearInterval(pollingRef.current);
    };
  }, [orderId]);

  useEffect(() => {
    document.body.style.overflow = "hidden";
    return () => { document.body.style.overflow = ""; };
  }, []);

  const states = computeStepStates(order);
  const isProcessing = !order || order.status === "PENDING";
  const isConfirmed = order?.status === "CONFIRMED";
  const isCancelled = order?.status === "CANCELLED";

  const formatPrice = (n) => n != null
    ? Number(n).toLocaleString("tr-TR", { style: "currency", currency: "TRY" })
    : "";

  const handleViewOrder = () => {
    onClose();
    navigate(`/orders/${orderId}`);
  };

  const handleContinueShopping = () => {
    onClose();
    navigate("/");
  };

  const handleBackToCart = () => {
    onClose();
    navigate("/cart");
  };

  const handleRetry = () => {
    onClose();
    navigate("/checkout");
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 animate-fade-in">
      <div className="absolute inset-0 bg-black/60 backdrop-blur-sm"
           onClick={isProcessing ? undefined : onClose}
           aria-hidden="true" />

      <div className="relative bg-white rounded-3xl shadow-2xl w-full max-w-md overflow-hidden animate-slide-up">

        {isProcessing && (
          <div className="p-8">
            <div className="flex flex-col items-center text-center mb-6">
              <div className="relative w-16 h-16 mb-4">
                <div className="absolute inset-0 rounded-full bg-gradient-to-br from-brand-500 to-accent-500 animate-pulse-soft"></div>
                <div className="absolute inset-1 bg-white rounded-full flex items-center justify-center">
                  <svg className="w-8 h-8 text-brand-600 animate-spin" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/>
                    <path className="opacity-75" fill="currentColor"
                      d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"/>
                  </svg>
                </div>
              </div>
              <h2 className="text-xl font-extrabold text-gray-900 mb-1">
                Siparişiniz işleniyor
              </h2>
              <p className="text-sm text-gray-500">
                Lütfen bekleyiniz, bu işlem birkaç saniye sürebilir
              </p>
            </div>

            <div className="bg-gray-50 rounded-2xl p-4 space-y-1">
              {STEPS.map((step) => (
                <MiniStep key={step.key} label={step.label} status={states[step.key]} />
              ))}
            </div>

            {error && (
              <div className="mt-4 p-3 bg-red-50 border border-red-200 rounded-xl text-sm text-red-700">
                {error}
              </div>
            )}
          </div>
        )}

        {isConfirmed && (
          <div className="p-8 animate-fade-in">
            <div className="flex flex-col items-center text-center mb-6">
              <div className="w-20 h-20 rounded-full bg-gradient-to-br from-green-400 to-green-600 flex items-center justify-center mb-4 shadow-lg shadow-green-500/30">
                <svg className="w-10 h-10 text-white" fill="none" stroke="currentColor" strokeWidth="3" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
                </svg>
              </div>
              <h2 className="text-2xl font-extrabold text-gray-900 mb-1">
                Siparişiniz Alındı!
              </h2>
              <p className="text-sm text-gray-500">
                Sipariş #{order.id} başarıyla oluşturuldu
              </p>
              {order.totalAmount != null && (
                <p className="text-lg font-extrabold mt-2 bg-gradient-to-r from-brand-600 to-accent-500 bg-clip-text text-transparent">
                  {formatPrice(order.totalAmount)}
                </p>
              )}
            </div>

            <div className="bg-green-50 border border-green-200 rounded-xl p-4 mb-6">
              <p className="text-sm text-green-800 text-center">
                Ödemeniz alındı, ürünleriniz hazırlanıyor.
              </p>
            </div>

            <div className="flex flex-col sm:flex-row gap-2">
              <button onClick={handleContinueShopping}
                className="flex-1 py-2.5 bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-xl transition font-semibold">
                Alışverişe Devam
              </button>
              <button onClick={handleViewOrder}
                className="flex-1 py-2.5 bg-brand-600 hover:bg-brand-700 text-white rounded-xl transition font-bold shadow-sm hover:shadow-md hover:shadow-brand-600/30">
                Sipariş Detayı
              </button>
            </div>
          </div>
        )}

        {isCancelled && (
          <div className="p-8 animate-fade-in">
            <div className="flex flex-col items-center text-center mb-6">
              <div className="w-20 h-20 rounded-full bg-gradient-to-br from-red-400 to-red-600 flex items-center justify-center mb-4 shadow-lg shadow-red-500/30">
                <svg className="w-10 h-10 text-white" fill="none" stroke="currentColor" strokeWidth="3" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </div>
              <h2 className="text-2xl font-extrabold text-gray-900 mb-1">
                Sipariş Tamamlanamadı
              </h2>
              <p className="text-sm text-gray-500">
                Sipariş #{order.id} işlenemedi
              </p>
            </div>

            <div className="bg-red-50 border border-red-200 rounded-xl p-4 mb-6">
              <p className="text-sm text-red-800">
                {order.failureReason || "Ödeme işlemi sırasında bir hata oluştu. Lütfen tekrar deneyiniz."}
              </p>
            </div>

            <div className="flex flex-col sm:flex-row gap-2">
              <button onClick={handleBackToCart}
                className="flex-1 py-2.5 bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-xl transition font-semibold">
                Sepete Dön
              </button>
              <button onClick={handleRetry}
                className="flex-1 py-2.5 bg-brand-600 hover:bg-brand-700 text-white rounded-xl transition font-bold shadow-sm hover:shadow-md hover:shadow-brand-600/30">
                Tekrar Dene
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
