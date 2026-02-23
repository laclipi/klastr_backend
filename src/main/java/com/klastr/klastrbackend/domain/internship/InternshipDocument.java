package com.klastr.klastrbackend.domain.internship;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;

import com.klastr.klastrbackend.domain.user.User;

import lombok.*;

@Entity
@Table(name = "internship_documents",
        indexes = {
            @Index(name = "idx_document_internship", columnList = "internship_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipDocument {

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "internship_id", nullable = false)
    private Internship internship;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath; // Ruta en almacenamiento (S3, local, etc.)

    @Column(nullable = false)
    private String contentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        this.uploadedAt = LocalDateTime.now();
    }
}
