/*******************************************************************************
 * Copyright 2014 Rafael Garcia Moreno.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.bladecoder.engineeditor.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Input.Keys;
import com.strongjoshua.console.CommandExecutor;
import com.strongjoshua.console.Console;
import com.strongjoshua.console.LogLevel;

public class EditorLogger {
	public static enum Levels {
		DEBUG, ERROR
	};

	public static Levels level = Levels.ERROR;

	public static Console console;

	private final static List<String> threadedMessages = new ArrayList<String>();

	public static void debug(String message) {
		if (level == Levels.DEBUG) {
			console.log(message, LogLevel.DEFAULT);
		}
	}

	public static void msg(String message) {
		console.log(message, LogLevel.SUCCESS);
	}

	public static synchronized void msgThreaded(String message) {
		threadedMessages.add(message);
	}

	public static synchronized void drawConsole() {
		for (String msg : threadedMessages)
			msg(msg);

		threadedMessages.clear();

		console.draw();
	}

	public static void error(String message) {
		console.log(message, LogLevel.ERROR);
	}

	public static void error(String message, Exception e) {
		console.log(message + " Exception: " + e.getMessage(), LogLevel.ERROR);

		printStackTrace(e);
	}

	public static void printStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		console.log(sw.toString(), LogLevel.ERROR);
	}

	public static void toggle() {
		if (level == Levels.DEBUG) {
			level = Levels.ERROR;
			console.setLoggingToSystem(false);
		} else {
			level = Levels.DEBUG;
			console.setLoggingToSystem(true);
		}
	}

	public static boolean debugMode() {
		if (level == Levels.DEBUG)
			return true;

		return false;
	}

	public static Levels getDebugLevel() {
		return level;
	}

	public static void setDebugLevel(Levels l) {
		level = l;
	}

	public static void setDebug() {
		level = Levels.DEBUG;
		console.setLoggingToSystem(true);
	}

	public static void setConsole(Console console) {
		EditorLogger.console = console;
		EditorLogger.console.setKeyID(Keys.F1);
		console.setMaxEntries(1000);

		console.setCommandExecutor(new CommandExecutor() {

			@SuppressWarnings("unused")
			public void exit() {
				super.exitApp();
			}

			@SuppressWarnings("unused")
			public void saveLog(String path) {
				super.printLog(path);
			}

			@SuppressWarnings("unused")
			public void checkI18NMissingKeys() {
				try {
					ModelTools.checkI18NMissingKeys();
					EditorLogger.msg("FINISHED PROCCESS: checkI18NMissingKeys.");
				} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
					EditorLogger.printStackTrace(e);
				}
			}

			@SuppressWarnings("unused")
			public void debug(boolean value) {
				if (!value) {
					level = Levels.ERROR;
					console.setLoggingToSystem(false);
				} else {
					level = Levels.DEBUG;
					console.setLoggingToSystem(true);
				}
			}
			
			@SuppressWarnings("unused")
			public void extractDialogs() {
				ModelTools.extractDialogs();
			}

		});
	}
}
