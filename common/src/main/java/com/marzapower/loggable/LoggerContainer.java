/*
 * This file is part of "Loggable".
 * 
 * "Loggable" is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * "Loggable" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with "Loggable".  If not, see <http://www.gnu.org/licenses/>
 */
package com.marzapower.loggable;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.marzapower.loggable.Loggable.LogLevel;

/**
 * The {@link Logger} container class. This class has an {@link HashMap}
 * instance that will contain all the references to {@link Logger} instances to
 * be used by the application classes
 * 
 * @author Daniele Di Bernardo
 * @version 1.2.0
 * 
 */
@Loggable(exclude = true)
public class LoggerContainer {

	private static Map<String, Logger> instances;
	private static final String VOID_LOGGER = "";
	private static final String ROOT_LOGGER = Object.class.getCanonicalName();

	/**
	 * Loads in memory the void logger instance and the root logger instance
	 */
	static {
		instances = new HashMap<String, Logger>();
		
		Logger voidLogger = Logger.getLogger(VOID_LOGGER);
		voidLogger.setLevel(Level.OFF);
		instances.put(VOID_LOGGER, voidLogger);

		Logger rootLogger = Logger.getRootLogger();
		instances.put(ROOT_LOGGER, rootLogger);
	}

	/**
	 * All methods of the class should be accessed in a static way
	 */
	private LoggerContainer() {
	}
	
	/**
	 * Given a class returns a {@link Logger} instance with its default configuration.
	 *
	 * @param clazz
	 *            the {@link Class} object that links to the caller class
	 *
	 * @return a {@link Logger} instance suitable for <code>clazz</code>,
	 *         with its default configuration
	 */
	protected static Logger getInstance(Class<?> clazz) {
		return getInstance(clazz, null);
	}

	/**
	 * Given an annotation, and a class, returns a {@link Logger} instance. If a
	 * suitable instance already exists in the local container, that instances
	 * is returned. Otherwise a new instance is created, added to the container
	 * and then returned.
	 * <p>
	 * This approach permits to reduce the total number of objects created at
	 * runtime to handle all the log request events.
	 * 
	 * @param clazz
	 *            the {@link Class} object that links to the caller class
	 * @param annotation
	 *            the annotation that will be use to handle the logic of
	 *            creation of the {@link Logger} instance
	 * @return a {@link Logger} instance suitable for <code>clazz</code>,
	 *         adapted as required by the annotation
	 */
	protected static Logger getInstance(Class<?> clazz, Loggable annotation) {
		// This control avoids a strange NullPointerException when calling this method
		if (instances == null) {
			instances = new HashMap<String, Logger>();
		}
		
		Logger logger = instances.get(clazz);

		if (logger == null) {
			logger = createNewLoggerFor(clazz, annotation);
			instances.put(clazz.getCanonicalName(), logger);
		}

		return logger;
	}

	/**
	 * Given an annotation, and a <code>log4j</code> logger name, returns a
	 * {@link Logger} instance. If a suitable instance already exists in the
	 * local container, that instances is returned. Otherwise a new instance is
	 * created, added to the container and then returned.
	 * <p>
	 * This approach permits to reduce the total number of objects created at
	 * runtime to handle all the log request events.
	 * 
	 * @param loggerName
	 *            the name of the <code>log4j</code> logger to be returned
	 * @param annotation
	 *            the annotation that will be use to handle the logic of
	 *            creation of the {@link Logger} instance
	 * @return a {@link Logger} instance whose name is <code>loggerName</code>,
	 *         adapted as required by the annotation
	 */
	protected static Logger getInstance(String loggerName, Loggable annotation) {
		Logger logger = instances.get(loggerName);

		if (logger == null) {
			logger = createNewLoggerFor(loggerName, annotation);
			instances.put(loggerName, logger);
		}

		return logger;
	}

	/**
	 * Returns a void logger, that is to say a logger that will print nothing.
	 * 
	 * @return a void logger
	 */
	public static Logger getVoidLogger() {
		return instances.get(null);
	}

	/**
	 * Returns the log4j root logger
	 * 
	 * @return the log4j root logger
	 */
	public static Logger getRootLogger() {
		return instances.get(ROOT_LOGGER);
	}

	/**
	 * Locally creates an instance of {@link Logger}. This instance will be
	 * created for the <code>clazz</code> object. Also, the annotation is taken
	 * into account because of the {@link Loggable#logLevel()} parameter: the
	 * new instance will be created with the correct required log level.
	 * 
	 * @param clazz
	 *            the caller class
	 * @param annotation
	 *            the {@link Loggable} annotation of the caller class
	 * @return the new instance of the required logger
	 */
	private static Logger createNewLoggerFor(Class<?> clazz, Loggable annotation) {
		Logger logger = Logger.getLogger(clazz);
		
		if (annotation == null) {
			return logger;
		} else {
			return implementLogic(logger, annotation);
		}
	}

	/**
	 * Locally creates an instance of {@link Logger}. This instance will be the
	 * log4j logger whose name is defined with the <code>loggerName</code>
	 * parameter. Also, the annotation is taken into account because of the
	 * {@link Loggable#logLevel()} parameter: the new instance will be created
	 * with the correct required log level.
	 * 
	 * @param loggerName
	 *            the name of the required logger
	 * @param annotation
	 *            the {@link Loggable} annotation of the caller class
	 * @return the new instance of the required logger
	 */
	private static Logger createNewLoggerFor(String loggerName,
			Loggable annotation) {
		Logger logger = Logger.getLogger(loggerName);
		return implementLogic(logger, annotation);
	}

	/**
	 * Implements the logic for the retrieval of the logger instance.
	 * 
	 * @param logger
	 *            the asked logger
	 * @param annotation
	 *            the {@link Loggable} annotation of the caller class
	 * @return the adapted logger, coherent with the {@link Loggable} annotation
	 */
	private static Logger implementLogic(Logger logger, Loggable annotation) {
		if (annotation == null || annotation.logLevel() == null)
			return logger;

		LogLevel level = annotation.logLevel();
		if (level == null || level == LogLevel.LOG4J)
			return logger;

		setLogLevel(logger, annotation.logLevel());
		return logger;
	}

	/**
	 * Sets the correct log level for the logger
	 * 
	 * @param logger
	 *            the retrieved logger
	 * @param level
	 *            the required custom log level
	 */
	private static void setLogLevel(Logger logger, LogLevel level) {
		if (level == LogLevel.DEBUG)
			logger.setLevel(Level.DEBUG);
		else if (level == LogLevel.ERROR)
			logger.setLevel(Level.ERROR);
		else if (level == LogLevel.TRACE)
			logger.setLevel(Level.TRACE);
		else if (level == LogLevel.WARN)
			logger.setLevel(Level.WARN);
		else if (level == LogLevel.INFO)
			logger.setLevel(Level.INFO);
		else if (level == LogLevel.FATAL)
			logger.setLevel(Level.FATAL);
	}

}