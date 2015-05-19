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

import org.apache.log4j.Logger;

/**
 * This is the global Logger factory class. It's only public method is
 * {@link Log#get()}, which returns a suitable logger for the calling class. The
 * returned {@link Logger} instance is decided upon the {@link Loggable}
 * annotation definition in the declaration of the caller class.
 * <p>
 * For inifinite recursion problems, this class have to call the {@link Logger}
 * instance directly for logging purposes
 * 
 * @author Daniele Di Bernardo
 * @version 1.2.0
 * 
 */
@Loggable(exclude = true)
public class Log {
	/**
	 * The constructor is forced to be private. Class methods should be accessed
	 * in a static way.
	 */
	private Log() {
	}

	/**
	 * Returns the {@link Logger} instance that the caller class should use.
	 * <p>
	 * If the caller class has not been marked with the {@link Loggable}
	 * annotation, then a the result of {@link Logger#getRootLogger()} will be
	 * returned. If the {@link Loggable} annotation has been used, it will
	 * return a {@link Logger} that is coherent with the parameter returned by
	 * {@link Loggable#clazz()}
	 * 
	 * @return a suitable logger for the caller class, depending on the
	 *         parameters of the {@link Loggable} annotation.
	 */
	public static Logger get() {
		// Retrieve the StacKTraceElement of the caller method
		StackTraceElement caller = getCaller();
		// Retrieve the caller class
		Class<?> clazz = getClass(caller);
		// and its Loggable annotation instance
		Loggable annotation = getAnnotation(clazz);

		// Retrieve the suitable logger for the caller class and returns it
		Logger suitableLogger = getLogger(annotation, clazz);
		return suitableLogger;
	}

	/**
	 * Returns the {@link StackTraceElement} related to the caller class
	 * 
	 * @return the {@link StackTraceElement} related to the caller class
	 */
	private static StackTraceElement getCaller() {
		// Read the current stack trace
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();

		/**
		 * Read the caller method/class, knowing that:<br/>
		 * stack[0] = Thread#getStackTrace() <br/>
		 * stack[1] = MyLogger#getCaller() <br/>
		 * stack[2] = MyLogger#get() <br/>
		 * stack[3] = CallerClass#callerMethod() ...
		 */
		return stack[3];
	}

	/**
	 * Given a {@link StackTraceElement}, returns the {@link Class} object which
	 * the caller method belongs to
	 * 
	 * @param caller
	 *            the {@link StackTraceElement} related to the caller method
	 * @return the {@link Class} object which the caller method belongs to
	 */
	private static Class<?> getClass(StackTraceElement caller) {
		String className = caller.getClassName();
		Class<?> result = null;
		try {
			result = Class.forName(className);
		} catch (ClassNotFoundException e) {
			// This call cannot generate a real exception, since the class name
			// has been taken directly from the current stack trace, hence class
			// surely exists
		}
		return result;
	}

	/**
	 * Given a class, returns its {@link Loggable} annotation, if present
	 * 
	 * @param clazz
	 *            the {@link Class} object
	 * @return the {@link Loggable} annotation of the class, if present
	 */
	private static Loggable getAnnotation(Class<?> clazz) {
		return clazz.getAnnotation(Loggable.class);
	}

	/**
	 * Given a {@link Loggable} annotation, returns a suitable {@link Logger}
	 * given its parameters.
	 * <p>
	 * If no {@link Loggable} annotation is present, returns the root logger for
	 * log4j; behaving this way, every plain class will be able to log
	 * immediately from the start of development without further specifications.
	 * 
	 * @param annotation
	 *            the {@link Loggable} annotation of the logger class
	 * @param clazz
	 *            the caller class
	 * @return a suitable {@link Logger} for the caller class
	 */
	private static Logger getLogger(Loggable annotation, Class<?> clazz) {
		if (annotation == null)
			return LoggerContainer.getInstance(clazz);
		
		if (annotation.root())
			return LoggerContainer.getRootLogger();

		if (annotation.exclude())
			return LoggerContainer.getVoidLogger();

		// If a "loggerName" parameter has been passed to the annotation,
		// returns that specific logger instance
		if (!"".equals(annotation.loggerName()))
			return LoggerContainer.getInstance(annotation.loggerName(),
					annotation);

		// If no "class" parameter has been passed to the annotation, returns
		// the default logger for the caller class ...
		if (annotation.clazz() == Object.class)
			return LoggerContainer.getInstance(clazz, annotation);

		// ... otherwise use the "class" parameter to build the logger
		return LoggerContainer.getInstance(annotation.clazz(), annotation);
	}

}
