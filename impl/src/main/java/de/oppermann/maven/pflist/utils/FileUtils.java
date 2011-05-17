package de.oppermann.maven.pflist.utils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class FileUtils {

    public static String getFileInOneString(File file) {
        byte[] buffer;
        try {
            buffer = new byte[(int) file.length()];
            BufferedInputStream f = null;
            try {
                f = new BufferedInputStream(new FileInputStream(file));
                f.read(buffer);
            } finally {
                if (f != null)
                    f.close();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new String(buffer);
    }

    public static List<String> getFileAsLines(URL fileURL) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileURL.openStream()));
            List<String> lines = new ArrayList<String>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
            bufferedReader.close();
            return lines;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static URL getFileUrl(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

}
