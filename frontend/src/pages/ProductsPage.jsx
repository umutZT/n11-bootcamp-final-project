import { useEffect, useState } from "react";
import { listProducts } from "../api/products";
import ProductCard from "../components/ProductCard";
import { useCart } from "../context/CartContext";

export default function ProductsPage() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [toast, setToast] = useState("");
  const { addItem } = useCart();

  useEffect(() => {
    listProducts(0, 20)
      .then((data) => setProducts(data.content || []))
      .catch((err) => setError(err.message || "Ürünler yüklenemedi"))
      .finally(() => setLoading(false));
  }, []);

  const handleAddToCart = (product) => {
    addItem(product);
    setToast(`"${product.name}" sepete eklendi`);
    setTimeout(() => setToast(""), 2200);
  };

  return (
    <div>
      <div className="relative bg-gradient-to-br from-brand-700 via-brand-600 to-accent-500 text-white overflow-hidden">
        <div className="absolute inset-0 opacity-20">
          <div className="absolute -top-20 -right-20 w-80 h-80 bg-accent-400 rounded-full blur-3xl"></div>
          <div className="absolute -bottom-20 -left-20 w-80 h-80 bg-brand-400 rounded-full blur-3xl"></div>
        </div>
        <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 sm:py-20">
          <div className="max-w-2xl animate-slide-up">
            <div className="inline-flex items-center gap-2 px-3 py-1 bg-white/15 backdrop-blur rounded-full text-xs font-bold mb-4 border border-white/20">
              <span className="w-2 h-2 bg-accent-300 rounded-full animate-pulse"></span>
              Hızlı teslimat · Güvenli ödeme
            </div>
            <h1 className="text-3xl sm:text-4xl lg:text-5xl font-black mb-3 leading-tight tracking-tight">
              Aradığınız her şey<br className="hidden sm:block" /> bir tık uzağınızda
            </h1>
            <p className="text-base sm:text-lg text-white/90 leading-relaxed">
              Elektronikten modaya, ev yaşamından spora binlerce ürün.
              Avantajlı fiyatlarla n11'de.
            </p>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-2xl font-extrabold tracking-tight">Tüm Ürünler</h2>
            {!loading && products.length > 0 && (
              <p className="text-sm text-gray-500 mt-1">{products.length} ürün listeleniyor</p>
            )}
          </div>
        </div>

        {toast && (
          <div className="fixed bottom-6 right-6 bg-gray-900 text-white px-4 py-3 rounded-xl shadow-2xl z-40 animate-slide-up flex items-center gap-2 border border-brand-500/30">
            <div className="w-5 h-5 bg-green-500 rounded-full flex items-center justify-center">
              <svg className="w-3 h-3 text-white" fill="none" stroke="currentColor" strokeWidth="3" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
              </svg>
            </div>
            <span className="text-sm font-medium">{toast}</span>
          </div>
        )}

        {loading && (
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
            {[...Array(8)].map((_, i) => (
              <div key={i} className="bg-white rounded-2xl border border-gray-200 overflow-hidden animate-pulse">
                <div className="aspect-square bg-gray-100"></div>
                <div className="p-4 space-y-3">
                  <div className="h-3 bg-gray-100 rounded w-1/3"></div>
                  <div className="h-4 bg-gray-100 rounded w-3/4"></div>
                  <div className="h-6 bg-gray-100 rounded w-1/2"></div>
                </div>
              </div>
            ))}
          </div>
        )}

        {error && (
          <div className="p-4 bg-red-50 border border-red-200 rounded-xl text-red-700">
            {error}
          </div>
        )}

        {!loading && !error && products.length === 0 && (
          <div className="text-center py-20 bg-white rounded-2xl border border-gray-200">
            <svg className="w-16 h-16 mx-auto text-gray-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5}
                d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z" />
            </svg>
            <p className="text-gray-500">Henüz ürün yok</p>
          </div>
        )}

        {!loading && products.length > 0 && (
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
            {products.map((p) => (
              <ProductCard key={p.id} product={p} onAddToCart={handleAddToCart} />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
