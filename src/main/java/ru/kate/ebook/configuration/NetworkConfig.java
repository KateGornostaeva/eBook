package ru.kate.ebook.configuration;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NetworkConfig {

    @Builder.Default
    private String port = "8080";

    @Builder.Default
    private String username = "Jonson";

    @Builder.Default
    private String password = "my_1secret1_password";

    @Builder.Default
    private String host = "http://localhost";

    @Builder.Default
    private String wsHost = "ws://localhost";

    @Builder.Default
    private String wsEndpoint = "/chat";
}