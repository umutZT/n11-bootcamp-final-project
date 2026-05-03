package com.bootcamp.productservice.seed;

import com.bootcamp.productservice.entity.Product;
import com.bootcamp.productservice.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@Order(1)
public class ProductSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ProductSeeder.class);

    private final ProductRepository productRepository;

    public ProductSeeder(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        List<Product> seedProducts = buildSeedProducts();
        int inserted = 0;
        for (Product p : seedProducts) {
            if (!productRepository.existsBySku(p.getSku())) {
                productRepository.save(p);
                inserted++;
            }
        }
        log.info("ProductSeeder: {} ürün eklendi, toplam {} ürün mevcut",
                inserted, productRepository.count());
    }

    private Product create(String name, String description, String price, int stock,
                           String category, String brand, String sku, String imageUrl) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(description);
        p.setPrice(new BigDecimal(price));
        p.setStock(stock);
        p.setCategory(category);
        p.setBrand(brand);
        p.setSku(sku);
        p.setImageUrl(imageUrl);
        p.setActive(true);
        return p;
    }

    private List<Product> buildSeedProducts() {
        return List.of(
                // ===== ELEKTRONİK (6) =====
                create("iPhone 15 Pro 256GB",
                        "Apple A17 Pro çipli, ProMotion ekranlı amiral gemisi telefon",
                        "65999.00", 25, "Elektronik", "Apple", "ELK-IPH15P-256",
                        "https://images.unsplash.com/photo-1592286927505-1def25115558?w=600&q=80"),

                create("Samsung Galaxy S24 Ultra",
                        "200MP kamera, S Pen ve Snapdragon 8 Gen 3 işlemci ile",
                        "58999.00", 18, "Elektronik", "Samsung", "ELK-SGS24U-512",
                        "https://images.unsplash.com/photo-1610792516775-01de03eae630?w=600&q=80"),

                create("MacBook Air M3 13\"",
                        "Apple M3 çipli, 8GB RAM 256GB SSD, ultra ince ve sessiz",
                        "44999.00", 12, "Elektronik", "Apple", "ELK-MBA-M3-13",
                        "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600&q=80"),

                create("Sony WH-1000XM5 Kulaklık",
                        "Endüstri lideri gürültü engelleme, 30 saat pil ömrü",
                        "12499.00", 40, "Elektronik", "Sony", "ELK-WH1000XM5",
                        "https://images.unsplash.com/photo-1583394838336-acd977736f90?w=600&q=80"),

                create("iPad Air 11\" M2",
                        "8 çekirdekli M2 çipli, Apple Pencil Pro destekli tablet",
                        "29999.00", 22, "Elektronik", "Apple", "ELK-IPAD-AIR-M2",
                        "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=600&q=80"),

                create("LG 27\" 4K UltraGear Monitör",
                        "144Hz, 1ms tepki süresi, oyun ve profesyonel kullanım için",
                        "18499.00", 15, "Elektronik", "LG", "ELK-LG-27UG-4K",
                        "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=600&q=80"),

                // ===== MODA (6) =====
                create("Erkek Slim Fit Beyaz Gömlek",
                        "%100 pamuk, ütü gerektirmez, klasik yakalı",
                        "599.00", 60, "Moda", "Mavi", "MOD-GMLK-BYZ-M",
                        "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=600&q=80"),

                create("Kadın Trençkot Bej",
                        "Uzun kesim, kemer detaylı, su itici kumaş",
                        "1899.00", 35, "Moda", "Koton", "MOD-TRNC-BEJ-K",
                        "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=600&q=80"),

                create("Unisex Spor Ayakkabı Siyah",
                        "Hafif, nefes alabilen örgü kumaş, günlük kullanıma uygun",
                        "1299.00", 50, "Moda", "Adidas", "MOD-SPRA-SYH-42",
                        "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&q=80"),

                create("Deri El Çantası Kahve",
                        "Hakiki deri, çok bölmeli iç tasarım, ayarlanabilir askı",
                        "2499.00", 28, "Moda", "Derimod", "MOD-CNT-KHV-DR",
                        "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=600&q=80"),

                create("Erkek Kot Pantolon Slim",
                        "Streç dokuma, koyu indigo yıkama, modern slim kalıp",
                        "899.00", 75, "Moda", "Mavi", "MOD-KOT-IND-32",
                        "https://images.unsplash.com/photo-1542272604-787c3835535d?w=600&q=80"),

                create("Kadın Triko Kazak",
                        "Yumuşak akrilik karışım, geniş yaka, sonbahar için ideal",
                        "799.00", 45, "Moda", "LCW", "MOD-TRK-KRM-K",
                        "https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=600&q=80"),

                // ===== EV & YAŞAM (6) =====
                create("Philips Espresso Makinesi",
                        "Otomatik süt köpürtücü, 15 bar basınç, sessiz çalışma",
                        "8999.00", 20, "Ev & Yaşam", "Philips", "EVY-ESPR-PHL",
                        "https://images.unsplash.com/photo-1610889556528-9a770e32642f?w=600&q=80"),

                create("Bambu Mutfak Tezgahı Organizatörü",
                        "Çok bölmeli baharat ve gereç düzenleyici",
                        "449.00", 80, "Ev & Yaşam", "Bambum", "EVY-MTFK-ORG",
                        "https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=600&q=80"),

                create("Modern Pamuk Çarşaf Takımı",
                        "Çift kişilik, %100 pamuk satın, dört mevsim",
                        "1199.00", 55, "Ev & Yaşam", "English Home", "EVY-CRSF-PMK",
                        "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=600&q=80"),

                create("Dyson V12 Şarjlı Süpürge",
                        "Lazer teknolojili, HEPA filtreli, 60 dk çalışma süresi",
                        "24999.00", 8, "Ev & Yaşam", "Dyson", "EVY-DYSN-V12",
                        "https://images.unsplash.com/photo-1558317374-067fb5f30001?w=600&q=80"),

                create("Aromaterapi Difüzör",
                        "Ultrasonik, 7 renk LED, otomatik kapanma",
                        "599.00", 90, "Ev & Yaşam", "Tchibo", "EVY-DFZR-ARM",
                        "https://images.unsplash.com/photo-1602928321679-560bb453f190?w=600&q=80"),

                create("Şömine Tarzı Elektrikli Soba",
                        "1500W, gerçekçi alev efekti, uzaktan kumandalı",
                        "3299.00", 14, "Ev & Yaşam", "Vestel", "EVY-SBA-ELK",
                        "https://images.unsplash.com/photo-1513519245088-0e12902e5a38?w=600&q=80"),

                // ===== KİTAP & MÜZİK (6) =====
                create("Tutunamayanlar - Oğuz Atay",
                        "Türk edebiyatının başyapıtı, İletişim Yayınları",
                        "189.00", 100, "Kitap", "İletişim Yayınları", "KTP-TTNM-OA",
                        "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=600&q=80"),

                create("Clean Code - Robert C. Martin",
                        "Yazılım profesyonelleri için temel başvuru kitabı",
                        "459.00", 65, "Kitap", "Pearson", "KTP-CLNCD-RCM",
                        "https://images.unsplash.com/photo-1532012197267-da84d127e765?w=600&q=80"),

                create("Sapiens - Yuval Noah Harari",
                        "İnsanlığın kısa tarihi, dünyaca bestseller",
                        "229.00", 85, "Kitap", "Kolektif Kitap", "KTP-SPNS-YNH",
                        "https://images.unsplash.com/photo-1495446815901-a7297e633e8d?w=600&q=80"),

                create("Yamaha P-45 Dijital Piyano",
                        "88 tuş, ağırlıklandırılmış, başlangıç seviyesi için ideal",
                        "14999.00", 6, "Kitap", "Yamaha", "MZK-YMH-P45",
                        "https://images.unsplash.com/photo-1520523839897-bd0b52f945a0?w=600&q=80"),

                create("Akustik Gitar Cordoba C5",
                        "Sedir kapak, klasik gitar, başlangıç ve orta seviye",
                        "5499.00", 12, "Kitap", "Cordoba", "MZK-CRDB-C5",
                        "https://images.unsplash.com/photo-1510915361894-db8b60106cb1?w=600&q=80"),

                create("Kürk Mantolu Madonna - Sabahattin Ali",
                        "Türk edebiyatı klasiği, Yapı Kredi Yayınları",
                        "129.00", 120, "Kitap", "Yapı Kredi Yayınları", "KTP-KRKM-SA",
                        "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=600&q=80"),

                // ===== SPOR & OUTDOOR (6) =====
                create("Yoga Matı Kaymaz",
                        "6mm kalınlığında, TPE malzeme, taşıma askılı",
                        "399.00", 70, "Spor & Outdoor", "Decathlon", "SPR-YOGA-MT-6",
                        "https://images.unsplash.com/photo-1545205597-3d9d02c29597?w=600&q=80"),

                create("Dağ Bisikleti 27.5 Jant",
                        "21 vites, hidrolik disk fren, alüminyum kadro",
                        "12999.00", 9, "Spor & Outdoor", "Salcano", "SPR-MTB-275",
                        "https://images.unsplash.com/photo-1485965120184-e220f721d03e?w=600&q=80"),

                create("Profesyonel Koşu Ayakkabısı",
                        "Hafif, soluk alabilen, kemik yapısına uygun taban",
                        "2299.00", 45, "Spor & Outdoor", "Nike", "SPR-RUN-NK-43",
                        "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&q=80"),

                create("Kamp Çadırı 4 Kişilik",
                        "Su geçirmez, hızlı kurulum, sivrisinek filesi",
                        "3499.00", 22, "Spor & Outdoor", "Coleman", "SPR-CDR-4K",
                        "https://images.unsplash.com/photo-1504280390367-361c6d9f38f4?w=600&q=80"),

                create("Akıllı Spor Saati",
                        "GPS, kalp ritmi, 14 gün pil ömrü, 100m su geçirmez",
                        "4999.00", 30, "Spor & Outdoor", "Garmin", "SPR-WTC-GRM",
                        "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&q=80"),

                create("Termos 1L Vakumlu",
                        "24 saat sıcak, 12 saat soğuk tutar, paslanmaz çelik",
                        "599.00", 95, "Spor & Outdoor", "Stanley", "SPR-TRM-1L-S",
                        "https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=600&q=80")
        );
    }
}
