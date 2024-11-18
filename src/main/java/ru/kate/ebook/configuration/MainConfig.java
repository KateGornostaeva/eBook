package ru.kate.ebook.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainConfig {

    @Builder.Default
    private String locale = "ru";

    @Builder.Default
    private Boolean autoLogin = true;


}
