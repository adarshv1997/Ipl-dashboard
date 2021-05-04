package io.adarsh.ipldashboard.listeners;

import io.adarsh.ipldashboard.entities.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private EntityManager em;

    @Autowired
    public JobCompletionNotificationListener(EntityManager em) {
        this.em = em;
    }

    /*private final JdbcTemplate jdbcTemplate;*/

    /*@Autowired
    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }*/

    @Transactional
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("JOB FINISHED! TIME TO VERIFY THE RESULTS");

            /*jdbcTemplate.query("SELECT team1, team2, date from match",
                    (rs, row) -> " Team 1 " + rs.getString(1) + " Team 2 " + rs.getString(2) + " Date " + rs.getString(3)
                    ).forEach(log::info);*/

            Map<String, Team> teamData = new HashMap<>();

            /*select distinct team1 from Match m union select distinct team2 from Match m*/

             em.createQuery("select m.team1, count(*) from Match m group by m.team1", Object[].class)
                    .getResultList()
                     .stream()
                     .map(e -> new Team((String) e[0], (Long) e[1]))
                     .forEach(team -> teamData.put(team.getTeamName(),team));

             em.createQuery("select m.team2, count(*) from Match m group by m.team2", Object[].class)
                     .getResultList()
                     .stream()
                     .forEach(e -> {
                         Team team = teamData.get((String) e[0]);
                         team.setTotalMatches((Long) e[1] + team.getTotalMatches());
                     });

             em.createQuery("select m.matchWinner, count(*) from Match m group by m.matchWinner", Object[].class)
                     .getResultList()
                     .stream()
                     .forEach(e -> {
                         Team team = teamData.get((String) e[0]);
                         if(team != null) team.setTotalWins((Long) e[1]);
                     });

             teamData.values()
                     .forEach(team -> em.persist(team));

             teamData.values()
                     .forEach(System.out::println);


        }
    }
}
