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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.log4j.Logger;

/**
 * This interface will define a class that can access the global logging
 * capabilities. You can force a class to log using a specific {@link Class}
 * type {@link Logger}, or you can let the class use its default {@link Logger}.
 * <p>
 * Also, you can force the log level of your class using this annotation,
 * despite of the global log4j settings.
 * 
 * @author Daniele Di Bernardo
 * @version 1.2.0
 * 
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface Loggable {
	/**
	 * This parameter defines the name of the logger to be used by the class. It
	 * overrides the parameter specified with {@link #clazz()}.
	 */
	String loggerName() default "";

	/**
	 * This parameter defines the {@link Class} to be passed to the
	 * {@link Logger#getLogger(Class)} method. It is overridden by the
	 * {@link #loggerName()} parameter, if present.
	 */
	Class<?> clazz() default Object.class;

	/**
	 * This parameter defines if the class has to be excluded from the logging
	 * mechanisms
	 */
	boolean exclude() default false;

	
	/**
	 * This parameter defines if the class should log through the root logger,
	 * despite all the other specific parameters
	 */
	boolean root() default false;
	
	/**
	 * This parameter defines the default log level required for logging. If
	 * {@link LogLevel#LOG4J} is passed, the class will log with the default log
	 * level configuration of log4j.
	 */
	LogLevel logLevel() default LogLevel.LOG4J;

	/**
	 * This is the collection of the available custom log levels for the
	 * {@link Loggable} annotation
	 * 
	 * @author Daniele Di Bernardo
	 * @version 1.2.0
	 * 
	 */
	public enum LogLevel {
		TRACE, DEBUG, INFO, WARN, ERROR, FATAL, LOG4J
	}
}