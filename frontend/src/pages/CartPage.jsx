import { Link, useNavigate } from "react-router-dom";
import { useCart } from "../context/CartContext";
import { useAuth } from "../context/AuthContext";

export default function CartPage() {
  const { items, removeItem, updateQuantity, total, clearCart } = useCart();
  const { user } = useAuth();
  const navigate = useNavigate();

  const formatPrice = (n) => Number(n).toLocaleString("tr-TR", {
    style: "currency", currency: "TRY"
  });

  const handleCheckout = () => {
    if (!user) navigate("/login", { state: { from: "/checkout" } });
    else navigate("/checkout");
  };

  if (items.length === 0) {
    return (
      <div className="max-w-3xl mx-auto px-4 py-20 text-center animate-fade-in">
        <div className="inline-flex items-center justify-center w-20 h-20 bg-brand-50 rounded-2xl mb-6">
          <svg className="w-10 h-10 text-brand-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5}
              d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
          </svg>
        </div>
        <h2 className="text-2xl font-extrabold mb-2">Sepetiniz boş</h2>
        <p className="text-gray-500 mb-6">Alışverişe başlamak için ürünleri inceleyin</p>
        <Link to="/" className="inline-block px-6 py-3 bg-brand-600 text-white rounded-xl hover:bg-brand-700 transition font-bold shadow-sm hover:shadow-md hover:shadow-brand-600/30">
          Ürünlere Git
        </Link>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8 animate-fade-in">
      <div className="mb-6">
        <h1 className="text-3xl font-extrabold tracking-tight">Sepetim</h1>
        <p className="text-sm text-gray-500 mt-1">{items.length} ürün</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 space-y-3">
          {items.map((item) => (
            <div key={item.productId} className="bg-white rounded-2xl border border-gray-200 p-4 flex items-center gap-4 hover:border-brand-300 transition">
              <div className="w-20 h-20 bg-gradient-to-br from-brand-50 to-accent-50 rounded-xl flex-shrink-0 flex items-center justify-center text-gray-300 overflow-hidden">
                {item.imageUrl ? (
                  <img src={item.imageUrl} alt={item.name} className="w-full h-full object-cover" />
                ) : (
                  <svg className="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5}
                      d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z" />
                  </svg>
                )}
              </div>
              <div className="flex-1 min-w-0">
                <h3 className="font-semibold truncate">{item.name}</h3>
                <p className="text-sm text-gray-500">{formatPrice(item.price)} / adet</p>
              </div>
              <div className="flex items-center gap-1 bg-gray-100 rounded-xl p-1">
                <button onClick={() => updateQuantity(item.productId, item.quantity - 1)}
                  className="w-8 h-8 rounded-lg hover:bg-white text-gray-700 font-bold transition">−</button>
                <span className="w-8 text-center font-bold text-sm">{item.quantity}</span>
                <button onClick={() => updateQuantity(item.productId, item.quantity + 1)}
                  disabled={item.quantity >= item.stock}
                  className="w-8 h-8 rounded-lg hover:bg-white text-gray-700 font-bold disabled:opacity-30 disabled:cursor-not-allowed transition">+</button>
              </div>
              <div className="text-right min-w-[100px]">
                <p className="font-extrabold">{formatPrice(item.price * item.quantity)}</p>
                <button onClick={() => removeItem(item.productId)}
                  className="text-xs text-gray-500 hover:text-red-600 transition mt-1 font-semibold">
                  Kaldır
                </button>
              </div>
            </div>
          ))}
          <button onClick={clearCart}
            className="text-sm text-gray-500 hover:text-red-600 transition font-semibold">
            Sepeti temizle
          </button>
        </div>

        <div className="lg:col-span-1">
          <div className="bg-white rounded-2xl border border-gray-200 p-6 sticky top-20 shadow-sm">
            <h2 className="text-lg font-extrabold mb-4">Sipariş Özeti</h2>
            <div className="space-y-2 mb-4 pb-4 border-b border-gray-200 text-sm">
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
            <button onClick={handleCheckout}
              className="w-full py-3 bg-brand-600 text-white rounded-xl hover:bg-brand-700 transition font-bold shadow-sm hover:shadow-md hover:shadow-brand-600/30">
              Siparişi Tamamla
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
