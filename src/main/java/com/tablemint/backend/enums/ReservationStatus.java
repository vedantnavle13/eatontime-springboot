package com.tablemint.backend.enums;

public enum ReservationStatus {
    PENDING,       // just created, awaiting confirmation
    CONFIRMED,     // restaurant confirmed
    SEATED,        // QR scanned, guest is in
    COMPLETED,     // meal done, review unlocked
    CANCELLED,     // cancelled by guest or restaurant
    NO_SHOW        // guest didn't arrive
}
