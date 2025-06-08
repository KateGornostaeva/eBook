package ru.kate.ebook.configuration;

import lombok.Builder;
import lombok.Data;

/**
 * хотела сделать что то оригинальное но не получилось
 */
@Data
@Builder
public class GraphicsConfig {

    @Builder.Default
    private Boolean fullScreen = false;
    @Builder.Default
    private int screenWidth = 800;
    @Builder.Default
    private int screenHeight = 480;

}
