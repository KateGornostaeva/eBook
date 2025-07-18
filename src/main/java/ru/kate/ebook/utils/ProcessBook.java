package ru.kate.ebook.utils;

import com.kursx.parser.fb2.*;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.SAXException;
import ru.kate.ebook.Context;
import ru.kate.ebook.exceptions.NotSupportedExtension;
import ru.kate.ebook.exceptions.WrongFileFormat;
import ru.kate.ebook.pdfdisplayer.PDFDisplayer;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

@Slf4j
public class ProcessBook {

    private final Context ctx;

    public ProcessBook(Context ctx) {
        this.ctx = ctx;
    }

    public void processFb2(File file) throws ParserConfigurationException, IOException, SAXException {

        FictionBook fb = new FictionBook(file);

        Path tempDir = Files.createTempDirectory(Paths.get(System.getProperty("java.io.tmpdir")), "ebookTemp");
        extractImgFromFb2(tempDir, fb);

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
                    if (element instanceof P) {
                        ArrayList<Image> images = ((P) element).getImages();
                        if (images != null) {
                            for (Image image : images) {
                                String name = image.getValue().replace("#", "");
                                sb.append("<div align=\"center\">");
                                sb.append("<img src=\"file:" + File.separator + File.separator + tempDir.toString() + File.separator + name + "\">");
                                sb.append("</div>");
                            }
                        } else {
                            sb.append("<p>");
                            sb.append(element.getText()).append("\n");
                            sb.append("</p>");
                        }
                    }
                }
                sb.append("</p>");

            }
        }

        sb.append("</body></html>");
        ctx.getWebView().getEngine().loadContent(html + sb);
    }

    private void extractImgFromFb2(Path tempDir, FictionBook fb) {

        Base64.Decoder decoder = Base64.getMimeDecoder();
        Map<String, Binary> binaryMap = fb.getBinaries();
        binaryMap.forEach((key, value) -> {
            byte[] decodedBytes = decoder.decode(value.getBinary());
            try {
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(decodedBytes));
                File outputfile = new File(tempDir + File.separator + key);
                switch (value.getContentType()) {
                    case "image/png":
                        ImageIO.write(img, "png", outputfile);
                        break;
                    case "image/jpeg":
                        ImageIO.write(img, "jpg", outputfile);
                        break;
                    case "image/gif":
                        ImageIO.write(img, "gif", outputfile);
                        break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

    private void processPdf(File file) throws IOException {
        PDFDisplayer displayer = new PDFDisplayer(file);
        displayer.createWebView(ctx.getWebView());
    }

    public void checkExtAndProcess(File file) throws IOException, NotSupportedExtension, WrongFileFormat, SQLException {
        int indexOf = file.getName().lastIndexOf(".");
        if (indexOf >= 0) {

            String ext = file.getName().substring(indexOf).toLowerCase();
            switch (ext) {
                case ".fb2":
                    if (!checkFb2(file)) throw new WrongFileFormat();
                    try {
                        processFb2(file);
                        break;
                    } catch (ParserConfigurationException | SAXException | IOException e) {
                        throw new WrongFileFormat();
                    }

                case ".epub":
                    break;

                case ".pdf":
                    processPdf(file);
                    break;

                default:
                    throw new NotSupportedExtension(file.getName());
            }
        } else {
            throw new IOException("Aren't extension for file: " + file.getName());
        }
    }

    private boolean checkFb2(File file) {
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

    //для формирования заголовка FB2
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
        sb.append("<style type=\"text/css\">");
        sb.append("body {font-size: 200%;}");
        sb.append("</style>");
        sb.append("</head>");
        sb.append("<body>");
        return sb.toString();
    }
}
