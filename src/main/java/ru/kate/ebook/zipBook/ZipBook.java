package ru.kate.ebook.zipBook;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.image.Image;
import ru.kate.ebook.localStore.BookMeta;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipBook {

    private final static ObjectMapper mapper = new ObjectMapper();

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

    public File addTest(File zipBookFile, File testFile) {
        return null;
    }

    public File addBookAndTest(File bookFile, File testFile) throws IOException {
        return addTest(addBook(bookFile), testFile);
    }

    public static BookMeta getBookMeta(File zipBookFile) throws IOException {

        BookMeta bookMeta = new BookMeta();
        bookMeta.setPath(zipBookFile.toPath());

        try (ZipFile zipFile = new ZipFile(zipBookFile)) {

            for (ZipEntry entry : Collections.list(zipFile.entries())) {
                if (!entry.isDirectory() && entry.getName().equals(BookMeta.META_NAME)) {
                    InputStream inputStream = zipFile.getInputStream(entry);
                    BookMeta rawMeta = mapper.readValue(inputStream, BookMeta.class);
                    bookMeta.setAuthor(rawMeta.getAuthor());
                    bookMeta.setTitle(rawMeta.getTitle());
                    bookMeta.setFileName(rawMeta.getFileName());
                    bookMeta.setIsTestIn(rawMeta.getIsTestIn());
                }
                if (!entry.isDirectory() && entry.getName().equals(BookMeta.COVER_NAME)) {
                    InputStream inputStream = zipFile.getInputStream(entry);
                    bookMeta.setCover(new Image(inputStream));
                }
            }
        }
        return bookMeta;
    }

    public static File getBookFile(BookMeta bookMeta) throws IOException {
        File outputFile = null;
        try (ZipFile zipFile = new ZipFile(String.valueOf(bookMeta.getPath()))) {
            for (ZipEntry entry : Collections.list(zipFile.entries())) {
                if (!entry.isDirectory() && entry.getName().equals(bookMeta.getFileName())) {
                    InputStream inputStream = zipFile.getInputStream(entry);
                    Path tempDir = Files.createTempDirectory(Paths.get(System.getProperty("java.io.tmpdir")), "ebookTemp");
                    outputFile = new File(tempDir + "/" + bookMeta.getFileName());
                    Files.copy(inputStream, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
        return outputFile;
    }
}
