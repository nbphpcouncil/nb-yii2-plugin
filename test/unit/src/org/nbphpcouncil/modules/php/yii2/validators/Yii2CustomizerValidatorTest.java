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
package org.nbphpcouncil.modules.php.yii2.validators;

import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nbphpcouncil.modules.php.yii2.Yii2ProjectType;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.php.api.validation.ValidationResult;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author junichi11
 */
public class Yii2CustomizerValidatorTest extends NbTestCase {

    public Yii2CustomizerValidatorTest(String name) {
        super(name);
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    @Override
    public void setUp() {
    }

    @After
    @Override
    public void tearDown() {
    }

    /**
     * Test of validateProjectType method, of class Yii2CustomizerValidator.
     */
    @Test
    public void testValidateProjectType() {
        // OK
        ValidationResult result = new Yii2CustomizerValidator().validateProjectType(Yii2ProjectType.YII2_APP_ADVANCED)
                .getResult();
        assertTrue(!result.hasErrors());
        assertTrue(!result.hasWarnings());

        result = new Yii2CustomizerValidator().validateProjectType(Yii2ProjectType.YII2_APP_BASIC)
                .getResult();
        assertTrue(!result.hasErrors());
        assertTrue(!result.hasWarnings());

        // NG
        result = new Yii2CustomizerValidator().validateProjectType(Yii2ProjectType.YII2_NONE)
                .getResult();
        assertTrue(!result.hasErrors());
        assertTrue(result.hasWarnings());

        result = new Yii2CustomizerValidator().validateProjectType(null)
                .getResult();
        assertTrue(!result.hasErrors());
        assertTrue(result.hasWarnings());
    }

    /**
     * Test of validateBaseAppPath method, of class Yii2CustomizerValidator.
     */
    @Test
    public void testValidateBaseAppPath() {
        File dataDir = getDataDir();
        FileObject dataDirectory = FileUtil.toFileObject(dataDir);
        FileObject sourceDirectory = dataDirectory.getFileObject("base-app-path");

        // OK
        // base directory is source directory
        ValidationResult result = new Yii2CustomizerValidator()
                .validateBaseAppPath(sourceDirectory, "")
                .getResult();
        assertTrue(!result.hasErrors());
        assertTrue(!result.hasWarnings());

        result = new Yii2CustomizerValidator()
                .validateBaseAppPath(sourceDirectory, null)
                .getResult();
        assertTrue(!result.hasErrors());
        assertTrue(!result.hasWarnings());

        // sub directory
        result = new Yii2CustomizerValidator()
                .validateBaseAppPath(sourceDirectory, "app")
                .getResult();
        assertTrue(!result.hasErrors());
        assertTrue(!result.hasWarnings());

        // NG
        // project is broken
        result = new Yii2CustomizerValidator()
                .validateBaseAppPath(null, "")
                .getResult();
        assertTrue(!result.hasErrors());
        assertTrue(result.hasWarnings());

        // not directory
        result = new Yii2CustomizerValidator()
                .validateBaseAppPath(sourceDirectory, "app/empty")
                .getResult();
        assertTrue(!result.hasErrors());
        assertTrue(result.hasWarnings());

        // not found directory
        result = new Yii2CustomizerValidator()
                .validateBaseAppPath(sourceDirectory, "not-found")
                .getResult();
        assertTrue(!result.hasErrors());
        assertTrue(result.hasWarnings());

    }

    /**
     * Test of validatePathAliasesPath method, of class Yii2CustomizerValidator.
     */
    @Test
    public void testValidatePathAliasesPath() {
        File dataDir = getDataDir();
        FileObject dataDirectory = FileUtil.toFileObject(dataDir);

        // OK
        // normal
        FileObject sourceDirectory = dataDirectory.getFileObject("path-aliases-file");
        ValidationResult result = new Yii2CustomizerValidator()
                .validatePathAliasesPath(sourceDirectory, "yii.json")
                .getResult();
        assertTrue(!result.hasErrors());
        assertTrue(!result.hasWarnings());

        // subpath
        result = new Yii2CustomizerValidator()
                .validatePathAliasesPath(sourceDirectory, "directory/yii.json")
                .getResult();
        assertTrue(!result.hasErrors());
        assertTrue(!result.hasWarnings());

        // empty
        result = new Yii2CustomizerValidator()
                .validatePathAliasesPath(sourceDirectory, "")
                .getResult();
        assertTrue(!result.hasErrors());
        assertTrue(!result.hasWarnings());

        result = new Yii2CustomizerValidator()
                .validatePathAliasesPath(sourceDirectory, null)
                .getResult();
        assertTrue(!result.hasErrors());
        assertTrue(!result.hasWarnings());

        // NG
        // not found file
        result = new Yii2CustomizerValidator()
                .validatePathAliasesPath(sourceDirectory, "yii")
                .getResult();
        assertTrue(!result.hasErrors());
        assertTrue(result.hasWarnings());

        // broken project
        result = new Yii2CustomizerValidator()
                .validatePathAliasesPath(null, "yii.json")
                .getResult();
        assertTrue(!result.hasErrors());
        assertTrue(result.hasWarnings());

        // not json
        result = new Yii2CustomizerValidator()
                .validatePathAliasesPath(sourceDirectory, "notJson")
                .getResult();
        assertTrue(!result.hasErrors());
        assertTrue(result.hasWarnings());

        // not file
        result = new Yii2CustomizerValidator()
                .validatePathAliasesPath(sourceDirectory, "directory")
                .getResult();
        assertTrue(!result.hasErrors());
        assertTrue(result.hasWarnings());
    }

}
