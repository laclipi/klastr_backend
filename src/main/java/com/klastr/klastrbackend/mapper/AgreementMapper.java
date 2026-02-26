package com.klastr.klastrbackend.mapper;

import org.springframework.stereotype.Component;

import com.klastr.klastrbackend.dto.internship.CreateAgreementRequest;
import com.klastr.klastrbackend.dto.internship.AgreementResponse;
import com.klastr.klastrbackend.com.klastr.klastrbackend.domain.internshipomain.organization.Organization;
import com.klastr.klastrbackend..InternshipAgreement;

@Component
public class AgreementMapper {

    public InternshipAgreement toEntity(
            CreateAgreementRequest request,
            Organization organization) {

        return InternshipAgreement.builder()
                .organization(organization)
                .agreementNumber(request.getAgreementNumber())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .description(request.getDescription())
                .build();
    }

    public AgreementResponse toResponse(InternshipAgreement agreement) {

        return new AgreementResponse(
                agreement.getId(),
                agreement.getOrganization().getId(),
                agreement.getAgreementNumber(),
                agreement.getStartDate(),
                agreement.getEndDate(),
                agreement.getDescription(),
                agreement.getStatus());
    }
}