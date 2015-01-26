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
import org.nbphpcouncil.modules.php.yii2.api.Alias;
import org.nbphpcouncil.modules.php.yii2.api.Yii2Module.Category;
import org.nbphpcouncil.modules.php.yii2.api.Yii2Module.DirKind;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.filesystems.FileObject;

public class Yii2AppBasicModule extends Yii2Module {

    public Yii2AppBasicModule(PhpModule phpModule) {
        super(phpModule, null);
    }

    public Yii2AppBasicModule(PhpModule phpModule, PathAliases pathAliases) {
        super(phpModule, pathAliases);
    }

    @Override
    public FileObject getDirectory(Alias alias) {
        FileObject baseDirectory = getBaseDirectory();
        if (baseDirectory == null) {
            return null;
        }

        FileObject targetDirectory = null;
        PathAliases pathAliases = getPathAliases();
        List<String> paths = pathAliases.getPaths(Alias.APP.getAliasName());
        for (String path : paths) {
            FileObject customAppDirectory = baseDirectory.getFileObject(path);
            if (customAppDirectory != null) {
                baseDirectory = customAppDirectory;
            }
            break;
        }

        switch (alias) {
            case APP:
                return baseDirectory;
            case RUNTIME: // no break;
            case VENDOR: // no break;
            case WEBROOT: // no break;
            case YII:
                List<String> aliasPaths = pathAliases.getPaths(alias.getAliasName());
                for (String path : aliasPaths) {
                    FileObject fileObject = baseDirectory.getFileObject(path);
                    if (fileObject != null) {
                        return fileObject;
                    }
                }
                targetDirectory = baseDirectory.getFileObject(alias.getDefaultPath()); // NOI18N
                break;
            default:
                break;
        }
        return targetDirectory;
    }

    @CheckForNull
    @Override
    public FileObject getDirectory(DirKind dirKind) {
        switch (dirKind) {
            case BASIC:
                return getDirectory(Alias.APP);
            default:
                return null;
        }
    }

    @Override
    public List<FileObject> getDirectories(DirKind dirKind, Category category) {
        List<FileObject> customDirectories = getCustomDirectories(dirKind, category);
        if (!customDirectories.isEmpty()) {
            return customDirectories;
        }
        return getDefaultDirectories(dirKind, category);
    }

    /**
     * Get custom directories. Get information of directory path from json file.
     *
     * @param dirKind
     * @param category
     * @return
     */
    private List<FileObject> getCustomDirectories(DirKind dirKind, Category category) {
        FileObject directory = getDirectory(dirKind);
        if (directory == null) {
            return Collections.emptyList();
        }
        String aliasName = ""; // NOI18N
        switch (category) {
            case ASSETS: // no break
            case COMMANDS: // no break
            case CONFIG: // no break
            case CONTROLLERS:  // no break
            case MODELS: // no break
            case TESTS: // no break
            case VIEWS: // no break
                aliasName = String.format("%s/%s", Alias.APP.getAliasName(), category.toString());
                break;
            case WEB:
                aliasName = Alias.WEBROOT.getAliasName();
                break;
            case VENDOR:
                aliasName = Alias.VENDOR.getAliasName();
                break;
            default:
                break;
        }

        List<String> paths = getPathAliases().getPaths(aliasName);
        List<FileObject> directories = new ArrayList<>(paths.size());
        FileObject baseDirectory = getBaseDirectory();
        if (baseDirectory == null) {
            return Collections.emptyList();
        }
        for (String path : paths) {
            FileObject target = baseDirectory.getFileObject(path);
            if (target != null) {
                directories.add(target);
                if (category == Category.WEB || category == Category.VENDOR) {
                    break;
                }
            }
        }
        return directories;
    }

    /**
     * Get default directories.
     *
     * @param dirKind
     * @param category
     * @return
     */
    private List<FileObject> getDefaultDirectories(DirKind dirKind, Category category) {
        FileObject directory = getDirectory(dirKind);
        if (directory == null) {
            return Collections.emptyList();
        }
        if (category == Category.NONE) {
            return Collections.singletonList(directory);
        }
        FileObject targetDirectory = directory.getFileObject(category.toString());
        if (targetDirectory == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(targetDirectory);
    }

    @Override
    public DirKind getDirKind(FileObject target) {
        if (target != null) {
            FileObject directory = getDirectory(DirKind.BASIC);
            if (directory == null) {
                return DirKind.NONE;
            }
            String basePath = directory.getPath();
            if (target.getPath().startsWith(basePath)) {
                return DirKind.BASIC;
            }
        }
        return DirKind.NONE;
    }

    @Override
    public Category getCategory(FileObject target) {
        if (target != null) {
            for (Category category : Category.values()) {
                if (category == Category.NONE) {
                    continue;
                }
                List<FileObject> directories = getDirectories(DirKind.BASIC, category);
                for (FileObject directory : directories) {
                    String path = directory.getPath();
                    if (target.getPath().startsWith(path)) {
                        return category;
                    }
                }
            }
        }
        return Category.NONE;
    }

    @Override
    public boolean isCategory(FileObject target, Category category) {
        return category == getCategory(target);
    }

}
