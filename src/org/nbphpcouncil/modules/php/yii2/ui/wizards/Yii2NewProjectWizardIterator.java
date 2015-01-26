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
package org.nbphpcouncil.modules.php.yii2.ui.wizards;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.nbphpcouncil.modules.php.yii2.Yii2ProjectType;
import org.nbphpcouncil.modules.php.yii2.command.Yii2ProjectGenerator;
import org.nbphpcouncil.modules.php.yii2.preferences.Yii2Preferences;
import org.nbphpcouncil.modules.php.yii2.utils.Yii2ImageUtils;
import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.modules.php.api.PhpVersion;
import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModuleGenerator;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

@TemplateRegistration(folder = "Project/PHP", displayName = "#Yii2AppBasic_displayName", description = "Yii2NewProjectDescription.html", iconBase = Yii2ImageUtils.YII_ICON_16, position = 2000)
@Messages("Yii2AppBasic_displayName=Yii2 Framework Application")
public class Yii2NewProjectWizardIterator implements WizardDescriptor./*Progress*/InstantiatingIterator {

    private int index;
    private WizardDescriptor.Panel[] panels;
    private WizardDescriptor wiz;

    public Yii2NewProjectWizardIterator() {
    }

    public static Yii2NewProjectWizardIterator createIterator() {
        return new Yii2NewProjectWizardIterator();
    }

    private WizardDescriptor.Panel[] createPanels() {
        return new WizardDescriptor.Panel[]{
            new Yii2NewProjectWizardPanel(),};
    }

    private String[] createSteps() {
        return new String[]{
            NbBundle.getMessage(Yii2NewProjectWizardIterator.class, "LBL_CreateProjectStep")
        };
    }

    @Override
    public Set<FileObject> instantiate(/*ProgressHandle handle*/) throws IOException {
        Set<FileObject> resultSet = new LinkedHashSet<>();
        File dirF = FileUtil.normalizeFile((File) wiz.getProperty(Yii2NewProjectWizardPanel.PROP_PROJ_DIR));
        dirF.mkdirs();
        String name = (String) wiz.getProperty(Yii2NewProjectWizardPanel.PROP_NAME);
        String projectType = (String) wiz.getProperty(Yii2NewProjectWizardPanel.PROP_PROJECT_TYPE);

        // install yii2 application via composer
        try {
            Yii2ProjectGenerator projectGenerator = Yii2ProjectGenerator.getDefault();
            projectGenerator.generate(projectType, dirF, name);
        } catch (InvalidPhpExecutableException ex) {
            // composer path is not set
            // check it in visual panel
            Exceptions.printStackTrace(ex);
        }
        FileObject dir = FileUtil.toFileObject(dirF);

        // Always open top dir as a project:
        resultSet.add(dir);

        // generate PhpModule
        PhpModuleGenerator generator = Lookup.getDefault().lookup(PhpModuleGenerator.class);
        if (generator != null) {
            PhpModuleGenerator.CreateProperties properties = new PhpModuleGenerator.CreateProperties()
                    .setName(name)
                    .setPhpVersion(PhpVersion.PHP_54)
                    .setCharset(Charset.forName("UTF-8")) // NOI18N
                    .setProjectDirectory(dirF)
                    .setSourcesDirectory(dirF);
            PhpModule phpModule = generator.createModule(properties);
            Yii2Preferences.setEnabled(phpModule, true);
            Yii2Preferences.setProjectType(phpModule, Yii2ProjectType.toEnum(projectType));
        }

        File parent = dirF.getParentFile();
        if (parent != null && parent.exists()) {
            ProjectChooser.setProjectsFolder(parent);
        }

        return resultSet;
    }

    @Override
    public void initialize(WizardDescriptor wiz) {
        this.wiz = wiz;
        index = 0;
        panels = createPanels();
        // Make sure list of steps is accurate.
        String[] steps = createSteps();
        for (int i = 0; i < panels.length; i++) {
            Component c = panels[i].getComponent();
            if (steps[i] == null) {
                // Default step name to component name of panel.
                // Mainly useful for getting the name of the target
                // chooser to appear in the list of steps.
                steps[i] = c.getName();
            }
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                // Step #.
                // TODO if using org.openide.dialogs >= 7.8, can use WizardDescriptor.PROP_*:
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                // Step name (actually the whole list for reference).
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
            }
        }
    }

    @Override
    public void uninitialize(WizardDescriptor wiz) {
        this.wiz.putProperty(Yii2NewProjectWizardPanel.PROP_PROJ_DIR, null);
        this.wiz.putProperty(Yii2NewProjectWizardPanel.PROP_NAME, null);
        this.wiz.putProperty(Yii2NewProjectWizardPanel.PROP_PROJECT_TYPE, null);
        this.wiz = null;
        panels = null;
    }

    @Override
    public String name() {
        return MessageFormat.format("{0} of {1}",
                new Object[]{index + 1, panels.length});
    }

    @Override
    public boolean hasNext() {
        return index < panels.length - 1;
    }

    @Override
    public boolean hasPrevious() {
        return index > 0;
    }

    @Override
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    @Override
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    @Override
    public WizardDescriptor.Panel current() {
        return panels[index];
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    @Override
    public final void addChangeListener(ChangeListener l) {
    }

    @Override
    public final void removeChangeListener(ChangeListener l) {
    }

}
