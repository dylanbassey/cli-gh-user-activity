package com.ghuseractivity;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

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
                .header("Content-Type", "application/json")
                .GET()
                .build();

        try {
            String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            System.out.println(response);
        } catch (Exception e) {
            System.err.println("Error fetching activity: " + e.getMessage());
        }
    }
}