package main.model.test.result

import com.fasterxml.jackson.annotation.JsonView
import main.model.test.test.TestAnswerVariant
import main.model.test.test.TestQuestion
import main.util.Views
import javax.persistence.*

@Entity
class TestResultAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.Minimal::class)
    var id: Long = 0

    @ManyToOne
    @JsonView(Views.Minimal::class)
    var question: TestQuestion? = null

    @JsonView(Views.Minimal::class)
    var answer: String? = null

    @ManyToMany
    @JsonView(Views.Minimal::class)
    var answers: MutableList<TestAnswerVariant>? = null
}