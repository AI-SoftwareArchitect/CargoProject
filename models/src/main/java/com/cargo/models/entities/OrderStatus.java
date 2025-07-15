package com.cargo.models.entities;

public enum OrderStatus {
    PENDING("Beklemede"),
    APPROVED("Onaylandı"),
    DELIVERED("yerine ulaştı"),
    CANCELLED("İptal Edildi"),
    AWAITING_SHIPMENT("Sevk Bekliyor"),
    SHIPPED("Sevk Edildi");


    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}