package com.swimHelper.service.competition;

import com.swimHelper.CompetitionTestUtil;
import com.swimHelper.TestUtil;
import com.swimHelper.exception.InvalidCompetitionException;
import com.swimHelper.model.Competition;
import com.swimHelper.model.User;
import com.swimHelper.repository.CompetitionRepository;
import com.swimHelper.repository.UserRepository;
import com.swimHelper.service.CompetitionService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * Created by Marcin Szalek on 05.09.17.
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class CompetitionServiceIntegrationTest {

    @Autowired
    private CompetitionService sut;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void prepare() {
        cleanUp();
    }

    @After
    public void cleanUp() {
        competitionRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void addCompetition_whenInvalidCompetition_throwsException() throws Exception {
        //given
        Competition competition = new Competition();
        //when
        Throwable throwable = catchThrowable(() -> sut.addCompetition(competition));
        //then
        assertThat(throwable).isInstanceOf(InvalidCompetitionException.class);
    }

    @Test
    public void addCompetition_whenValidCompetition_returnsCompetition() throws Exception {
        //given
        Competition competition = new CompetitionTestUtil().createValidCompetition();
        //when
        Competition competitionReturned = sut.addCompetition(competition);
        //then
        assertThat(competitionReturned).isNotNull();
        assertThat(competitionReturned.getId()).isNotNull();
    }

    @Test
    public void cancelCompetition_test() throws Exception {
        //given
        Competition competition = new CompetitionTestUtil().createValidCompetition();
        competition = competitionRepository.saveAndFlush(competition);
        //when
        Competition resultCompetition = sut.cancelCompetition(competition.getId());
        //then
        assertThat(resultCompetition.isCancelled()).isTrue();
        assertThat(competitionRepository.findOne(competition.getId()).isCancelled()).isTrue();
    }

    @Test
    public void assignToCompetitionTest() throws Exception {
        //given
        Competition competition = new CompetitionTestUtil().createValidCompetition();
        competition = competitionRepository.saveAndFlush(competition);
        User user = new TestUtil().createValidUser();
        user = userRepository.saveAndFlush(user);
        //when
        Competition competitionReturned = sut.assignToCompetition(competition.getId(), user.getId());
        //then
        assertThat(competitionReturned.getParticipants()).contains(user);
        assertThat(competitionReturned.getParticipantsCounter()).isEqualTo(1);
        assertThat(userRepository.findOne(user.getId()).getCompetitions()).contains(competitionReturned);
    }

    @Test
    public void leaveCompetitionTest() throws Exception {
        //given
        Competition competition = new CompetitionTestUtil().createValidCompetition();
        competition = competitionRepository.saveAndFlush(competition);
        User user = new TestUtil().createValidUser();
        user = userRepository.saveAndFlush(user);
        Competition competitionReturned = sut.assignToCompetition(competition.getId(), user.getId());
        //when
        sut.leaveCompetition(competition.getId(), user.getId());
        //then
        Competition competition1 = competitionService.getCompetition(competition.getId());
        assertThat(competition1.getParticipants()).doesNotContain(user);
        assertThat(competition1.getParticipantsCounter()).isEqualTo(0);
        assertThat(userRepository.findOne(user.getId()).getCompetitions()).doesNotContain(competitionReturned);
    }
}
