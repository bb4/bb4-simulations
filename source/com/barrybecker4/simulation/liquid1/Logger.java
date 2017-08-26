/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid1;

import com.barrybecker4.common.app.ILog;
import com.barrybecker4.ui.dialogs.OutputWindow;
import com.barrybecker4.ui.util.Log;

/**
 * Singleton instance of logger.
 *
 * @author Barry Becker
 */
public class Logger {

    /** for debugging */
    public static final int LOG_LEVEL = 0;

    /** The singleton instance */
    private static ILog logger;

    /**
     * Constructor
     */
    private Logger() {}

    public static ILog getInstance()  {
        if (logger == null)   {

            logger = new Log( new OutputWindow( "Log", null ) );
            logger.setDestination(Log.LOG_TO_WINDOW);
        }
        return logger;
    }

    public static void log(int level, String msg) {
       getInstance().println(level, LOG_LEVEL, msg);
    }
}