package ru.kate.ebook.zipBook;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

class ZipBookTest {

    @org.junit.jupiter.api.Test
    void addBook() throws IOException {
        String sourceFile = "test1.txt";
        File fileToZip = new File(sourceFile);
        ZipFile zipFile = new ZipFile(fileToZip);
    }
}