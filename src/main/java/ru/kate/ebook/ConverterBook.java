package ru.kate.ebook;

import ru.kate.ebook.exceptions.NotSupportedExtension;
import ru.kate.ebook.exceptions.WrongFileFormat;

import java.io.File;
import java.io.IOException;

public class ConverterBook {

    public void checkExt(File file) throws IOException, NotSupportedExtension, WrongFileFormat {
        int indexOf = file.getName().lastIndexOf(".");
        if (indexOf >= 0) {
            String ext = file.getName().substring(indexOf).toLowerCase();
            switch (ext) {
                case "etb":
                    if (!checkEtb(file)) throw new WrongFileFormat();
                    break;
                case "fb2":


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
}
