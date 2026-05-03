import { Link, useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useCart } from "../context/CartContext";

function N11Logo() {
  return (
    <div className="flex items-center gap-2.5">
      <div className="relative w-10 h-10 rounded-xl bg-gradient-to-br from-brand-600 to-accent-500 flex items-center justify-center text-white font-black text-lg shadow-lg shadow-brand-600/30">
        n11
      </div>
      <div className="hidden sm:block">
        <div className="text-base font-extrabold text-gray-900 leading-tight tracking-tight">
          n11
        </div>
        <div className="text-[10px] font-semibold text-gray-500 uppercase tracking-widest -mt-0.5">
          Marketplace
        </div>
      </div>
    </div>
  );
}

export default function Navbar() {
  const { user, logout } = useAuth();
  const { itemCount } = useCart();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => { logout(); navigate("/login"); };

  const isActive = (path) => location.pathname === path;
  const linkClass = (path) =>
    `text-sm font-semibold transition relative ${
      isActive(path) ? "text-brand-600" : "text-gray-700 hover:text-brand-600"
    }`;

  return (
    <nav className="bg-white border-b border-gray-200 sticky top-0 z-30 shadow-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          <div className="flex items-center gap-8">
            <Link to="/" className="hover:opacity-80 transition">
              <N11Logo />
            </Link>
            <div className="hidden sm:flex gap-6">
              <Link to="/" className={linkClass("/")}>Ürünler</Link>
              {user && (
                <Link to="/orders" className={linkClass("/orders")}>Siparişlerim</Link>
              )}
            </div>
          </div>
          <div className="flex items-center gap-3">
            <Link
              to="/cart"
              className="relative p-2 rounded-lg text-gray-700 hover:bg-brand-50 hover:text-brand-600 transition"
              aria-label="Sepet"
            >
              <svg className="w-6 h-6" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round"
                  d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
              </svg>
              {itemCount > 0 && (
                <span className="absolute -top-0.5 -right-0.5 bg-accent-500 text-white text-[10px] rounded-full min-w-[20px] h-5 px-1 flex items-center justify-center font-bold ring-2 ring-white animate-fade-in">
                  {itemCount}
                </span>
              )}
            </Link>
            {user ? (
              <>
                <div className="hidden sm:flex items-center gap-2 px-3 py-1.5 bg-brand-50 rounded-lg">
                  <div className="w-7 h-7 bg-gradient-to-br from-brand-600 to-accent-500 text-white rounded-full flex items-center justify-center text-xs font-bold uppercase">
                    {user.username[0]}
                  </div>
                  <span className="text-sm font-semibold text-gray-700">{user.username}</span>
                </div>
                <button onClick={handleLogout}
                  className="px-3 py-2 text-sm font-semibold text-gray-700 hover:text-brand-600 hover:bg-brand-50 rounded-lg transition">
                  Çıkış
                </button>
              </>
            ) : (
              <>
                <Link to="/login"
                  className="px-4 py-2 text-sm font-semibold text-gray-700 hover:text-brand-600 transition">
                  Giriş
                </Link>
                <Link to="/signup"
                  className="px-4 py-2 text-sm font-bold bg-brand-600 text-white hover:bg-brand-700 rounded-lg transition shadow-sm hover:shadow-md hover:shadow-brand-600/30">
                  Kayıt Ol
                </Link>
              </>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}
