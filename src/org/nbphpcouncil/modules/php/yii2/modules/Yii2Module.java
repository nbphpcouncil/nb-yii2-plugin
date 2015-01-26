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
package org.nbphpcouncil.modules.php.yii2.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.nbphpcouncil.modules.php.yii2.api.Alias;
import org.nbphpcouncil.modules.php.yii2.api.Yii2Module.Category;
import org.nbphpcouncil.modules.php.yii2.api.Yii2Module.DirKind;
import org.nbphpcouncil.modules.php.yii2.preferences.Yii2Preferences;
import org.nbphpcouncil.modules.php.yii2.utils.Yii2Utils;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public abstract class Yii2Module {

    private FileObject baseDirecotry;
    private final PhpModule phpModule;
    private final PathAliases pathAliases;
    private static final Logger LOGGER = Logger.getLogger(Yii2Module.class.getName());

    public Yii2Module(PhpModule phpModule, PathAliases pathAliases) {
        this.phpModule = phpModule;
        this.pathAliases = pathAliases == null ? new PathAliases(Collections.<String, List<String>>emptyMap()) : pathAliases;
    }

    public abstract FileObject getDirectory(Alias aliases);

    public abstract FileObject getDirectory(DirKind dirKind);

    public abstract List<FileObject> getDirectories(DirKind dirKind, Category category);

    public abstract DirKind getDirKind(FileObject target);

    public abstract Category getCategory(FileObject target);

    public abstract boolean isCategory(FileObject target, Category category);

    @CheckForNull
    FileObject getBaseDirectory() {
        if (baseDirecotry == null) {
            String baseAppPath = Yii2Preferences.getBaseAppPath(phpModule);
            FileObject sourceDirectory = phpModule.getSourceDirectory();
            if (sourceDirectory == null) {
                LOGGER.log(Level.WARNING, "source directory is null! Project may be broken.");
                return null;
            }
            baseDirecotry = sourceDirectory.getFileObject(baseAppPath);
        }
        return baseDirecotry;
    }

    public List<FileObject> getDirectoriesFromPathAlias(String aliasPath) {
        if (!Yii2Utils.isPathAlias(aliasPath)) {
            return Collections.emptyList();
        }
        int fromIndex = aliasPath.length();
        int indexOfSlash = fromIndex == 0 ? 0 : fromIndex - 1;
        String searchPath = aliasPath;
        String subPath = ""; // NOI18N
        List<String> paths;

        // user aliases
        do {
            paths = pathAliases.getPaths(searchPath);
            if (!paths.isEmpty()) {
                subPath = aliasPath.substring(indexOfSlash + 1);
                break;
            }
            indexOfSlash = searchPath.lastIndexOf("/", fromIndex); // NOI18N
            if (indexOfSlash != -1) {
                searchPath = searchPath.substring(0, indexOfSlash);
                fromIndex = indexOfSlash - 1;
            }
        } while (indexOfSlash != -1);

        if (paths.isEmpty()) {
            // default alias
            int index = aliasPath.indexOf("/"); // NOI18N
            if (index != -1) {
                searchPath = aliasPath.substring(0, index);
                subPath = aliasPath.substring(index + 1);
            }
            Alias alias = Alias.toEnum(searchPath);
            FileObject directory = getDirectory(alias);
            if (directory != null) {
                FileObject fileObject = directory.getFileObject(subPath);
                if (fileObject != null) {
                    return Collections.singletonList(fileObject);
                }
            }
            return Collections.emptyList();
        }

        List<FileObject> directories = new ArrayList<>(paths.size());
        for (String path : paths) {
            FileObject baseDirectory = getBaseDirectory();
            if (baseDirectory == null) {
                return Collections.emptyList();
            }

            FileObject aliasRoot = baseDirectory.getFileObject(path);
            if (aliasRoot == null) {
                continue;
            }
            FileObject targetDirectory = aliasRoot.getFileObject(subPath);
            if (targetDirectory != null) {
                directories.add(targetDirectory);
            }
        }
        return directories;
    }

    public FileObject getBaseDirecotry() {
        return baseDirecotry;
    }

    public PhpModule getPhpModule() {
        return phpModule;
    }

    public PathAliases getPathAliases() {
        return pathAliases;
    }
}
