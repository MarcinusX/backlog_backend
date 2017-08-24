package com.swimHelper.repository;

import com.swimHelper.model.Exercise;
import com.swimHelper.model.Style;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by mstobieniecka on 2017-07-20.
 */
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByStyle(Style style);

    List<Exercise> findByIsWarmUpRelax(boolean isWarmUpRelax);
}
