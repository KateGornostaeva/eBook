package ru.kate.ebook;

import com.kursx.parser.fb2.*;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.SAXException;
import ru.kate.ebook.exceptions.NotSupportedExtension;
import ru.kate.ebook.exceptions.WrongFileFormat;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class ConverterBook {

    private final Context ctx;

    public ConverterBook(Context ctx) {
        this.ctx = ctx;
    }

    public String convertFromFB2(File file) throws ParserConfigurationException, IOException, SAXException {

        FictionBook fb = new FictionBook(file);
        Description description = fb.getDescription();
        String lang = ctx.getLocale().getLanguage();
        String title = "No Title";
        if (description != null && description.getTitleInfo() != null) {
            lang = description.getTitleInfo().getLang();
            title = description.getTitleInfo().getBookTitle();
        }
        String html = getHtmlHead(lang, title);
        StringBuilder sb = new StringBuilder();
        sb.append("<H3>").append(title).append("</H3>");

        Annotation annotation = fb.getAnnotation();
        if (annotation != null && annotation.getElements() != null) {
            for (Element element : annotation.getElements()) {
                sb.append(element.getText()).append("\n");
            }
        }

        Body body = fb.getBody();
        if (body != null && body.getSections() != null) {
            for (Section section : body.getSections()) {
                sb.append("<p>");
                for (Element element : section.getElements()) {
                    sb.append("<p>");
                    sb.append(element.getText()).append("\n");
                    sb.append("</p>");
                }
                sb.append("</p>");

            }
        }

        sb.append("</body></html>");
        return html + sb;
    }

    public void checkExt(File file) throws IOException, NotSupportedExtension, WrongFileFormat {
        int indexOf = file.getName().lastIndexOf(".");
        if (indexOf >= 0) {
            String ext = file.getName().substring(indexOf).toLowerCase();
            switch (ext) {
                case "etb":
                    if (!checkEtb(file)) throw new WrongFileFormat();
                    break;
                case "fb2":
                    if (!checkFb2(file)) throw new WrongFileFormat();
                    break;

                default:
                    throw new NotSupportedExtension(file.getName());
            }
        } else {
            throw new IOException("Aren't extension for file: " + file.getName());

        }
    }

    private boolean checkEtb(File file) throws IOException, NotSupportedExtension {
        return true;
    }

    private boolean checkFb2(File file) throws IOException, NotSupportedExtension, WrongFileFormat {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine().trim();
            if (!line.startsWith("<")) {
                return false;
            }
            while (!line.endsWith("?>")) {
                line += "\n" + br.readLine().trim();
            }
            if (!line.toLowerCase().contains("xml")) {
                return false;
            }

        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return false;
        }

        return true;
    }

    private String hashFile(File file) throws IOException, NoSuchAlgorithmException {
        byte[] data = Files.readAllBytes(file.toPath());
        byte[] hash = MessageDigest.getInstance("SHA-512").digest(data);
        return new BigInteger(1, hash).toString(16);
    }

    private String buildDirPath(File file) throws IOException, NoSuchAlgorithmException {
        return ctx.getMc().getTempDir() + "/" + hashFile(file);
    }

    private String getHtmlHead(String lang, String title) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<html lang=\"");
        sb.append(lang);
        sb.append("\">");
        sb.append("<head>");
        sb.append("<meta charset=\"utf-8\">");
        sb.append("<title>");
        sb.append(title);
        sb.append("</title>");
        sb.append("</head>");
        sb.append("<body>");
        return sb.toString();
    }
}
