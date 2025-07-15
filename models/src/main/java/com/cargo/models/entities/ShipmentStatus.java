package com.cargo.models.entities;

public enum ShipmentStatus {
    PENDING,        // Gönderi bekliyor, daha işleme alınmadı
    PROCESSING,     // Gönderi hazırlanıyor veya yola çıkmak üzere
    SHIPPED,        // Gönderi yola çıktı (kargoya verildi)
    IN_TRANSIT,     // Gönderi taşıma sürecinde
    DELIVERED,      // Gönderi teslim edildi
    DELAYED,        // Gönderide gecikme var
    CANCELLED,      // Gönderi iptal edildi
    RETURNED        // Gönderi iade edildi
}