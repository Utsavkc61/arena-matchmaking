import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CONCURRENCY STRESS TEST for the Arena matchmaking queue.
 *
 * What this does:
 *   1. Creates N players via POST /api/players (one at a time - this part
 *      doesn't need to be concurrent, we just need N real players to exist).
 *   2. Fires all N "join queue" requests AT THE SAME TIME, using a thread
 *      pool - this is the part that actually tests thread-safety, since
 *      real concurrent requests can interleave in ways a simple loop of
 *      curl commands never would.
 *   3. Waits for the matchmaking engine's background ticks to run.
 *   4. Tells you what to check in Postgres afterward to confirm correctness.
 *
 * Run:
 *   javac StressTest.java
 *   java StressTest
 *
 * Make sure arena-core is already running on localhost:8080 first.
 */
public class StressTest {

    private static final int PLAYER_COUNT = 100;
    private static final String BASE_URL = "http://localhost:8080";

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // ---- Step 1: create PLAYER_COUNT real players, one at a time ----
        System.out.println("Creating " + PLAYER_COUNT + " players...");
        List<Long> playerIds = new ArrayList<>();

        for (int i = 1; i <= PLAYER_COUNT; i++) {
            String body = "{\"name\":\"StressPlayer" + i + "\",\"rating\":1000}";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/players"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Long id = extractId(response.body());
            if (id != null) {
                playerIds.add(id);
            }
        }
        System.out.println("Created " + playerIds.size() + " players. IDs range from "
                + playerIds.get(0) + " to " + playerIds.get(playerIds.size() - 1));

        // ---- Step 2: fire ALL "join queue" requests at once, concurrently ----
        // ExecutorService gives us a pool of real threads. Instead of one
        // request finishing before the next starts (like a normal loop),
        // we submit all requests near-simultaneously and let the pool run
        // many of them in parallel.
        ExecutorService executor = Executors.newFixedThreadPool(20);

        // CountDownLatch lets the main thread wait until ALL submitted
        // tasks have actually finished, before moving on. Think of it as
        // a shared countdown: starts at playerIds.size(), each finished
        // task decrements it by 1, and await() blocks until it hits 0.
        CountDownLatch latch = new CountDownLatch(playerIds.size());

        System.out.println("Firing " + playerIds.size() + " concurrent join-queue requests...");

        for (Long id : playerIds) {
            executor.submit(() -> {
                try {
                    HttpRequest joinRequest = HttpRequest.newBuilder()
                            .uri(URI.create(BASE_URL + "/api/queue/join/" + id))
                            .POST(HttpRequest.BodyPublishers.noBody())
                            .build();
                    client.send(joinRequest, HttpResponse.BodyHandlers.ofString());
                } catch (Exception e) {
                    System.out.println("Join failed for player " + id + ": " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // block here until every single join request has completed
        executor.shutdown();
        System.out.println("All join requests completed.");

        // ---- Step 3: give the matchmaking engine time to run its ticks ----
        System.out.println("Waiting 12 seconds for the matchmaking engine to process the queue...");
        Thread.sleep(12000);

        System.out.println();
        System.out.println("=== Stress test done. Now verify in Postgres: ===");
        System.out.println("docker exec -it arena-postgres psql -U arena -d arena -c \"SELECT COUNT(*) FROM match WHERE playera_id BETWEEN "
                + playerIds.get(0) + " AND " + playerIds.get(playerIds.size() - 1) + " OR playerb_id BETWEEN "
                + playerIds.get(0) + " AND " + playerIds.get(playerIds.size() - 1) + ";\"");
        System.out.println("Expected: " + (PLAYER_COUNT / 2) + " matches (since " + PLAYER_COUNT + " players, 2 per match)");
    }

    // Tiny helper: pulls the "id" value out of a raw JSON response string
    // like {"id":42,"name":"StressPlayer1","rating":1000} - just enough
    // regex to grab the number, not a real JSON parser, since we don't
    // want to pull in a dependency for a throwaway test script.
    private static Long extractId(String json) {
        Pattern pattern = Pattern.compile("\"id\":(\\d+)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        return null;
    }
}
