package com.sungchul.blog.repository;

import com.sungchul.blog.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    
    Optional<Visitor> findByVisitDate(LocalDate date);
    
    @Query("SELECT MAX(v.totalCount) FROM Visitor v")
    Integer findMaxTotalCount();
    
    @Query("SELECT v FROM Visitor v WHERE v.visitDate = (SELECT MAX(v2.visitDate) FROM Visitor v2)")
    Optional<Visitor> findLatestVisitor();
}