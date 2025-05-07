package ru.kate.ebook.configuration;

public enum Role {
    ROLE_STUDENT("Студент"),
    ROLE_TEACHER("Педагог"),
    ROLE_GUEST("Гость"),
    ROLE_ADMIN("Администратор");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
