package com.swimHelper.repository;

import com.swimHelper.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Marcin Szalek on 19.07.17.
 */
@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {
    @Query("select t from Training t where t.hasUserBeenNotified = false and t.notificationDateTime is not null and t.notificationDateTime < :currentDate ")
    List<Training> findTrainingsToBeNotified(@Param("currentDate") LocalDateTime currentLocalDateTime);

    @Query("select t from Training t where t.user.id = :userId")
    List<Training> findTrainingsByUser(@Param("userId") Long userId);

    @Query("select t from Training t where t.user.id = :userId " +
            " and t.id = :trainingId")
    Training findTrainingByUserAndId(@Param("userId") Long userId,
                                     @Param("trainingId") Long trainingId);

    @Query("select t from Training t where t.user.id = :userId " +
            " and t.trainingDateTime >= :startDate" +
            " and t.trainingDateTime <= :endDate")
    List<Training> findTrainingsByUserAndDates(@Param("userId") Long userId,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);
}
