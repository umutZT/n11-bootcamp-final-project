import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { signup } from "../api/auth";

export default function SignupPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ username: "", email: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await signup(form.username, form.email, form.password);
      navigate("/login");
    } catch (err) {
      setError(err.response?.data?.message || "Kayıt başarısız.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[calc(100vh-4rem)] flex items-center justify-center px-4 py-12 bg-gradient-to-br from-brand-50 via-white to-accent-50">
      <div className="w-full max-w-md animate-slide-up">
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-14 h-14 bg-gradient-to-br from-brand-600 to-accent-500 rounded-2xl text-white font-black text-xl mb-4 shadow-xl shadow-brand-600/30">
            n11
          </div>
          <h1 className="text-2xl font-extrabold tracking-tight">Hesap oluştur</h1>
          <p className="text-gray-500 text-sm mt-1">Birkaç saniye sürer</p>
        </div>

        <div className="bg-white rounded-2xl border border-gray-200 p-8 shadow-sm">
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-semibold mb-1.5 text-gray-700">Kullanıcı Adı</label>
              <input type="text" name="username" value={form.username} onChange={handleChange}
                className="w-full px-4 py-2.5 border border-gray-300 rounded-xl focus:ring-2 focus:ring-brand-500 focus:border-transparent transition"
                required minLength={3} maxLength={50} />
            </div>
            <div>
              <label className="block text-sm font-semibold mb-1.5 text-gray-700">E-posta</label>
              <input type="email" name="email" value={form.email} onChange={handleChange}
                className="w-full px-4 py-2.5 border border-gray-300 rounded-xl focus:ring-2 focus:ring-brand-500 focus:border-transparent transition"
                required />
            </div>
            <div>
              <label className="block text-sm font-semibold mb-1.5 text-gray-700">Şifre</label>
              <input type="password" name="password" value={form.password} onChange={handleChange}
                className="w-full px-4 py-2.5 border border-gray-300 rounded-xl focus:ring-2 focus:ring-brand-500 focus:border-transparent transition"
                required minLength={6} />
              <p className="text-xs text-gray-500 mt-1">En az 6 karakter</p>
            </div>
            {error && (
              <div className="p-3 bg-red-50 border border-red-200 rounded-xl text-sm text-red-700">
                {error}
              </div>
            )}
            <button type="submit" disabled={loading}
              className="w-full py-2.5 bg-brand-600 text-white rounded-xl hover:bg-brand-700 disabled:bg-gray-300 active:scale-[0.99] transition-all font-bold shadow-sm hover:shadow-md hover:shadow-brand-600/30">
              {loading ? "Kayıt olunuyor..." : "Kayıt Ol"}
            </button>
          </form>
          <p className="mt-5 text-sm text-center text-gray-600">
            Hesabınız var mı?{" "}
            <Link to="/login" className="text-brand-600 hover:text-accent-500 hover:underline font-bold transition">
              Giriş Yap
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
