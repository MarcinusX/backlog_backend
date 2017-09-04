package com.swimHelper;

import com.swimHelper.model.ExerciseSeries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by mstobieniecka on 2017-08-27.
 */
@Repository
public interface ExerciseSeriesRepository extends JpaRepository<ExerciseSeries, Long> {
}
