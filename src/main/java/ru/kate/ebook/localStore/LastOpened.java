package ru.kate.ebook.localStore;

import java.io.File;

public class LastOpened {

    public final static String PATH = "." + File.separator + "lastOpened" + File.separator;
    private File dir;

    public LastOpened() {
        dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }
}
