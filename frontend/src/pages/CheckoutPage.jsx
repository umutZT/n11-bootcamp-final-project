import { useState } from "react";
import { useNavigate, Navigate } from "react-router-dom";
import { useCart } from "../context/CartContext";
import { createOrder } from "../api/orders";
import OrderProcessingModal from "../components/OrderProcessingModal";

const TEST_CARDS = {
  success: {
    label: "Onaylanan Test Kartı",
    cardHolderName: "Test User",
    cardNumber: "5528790000000008",
    expireMonth: "12", expireYear: "2030", cvc: "123",
  },
  fail: {
    label: "Reddedilen Test Kartı",
    cardHolderName: "Test User",
    cardNumber: "4111111111111129",
    expireMonth: "12", expireYear: "2030", cvc: "123",
  },
};

export default function CheckoutPage() {
  const { items, total, clearCart } = useCart();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    cardHolderName: "", cardNumber: "", expireMonth: "", expireYear: "", cvc: "",
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [activeOrderId, setActiveOrderId] = useState(null);

  if (items.length === 0 && !activeOrderId) return <Navigate to="/cart" replace />;

  const formatPrice = (n) => Number(n).toLocaleString("tr-TR", {
    style: "currency", currency: "TRY"
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    let cleaned = value;
    if (name === "cardNumber") cleaned = value.replace(/\D/g, "").slice(0, 19);
    if (name === "cvc") cleaned = value.replace(/\D/g, "").slice(0, 4);
    if (name === "expireMonth") cleaned = value.replace(/\D/g, "").slice(0, 2);
    if (name === "expireYear") cleaned = value.replace(/\D/g, "").slice(0, 4);
    setForm({ ...form, [name]: cleaned });
  };

  const fillTestCard = (key) => {
    const card = TEST_CARDS[key];
    setForm({
      cardHolderName: card.cardHolderName, cardNumber: card.cardNumber,
      expireMonth: card.expireMonth, expireYear: card.expireYear, cvc: card.cvc,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const payload = {
        items: items.map((i) => ({ productId: i.productId, quantity: i.quantity })),
        ...form,
      };
      const order = await createOrder(payload);
      clearCart();
      setActiveOrderId(order.id);
    } catch (err) {
      setError(err.response?.data?.message || "Sipariş oluşturulamadı");
    } finally {
      setLoading(false);
    }
  };

  const displayCardNumber = form.cardNumber.replace(/(\d{4})(?=\d)/g, "$1 ");

  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8 animate-fade-in">
      <h1 className="text-3xl font-extrabold tracking-tight mb-6">Ödeme</h1>

      <form onSubmit={handleSubmit} className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 space-y-4">
          <div className="bg-gradient-to-br from-brand-50 to-accent-50 border border-brand-200 rounded-2xl p-4">
            <div className="flex items-start gap-3">
              <div className="w-10 h-10 bg-gradient-to-br from-brand-600 to-accent-500 rounded-xl flex items-center justify-center flex-shrink-0 text-white text-lg">
                💳
              </div>
              <div className="flex-1">
                <p className="text-sm font-bold text-brand-900 mb-1">Test Kartları</p>
                <p className="text-xs text-brand-700 mb-3">
                  Hızlı test için kart bilgilerini otomatik doldurabilirsiniz
                </p>
                <div className="flex gap-2">
                  <button type="button" onClick={() => fillTestCard("success")}
                    className="flex-1 px-3 py-2 bg-white hover:bg-green-50 text-green-700 rounded-lg text-xs font-bold transition border border-green-200 shadow-sm">
                    ✓ {TEST_CARDS.success.label}
                  </button>
                  <button type="button" onClick={() => fillTestCard("fail")}
                    className="flex-1 px-3 py-2 bg-white hover:bg-red-50 text-red-700 rounded-lg text-xs font-bold transition border border-red-200 shadow-sm">
                    ✗ {TEST_CARDS.fail.label}
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-2xl border border-gray-200 p-6 shadow-sm">
            <div className="flex items-center justify-between mb-5">
              <h2 className="text-lg font-extrabold">Kart Bilgileri</h2>
              <div className="flex gap-1">
                <div className="w-8 h-5 bg-gradient-to-r from-blue-500 to-blue-700 rounded text-white text-[8px] flex items-center justify-center font-bold">VISA</div>
                <div className="w-8 h-5 bg-gradient-to-r from-orange-500 to-red-600 rounded text-white text-[8px] flex items-center justify-center font-bold">MC</div>
              </div>
            </div>

            <div className="space-y-4">
              <div>
                <label className="block text-xs font-bold uppercase tracking-wider text-gray-600 mb-1.5">Kart Üzerindeki İsim</label>
                <input type="text" name="cardHolderName" value={form.cardHolderName} onChange={handleChange}
                  className="w-full px-4 py-2.5 border border-gray-300 rounded-xl focus:ring-2 focus:ring-brand-500 focus:border-transparent transition"
                  required minLength={3} maxLength={100} placeholder="JOHN DOE" />
              </div>
              <div>
                <label className="block text-xs font-bold uppercase tracking-wider text-gray-600 mb-1.5">Kart Numarası</label>
                <input type="text" name="cardNumber" value={displayCardNumber}
                  onChange={(e) => handleChange({ target: { name: "cardNumber", value: e.target.value }})}
                  className="w-full px-4 py-2.5 border border-gray-300 rounded-xl focus:ring-2 focus:ring-brand-500 focus:border-transparent font-mono tracking-wider transition"
                  required pattern="[\d ]+" placeholder="•••• •••• •••• ••••" />
              </div>
              <div className="grid grid-cols-3 gap-3">
                <div>
                  <label className="block text-xs font-bold uppercase tracking-wider text-gray-600 mb-1.5">Ay</label>
                  <input type="text" name="expireMonth" value={form.expireMonth} onChange={handleChange}
                    className="w-full px-4 py-2.5 border border-gray-300 rounded-xl focus:ring-2 focus:ring-brand-500 transition"
                    required pattern="(0[1-9]|1[0-2])" placeholder="MM" />
                </div>
                <div>
                  <label className="block text-xs font-bold uppercase tracking-wider text-gray-600 mb-1.5">Yıl</label>
                  <input type="text" name="expireYear" value={form.expireYear} onChange={handleChange}
                    className="w-full px-4 py-2.5 border border-gray-300 rounded-xl focus:ring-2 focus:ring-brand-500 transition"
                    required pattern="\d{4}" placeholder="YYYY" />
                </div>
                <div>
                  <label className="block text-xs font-bold uppercase tracking-wider text-gray-600 mb-1.5">CVC</label>
                  <input type="text" name="cvc" value={form.cvc} onChange={handleChange}
                    className="w-full px-4 py-2.5 border border-gray-300 rounded-xl focus:ring-2 focus:ring-brand-500 transition"
                    required pattern="\d{3,4}" placeholder="123" />
                </div>
              </div>
            </div>

            {error && (
              <div className="mt-4 p-3 bg-red-50 border border-red-200 rounded-xl text-sm text-red-700 animate-fade-in">
                {error}
              </div>
            )}
          </div>

          <div className="flex items-center gap-2 text-xs text-gray-500 px-2">
            <svg className="w-4 h-4" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round"
                d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
            </svg>
            Kart bilgileriniz Iyzico üzerinden işlenir, hiçbir şekilde veritabanımızda saklanmaz.
          </div>
        </div>

        <div className="lg:col-span-1">
          <div className="bg-white rounded-2xl border border-gray-200 p-6 sticky top-20 shadow-sm">
            <h2 className="text-lg font-extrabold mb-4">Sipariş Özeti</h2>
            <div className="space-y-2 mb-4 pb-4 border-b text-sm">
              {items.map((item) => (
                <div key={item.productId} className="flex justify-between gap-2">
                  <span className="text-gray-600 truncate">{item.name} × {item.quantity}</span>
                  <span className="font-medium whitespace-nowrap">{formatPrice(item.price * item.quantity)}</span>
                </div>
              ))}
            </div>
            <div className="flex justify-between items-end mb-6">
              <span className="text-gray-600">Toplam</span>
              <span className="text-2xl font-extrabold bg-gradient-to-r from-brand-600 to-accent-500 bg-clip-text text-transparent">
                {formatPrice(total)}
              </span>
            </div>
            <button type="submit" disabled={loading}
              className="w-full py-3 bg-brand-600 text-white rounded-xl hover:bg-brand-700 disabled:bg-gray-300 active:scale-[0.99] transition-all font-bold shadow-sm hover:shadow-md hover:shadow-brand-600/30">
              {loading ? (
                <span className="flex items-center justify-center gap-2">
                  <svg className="w-4 h-4 animate-spin" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/>
                    <path className="opacity-75" fill="currentColor"
                      d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"/>
                  </svg>
                  Sipariş oluşturuluyor...
                </span>
              ) : (
                `${formatPrice(total)} Öde`
              )}
            </button>
          </div>
        </div>
      </form>

      {activeOrderId && (
        <OrderProcessingModal
          orderId={activeOrderId}
          onClose={() => setActiveOrderId(null)}
        />
      )}
    </div>
  );
}
