package com.marinamooringmanagement.constants.enums;

public enum EntityEnum {

    CUSTOMER("Customer"),

    MOORING("Mooring"),

    BOATYARD("Boatyard"),

    ESTIMATE("Estimate"),

    FORM("Form"),

    INVENTORY("Inventory"),

    NOTIFICATION("Notification"),

    PAYMENT("Payment"),

    QUICKBOOK_CUSTOMER("Quickbook Customer"),

    ROLE("Role"),

    SERVICE_AREAS("Service Area"),

    TOKEN("Token"),

    USER("User"),

    VENDOR("Vendor"),

    WORK_ORDER("Work Order"),

    WORK_ORDER_INVOICE ("Word Order Invoice");

    final String entityType;

    EntityEnum(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityType() {
        return this.entityType;
    }
}
