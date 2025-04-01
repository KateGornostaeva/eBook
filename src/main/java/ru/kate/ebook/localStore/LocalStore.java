package ru.kate.ebook.localStore;

import ru.kate.ebook.utils.ZipBook;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocalStore {

    public final static String PATH = "." + File.separator + "localStore" + File.separator;
    private File dir;

    public LocalStore() {
        dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    public List<BookMeta> getBooks() {
        List<BookMeta> books = new ArrayList<>();
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
