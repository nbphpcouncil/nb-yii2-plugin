/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.nbphpcouncil.modules.php.yii2.api;

import java.util.List;
import org.nbphpcouncil.modules.php.yii2.utils.Yii2Utils;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.util.StringUtils;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author junichi11
 */
public final class Yii2ModuleUtils {

    private Yii2ModuleUtils() {
    }

    /**
     * Get controller file from view file.
     *
     * @param yii2Module
     * @param view view file
     * @return controlle file if file exists, {@code null} otherwise.
     */
    @CheckForNull
    public static FileObject getController(Yii2Module yii2Module, FileObject view) {
        Yii2Module.DirKind dirKind = yii2Module.getDirKind(view);
        String viewPath = view.getPath();
        List<FileObject> directries = yii2Module.getDirectries(dirKind, Yii2Module.Category.VIEWS);
        for (FileObject directory : directries) {
            // build controller path
            String dirPath = directory.getPath();
            viewPath = viewPath.replace(dirPath, "").replace(view.getNameExt(), ""); // NOI18N
            String controllerId = Yii2Utils.trimWith(viewPath, "/"); // NOI18N
            String controllerName;
            String subpath = ""; // NOI18N
            if (controllerId.contains("/") && !controllerId.endsWith("/")) { // NOI18N
                int lastIndexOfSlash = controllerId.lastIndexOf("/"); // NOI18N
                subpath = controllerId.substring(0, lastIndexOfSlash + 1);
                controllerName = toControllerName(controllerId.substring(lastIndexOfSlash + 1));
            } else {
                controllerName = toControllerName(controllerId);
            }
            String controllerPath = String.format("%s%s.php", subpath, controllerName); // NOI18N

            // get controller
            List<FileObject> controllerDirectories = yii2Module.getDirectries(dirKind, Yii2Module.Category.CONTROLLERS);
            for (FileObject controllerDirectory : controllerDirectories) {
                FileObject controller = controllerDirectory.getFileObject(controllerPath);
                if (controller != null) {
                    return controller;
                }
            }
        }
        return null;
    }

    /**
     * Get view file from controller file.
     *
     * @param yii2Module
     * @param controller
     * @param offset
     * @return view file if file exists, {@code null} otherwise.
     */
    @CheckForNull
    public static FileObject getView(Yii2Module yii2Module, FileObject controller, int offset) {
        String controllerId = getControllerId(yii2Module, controller);
        Yii2Module.DirKind dirKind = yii2Module.getDirKind(controller);
        List<FileObject> viewDirectries = yii2Module.getDirectries(dirKind, Yii2Module.Category.VIEWS);
        EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);
        PhpBaseElement element = editorSupport.getElement(controller, offset);
        if (element instanceof PhpClass.Method) {
            PhpClass.Method method = (PhpClass.Method) element;
            String methodName = method.getName();

            if (!isActionName(methodName)) {
                return null;
            }

            String actionId = toActionId(methodName);
            for (FileObject viewDirectory : viewDirectries) {
                FileObject view = viewDirectory.getFileObject(String.format("%s/%s.php", controllerId, actionId)); // NOI18N
                if (view != null) {
                    return view;
                }
            }
        }
        return null;
    }

    public static FileObject getView(Yii2Module yii2Module, FileObject controller, String viewPath) {
        if (StringUtils.isEmpty(viewPath)) {
            return null;
        }
        if (!yii2Module.isCategory(controller, Yii2Module.Category.CONTROLLERS)) {
            return null;
        }

        String controllerId = getControllerId(yii2Module, controller);
        Yii2Module.DirKind dirKind = yii2Module.getDirKind(controller);
        List<FileObject> viewDirectries = yii2Module.getDirectries(dirKind, Yii2Module.Category.VIEWS);
        for (FileObject viewDirectory : viewDirectries) {
            FileObject view = viewDirectory.getFileObject(String.format("%s/%s.php", controllerId, viewPath));
            if (view != null) {
                return view;
            }
        }
        return null;
    }

    /**
     * Get controller id form controlle file.
     *
     * @param yii2Module
     * @param controller controller file
     * @return controller id.
     */
    public static String getControllerId(Yii2Module yii2Module, FileObject controller) {
        Yii2Module.DirKind dirKind = yii2Module.getDirKind(controller);
        if (dirKind == Yii2Module.DirKind.NONE) {
            return ""; // NOI18N
        }
        List<FileObject> directries = yii2Module.getDirectries(dirKind, Yii2Module.Category.CONTROLLERS);
        String controllerPath = controller.getPath();
        String controllerId = ""; // NOI18N
        for (FileObject directory : directries) {
            String path = directory.getPath();
            if (controllerPath.startsWith(path)) {
                controllerId = controllerPath
                        .replace(path, "") // NOI18N
                        .replace(controller.getNameExt(), toControllerId(controller.getName()));
                controllerId = Yii2Utils.trimWith(controllerId, "/");
                return controllerId;
            }
        }
        return controllerId;
    }

    /**
     * Convert to controller id from controller name.
     *
     * @param controllerName controller name e.g. SiteController
     * @return controller id e.g. site
     */
    private static String toControllerId(@NonNull String controllerName) {
        String replaced = controllerName.replace("Controller", ""); // NOI18N
        return Yii2Utils.toLowercaseWithDashes(replaced);
    }

    /**
     * Convert to controller name from controller id.
     *
     * @param controllerId controller id e.g. my-site
     * @return controller name e.g. MySiteController
     */
    private static String toControllerName(@NonNull String controllerId) {
        StringBuilder sb = new StringBuilder(Yii2Utils.toUpperCamelcase(controllerId));
        if (!controllerId.isEmpty()) {
            sb.append("Controller"); // NOI18N
        }
        return sb.toString();
    }

    /**
     * Check whether the method name is action name.
     *
     * @param methodName method name e.g. actionIndex
     * @return {@code true} if action name, {@code false} otherwise.
     */
    private static boolean isActionName(String methodName) {
        if (methodName == null || methodName.isEmpty()) {
            return false;
        }
        return methodName.startsWith("action"); // NOI18N
    }

    /**
     * Convert action method name to action id. e.g. actionIndex -> index
     *
     * @param actionMethodName action method name e.g. actionIndex
     * @return action id.
     */
    private static String toActionId(String actionMethodName) {
        String actionId = actionMethodName.replace("action", ""); // NOI18N
        return Yii2Utils.toLowercaseWithDashes(actionId);
    }

}
