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
package org.nbphpcouncil.modules.php.yii2.utils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.netbeans.junit.NbTestCase;

/**
 *
 * @author junichi11
 */
public class Yii2UtilsTest extends NbTestCase {

    public Yii2UtilsTest(String name) {
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
     * Test of isPathAlias method, of class Yii2Utils.
     */
    @Test
    public void testIsPathAlias() {
        assertTrue(Yii2Utils.isPathAlias("@app"));
        assertTrue(Yii2Utils.isPathAlias("@app/controller"));
        assertFalse(Yii2Utils.isPathAlias("app/controller"));
        assertFalse(Yii2Utils.isPathAlias(""));
        assertFalse(Yii2Utils.isPathAlias(null));
    }

    /**
     * Test of trimWith method, of class Yii2Utils.
     */
    @Test
    public void testTrimWith() {
        assertEquals("site", Yii2Utils.trimWith("/site/", "/"));
        assertEquals("site/", Yii2Utils.trimWith("/site//", "/"));
        assertEquals("/site", Yii2Utils.trimWith("//site/", "/"));
        assertEquals("/site/", Yii2Utils.trimWith("/site/", ""));
        assertEquals("admin/site", Yii2Utils.trimWith("/admin/site/", "/"));
        assertEquals("site", Yii2Utils.trimWith("--site--", "--"));
        assertEquals("", Yii2Utils.trimWith("----", "--"));
        assertEquals("", Yii2Utils.trimWith("--", "--"));
    }

    /**
     * Test of dequote method, of class Yii2Utils.
     */
    @Test
    public void testDequote() {
        assertEquals("text", Yii2Utils.dequote("\"text\""));
        assertEquals("text", Yii2Utils.dequote("'text'"));

        assertEquals("text", Yii2Utils.dequote("text"));
        assertEquals("text\"", Yii2Utils.dequote("text\""));
        assertEquals("\"text", Yii2Utils.dequote("\"text"));
        assertEquals("'text", Yii2Utils.dequote("'text"));
        assertEquals("text'", Yii2Utils.dequote("text'"));
        assertEquals("\"text", Yii2Utils.dequote("\"\"text\""));
        assertEquals("'text", Yii2Utils.dequote("''text'"));

        assertEquals("", Yii2Utils.dequote(""));
        assertEquals(null, Yii2Utils.dequote(null));
    }

    /**
     * Test of toLowercaseWithDashes method, of class Yii2Utils.
     */
    @Test
    public void testToLowercaseWithDashes() {
        assertEquals("site", Yii2Utils.toLowercaseWithDashes("Site"));
        assertEquals("my-site", Yii2Utils.toLowercaseWithDashes("MySite"));
        assertEquals("my-special-site", Yii2Utils.toLowercaseWithDashes("MySpecialSite"));
        assertEquals("", Yii2Utils.toLowercaseWithDashes(""));
    }

    /**
     * Test of toUpperCamelcase method, of class Yii2Utils.
     */
    @Test
    public void testToUpperCamelcase() {
        assertEquals("Site", Yii2Utils.toUpperCamelcase("site"));
        assertEquals("MySite", Yii2Utils.toUpperCamelcase("my-site"));
        assertEquals("MySpecialSite", Yii2Utils.toUpperCamelcase("my-special-site"));
        assertEquals("", Yii2Utils.toUpperCamelcase(""));
    }

}
