package com.sungchul.blog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "visit_date", nullable = false, unique = true)
    private LocalDate visitDate;

    @Column(name = "daily_count", nullable = false)
    private Integer dailyCount = 0;

    @Column(name = "total_count", nullable = false)
    private Integer totalCount = 0;

    @PrePersist
    protected void onCreate() {
        if (visitDate == null) {
            visitDate = LocalDate.now();
        }
    }
}