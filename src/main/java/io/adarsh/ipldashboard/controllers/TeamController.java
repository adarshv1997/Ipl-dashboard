package io.adarsh.ipldashboard.controllers;

import io.adarsh.ipldashboard.entities.Match;
import io.adarsh.ipldashboard.entities.Team;
import io.adarsh.ipldashboard.repos.MatchRepository;
import io.adarsh.ipldashboard.repos.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class TeamController {
    
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    MatchRepository matchRepository;

    @GetMapping("/team/{teamName}")
    public Team getTeam(@PathVariable("teamName") String teamName) throws Exception {
        Optional<Team> teamOptional = teamRepository.findByTeamName(teamName);
        if(teamOptional.isPresent()) {
            Team team = teamOptional.get();
            List<Match> matchList = matchRepository.findLatestMatchesByTeam(teamName, 4);
            team.setMatches(matchList);

            return team;
        }
        else {
            throw new Exception("Bad Request");
        }
    }
}
