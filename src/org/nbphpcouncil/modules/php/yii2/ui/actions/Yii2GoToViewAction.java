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
package org.nbphpcouncil.modules.php.yii2.ui.actions;

import org.nbphpcouncil.modules.php.yii2.api.Yii2Module;
import org.nbphpcouncil.modules.php.yii2.api.Yii2ModuleUtils;
import org.netbeans.modules.csl.api.UiUtils;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.framework.actions.GoToViewAction;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;

/**
 *
 * @author junichi11
 */
public class Yii2GoToViewAction extends GoToViewAction {

    private static final long serialVersionUID = -1486086465026340189L;
    private final FileObject controller;
    private final int offset;

    public Yii2GoToViewAction(FileObject controller, int offset) {
        this.controller = controller;
        this.offset = offset;
    }

    @Override
    public boolean goToView() {
        PhpModule phpModule = PhpModule.Factory.forFileObject(controller);
        if (phpModule == null) {
            return false;
        }
        Yii2Module yii2Module = Yii2Module.forPhpModule(phpModule);
        FileObject view = Yii2ModuleUtils.getView(yii2Module, controller, offset);
        if (view != null) {
            // file is already opened
            // try to open with EditorCookie
            DataObject dataObject;
            try {
                dataObject = DataObject.find(view);
                EditorCookie editorCookie = dataObject.getLookup().lookup(EditorCookie.class);
                if (editorCookie != null) {
                    editorCookie.open();
                    return true;
                }
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }

            UiUtils.open(view, DEFAULT_OFFSET);
            return true;
        }
        return false;
    }

}
