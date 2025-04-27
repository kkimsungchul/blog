package com.sungchul.blog.service;

import com.sungchul.blog.entity.Visitor;
import com.sungchul.blog.repository.VisitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class VisitorService {

    private final VisitorRepository visitorRepository;

    @Autowired
    public VisitorService(VisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;
    }

    @Transactional
    public void incrementVisitorCount() {
        LocalDate today = LocalDate.now();
        Optional<Visitor> todayVisitor = visitorRepository.findByVisitDate(today);
        
        if (todayVisitor.isPresent()) {
            // Update existing record for today
            Visitor visitor = todayVisitor.get();
            visitor.setDailyCount(visitor.getDailyCount() + 1);
            visitor.setTotalCount(visitor.getTotalCount() + 1);
            visitorRepository.save(visitor);
        } else {
            // Create new record for today
            Integer lastTotalCount = visitorRepository.findMaxTotalCount();
            if (lastTotalCount == null) {
                lastTotalCount = 0;
            }
            
            Visitor visitor = new Visitor();
            visitor.setVisitDate(today);
            visitor.setDailyCount(1);
            visitor.setTotalCount(lastTotalCount + 1);
            visitorRepository.save(visitor);
        }
    }

    @Transactional(readOnly = true)
    public Integer getTodayVisitorCount() {
        LocalDate today = LocalDate.now();
        return visitorRepository.findByVisitDate(today)
                .map(Visitor::getDailyCount)
                .orElse(0);
    }

    @Transactional(readOnly = true)
    public Integer getTotalVisitorCount() {
        return visitorRepository.findMaxTotalCount() != null 
                ? visitorRepository.findMaxTotalCount() 
                : 0;
    }
}