package com.klastr.klastrbackend.domain.tenant;

public enum TenantStatus {

    ACTIVE, // Operativo
    INACTIVE, // Temporalmente deshabilitado
    SUSPENDED, // Bloqueado por administración
    ARCHIVED       // Histórico / ya no operativo
}
