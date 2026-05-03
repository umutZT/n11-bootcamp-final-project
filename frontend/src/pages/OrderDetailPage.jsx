import { useEffect, useState, useRef } from "react";
import { useParams, Link } from "react-router-dom";
import { getOrder } from "../api/orders";
import SagaStep from "../components/SagaStep";

function computeStepStates(order) {
  if (!order) return {};
  const s = order.sagaStatus;
  const states = {
    created: "DONE",
    stockReserve: "PENDING",
    paymentProcess: "PENDING",
    stockConfirm: "PENDING",
    compensation: "SKIPPED",
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
      states.stockReserve = "FAILED"; states.paymentProcess = "SKIPPED"; states.stockConfirm = "SKIPPED"; break;
    case "COMPENSATING_STOCK":
      states.stockReserve = "DONE"; states.paymentProcess = "FAILED";
      states.stockConfirm = "SKIPPED"; states.compensation = "ACTIVE"; break;
    case "COMPENSATED":
      states.stockReserve = "DONE"; states.paymentProcess = "FAILED";
      states.stockConfirm = "SKIPPED"; states.compensation = "DONE"; break;
    default: break;
  }
  return states;
}

export default function OrderDetailPage() {
  const { id } = useParams();
  const [order, setOrder] = useState(null);
  const [error, setError] = useState("");
  const pollingRef = useRef(null);

  const fetchOrder = async () => {
    try {
      const data = await getOrder(id);
      setOrder(data);
      const terminal = ["CONFIRMED", "CANCELLED"].includes(data.status);
      if (terminal && pollingRef.current) {
        clearInterval(pollingRef.current);
        pollingRef.current = null;
      }
    } catch (err) {
      setError(err.response?.data?.message || "Sipariş yüklenemedi");
    }
  };

  useEffect(() => {
    fetchOrder();
    pollingRef.current = setInterval(fetchOrder, 1000);
    return () => { if (pollingRef.current) clearInterval(pollingRef.current); };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  if (error) return (
    <div className="max-w-3xl mx-auto px-4 py-16 text-center">
      <div className="p-6 bg-red-50 border border-red-200 rounded-2xl text-red-700">{error}</div>
    </div>
  );
  if (!order) return (
    <div className="max-w-3xl mx-auto px-4 py-16 text-center text-gray-500 animate-pulse">
      Yükleniyor...
    </div>
  );

  const states = computeStepStates(order);
  const formatPrice = (n) => Number(n).toLocaleString("tr-TR", {
    style: "currency", currency: "TRY"
  });

  const isProcessing = order.status === "PENDING";
  const isConfirmed = order.status === "CONFIRMED";
  const isCancelled = order.status === "CANCELLED";

  const showCompensation = states.compensation !== "SKIPPED";

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-5 animate-fade-in">
      <div>
        <Link to="/orders"
          className="inline-flex items-center gap-1 text-sm text-gray-600 hover:text-brand-600 transition font-semibold">
          <svg className="w-4 h-4" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" d="M15 19l-7-7 7-7" />
          </svg>
          Siparişlerime dön
        </Link>
        <h1 className="text-3xl font-extrabold tracking-tight mt-2">Sipariş #{order.id}</h1>
      </div>

      <div className="bg-white rounded-2xl border border-gray-200 p-6 shadow-sm">
        <div className="flex items-center justify-between mb-5">
          <div>
            <h2 className="text-lg font-extrabold">Sipariş Süreci</h2>
            <p className="text-xs text-gray-500 mt-0.5">Adım adım sipariş durumu</p>
          </div>
          {isProcessing && (
            <div className="flex items-center gap-2 px-3 py-1 bg-brand-50 border border-brand-200 rounded-full">
              <div className="w-2 h-2 bg-brand-500 rounded-full animate-pulse"></div>
              <span className="text-xs font-bold text-brand-700">Canlı</span>
            </div>
          )}
        </div>

        <div className="space-y-0">
          <SagaStep label="Sipariş oluşturuldu" status={states.created}
            detail={`#${order.id} — ${formatPrice(order.totalAmount)}`} />
          <SagaStep label="Stok rezervasyonu" status={states.stockReserve}
            detail={
              states.stockReserve === "FAILED" ? order.failureReason :
              states.stockReserve === "ACTIVE" ? "Ürünler kontrol ediliyor..." :
              states.stockReserve === "DONE" ? "Ürünler ayrıldı" :
              "Stok kontrolü bekleniyor"
            } />
          <SagaStep label="Ödeme işlemi" status={states.paymentProcess}
            detail={
              states.paymentProcess === "FAILED" ? order.failureReason :
              states.paymentProcess === "ACTIVE" ? "Ödemeniz işleniyor..." :
              states.paymentProcess === "DONE" ? "Ödeme alındı" :
              "Ödeme bekleniyor"
            } />
          <SagaStep label="Stok onayı" status={states.stockConfirm}
            detail={
              states.stockConfirm === "DONE" ? "Sipariş hazırlanıyor" :
              states.stockConfirm === "ACTIVE" ? "Onaylanıyor..." :
              "Ödeme sonrası kesinleştirilir"
            }
            isLast={!showCompensation} />
          {showCompensation && (
            <SagaStep label="Sipariş iptali" status={states.compensation}
              detail={
                states.compensation === "DONE" ? "Sipariş iptal edildi, stok geri yüklendi" :
                states.compensation === "ACTIVE" ? "İptal işlemi sürüyor..." :
                "İptal hazırlanıyor"
              }
              isLast={true} />
          )}
        </div>

        <div className="mt-6 pt-5 border-t border-gray-100">
          {isConfirmed && (
            <div className="p-4 bg-green-50 border border-green-200 rounded-xl flex items-start gap-3 animate-fade-in">
              <div className="w-8 h-8 bg-green-500 rounded-full flex items-center justify-center text-white flex-shrink-0">
                <svg className="w-5 h-5" fill="none" stroke="currentColor" strokeWidth="3" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
                </svg>
              </div>
              <div>
                <p className="font-bold text-green-900">Siparişiniz onaylandı</p>
                <p className="text-sm text-green-700 mt-0.5">Ödeme alındı, stok kesinleşti.</p>
              </div>
            </div>
          )}
          {isCancelled && (
            <div className="p-4 bg-red-50 border border-red-200 rounded-xl flex items-start gap-3 animate-fade-in">
              <div className="w-8 h-8 bg-red-500 rounded-full flex items-center justify-center text-white flex-shrink-0">
                <svg className="w-5 h-5" fill="none" stroke="currentColor" strokeWidth="3" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </div>
              <div>
                <p className="font-bold text-red-900">Siparişiniz iptal edildi</p>
                <p className="text-sm text-red-700 mt-0.5">{order.failureReason || "Bir hata oluştu."}</p>
              </div>
            </div>
          )}
          {isProcessing && (
            <div className="p-4 bg-brand-50 border border-brand-200 rounded-xl flex items-start gap-3">
              <div className="w-8 h-8 bg-brand-500 rounded-full flex items-center justify-center text-white flex-shrink-0">
                <svg className="w-4 h-4 animate-spin" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/>
                  <path className="opacity-75" fill="currentColor"
                    d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"/>
                </svg>
              </div>
              <div>
                <p className="font-bold text-brand-900">Siparişiniz işleniyor</p>
                <p className="text-sm text-brand-700 mt-0.5">
                  Süreç tamamlandığında size bilgi verilecektir.
                </p>
              </div>
            </div>
          )}
        </div>
      </div>

      <div className="bg-white rounded-2xl border border-gray-200 p-6 shadow-sm">
        <h2 className="text-lg font-extrabold mb-4">Ürünler</h2>
        <div className="space-y-3">
          {order.items?.map((item) => (
            <div key={item.id} className="flex justify-between items-center py-2 border-b border-gray-100 last:border-0">
              <div className="flex-1 min-w-0">
                <p className="font-semibold truncate">{item.productName}</p>
                <p className="text-sm text-gray-500">{item.quantity} adet × {formatPrice(item.unitPrice)}</p>
              </div>
              <p className="font-extrabold whitespace-nowrap">{formatPrice(item.subtotal)}</p>
            </div>
          ))}
          <div className="flex justify-between pt-3 text-lg font-extrabold">
            <span>Toplam</span>
            <span className="bg-gradient-to-r from-brand-600 to-accent-500 bg-clip-text text-transparent">
              {formatPrice(order.totalAmount)}
            </span>
          </div>
        </div>
      </div>

      <details className="bg-gray-50 rounded-2xl border border-gray-200 overflow-hidden">
        <summary className="px-4 py-3 text-sm font-semibold text-gray-700 cursor-pointer hover:bg-gray-100 transition select-none">
          🛠️ Teknik detaylar (demo için)
        </summary>
        <div className="px-4 pb-4 text-xs text-gray-600 font-mono space-y-1">
          <p>Order Status: <span className="font-bold text-gray-900">{order.status}</span></p>
          <p>Saga Status: <span className="font-bold text-gray-900">{order.sagaStatus}</span></p>
          <p>Created: {new Date(order.createdAt).toLocaleString("tr-TR")}</p>
          <p>Updated: {new Date(order.updatedAt).toLocaleString("tr-TR")}</p>
          {order.failureReason && (
            <p>Failure Reason: <span className="font-bold text-red-600">{order.failureReason}</span></p>
          )}
        </div>
      </details>
    </div>
  );
}
