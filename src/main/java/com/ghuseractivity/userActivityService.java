package com.ghuseractivity;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "github-activity", description = "Retrieves github activity using given username")
public class userActivityService implements Runnable {

    @Parameters(index = "0", description = "username")
    private String username;

    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(20))
            .build();

    @Override
    public void run() {
        fetchUserActivity();
    }

    public void fetchUserActivity() {
        String url = "https://api.github.com/users/" + username + "/events";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMinutes(1))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode events = mapper.readTree(response);

            for (JsonNode event : events) {
                String type = event.get("type").asText();
                String repo = event.path("repo").path("name").asText();
                System.out.println(formatEvent(type, repo, event));
            }

        } catch (Exception e) {
            System.err.println("Error fetching activity: " + e.getMessage());
        }
    }

    private String formatEvent(String type, String repo, JsonNode event) {
        JsonNode payload = event.path("payload");

        switch (type) {
            case "PushEvent":
                int commits = payload.path("size").asInt(0); // default 0
                return "Pushed " + commits + " commits to " + repo;

            case "IssuesEvent":
                String action = payload.path("action").asText("performed an action on");
                return action + " an issue in " + repo;

            case "WatchEvent":
                return "Starred " + repo;

            case "ForkEvent":
                return "Forked " + repo;

            case "CreateEvent":
                String refType = payload.path("ref_type").asText("something");
                return "Created " + refType + " in " + repo;

            default:
                return "Did " + type + " on " + repo;
        }
    }
}