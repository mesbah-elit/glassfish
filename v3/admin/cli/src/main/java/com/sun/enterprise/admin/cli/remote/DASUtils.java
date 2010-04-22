/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package com.sun.enterprise.admin.cli.remote;

import java.io.*;
import java.net.*;
import java.util.logging.*;
import org.glassfish.api.admin.*;
import com.sun.enterprise.admin.cli.*;

/**
 * Domain Admin Server utility method.
 */
public class DASUtils {
    public enum Error {
        NONE, AUTHENTICATION, CONNECTION, IO, UNKNOWN
    };

    private DASUtils() {
        // can't instantiate
    }

    /**
     * See if DAS is alive.
     * Do not print out the results of the version command from the server.
     *
     * @return true if DAS can be reached and can handle commands,
     * otherwise false.
     */
    public static boolean pingDASQuietly(ProgramOptions programOpts,
            Environment env) {
        try {
            RemoteCommand cmd = new RemoteCommand("version", programOpts, env);
            cmd.executeAndReturnOutput(new String[]{"version"});
            return true;
        }
        catch (AuthenticationException aex) {
            return true;
        }
        catch (Exception ex) {
            ExceptionAnalyzer ea = new ExceptionAnalyzer(ex);
            if(ea.getFirstInstanceOf(ConnectException.class) != null) {
                CLILogger.getInstance().printDebugMessage(
                        "Got java.net.ConnectException");
                return false; // this definitely means server is not up
            }
            else if(ea.getFirstInstanceOf(IOException.class) != null) {
                CLILogger.getInstance().printDebugMessage(
                        "It appears that server has started, but for"
                        + " some reason the exception is thrown: "
                        + ex.getMessage());
                return true;
            }
            else {
                return false; // unknown error, shouldn't really happen
            }
        }
    }

    /**
     * See if DAS is alive, but insist that athentication is correct.
     * Do not print out the results of the version command from the server.
     *
     * @return Error code indicating status
     */
    public static Error pingDASWithAuth(ProgramOptions programOpts,
            Environment env) throws CommandException {
        try {
            RemoteCommand cmd = new RemoteCommand("version", programOpts, env);
            cmd.executeAndReturnOutput(new String[]{"version"});
        }
        catch (AuthenticationException aex) {
            return Error.AUTHENTICATION;
        }
        catch (Exception ex) {
            ExceptionAnalyzer ea = new ExceptionAnalyzer(ex);
            if(ea.getFirstInstanceOf(ConnectException.class) != null) {
                CLILogger.getInstance().printDebugMessage(
                        "Got java.net.ConnectException");
                return Error.CONNECTION;
            }
            else if(ea.getFirstInstanceOf(IOException.class) != null) {
                CLILogger.getInstance().printDebugMessage(
                        "It appears that server has started, but for"
                        + " some reason the exception is thrown: "
                        + ex.getMessage());
                return Error.IO;
            }
            else {
                return Error.UNKNOWN;
            }
        }
        return Error.NONE;
    }
}
