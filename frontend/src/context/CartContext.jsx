import { createContext, useContext, useState, useEffect } from "react";

const CartContext = createContext(null);
const STORAGE_KEY = "saga-shop-cart";

export function CartProvider({ children }) {
  const [items, setItems] = useState([]);

  useEffect(() => {
    const stored = localStorage.getItem(STORAGE_KEY);
    if (stored) {
      try { setItems(JSON.parse(stored)); }
      catch { localStorage.removeItem(STORAGE_KEY); }
    }
  }, []);

  useEffect(() => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(items));
  }, [items]);

  const addItem = (product) => {
    setItems((prev) => {
      const existing = prev.find((i) => i.productId === product.id);
      if (existing) {
        if (existing.quantity >= product.stock) return prev;
        return prev.map((i) =>
          i.productId === product.id ? { ...i, quantity: i.quantity + 1 } : i
        );
      }
      return [...prev, {
        productId: product.id,
        name: product.name,
        price: Number(product.price),
        stock: product.stock,
        imageUrl: product.imageUrl,
        quantity: 1,
      }];
    });
  };

  const removeItem = (productId) => {
    setItems((prev) => prev.filter((i) => i.productId !== productId));
  };

  const updateQuantity = (productId, quantity) => {
    if (quantity < 1) return removeItem(productId);
    setItems((prev) => prev.map((i) =>
      i.productId === productId
        ? { ...i, quantity: Math.min(quantity, i.stock) }
        : i
    ));
  };

  const clearCart = () => setItems([]);

  const total = items.reduce((sum, i) => sum + i.price * i.quantity, 0);
  const itemCount = items.reduce((sum, i) => sum + i.quantity, 0);

  return (
    <CartContext.Provider value={{
      items, addItem, removeItem, updateQuantity, clearCart, total, itemCount
    }}>
      {children}
    </CartContext.Provider>
  );
}

export function useCart() {
  const ctx = useContext(CartContext);
  if (!ctx) throw new Error("useCart must be used within CartProvider");
  return ctx;
}
