package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;

import java.util.HashSet;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.CLARIFICATION_IS_EMPTY;

@Entity
@Table(name = "clarifications")
public class Clarification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean isAnswered;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "clarification")
    private Set<ClarificationAnswer> clarificationAnswers = new HashSet<>();

    public Clarification() {}

    public Clarification(String content, Question question, User user) {
        this.user = user;
        this.question = question;
        this.isAnswered = false;

        if (content == null || content.isEmpty() || content.isBlank())
            throw new TutorException(CLARIFICATION_IS_EMPTY);
        else
            this.content = content;
    }

    public Integer getId() { return id; }

    public String getContent() { return content; }

    public Question getQuestion() { return question; }

    public User getUser() { return user; }

    public boolean isAnswered() { return isAnswered; }

    public void setId(Integer id) { this.id = id; }

    public void setContent(String content) { this.content = content; }

    public void setQuestion(Question question) { this.question = question; }

    public void setUser(User user) { this.user = user; }

    public Set<ClarificationAnswer> getClarificationAnswers() { return clarificationAnswers; }

    public void setClarificationAnswers(Set<ClarificationAnswer> clarificationAnswers) { this.clarificationAnswers = clarificationAnswers; }

    public void setIsAnswered(boolean status) { this.isAnswered = status; }

    public void addClarificationAnswer(ClarificationAnswer clarificationAnswer) {
        if (!isAnswered) isAnswered = true;
        this.clarificationAnswers.add(clarificationAnswer);
    }

    public void remove() {
        user.getClarifications().remove(this);
        question.getClarifications().remove(this);
    }

    @Override
    public String toString() {
        return "Clarification{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", isAnswered=" + isAnswered +
                ", user=" + user +
                ", question=" + question +
                ", clarificationAnswers=" + clarificationAnswers +
                '}';
    }
}
