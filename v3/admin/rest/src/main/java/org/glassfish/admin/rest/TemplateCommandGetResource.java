/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Sun Microsystems, Inc. All rights reserved.
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
package org.glassfish.admin.rest;

import java.util.HashMap;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

import com.sun.enterprise.util.LocalStringManagerImpl;

import org.glassfish.admin.rest.provider.OptionsResult;
import org.glassfish.admin.rest.provider.MethodMetaData;
import org.glassfish.admin.rest.provider.StringResult;
import org.glassfish.api.ActionReport;

/**
 *
 * @author ludovic champenois ludo@dev.java.net
 * Code moved from generated classes to here. Gen code inherits from this template class
 * that contains the logic for mapped commands RS Resources
 *
 */
 public class TemplateCommandGetResource {

    public final static LocalStringManagerImpl localStrings = new LocalStringManagerImpl(ResourceUtil.class);
    @Context
    protected HttpHeaders requestHeaders;
    @Context
    protected UriInfo uriInfo;
    private String resourceName;
    private String commandName;
    private String commandMethod;
    private HashMap<String, String> commandParams = null;
    private boolean isLinkedToParent = false;
    private ResourceUtil resourceUtil = new ResourceUtil();

    public TemplateCommandGetResource(String resourceName, String commandName, String commandMethod,
             HashMap<String, String> m, boolean b) {
        this.resourceName = resourceName;
        this.commandName = commandName;
        this.commandMethod = commandMethod;
                this.commandParams = m;
        this.isLinkedToParent = b;
    }

    @GET
    @Produces({
        MediaType.TEXT_HTML,
        MediaType.APPLICATION_JSON,
        MediaType.APPLICATION_XML,
        MediaType.APPLICATION_FORM_URLENCODED})
    public StringResult executeCommand() {
        try {
            java.util.Properties properties = new java.util.Properties();
            if (commandParams != null) {
//formulate parent-link attribute for this command resource
//Parent link attribute may or may not be the id/target attribute
                if (isLinkedToParent) {
                    resourceUtil.resolveParentParamValue(commandParams, uriInfo);
                }
                properties.putAll(commandParams);
            }

            ActionReport actionReport = resourceUtil.runCommand(commandName, properties, RestService.getHabitat());
            ActionReport.ExitCode exitCode = actionReport.getActionExitCode();
            StringResult results = new StringResult(commandName, resourceUtil.getMessage(actionReport), options());
            
            if (exitCode == ActionReport.ExitCode.SUCCESS) {
                results.setStatusCode(200); /*200 - ok*/
            } else {
                results.setStatusCode(400); /*400 - bad request*/
                results.setIsError(true);
                results.setErrorMessage(actionReport.getMessage());
            }

            return results;

        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @OPTIONS
    @Produces({
        MediaType.APPLICATION_JSON,
        MediaType.TEXT_HTML,
        MediaType.APPLICATION_XML})
    public OptionsResult options() {
        OptionsResult optionsResult = new OptionsResult(resourceName);
        try {
//command method metadata
            MethodMetaData methodMetaData = resourceUtil.getMethodMetaData(
                    commandName, commandParams, Constants.QUERY_PARAMETER, RestService.getHabitat(), RestService.logger);
            optionsResult.putMethodMetaData(commandMethod, methodMetaData);
        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }

        return optionsResult;
    }
}
