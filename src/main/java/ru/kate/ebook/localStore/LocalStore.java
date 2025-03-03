package ru.kate.ebook.localStore;

import ru.kate.ebook.zipBook.ZipBook;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocalStore {

    private final static String PATH = "./localStore/";
    private File dir;

    public LocalStore() {
        dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    public List<BookMeta> getBooks() {
        List<BookMeta> books = new ArrayList<BookMeta>();
        File[] files = dir.listFiles();
        for (File file : files) {
            try {
                books.add(ZipBook.getBookMeta(file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return books;
    }
}
