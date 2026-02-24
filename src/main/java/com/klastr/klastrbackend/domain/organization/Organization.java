package com.klastr.klastrbackend.domain.organization;

import com.klastr.klastrbackend.domain.base.BaseEntity;
import com.klastr.klastrbackend.domain.tenant.Tenant;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "organizations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;
}