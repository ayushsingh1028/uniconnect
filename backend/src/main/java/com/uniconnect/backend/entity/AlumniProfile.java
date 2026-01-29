package com.uniconnect.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "alumni_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class AlumniProfile {

    @Id
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String company;

    @Column(name = "job_role", nullable = false)
    private String jobRole;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(columnDefinition = "TEXT")
    private String review;

    @Column(name = "linkedin_url")
    private String linkedinUrl;
}
