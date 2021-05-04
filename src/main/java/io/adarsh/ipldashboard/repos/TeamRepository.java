package io.adarsh.ipldashboard.repos;

import io.adarsh.ipldashboard.entities.Team;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends CrudRepository<Team, Long> {

    Optional<Team> findByTeamName(String teamName);
}
