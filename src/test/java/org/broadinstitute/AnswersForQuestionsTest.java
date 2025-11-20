package org.broadinstitute;

import org.junit.jupiter.api.Test;

/**
 * Better than nothing? Debatable.
 * Options: I could replace the use of System.out with a custom output stream that collects the results
 * into a string that these tests could examine. Or I could have each of the question methods return one or more records
 * which could be printed to stdout (normal execution) or examined by the tests. Neither feel great given the .
 */
class AnswersForQuestionsTest {

    @Test
    void allQuestions() {
        var answer = new AnswersForQuestions();
        answer.questionOne();
        answer.questionTwo();

        answer.questionThree("Malden Center", "South Station");
        answer.questionThree("Malden Center", "Wellington");
        answer.questionThree("Malden Center", "Symphony");
    }
}