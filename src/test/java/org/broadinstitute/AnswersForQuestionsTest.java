package org.broadinstitute;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the various methods of AnswersForQuestions.
 * Subject to MBTA API availability and rate throttling. :-(
 */
class AnswersForQuestionsTest {

    private static final Logger LOG = LoggerFactory.getLogger(AnswersForQuestionsTest.class);

    @Test
    void questionOne() {
        var answer = new AnswersForQuestions();
        final List<Route> routes = answer.questionOne();
        LOG.info(routes.toString());
        assertEquals(8, routes.size());
        Route orange = new Route("Orange", "route", new RouteAttributes("Orange Line"));
        assertTrue(routes.contains(orange));
    }

    @Test
    void questionTwo() {
        var answer = new AnswersForQuestions();
        final StopInfo stopInfo = answer.questionTwo();
        assertNotNull(stopInfo);
        LOG.info(stopInfo.toString());
        assertEquals("Green-D", stopInfo.mostStops().id());
        assertEquals(25, stopInfo.maxStops());
        assertEquals("Mattapan", stopInfo.leastStops().id());
        assertEquals(8, stopInfo.minStops());
        assertNotNull(stopInfo.routesByStopName());
        assertEquals(125, stopInfo.routesByStopName().size());
        final Set<String> routesForLechmere = stopInfo.routesByStopName().get("Lechmere");
        assertNotNull(routesForLechmere);
        assertTrue(routesForLechmere.containsAll(List.of("Green Line E", "Green Line D")));
        //etc...
    }

    @Test
    void orangeOnly() {
        assertDoesNotThrow(() -> {
            var answer = new AnswersForQuestions();
            final Journey journey = answer.questionThree("Malden Center", "Wellington");
            assertNotNull(journey);
            assertEquals(1, journey.routes().size());
            assertEquals(AnswersForQuestions.NO_CONNECTING_STOP, journey.connectingStop());
        });
    }

    @Test
    void orangeToRed() {
        assertDoesNotThrow(() -> {
            var answer = new AnswersForQuestions();
            final Journey journey = answer.questionThree("Malden Center", "South Station");
            assertNotNull(journey);
            assertEquals(2, journey.routes().size());
            assertEquals("Downtown Crossing", journey.connectingStop());
        });
    }

    @Test
    void orangeToGreen() {
        assertDoesNotThrow(() -> {
            var answer = new AnswersForQuestions();
            final Journey journey = answer.questionThree("Malden Center", "Symphony");
            assertNotNull(journey);
            assertEquals(2, journey.routes().size());
            assertEquals("Haymarket", journey.connectingStop());
        });
    }

}