package org.broadinstitute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.GZIPInputStream;

//TODO move the URLS here.
import static org.broadinstitute.AnswersForQuestions.MBTA_ROUTES_MIN;
import static org.broadinstitute.AnswersForQuestions.STOPS_BY_ROUTE;

public class APIClient {

    private static final Logger LOG = LoggerFactory.getLogger(APIClient.class);

    // NB: Both of these are thread-safe which allows us to implement this as a straight function
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<Route> fetchAllRoutes() {
        String json = fetch(MBTA_ROUTES_MIN);
        Routes response = objectMapper.readValue(json, Routes.class);
        return response.data();
    }

    public static List<Stop> fetchStopsForRoute(String id) {
        String json = fetch(STOPS_BY_ROUTE + id);
        Stops stops = objectMapper.readValue(json, Stops.class);
        return stops.data();
    }

    // Convenience version.
    public static String fetch(String endpoint) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .GET()
                .header("Accept-Encoding", "gzip") // prefer gzip compression
                .build();
        return fetch(request);
    }

    // Prefer this method to enable reuse of HttpRequest objects.
    public static String fetch(HttpRequest request) {
        try {
            // The range of 4xx errors could suggest to the caller how failures should be handled.
            // e.g. for 429 the caller might want to simply wait for a period before retrying whereas
            // a 400 suggests there's something wrong with the request and will never succeed.
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            return switch (response.statusCode()) { // NB: This covers only the documented response codes.
                case 200 -> {
                    if (response.headers().firstValue("Content-Encoding").orElse("").equalsIgnoreCase("gzip")) {
                        //LOG.info("Received requested gzip stream for {}", request.uri());
                        try (InputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(response.body()))) {
                            yield new String(gzipInputStream.readAllBytes(), StandardCharsets.UTF_8);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        //LOG.info("Received uncompressed stream for {}", request.uri());
                        yield new String(response.body(), StandardCharsets.UTF_8);
                    }
                }
                case 400, 403, 429 -> {
                    LOG.error("Got {} for request, {}", response.statusCode(), request.uri());
                    yield null;
                }
                default-> {
                    LOG.error("Unexpected error {} for request, {}", response.statusCode() ,request.uri());
                    yield null;
                }

            };
        } catch (IOException | InterruptedException e) {
            LOG.error("fetch failed: {}", e.getMessage());
            return null; // Optional instead?
        }
    }

}
