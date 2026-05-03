export default function ProductCard({ product, onAddToCart }) {
  const outOfStock = product.stock <= 0;
  const lowStock = product.stock > 0 && product.stock <= 5;

  return (
    <div className="group bg-white rounded-2xl border border-gray-200 overflow-hidden hover:border-brand-300 hover:shadow-xl hover:shadow-brand-600/10 transition-all duration-300 hover:-translate-y-0.5 animate-fade-in">
      <div className="aspect-square bg-gradient-to-br from-brand-50 via-white to-accent-50 flex items-center justify-center text-gray-300 relative overflow-hidden">
        {product.imageUrl ? (
          <img src={product.imageUrl} alt={product.name}
            className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500" />
        ) : (
          <svg className="w-20 h-20" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5}
              d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z" />
          </svg>
        )}
        {lowStock && (
          <div className="absolute top-2 left-2 bg-accent-500 text-white text-[10px] font-bold px-2 py-0.5 rounded-full shadow-sm">
            Son {product.stock}
          </div>
        )}
        {outOfStock && (
          <div className="absolute inset-0 bg-white/70 backdrop-blur-sm flex items-center justify-center">
            <span className="bg-gray-900 text-white text-xs font-bold px-3 py-1 rounded-full">
              Stokta yok
            </span>
          </div>
        )}
      </div>
      <div className="p-4 space-y-3">
        <div>
          <p className="text-[10px] font-bold tracking-wider text-accent-500 uppercase mb-1">
            {product.category}
          </p>
          <h3 className="font-semibold text-gray-900 line-clamp-2 min-h-[2.5rem] leading-tight">
            {product.name}
          </h3>
        </div>
        <div className="flex items-end justify-between gap-2">
          <div>
            <p className="text-xl font-extrabold text-gray-900 tracking-tight">
              {Number(product.price).toLocaleString("tr-TR", {
                style: "currency", currency: "TRY", minimumFractionDigits: 2
              })}
            </p>
          </div>
          <button
            disabled={outOfStock}
            onClick={() => onAddToCart?.(product)}
            className="px-3 py-2 text-xs font-bold bg-brand-600 text-white rounded-lg hover:bg-brand-700 active:scale-95 disabled:bg-gray-300 disabled:cursor-not-allowed transition-all whitespace-nowrap shadow-sm hover:shadow-md hover:shadow-brand-600/40"
          >
            Sepete Ekle
          </button>
        </div>
      </div>
    </div>
  );
}
