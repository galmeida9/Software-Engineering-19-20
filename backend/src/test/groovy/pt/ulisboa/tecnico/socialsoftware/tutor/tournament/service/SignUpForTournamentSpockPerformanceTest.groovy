package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class SignUpForTournamentSpockPerformanceTest extends Specification{
    @Autowired
    TournamentService tournamentService
    @Autowired
    TournamentRepository tournamentRepository
    @Autowired
    CourseExecutionRepository courseExecutionRepository
    @Autowired
    UserRepository userRepository
    @Autowired
    QuizService quizService
    @Autowired
    AnswerService answerService
    @Autowired
    AnswersXmlImport answersXmlImport
    @Autowired
    QuestionService questionService

    User creator
    User user

    def setup() {
        creator = new User("host", "chost", 1, User.Role.STUDENT)
        userRepository.save(creator)

        CourseExecution courseExe = new CourseExecution();
        courseExecutionRepository.save(courseExe)

        user = new User("pessoa","pessoa1337",3, User.Role.STUDENT)
        user.addCourse(courseExe)
        HashSet<CourseExecution> courseExes = new HashSet<CourseExecution>(1)
        courseExes.add(courseExe)
        user.setCourseExecutions(courseExes)
        userRepository.save(user)
    }

    def "performance test for signing up for x tournaments"() {
        given: "a number of loops"
        def loopTimes = 1
        and: "x tournaments"
        def currentDate = DateHandler.now()
        1.upto(loopTimes, {
            def tournament = new Tournament(creator, "TEST",  currentDate.plusDays(1), currentDate.plusDays(2), 10)
            tournament.setId(it)
            tournament.setCourseExecution(user.getCourseExecutions()[0])
            tournamentRepository.save(tournament)
        })
        and: "a user"
        def userId = user.getId()

        when:
        1.upto(loopTimes, { tournamentService.signUp(userId, it)} )

        then:
        true
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }

        @Bean
        QuizService quizService() {
            return new QuizService()
        }

        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }

        @Bean
        AnswersXmlImport answersXmlImport() {
            return new AnswersXmlImport()
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }
}