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
package org.nbphpcouncil.modules.php.yii2.ui.customizer;

import java.util.EnumSet;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.nbphpcouncil.modules.php.yii2.Yii2ProjectType;
import org.nbphpcouncil.modules.php.yii2.api.Yii2Module;
import org.nbphpcouncil.modules.php.yii2.preferences.Yii2Preferences;
import org.nbphpcouncil.modules.php.yii2.validators.Yii2CustomizerValidator;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.validation.ValidationResult;
import org.netbeans.modules.php.spi.framework.PhpModuleCustomizerExtender;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.HelpCtx;

/**
 *
 * @author junichi11
 */
public class Yii2PhpModuleCustomizerExtender extends PhpModuleCustomizerExtender {

    private Yii2CustomizerPanel panel;
    private String errorMessage;
    private final boolean isYii2Enabled;
    private final String baseAppPath;
    private final String pathAliasesPath;
    private final Yii2ProjectType projectType;
    private final FileObject sourceDirectory;
    private boolean isValid;

    public Yii2PhpModuleCustomizerExtender(PhpModule phpModule) {
        isYii2Enabled = Yii2Preferences.isEnabled(phpModule);
        baseAppPath = Yii2Preferences.getBaseAppPath(phpModule);
        projectType = Yii2Preferences.getProjectType(phpModule);
        pathAliasesPath = Yii2Preferences.getPathAliasesPath(phpModule);
        sourceDirectory = phpModule.getSourceDirectory();
    }

    @Override
    public String getDisplayName() {
        return "Yii2"; // NOI18N
    }

    @Override
    public void addChangeListener(ChangeListener changeListener) {
        getPanel().addChangeListener(changeListener);
    }

    @Override
    public void removeChangeListener(ChangeListener changeListener) {
        getPanel().removeChangeListener(changeListener);
    }

    @Override
    public JComponent getComponent() {
        return getPanel();
    }

    @Override
    public HelpCtx getHelp() {
        return null;
    }

    @Override
    public boolean isValid() {
        validate();
        return isValid;
    }

    @Override
    public String getErrorMessage() {
        validate();
        return errorMessage;
    }

    private void validate() {
        // TODO add check for directory structure?
        Yii2CustomizerPanel p = getPanel();
        if (!p.isYii2Enabled()) {
            isValid = true;
            errorMessage = null;
            return;
        }
        Yii2ProjectType type = p.getProjectType();
        Yii2CustomizerValidator validator = new Yii2CustomizerValidator();
        ValidationResult result = validator.validateProjectType(type)
                .validateBaseAppPath(sourceDirectory, p.getBasePath())
                .validatePathAliasesPath(sourceDirectory, p.getPathAliasesPath())
                .getResult();
        if (result.hasErrors()) {
            errorMessage = result.getErrors().get(0).getMessage();
            isValid = false;
            return;
        }

        if (result.hasWarnings()) {
            errorMessage = result.getWarnings().get(0).getMessage();
            isValid = false;
            return;
        }

        // everything ok
        isValid = true;
        errorMessage = null;
    }

    @Override
    public EnumSet<Change> save(PhpModule phpModule) {
        EnumSet<Change> enumSet = EnumSet.of(Change.FRAMEWORK_CHANGE);
        Yii2Preferences.setEnabled(phpModule, getPanel().isYii2Enabled());
        Yii2Preferences.setBaseAppPath(phpModule, getPanel().getBasePath());
        Yii2Preferences.setProjectType(phpModule, getPanel().getProjectType());
        Yii2Preferences.setPathAliasesPath(phpModule, getPanel().getPathAliasesPath());
        Yii2Module.reset(phpModule);
        return enumSet;
    }

    private Yii2CustomizerPanel getPanel() {
        if (panel == null) {
            panel = new Yii2CustomizerPanel(FileUtil.toFile(sourceDirectory));
            panel.setYii2Enabled(isYii2Enabled);
            panel.setBasePath(baseAppPath);
            panel.setProjectType(projectType);
            panel.setPathAliasesPaht(pathAliasesPath);
        }
        return panel;
    }

}
