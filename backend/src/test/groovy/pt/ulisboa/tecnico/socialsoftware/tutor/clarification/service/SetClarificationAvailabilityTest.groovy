package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Shared
import spock.lang.Specification

@DataJpaTest
class SetClarificationAvailabilityTest extends Specification {
    static final User.Role ROLE_TEACHER = User.Role.TEACHER
    static final User.Role ROLE_STUDENT = User.Role.STUDENT
    static final String CONTENT = "I want a clarification in this question."
    static final String COURSE_NAME = "Software Architecture"

    @Autowired
    ClarificationService clarificationService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    ClarificationRepository clarificationRepository

    @Shared
    User student

    @Shared
    User studentNotCreator

    @Shared
    User teacher

    @Shared
    Clarification clarification

    def setup() {
        student = new User()
        student.setKey(1)
        student.setRole(ROLE_STUDENT)
        userRepository.save(student)

        teacher = new User()
        teacher.setKey(userRepository.getMaxUserNumber()+1)
        teacher.setRole(ROLE_TEACHER)
        userRepository.save(teacher)

        studentNotCreator = new User()
        studentNotCreator.setRole(ROLE_STUDENT)
        studentNotCreator.setKey(userRepository.getMaxUserNumber()+1)
        userRepository.save(studentNotCreator)


        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        def question = new Question()
        question.setKey(1)
        question.setTitle("Title")
        question.setCourse(course)
        questionRepository.save(question)

        course.addQuestion(question)

        clarification = new Clarification(CONTENT, question, student)
    }

    def "clarification is made available by the creator student"() {
        given: "a clarification"
        clarificationRepository.save(clarification)

        when:
        clarificationService.setClarificationAvailabilityByStudent(clarification.getId(), true, student.getId())

        then: "the clarification is with student availability"
        clarification.getAvailability() == Clarification.Availability.STUDENT
    }

    def "clarification is made available by a teacher"() {
        given: "a clarification"
        clarificationRepository.save(clarification)

        when:
        clarificationService.makeClarificationAvailableByTeacher(clarification.getId())

        then: "the clarification is with teacher availability"
        clarification.getAvailability() == Clarification.Availability.TEACHER
    }

    def "clarification is made available by the creator student and the teacher"() {
        given: "a clarification"
        clarificationRepository.save(clarification)

        when:
        clarificationService.setClarificationAvailabilityByStudent(clarification.getId(), true, student.getId())
        clarificationService.makeClarificationAvailableByTeacher(clarification.getId())

        then: "the clarification is with full availability"
        clarification.getAvailability() == Clarification.Availability.BOTH
    }

    def "clarification is with student availability and student creator makes it unavailable"() {
        given: "a clarification"
        clarification.setAvailability(Clarification.Availability.STUDENT)
        clarificationRepository.save(clarification)

        when:
        clarificationService.setClarificationAvailabilityByStudent(clarification.getId(), false, student.getId())

        then: "the clarification is with no availability"
        clarification.getAvailability() == Clarification.Availability.NONE
    }

    def "clarification is with teacher and student availability and student makes it unavailable"() {
        given: "a clarification"
        clarification.setAvailability(Clarification.Availability.BOTH)
        clarificationRepository.save(clarification)

        when:
        clarificationService.setClarificationAvailabilityByStudent(clarification.getId(), false, student.getId())

        then: "the clarification is with teacher availability"
        clarification.getAvailability() == Clarification.Availability.TEACHER
    }

    def "clarification has no availability and student that isn't the creator tries to make it available"() {
        given: "a clarification with a creator"
        clarificationRepository.save(clarification)

        and: "a student that isn't the creator"

        when:
        clarificationService.setClarificationAvailabilityByStudent(clarification.getId(), true, studentNotCreator.getId())

        then: "an exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.CLARIFICATION_NOT_CREATOR
        and: "clarification stays with same availability"
        clarification.getAvailability() == Clarification.Availability.NONE
    }

    def "clarification is with student availability and student that isn't the creator tries to make it unavailable"() {
        given: "a clarification with a creator"
        clarification.setAvailability(Clarification.Availability.STUDENT)
        clarificationRepository.save(clarification)

        and: "a student that isn't the creator"

        when:
        clarificationService.setClarificationAvailabilityByStudent(clarification.getId(), false, studentNotCreator.getId())

        then: "an exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.CLARIFICATION_NOT_CREATOR
        and: "clarification stays with same availability"
        clarification.getAvailability() == Clarification.Availability.STUDENT
    }

    @TestConfiguration
    static class ClarificationServiceImplTestContextConfiguration {
        @Bean
        ClarificationService clarificationService() {
            return new ClarificationService()
        }
    }
}
