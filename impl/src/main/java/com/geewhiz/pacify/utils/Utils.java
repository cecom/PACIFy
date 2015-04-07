package com.geewhiz.pacify.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mozilla.universalchardet.UniversalDetector;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.PropertyNotReplacedDefect;
import com.geewhiz.pacify.replacer.PropertyFileReplacer;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class Utils {

	public static String getJarVersion() {
		URL jarURL = Utils.class.getResource("/com/geewhiz/pacify/PFListPropertyReplacer.class");
		Manifest mf;
		try {
			JarURLConnection jurlConn;
			if (jarURL.getProtocol().equals("file")) {
				return "Not a Jar";
			} else {
				jurlConn = (JarURLConnection) jarURL.openConnection();
			}
			mf = jurlConn.getManifest();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		Attributes attr = mf.getMainAttributes();
		return attr.getValue("Implementation-Version");
	}

	public static List<Defect> checkFileForNotReplacedStuff(File file) {
		List<Defect> defects = new ArrayList<Defect>();

		String fileContent = com.geewhiz.pacify.utils.FileUtils.getFileInOneString(file);

		Pattern pattern = PropertyFileReplacer.getPattern("([^}]*)", false);
		Matcher matcher = pattern.matcher(fileContent);

		while (matcher.find()) {
			String propertyId = matcher.group(1);
			Defect defect = new PropertyNotReplacedDefect(file, propertyId);
			defects.add(defect);
		}
		return defects;
	}

	public static String getEncoding(URL fileUrl) {
		try {
			return getEncoding(fileUrl.openStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getEncoding(File file) {
		try {
			return getEncoding(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private static String getEncoding(InputStream inputStream) {
		try {
			UniversalDetector detector = new UniversalDetector(null);
			int nread;
			byte[] buf = new byte[4096];

			while ((nread = inputStream.read(buf)) > 0 && !detector.isDone()) {
				detector.handleData(buf, 0, nread);
			}
			detector.dataEnd();

			inputStream.close();

			if (detector.getDetectedCharset() != null) {
				return detector.getDetectedCharset();
			} else {
				return Charset.defaultCharset().name();
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}