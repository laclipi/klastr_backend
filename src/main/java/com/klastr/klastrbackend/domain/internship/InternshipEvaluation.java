package com.klastr.klastrbackend.domain.internship;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;

import com.klastr.klastrbackend.domain.user.User;

import lombok.*;

@Entity
@Table(name = "internship_evaluations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class InternshipEvaluation {

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "internship_id", nullable = false, unique = true)
    private Internship internship;

    @Column(nullable = false)
    private Integer score; // 0-10 por ejemplo

    @Column(length = 2000)
    private String comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluated_by")
    private User evaluatedBy;

    @Column(name = "evaluated_at", nullable = false)
    private LocalDateTime evaluatedAt;

    @PrePersist
    protected void onCreate() {
        this.evaluatedAt = LocalDateTime.now();
    }
}
