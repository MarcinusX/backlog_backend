package com.swimHelper.service;

import com.swimHelper.exception.*;
import com.swimHelper.model.Competition;
import com.swimHelper.model.User;
import com.swimHelper.repository.CompetitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Created by Marcin Szalek on 04.09.17.
 */
@Service
public class CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final UserService userService;

    @Autowired
    public CompetitionService(CompetitionRepository competitionRepository, UserService userService) {
        this.competitionRepository = competitionRepository;
        this.userService = userService;
    }

    public Competition addCompetition(Competition competition) throws BusinessException {
        try {
            return competitionRepository.saveAndFlush(competition);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidCompetitionException(e);
        }
    }

    public Competition cancelCompetition(long competitionId) throws BusinessException {
        Competition competition = safeGet(competitionId);
        competition.setCancelled(true);
        return safeEdit(competition);
    }

    public Competition updateCompetition(Competition competition) throws BusinessException {
        Competition competitionFromRepo = safeGet(competition.getId());
        if (competitionFromRepo.getParticipantsCounter() > competition.getMaxParticipants()) {
            throw new CompetitionFullException();
        }
        assignValues(competitionFromRepo, competition);
        return safeEdit(competitionFromRepo);
    }

    public List<Competition> getCompetitionsByLocation(double x, double y) {
        List<Competition> competitions = competitionRepository.findAll();
        competitions.sort(
                Comparator.comparingDouble(
                        comp -> Math.hypot(comp.getLocation().getX() - x, comp.getLocation().getY() - y)));
        return competitions;
    }

    public List<Competition> getCompetitions() {
        return competitionRepository.findAll();
    }

    public Competition getCompetition(long competitionId) throws BusinessException {
        return safeGet(competitionId);
    }

    public Competition assignToCompetition(long competitionId, long userId) throws BusinessException {
        User user = userService.getUser(userId);
        Competition competition = safeGet(competitionId);
        checkAssignConditions(user, competition);

        Set<User> participants = competition.getParticipants();
        participants.add(user);
        competition.setParticipantsCounter(competition.getParticipantsCounter() + 1);
        competition.setParticipants(participants);
        return safeEdit(competition);
    }

    public void leaveCompetition(long competitionId, long userId) throws BusinessException {
        User user = userService.getUser(userId);
        Competition competition = safeGet(competitionId);

        Set<User> participants = competition.getParticipants();
        if (!participants.contains(user)) {
            throw new UserNotSignedToCompetition();
        } else {
            participants.remove(user);
            competition.setParticipantsCounter(competition.getParticipantsCounter() - 1);
            competition.setParticipants(participants);
            safeEdit(competition);
        }
    }

    private void checkAssignConditions(User user, Competition competition) throws BusinessException {
        if (competition.getParticipants().contains(user)) {
            throw new UserAlreadySignedToCompetition();
        } else if (competition.getParticipantsCounter() >= competition.getMaxParticipants()) {
            throw new CompetitionFullException();
        } else if (competition.isCancelled()) {
            throw new CompetitionExpiredException();
        } else if (LocalDateTime.now().isAfter(competition.getDateTime())) {
            throw new CompetitionExpiredException();
        } else {
            //all good - pass
        }
    }

    private void assignValues(Competition to, Competition from) {
        to.setVersion(from.getVersion());
        to.setName(from.getName());
        to.setMaxParticipants(from.getMaxParticipants());
        to.setLocationName(from.getLocationName());
        to.setLocation(from.getLocation());
        to.setDateTime(from.getDateTime());
        to.setDescription(from.getDescription());
    }

    private Competition safeGet(long id) throws CompetitionNotFoundException {
        Competition competition = competitionRepository.findOne(id);
        if (competition == null) {
            throw new CompetitionNotFoundException("Couldn't find competition with id: " + id);
        } else {
            return competition;
        }
    }

    private Competition safeEdit(Competition competition) throws OptimisticLockException {
        try {
            return competitionRepository.saveAndFlush(competition);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new OptimisticLockException(e);
        }
    }
}
