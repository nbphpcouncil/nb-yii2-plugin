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

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.nbphpcouncil.modules.php.yii2.Yii2ProjectType;
import org.nbphpcouncil.modules.php.yii2.modules.PathAliases;
import org.nbphpcouncil.modules.php.yii2.modules.Yii2AppBasicModule;
import org.nbphpcouncil.modules.php.yii2.modules.Yii2DefaultModule;
import org.nbphpcouncil.modules.php.yii2.preferences.Yii2Preferences;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
final class Yii2ModuleFactory {

    private static final Map<PhpModule, Yii2Module> MODULES = new HashMap<>();
    private static final Logger LOGGER = Logger.getLogger(Yii2ModuleFactory.class.getName());

    private Yii2ModuleFactory() {
    }

    public static Yii2Module create(PhpModule phpModule) {
        Yii2Module module = MODULES.get(phpModule);
        if (module == null) {
            if (Yii2Preferences.isEnabled(phpModule)) {
                Yii2ProjectType projectType = Yii2Preferences.getProjectType(phpModule);

                // get path aliasses
                // it is json format
                String pathAliasesPath = Yii2Preferences.getPathAliasesPath(phpModule);
                PathAliases pathAliases = null;
                if (!StringUtils.isEmpty(pathAliasesPath)) {
                    FileObject sourceDirectory = phpModule.getSourceDirectory();
                    if (sourceDirectory != null) {
                        FileObject json = sourceDirectory.getFileObject(pathAliasesPath);
                        try (InputStream inputStream = json.getInputStream()) {
                            InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8"); // NOI18N
                            Gson gson = new Gson();
                            pathAliases = gson.fromJson(reader, PathAliases.class);
                        } catch (IOException ex) {
                            LOGGER.log(Level.WARNING, null, ex);
                        }
                    }
                }

                switch (projectType) {
                    case YII2_APP_BASIC:
                        module = new Yii2Module(new Yii2AppBasicModule(phpModule, pathAliases));
                        break;
                    case YII2_APP_ADVANCED:
                        // TODO
                        break;
                    default:
                        break;
                }
                if (module != null) {
                    MODULES.put(phpModule, module);
                } else {
                    module = new Yii2Module(new Yii2DefaultModule(phpModule, pathAliases));
                }
            }
        }
        return module;
    }

    public static void remove(PhpModule phpModule) {
        MODULES.remove(phpModule);
    }

    public static void clear() {
        MODULES.clear();
    }
}
