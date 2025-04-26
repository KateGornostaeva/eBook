package ru.kate.ebook.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import ru.kate.ebook.configuration.NetworkConfig;
import ru.kate.ebook.configuration.Role;
import ru.kate.ebook.exceptions.WrongAuthorisation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

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

    public void login() throws URISyntaxException, IOException, InterruptedException, WrongAuthorisation {
        jwt = getToken();
    }

    public void login(String username, String password) throws URISyntaxException, IOException, InterruptedException, WrongAuthorisation {
        nc.setUsername(username);
        nc.setPassword(password);
        login();
    }

    public void signUp(SignUpRequestDto dto) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(new URI(nc.getHost() + ":" + nc.getPort() + "/auth/sign-up"))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(TIMEOUT))
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(dto)))
                .build();
        HttpResponse<String> httpResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        log.info(httpResponse.body());
    }

    public Role getRole() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest httpRequest = getGetRequest("/user/role", "");
        HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        String body = httpResponse.body();
        return Role.valueOf(body);
    }

    public ProfileDto getProfile() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest httpRequest = getGetRequest("/user/profile", "");
        HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        String body = httpResponse.body();
        return mapper.readValue(body, ProfileDto.class);
    }

    public String upLoadBook(String endpoint, File zipFile) throws URISyntaxException, IOException, InterruptedException {

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(new URI(nc.getHost() + ":" + nc.getPort() + endpoint))
                .header("Content-Type", "application/octet-stream")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + jwt)
                .timeout(Duration.ofSeconds(TIMEOUT))
                .POST(HttpRequest.BodyPublishers.ofFile(zipFile.toPath()))
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public File downloadZipFile(UUID idBook) throws URISyntaxException, IOException, InterruptedException {

        HttpRequest httpRequest = getGetRequestNoJwt("/search/getBook", "id=" + idBook);
        HttpResponse<InputStream> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
        InputStream inputStream = httpResponse.body();
        File outputFile = new File(String.valueOf(Paths.get(System.getProperty("java.io.tmpdir") + File.separator + idBook + ".zip")));
        Files.copy(inputStream, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        inputStream.close();
        return outputFile;
    }

    public Page getPageBooks() {
        Page page = new Page();
        try {
            HttpRequest httpRequest = getGetRequestNoJwt("/books/list", "page=0&size=100");
            HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            page = mapper.readValue(httpResponse.body(), Page.class);
        } catch (Exception e) {

        }
        return page;
    }

    public List<BookDto> searchBooks(String query) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest httpRequest = getGetRequestNoJwt("/search/books", "query=" + query);
        HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(httpResponse.body(), mapper.getTypeFactory().constructCollectionType(List.class, BookDto.class));
    }

    private String getToken() throws URISyntaxException, IOException, InterruptedException, WrongAuthorisation {

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
        if (response.statusCode() == 403) {
            throw new WrongAuthorisation();
        }
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

    private HttpRequest getGetRequestNoJwt(String endpoint, String params) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(new URI(nc.getHost() + ":" + nc.getPort() + endpoint + "?" + params))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(TIMEOUT))
                .GET()
                .build();
    }
}
