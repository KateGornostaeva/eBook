package ru.kate.ebook.exceptions;

/**
 * пыталась сумничать в исключениями
 */
public class NotSupportedExtension extends Exception {
    public NotSupportedExtension(String message) {
        super("Not Supported Extension: " + message);
    }
}
