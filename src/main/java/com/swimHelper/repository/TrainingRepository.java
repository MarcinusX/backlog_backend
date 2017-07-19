package com.swimHelper.repository;

import com.swimHelper.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Marcin Szalek on 19.07.17.
 */
@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {
}
