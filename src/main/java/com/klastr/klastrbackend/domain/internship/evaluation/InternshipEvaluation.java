package com.klastr.klastrbackend.domain.internship.evaluation;

import com.klastr.klastrbackend.domain.user.User;
import com.klastr.klastrbackend.domain.base.BaseEntity;
import com.klastr.klastrbackend.domain.internship.lifecycle.StudentInternship;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "internship_evaluations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipEvaluation extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "internship_id", nullable = false)
    private StudentInternship internship;

    @ManyToOne(optional = false)
    @JoinColumn(name = "evaluated_by", nullable = false)
    private User evaluatedBy;

    private Integer score;
    private String comments;
}