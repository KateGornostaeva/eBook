package ru.kate.ebook.zipBook;

import ru.kate.ebook.test.Test;
import ru.kate.ebook.utils.ZipBook;

import java.io.File;
import java.io.IOException;

class ZipBookTest {

    @org.junit.jupiter.api.Test
    void addBook() throws IOException {
        File file = new File("C:\\Users\\angor\\OneDrive\\Desktop\\95614706.fb2");
        Test test = new Test();
        test.setName("Test test");
        File bookAndTest = ZipBook.addBookAndTest(file, test.getFile());
        bookAndTest.getName();

    }
}