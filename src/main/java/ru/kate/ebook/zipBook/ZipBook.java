package ru.kate.ebook.zipBook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipBook {

    public static File addBook(File bookFile) throws IOException {

        File outFile = new File(bookFile.getAbsolutePath() + ".zip");
        FileOutputStream fos = new FileOutputStream(outFile);
        ZipOutputStream zip = new ZipOutputStream(fos);
        FileInputStream fis = new FileInputStream(bookFile);
        ZipEntry entry = new ZipEntry(bookFile.getName());
        zip.putNextEntry(entry);

        byte[] bytes = new byte[4096];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zip.write(bytes, 0, length);
        }

        zip.closeEntry();
        zip.close();
        fis.close();
        fos.close();

        return outFile;
    }

    private File addTest(File zipBookFile, File testFile) {
        return null;
    }

    private File addBookAndTest(File bookFile, File testFile) throws IOException {
        return addTest(addBook(bookFile), testFile);
    }
}
