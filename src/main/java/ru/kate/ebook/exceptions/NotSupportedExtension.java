package ru.kate.ebook.exceptions;

public class NotSupportedExtension extends Exception {
    public NotSupportedExtension(String message) {
        super("Not Supported Extension: " + message);
    }
}
