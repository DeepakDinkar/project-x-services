package com.qomoi.repository;

import com.qomoi.entity.TrainerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainersRepository extends JpaRepository<TrainerEntity, Long> {
}
