package com.swimHelper.controller;

import com.swimHelper.exception.BusinessException;
import com.swimHelper.model.Competition;
import com.swimHelper.model.User;
import com.swimHelper.service.CompetitionService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Marcin Szalek on 04.09.17.
 */
@RestController
@RequestMapping(value = "competitions")
public class CompetitionController {

    private final CompetitionService competitionService;

    public CompetitionController(CompetitionService competitionService) {
        this.competitionService = competitionService;
    }

    //==== GET ====

    @GetMapping(value = "location")
    public List<Competition> getCompetitions(@RequestParam(value = "x") double x,
                                             @RequestParam(value = "y") double y) {
        return competitionService.getCompetitionsByLocation(x, y);
    }

    @GetMapping
    public List<Competition> getCompetitions() throws BusinessException {
        return competitionService.getCompetitions();
    }

    @GetMapping(value = "{competitionId}")
    public Competition getCompetition(@PathVariable(value = "competitionId") long competitionId) throws BusinessException {
        return competitionService.getCompetition(competitionId);
    }

    //====POST====

    @PostMapping
    public Competition addCompetition(@RequestBody Competition competition) throws BusinessException {
        return competitionService.addCompetition(competition);
    }

    @PostMapping(value = "{competitionId}")
    public Competition assignToCompetition(@PathVariable(value = "competitionId") long competitionId) throws BusinessException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return competitionService.assignToCompetition(competitionId, user.getId());
    }

    //====DELETE====

    @DeleteMapping(value = "cancel/{competitionId}")
    public Competition cancelCompetition(@PathVariable(value = "competitionId") long competitionId) throws BusinessException {
        return competitionService.cancelCompetition(competitionId);
    }

    @DeleteMapping(value = "leave/{competitionId}")
    public void leaveCompetition(@PathVariable(value = "competitionId") long competitionId) throws BusinessException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        competitionService.leaveCompetition(competitionId, user.getId());
    }

    //====PUT====

    @PutMapping
    public Competition updateCompetition(@RequestBody Competition competition) throws BusinessException {
        return competitionService.updateCompetition(competition);
    }
}
