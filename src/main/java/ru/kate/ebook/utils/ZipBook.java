package ru.kate.ebook.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.image.Image;
import ru.kate.ebook.localStore.BookMeta;

import java.io.*;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.Collections;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/*
 ** Служебный клас по работе с zip файлом
 *
 */
public class ZipBook {

    private final static ObjectMapper mapper = new ObjectMapper();

    //создать zip архив и положить в него книгу
    //создаётся в том же каталоге, что и книга
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

    //добавить файл в zip архив
    public static File addFile(File zipBookFile, File file) {
        Map<String, String> env = Map.of("create", "true");
        Path path = Paths.get(zipBookFile.getAbsolutePath());
        URI uri = URI.create("jar:" + path.toUri());
        try (FileSystem fs = FileSystems.newFileSystem(uri, env)) {
            Path nf = fs.getPath(file.getName());
            Files.write(nf, Files.readAllBytes(Paths.get(file.getAbsolutePath())), StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return zipBookFile;
    }

    //создать zip архив и положить в него книгу и файл с тестами
    public static File addBookAndTest(File bookFile, File testFile) throws IOException {
        return addFile(addBook(bookFile), testFile);
    }

    //получить метаданные книги из zip архива
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
                    bookMeta.setBookFileName(rawMeta.getBookFileName());
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

    //извлечь файл книги по данным из метаданных книги
    public static File getBookFile(BookMeta bookMeta) throws IOException {
        File outputFile = null;
        try (ZipFile zipFile = new ZipFile(String.valueOf(bookMeta.getPath()))) {
            for (ZipEntry entry : Collections.list(zipFile.entries())) {
                if (!entry.isDirectory() && entry.getName().equals(bookMeta.getBookFileName())) {
                    InputStream inputStream = zipFile.getInputStream(entry);
                    Path tempDir = Files.createTempDirectory(Paths.get(System.getProperty("java.io.tmpdir")), "ebookTemp");
                    outputFile = new File(tempDir + "/" + bookMeta.getBookFileName());
                    Files.copy(inputStream, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
        return outputFile;
    }
}
