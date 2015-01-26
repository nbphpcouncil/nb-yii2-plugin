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

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.nbphpcouncil.modules.php.yii2.api.Yii2Module;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.framework.PhpModuleActionsExtender;
import org.netbeans.modules.php.spi.framework.actions.GoToActionAction;
import org.netbeans.modules.php.spi.framework.actions.GoToViewAction;
import org.netbeans.modules.php.spi.framework.actions.RunCommandAction;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class Yii2PhpModuleActionsExtender extends PhpModuleActionsExtender {

    @NbBundle.Messages({
        "Yii2PhpModuleActionsExtender.menuName=Yii2"
    })
    @Override
    public String getMenuName() {
        return Bundle.Yii2PhpModuleActionsExtender_menuName();
    }

    @Override
    public List<? extends Action> getActions() {
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(CreateHtaccessAction.getInstance());
        return actions;
    }

    @Override
    public GoToViewAction getGoToViewAction(FileObject fo, int offset) {
        return new Yii2GoToViewAction(fo, offset);
    }

    @Override
    public GoToActionAction getGoToActionAction(FileObject fo, int offset) {
        return new Yii2GoToActionAction(fo);
    }

    @Override
    public boolean isActionWithView(FileObject fo) {
        return isController(fo);
    }

    @Override
    public boolean isViewWithAction(FileObject fo) {
        return isView(fo);
    }

    private boolean isView(FileObject fo) {
        return isCategory(fo, Yii2Module.Category.VIEWS);
    }

    private boolean isController(FileObject fo) {
        return isCategory(fo, Yii2Module.Category.CONTROLLERS);
    }

    private boolean isCategory(FileObject fo, Yii2Module.Category category) {
        PhpModule phpModule = PhpModule.Factory.forFileObject(fo);
        if (phpModule == null) {
            return false;
        }
        Yii2Module yii2Module = Yii2Module.forPhpModule(phpModule);
        return yii2Module.isCategory(fo, category);
    }

    @Override
    public RunCommandAction getRunCommandAction() {
        return null;
    }

}
