package com.klastr.klastrbackend.domain.tenant;

import com.klastr.klastrbackend.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseTenantEntity extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;
}
