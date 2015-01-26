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
package org.nbphpcouncil.modules.php.yii2;

import java.io.File;
import org.nbphpcouncil.modules.php.yii2.editor.Yii2EditorExtender;
import org.nbphpcouncil.modules.php.yii2.preferences.Yii2Preferences;
import org.nbphpcouncil.modules.php.yii2.ui.actions.Yii2PhpModuleActionsExtender;
import org.nbphpcouncil.modules.php.yii2.ui.customizer.Yii2PhpModuleCustomizerExtender;
import org.nbphpcouncil.modules.php.yii2.utils.Yii2ImageUtils;
import org.netbeans.modules.php.api.framework.BadgeIcon;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModuleProperties;
import org.netbeans.modules.php.spi.editor.EditorExtender;
import org.netbeans.modules.php.spi.framework.PhpFrameworkProvider;
import org.netbeans.modules.php.spi.framework.PhpModuleActionsExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleCustomizerExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleIgnoredFilesExtender;
import org.netbeans.modules.php.spi.framework.commands.FrameworkCommandSupport;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public final class Yii2PhpFrameworkProvider extends PhpFrameworkProvider {

    private static final Yii2PhpFrameworkProvider INSTANCE = new Yii2PhpFrameworkProvider();
    private static final String ICON_PATH = Yii2ImageUtils.BADGE_ICON_8;
    private final BadgeIcon badgeIcon;

    @NbBundle.Messages({
        "LBL_FrameworkName=Yii2 PHP Web Framework",
        "LBL_FrameworkDescription=Yii2 PHP Web Framework"
    })
    private Yii2PhpFrameworkProvider() {
        super("Yii2 PHP Web Framework", Bundle.LBL_FrameworkName(), Bundle.LBL_FrameworkDescription()); // NOI18N
        badgeIcon = new BadgeIcon(
                ImageUtilities.loadImage(ICON_PATH),
                Yii2PhpFrameworkProvider.class.getResource("/" + ICON_PATH)); // NOI18N
    }

    @PhpFrameworkProvider.Registration(position = 790)
    public static Yii2PhpFrameworkProvider getInstance() {
        return INSTANCE;
    }

    /**
     * Get Badge Icon. BadgeIcon size is 8x8 px.
     *
     * @return BadgeIcon yii_badge_8.png
     */
    @Override
    public BadgeIcon getBadgeIcon() {
        return badgeIcon;
    }

    /**
     * Check whether project is Yii2 Framework.
     *
     * @param phpModule PhpModule
     * @return
     */
    @Override
    public boolean isInPhpModule(PhpModule phpModule) {
        // check user settings
        return Yii2Preferences.isEnabled(phpModule);
    }

    /**
     * Get configuration files. Files is displayed on Important Files node.
     *
     *
     * @param phpModule PhpModule
     * @return File[]
     */
    @Override
    public File[] getConfigurationFiles(PhpModule phpModule) {
        return new File[0];
    }

    @Override
    public PhpModuleExtender createPhpModuleExtender(PhpModule phpModule) {
        return null;
    }

    /**
     * Get PhpModuleProperties. This method is called when only creating new
     * project.
     *
     * @param phpModule
     * @return PhpModuleProperties
     */
    @Override
    public PhpModuleProperties getPhpModuleProperties(PhpModule phpModule) {
        PhpModuleProperties properties = new PhpModuleProperties();
        return properties;
    }

    @Override
    public PhpModuleActionsExtender getActionsExtender(PhpModule phpModule) {
        return new Yii2PhpModuleActionsExtender();
    }

    @Override
    public PhpModuleIgnoredFilesExtender getIgnoredFilesExtender(PhpModule phpModule) {
        return null;
    }

    @Override
    public FrameworkCommandSupport getFrameworkCommandSupport(PhpModule phpModule) {
        return null;
    }

    @Override
    public PhpModuleCustomizerExtender createPhpModuleCustomizerExtender(PhpModule phpModule) {
        return new Yii2PhpModuleCustomizerExtender(phpModule);
    }

    @Override
    public EditorExtender getEditorExtender(PhpModule phpModule) {
        return new Yii2EditorExtender();
    }

}
