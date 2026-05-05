package com.campus.scems.repository;

import com.campus.scems.model.FeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Long> {
}
