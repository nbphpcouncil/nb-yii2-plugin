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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.nbphpcouncil.modules.php.yii2.Yii2PhpFrameworkProvider;
import org.nbphpcouncil.modules.php.yii2.api.Alias;
import org.nbphpcouncil.modules.php.yii2.api.Yii2Module;
import org.nbphpcouncil.modules.php.yii2.utils.Yii2UiUtils;
import org.nbphpcouncil.modules.php.yii2.utils.Yii2Utils;
import org.netbeans.modules.csl.api.UiUtils;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.framework.actions.BaseAction;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class CreateHtaccessAction extends BaseAction {

    private static final CreateHtaccessAction INSTANCE = new CreateHtaccessAction();
    private static final long serialVersionUID = 3401501819811661583L;
    private static final Logger LOGGER = Logger.getLogger(CreateHtaccessAction.class.getName());

    private CreateHtaccessAction() {
    }

    public static CreateHtaccessAction getInstance() {
        return INSTANCE;
    }

    @NbBundle.Messages({
        "# {0} - name",
        "CreateHtaccessAction.fullName=Yii:{0}"
    })
    @Override
    protected String getFullName() {
        return Bundle.CreateHtaccessAction_fullName(getPureName());
    }

    @NbBundle.Messages({
        "CreateHtaccessAction.name=Create .htaccess"
    })
    @Override
    protected String getPureName() {
        return Bundle.CreateHtaccessAction_name();
    }

    @Override
    protected void actionPerformed(PhpModule phpModule) {
        if (!Yii2Utils.isYii2(phpModule)) {
            // called via shortcut
            return;
        }

        Yii2Module yii2Module = Yii2Module.forPhpModule(phpModule);
        FileObject webroot = yii2Module.getDirectory(Alias.WEBROOT);
        if (webroot == null) {
            Yii2UiUtils.showErrorDialog("webroot directory doesn't exist.");
            return;
        }

        FileObject htaccess = webroot.getFileObject(".htaccess"); // NOI18N
        if (htaccess != null) {
            UiUtils.open(htaccess, 0);
            Yii2UiUtils.showErrorDialog(".htaccess already exists.");
            return;
        }

        try (InputStream inputStream = Yii2PhpFrameworkProvider.class.getResourceAsStream("resources/.htaccess")) { // NOI18N
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8")); // NOI18N
            OutputStream outputStream = webroot.createAndOpen(".htaccess"); // NOI18N
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"))) { // NOI18N
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    pw.println(line);
                }
            }
        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage());
        }

        // open file
        htaccess = webroot.getFileObject(".htaccess"); // NOI18N
        if (htaccess != null) {
            UiUtils.open(htaccess, 0);
        }
    }

}
