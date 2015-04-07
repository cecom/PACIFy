package com.geewhiz.pacify.utils;

import java.io.*;
import java.net.MalformedURLException;
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
        String encoding = Utils.getEncoding(file);
        try {
            return new String(buffer, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getFileAsLines(URL fileURL) {
        InputStream is = null;
        try {
            is = fileURL.openStream();
            String encoding = Utils.getEncoding(fileURL);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, encoding));
            List<String> lines = new ArrayList<String>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
            bufferedReader.close();
            return lines;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
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
