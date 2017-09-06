package com.swimHelper.repository;

import com.swimHelper.model.Competition;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Marcin Szalek on 05.09.17.
 */
@Repository
public interface CompetitionRepository extends JpaRepository<Competition, Long> {

    @EntityGraph("graph.competition.participants")
    Competition findOne(long id);
}
