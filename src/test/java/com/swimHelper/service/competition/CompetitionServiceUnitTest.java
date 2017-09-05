package com.swimHelper.service.competition;

import com.swimHelper.exception.CompetitionFullException;
import com.swimHelper.exception.InvalidCompetitionException;
import com.swimHelper.model.Competition;
import com.swimHelper.repository.CompetitionRepository;
import com.swimHelper.service.CompetitionService;
import com.swimHelper.service.UserService;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by Marcin Szalek on 05.09.17.
 */
public class CompetitionServiceUnitTest {
    private final CompetitionRepository repoMock = mock(CompetitionRepository.class);
    private final UserService userServiceMock = mock(UserService.class);
    private final CompetitionService sut = new CompetitionService(repoMock, userServiceMock);

    @Test
    public void addCompetition_callsRepository() throws Exception {
        //given
        Competition competition = new Competition();
        //when
        sut.addCompetition(competition);
        //then
        verify(repoMock).saveAndFlush(competition);
    }

    @Test
    public void addCompetition_whenRepoThrowsConstraintError_throwInvalidCompetitionException() throws Exception {
        //given
        Competition competition = new Competition();
        when(repoMock.saveAndFlush(any())).thenThrow(new DataIntegrityViolationException("msg"));
        //when
        Throwable throwable = catchThrowable(() -> sut.addCompetition(competition));
        //then
        assertThat(throwable).isInstanceOf(InvalidCompetitionException.class);
    }

    @Test
    public void updateCompetition_ifNewMaxCounterIsLessThanActualCounter_throwException() throws Exception {
        //given
        Competition competition = new Competition();
        competition.setId(1L);
        competition.setMaxParticipants(3);
        competition.setParticipantsCounter(2);
        Competition competitionToBeReturnedFromRepo = new Competition();
        competitionToBeReturnedFromRepo.setParticipantsCounter(5);
        when(repoMock.findOne(1L)).thenReturn(competitionToBeReturnedFromRepo);
        //when
        Throwable throwable = catchThrowable(() -> sut.updateCompetition(competition));
        //then
        assertThat(throwable).isInstanceOf(CompetitionFullException.class);
    }

    @Test
    public void updateCompetition_ifNewMaxCounterIsGreaterThanActualCounter_noException() throws Exception {
        //given
        Competition competition = new Competition();
        competition.setId(1L);
        competition.setMaxParticipants(3);
        competition.setParticipantsCounter(2);
        Competition competitionToBeReturnedFromRepo = new Competition();
        competitionToBeReturnedFromRepo.setParticipantsCounter(1);
        when(repoMock.findOne(1L)).thenReturn(competitionToBeReturnedFromRepo);
        //when
        sut.updateCompetition(competition);
        //then
        //no exception -- pass
    }

}
