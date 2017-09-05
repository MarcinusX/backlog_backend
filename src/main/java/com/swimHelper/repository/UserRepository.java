package com.swimHelper.repository;

import com.swimHelper.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by marcinus on 19.04.17.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph("graph.user.competitions")
    User findByEmail(String email);

    @EntityGraph("graph.user.competitions")
    User findOne(Long id);
}
