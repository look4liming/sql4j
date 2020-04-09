package lee.bright.sql4j.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lee.bright.sql4j.Sql4jException;

/**
 * @author Bright Lee
 */
public final class FileUtil {
	
	private static final Logger LOGGER = LoggerFactory.
			getLogger(FileUtil.class);
	
	public static Reader getCfgFileReader(String fileName) {
		if (fileName == null) {
			throw new NullPointerException();
		}
		String rootPath = Thread.currentThread().
				getContextClassLoader().
				getResource("").getPath();
		String path = rootPath + fileName;
		LOGGER.info("Configuration file path: " + path);
		File file = new File(path);
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new Sql4jException("The file is a directory.");
			}
			Reader reader = null;
			try {
				reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
				return reader;
			} catch (Exception e) {
				throw new Sql4jException("Read SQL file error.", e);
			}
		}
		LOGGER.info("Configuration file not exists.");
		String logicClassPath = '/' + fileName;
		LOGGER.info("Load configuration file from jar files. " + logicClassPath);
		URL url = FileUtil.class.getResource(logicClassPath);
		if (url == null) {
			throw new Sql4jException("No jar file contains configuration file: " + logicClassPath);
		}
		InputStream in = FileUtil.class.getResourceAsStream(logicClassPath);
		Reader reader = null;
		try {
			reader = new InputStreamReader(in, "UTF-8");
			return reader;
		} catch (UnsupportedEncodingException e) {
			throw new Sql4jException("UTF-8 not supported.", e);
		}
	}
	
	public static String getCofigurationFilePath(String fileName) {
		if (fileName == null) {
			throw new NullPointerException();
		}
		String rootPath = Thread.currentThread().
				getContextClassLoader().
				getResource("").getPath();
		String path = rootPath + fileName;
		File file = new File(path);
		if (file.exists()) {
			return file.getAbsolutePath();
		}
		String logicClassPath = '/' + fileName;
		URL url = FileUtil.class.getResource(logicClassPath);
		return url.getFile();
	}
	
	public static Reader getSqlFileReader(Class<?> clazz) {
		if (clazz == null) {
			throw new NullPointerException();
		}
		String rootPath = Thread.currentThread().
				getContextClassLoader().
				getResource("").getPath();
		String className = clazz.getName();
		String classPath = className.replaceAll("[.]+", "/");
		StringBuilder buf = new StringBuilder(200);
		buf.append(rootPath);
		buf.append(classPath);
		buf.append(".sql");
		String path = buf.toString();
		LOGGER.info("SQL file path: " + path);
		File file = new File(path);
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new Sql4jException("The file is a directory.");
			}
			Reader reader = null;
			try {
				reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
				return reader;
			} catch (Exception e) {
				throw new Sql4jException("Read SQL file error.", e);
			}
		}
		LOGGER.info("SQL file not exists.");
		String logicClassPath = '/' + classPath + ".sql";
		URL url = FileUtil.class.getResource(logicClassPath);
		if (url == null) {
			throw new Sql4jException("All jar file not contains SQL file: " + logicClassPath);
		}
		LOGGER.info("SQL file path: " + url.getFile());
		InputStream in = FileUtil.class.getResourceAsStream(logicClassPath);
		Reader reader = null;
		try {
			reader = new InputStreamReader(in, "UTF-8");
			return reader;
		} catch (UnsupportedEncodingException e) {
			throw new Sql4jException("UTF-8 not supported.", e);
		}
	}
	
	public static Reader getSqlFileReader_old(String className) {
		if (className == null) {
			throw new NullPointerException();
		}
		String rootPath = Thread.currentThread().
				getContextClassLoader().
				getResource("").getPath();
		//String className = clazz.getName();
		String classPath = className.replaceAll("[.]+", "/");
		StringBuilder buf = new StringBuilder(200);
		buf.append(rootPath);
		buf.append(classPath);
		buf.append(".sql");
		String path = buf.toString();
		LOGGER.info("SQL file path: " + path);
		File file = new File(path);
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new Sql4jException("The file is a directory.");
			}
			Reader reader = null;
			try {
				reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
				return reader;
			} catch (Exception e) {
				throw new Sql4jException("Read SQL file error.", e);
			}
		}
		LOGGER.info("SQL file not exists.");
		String logicClassPath = '/' + classPath + ".sql";
		URL url = FileUtil.class.getResource(logicClassPath);
		if (url == null) {
			throw new Sql4jException("All jar file not contains SQL file: " + logicClassPath);
		}
		LOGGER.info("SQL file path: " + url.getFile());
		InputStream in = FileUtil.class.getResourceAsStream(logicClassPath);
		Reader reader = null;
		try {
			reader = new InputStreamReader(in, "UTF-8");
			return reader;
		} catch (UnsupportedEncodingException e) {
			throw new Sql4jException("UTF-8 not supported.", e);
		}
	}
	
	public static String getSqlFilePath(Class<?> clazz) {
		if (clazz == null) {
			throw new NullPointerException();
		}
		String rootPath = Thread.currentThread().
				getContextClassLoader().
				getResource("").getPath();
		String className = clazz.getName();
		String classPath = className.replaceAll("[.]+", "/");
		StringBuilder buf = new StringBuilder(200);
		buf.append(rootPath);
		buf.append(classPath);
		buf.append(".sql");
		String path = buf.toString();
		File file = new File(path);
		if (file.exists()) {
			return file.getAbsolutePath();
		}
		String logicClassPath = '/' + classPath + ".sql";
		URL url = FileUtil.class.getResource(logicClassPath);
		return url.getFile();
	}
	
	public static String getSqlFilePath_old(String className) {
		if (className == null) {
			throw new NullPointerException();
		}
		String rootPath = Thread.currentThread().
				getContextClassLoader().
				getResource("").getPath();
		String classPath = className.replaceAll("[.]+", "/");
		StringBuilder buf = new StringBuilder(200);
		buf.append(rootPath);
		buf.append(classPath);
		buf.append(".sql");
		String path = buf.toString();
		File file = new File(path);
		if (file.exists()) {
			return file.getAbsolutePath();
		}
		String logicClassPath = '/' + classPath + ".sql";
		URL url = FileUtil.class.getResource(logicClassPath);
		return url.getFile();
	}

}
