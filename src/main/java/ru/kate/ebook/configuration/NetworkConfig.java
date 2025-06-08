package ru.kate.ebook.configuration;

import lombok.Builder;
import lombok.Data;

/**
 * настройки адреса сервера
 */
@Data
@Builder
public class NetworkConfig {

    @Builder.Default
    private String port = "8080"; //порт на котором запущен SpringBoot

    @Builder.Default
    private String username = "guest";

    @Builder.Default
    private String password = "nopassword";

    @Builder.Default
    private String host = "http://localhost"; // адрес сервера, по умолчанию тот же комп, где и само приложение
}
