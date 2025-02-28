package ru.kate.ebook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import ru.kate.ebook.configuration.NetworkConfig;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

@Slf4j
public class Network {

    private final static ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
    private final static HttpClient client = HttpClient.newHttpClient();
    private final static int TIMEOUT = 5;


    private final NetworkConfig nc;
    private static String jwt = null;

    public Network(NetworkConfig nc) {
        this.nc = nc;
    }

    public void login() throws URISyntaxException, IOException, InterruptedException {
        jwt = getToken();
    }

    public void login(String username, String password) throws URISyntaxException, IOException, InterruptedException {
        nc.setUsername(username);
        nc.setPassword(password);
        login();
    }

    public void signUp() throws URISyntaxException, IOException, InterruptedException {
        SignUpRequestDto dto = new SignUpRequestDto();
        dto.setUsername(nc.getUsername());
        dto.setPassword(nc.getPassword());
        dto.setEmail("kate@gmail.com");
        HttpRequest postRequest = getPostRequest("/auth/sign-up", mapper.writeValueAsString(dto));
        HttpResponse<String> httpResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        log.info(httpResponse.body());

    }

    public List<String> getBooks() throws URISyntaxException, IOException, InterruptedException {
        if (jwt == null) {
            login();
        }
        HttpRequest httpRequest = getGetRequest("/books/list", "page=0&size=10");
        HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        httpResponse.body();
        return mapper.readValue(httpResponse.body(), List.class);
    }

    private String getToken() throws URISyntaxException, IOException, InterruptedException {

        SignInRequestDto signInRequestDto = new SignInRequestDto();
        signInRequestDto.setUsername(nc.getUsername());
        signInRequestDto.setPassword(nc.getPassword());
        String s = mapper.writeValueAsString(signInRequestDto);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(nc.getHost() + ":" + nc.getPort() + "/auth/sign-in"))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(TIMEOUT))
                .POST(HttpRequest.BodyPublishers.ofString(s))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ResponseTokenDto responseTokenDto = mapper.readValue(response.body(), ResponseTokenDto.class);
        return responseTokenDto.getToken();
    }

    private HttpRequest getPostRequest(String endpoint, String body) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(new URI(nc.getHost() + ":" + nc.getPort() + endpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + jwt)
                .timeout(Duration.ofSeconds(TIMEOUT))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    private HttpRequest getGetRequest(String endpoint, String params) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(new URI(nc.getHost() + ":" + nc.getPort() + endpoint + "?" + params))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + jwt)
                .timeout(Duration.ofSeconds(TIMEOUT))
                .GET()
                .build();
    }
}
