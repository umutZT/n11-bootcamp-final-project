import { useState } from "react";
import { useNavigate, Link, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const from = location.state?.from || "/";

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await login(username, password);
      navigate(from, { replace: true });
    } catch (err) {
      setError(err.response?.data?.message || "Giriş başarısız. Bilgileri kontrol edin.");
    } finally {
      setLoading(false);
    }
  };

  const fillTestUser = (u, p) => { setUsername(u); setPassword(p); };

  return (
    <div className="min-h-[calc(100vh-4rem)] flex items-center justify-center px-4 py-12 bg-gradient-to-br from-brand-50 via-white to-accent-50">
      <div className="w-full max-w-md animate-slide-up">
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-14 h-14 bg-gradient-to-br from-brand-600 to-accent-500 rounded-2xl text-white font-black text-xl mb-4 shadow-xl shadow-brand-600/30">
            n11
          </div>
          <h1 className="text-2xl font-extrabold tracking-tight">Tekrar hoş geldiniz</h1>
          <p className="text-gray-500 text-sm mt-1">Hesabınıza giriş yapın</p>
        </div>

        <div className="bg-white rounded-2xl border border-gray-200 p-8 shadow-sm">
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-semibold mb-1.5 text-gray-700">Kullanıcı Adı</label>
              <input type="text" value={username} onChange={(e) => setUsername(e.target.value)}
                className="w-full px-4 py-2.5 border border-gray-300 rounded-xl focus:ring-2 focus:ring-brand-500 focus:border-transparent transition"
                required autoFocus />
            </div>
            <div>
              <label className="block text-sm font-semibold mb-1.5 text-gray-700">Şifre</label>
              <input type="password" value={password} onChange={(e) => setPassword(e.target.value)}
                className="w-full px-4 py-2.5 border border-gray-300 rounded-xl focus:ring-2 focus:ring-brand-500 focus:border-transparent transition"
                required />
            </div>
            {error && (
              <div className="p-3 bg-red-50 border border-red-200 rounded-xl text-sm text-red-700 animate-fade-in">
                {error}
              </div>
            )}
            <button type="submit" disabled={loading}
              className="w-full py-2.5 bg-brand-600 text-white rounded-xl hover:bg-brand-700 disabled:bg-gray-300 active:scale-[0.99] transition-all font-bold shadow-sm hover:shadow-md hover:shadow-brand-600/30">
              {loading ? "Giriş yapılıyor..." : "Giriş Yap"}
            </button>
          </form>
          <p className="mt-5 text-sm text-center text-gray-600">
            Hesabınız yok mu?{" "}
            <Link to="/signup" className="text-brand-600 hover:text-accent-500 hover:underline font-bold transition">
              Kayıt Ol
            </Link>
          </p>
        </div>

        <div className="mt-4 p-4 bg-gradient-to-br from-brand-50 to-accent-50 border border-brand-200 rounded-2xl">
          <p className="text-xs font-bold text-brand-900 mb-2">Hızlı Giriş</p>
          <div className="flex gap-2">
            <button type="button" onClick={() => fillTestUser("ahmet", "password123")}
              className="flex-1 text-xs px-3 py-1.5 bg-white hover:bg-brand-100 text-brand-900 rounded-lg font-semibold transition border border-brand-200">
              ahmet (Müşteri)
            </button>
            <button type="button" onClick={() => fillTestUser("admin", "admin123")}
              className="flex-1 text-xs px-3 py-1.5 bg-white hover:bg-accent-100 text-accent-700 rounded-lg font-semibold transition border border-accent-200">
              admin (Yönetici)
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
