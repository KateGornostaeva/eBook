package ru.kate.ebook.configuration;

import lombok.Builder;
import lombok.Data;

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
