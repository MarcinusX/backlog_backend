package com.swimHelper.service.competition;

import com.swimHelper.CompetitionTestUtil;
import com.swimHelper.TestUtil;
import com.swimHelper.exception.*;
import com.swimHelper.model.Competition;
import com.swimHelper.model.User;
import com.swimHelper.repository.CompetitionRepository;
import com.swimHelper.service.CompetitionService;
import com.swimHelper.service.UserService;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.geo.Point;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    @Test
    public void getCompetitionByLocation_sortsProperly() throws Exception {
        //given
        Competition competition1 = competition(0, 0);
        Competition competition2 = competition(2, 0);
        Competition competition3 = competition(2, 2);
        Competition competition4 = competition(5, 5);
        Competition competition5 = competition(100, 0);
        when(repoMock.findAll()).thenReturn(Arrays.asList(competition1, competition2, competition3, competition4, competition5));
        //when
        List<Competition> competitionsByLocation = sut.getCompetitionsByLocation(2, 2);
        //then
        assertThat(competitionsByLocation.get(0)).isEqualTo(competition3);
        assertThat(competitionsByLocation.get(1)).isEqualTo(competition2);
        assertThat(competitionsByLocation.get(2)).isEqualTo(competition1);
        assertThat(competitionsByLocation.get(3)).isEqualTo(competition4);
        assertThat(competitionsByLocation.get(4)).isEqualTo(competition5);
    }

    @Test
    public void assignToCompetition_ifFull_throwsException() throws Exception {
        //given
        Competition competition = new CompetitionTestUtil().createValidCompetition();
        competition.setParticipantsCounter(2);
        competition.setMaxParticipants(2);
        when(repoMock.findOne(anyLong())).thenReturn(competition);
        User user = new TestUtil().createValidUser();
        when(userServiceMock.getUser(anyLong())).thenReturn(user);
        //when
        Throwable throwable = catchThrowable(() -> sut.assignToCompetition(1L, 1L));
        //then
        assertThat(throwable).isInstanceOf(CompetitionFullException.class);
    }

    @Test
    public void assignToCompetition_ifAssigned_throwsException() throws Exception {
        //given
        User user = new TestUtil().createValidUser();
        when(userServiceMock.getUser(anyLong())).thenReturn(user);
        Competition competition = new CompetitionTestUtil().createValidCompetition();
        competition.setParticipants(Collections.singleton(user));
        when(repoMock.findOne(anyLong())).thenReturn(competition);
        //when
        Throwable throwable = catchThrowable(() -> sut.assignToCompetition(1L, 1L));
        //then
        assertThat(throwable).isInstanceOf(UserAlreadySignedToCompetition.class);
    }

    @Test
    public void assignToCompetition_ifExpired_throwsException() throws Exception {
        //given
        User user = new TestUtil().createValidUser();
        when(userServiceMock.getUser(anyLong())).thenReturn(user);
        Competition competition = new CompetitionTestUtil().createValidCompetition();
        competition.setDateTime(LocalDateTime.now().minusDays(1));
        when(repoMock.findOne(anyLong())).thenReturn(competition);
        //when
        Throwable throwable = catchThrowable(() -> sut.assignToCompetition(1L, 1L));
        //then
        assertThat(throwable).isInstanceOf(CompetitionExpiredException.class);
    }

    @Test
    public void assignToCompetition_ifCanceled_throwsException() throws Exception {
        //given
        User user = new TestUtil().createValidUser();
        when(userServiceMock.getUser(anyLong())).thenReturn(user);
        Competition competition = new CompetitionTestUtil().createValidCompetition();
        competition.setCancelled(true);
        when(repoMock.findOne(anyLong())).thenReturn(competition);
        //when
        Throwable throwable = catchThrowable(() -> sut.assignToCompetition(1L, 1L));
        //then
        assertThat(throwable).isInstanceOf(CompetitionExpiredException.class);
    }

    @Test
    public void leaveCompetition_ifNotSigned_throwsException() throws Exception {
        //given
        User user = new TestUtil().createValidUser();
        when(userServiceMock.getUser(anyLong())).thenReturn(user);
        Competition competition = new CompetitionTestUtil().createValidCompetition();
        when(repoMock.findOne(anyLong())).thenReturn(competition);
        //when
        Throwable throwable = catchThrowable(() -> sut.leaveCompetition(1L, 1L));
        //then
        assertThat(throwable).isInstanceOf(UserNotSignedToCompetition.class);
    }

    private Competition competition(double x, double y) {
        Competition competition = new CompetitionTestUtil().createValidCompetition();
        competition.setLocation(new Point(x, y));
        return competition;
    }

}
