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

import org.nbphpcouncil.modules.php.yii2.Yii2PhpFrameworkProvider;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;

/**
 *
 * @author junichi11
 */
public final class Yii2Utils {

    private Yii2Utils() {
    }

    /**
     * Check whether current project is in Yii2 application.
     *
     * @param phpModule
     * @return {@code true} if Yii2 application, {@code false} otherwise.
     */
    public static boolean isYii2(PhpModule phpModule) {
        if (phpModule == null) {
            return false;
        }
        return Yii2PhpFrameworkProvider.getInstance().isInPhpModule(phpModule);
    }

    /**
     * Check whether path aliase is used.
     *
     * @param path
     * @return {@code true} if path starts with @, {@code false} otherwise.
     */
    public static boolean isPathAlias(String path) {
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        return path.startsWith("@"); // NOI18N
    }

    /**
     * Trim specific text from target text. Check only start position and end
     * position.
     *
     * @param target target text
     * @param text for trimming
     * @return trimmed text
     */
    public static String trimWith(@NonNull String target, @NonNull String text) {
        if (text.isEmpty()) {
            return target;
        }
        String trimmed = ""; // NOI18N
        int length = target.length();
        if (target.startsWith(text) && length > 0) {
            trimmed = target.substring(text.length());
        }

        length = trimmed.length();
        if (trimmed.endsWith(text) && length > 0) {
            trimmed = trimmed.substring(0, length - text.length());
        }
        return trimmed;
    }

    /**
     * Dequote
     *
     * @param target
     * @return
     */
    public static String dequote(String target) {
        if (target == null) {
            return null;
        }
        if (target.startsWith("'") && target.endsWith("'")) { // NOI18N
            return trimWith(target, "'"); // NOI18N
        }
        if (target.startsWith("\"") && target.endsWith("\"")) { // NOI18N
            return trimWith(target, "\""); // NOI18N
        }
        return target;
    }

    /**
     * Conver to lowercase with dashes from upper camelcase. e.g. MySpecialSite
     * -> my-special-site.
     *
     * @param text target text
     * @return lowercase text
     */
    public static String toLowercaseWithDashes(String text) {
        char[] chars = text.toCharArray();
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (char c : chars) {
            if (isFirst) {
                sb.append(Character.toLowerCase(c));
                isFirst = false;
                continue;
            }

            if (Character.isUpperCase(c)) {
                sb.append("-").append(Character.toLowerCase(c)); // NOI18N
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Convert from lowercase with dashes to upper camelcase. e.g.
     * my-special-site -> MySpecialSite
     *
     * @param text target text
     * @return camelcase text
     */
    public static String toUpperCamelcase(String text) {
        char[] chars = text.toCharArray();
        boolean isNextUppercase = true;
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            if (isNextUppercase) {
                sb.append(Character.toUpperCase(c));
                isNextUppercase = false;
                continue;
            }
            if (c == '-') { // NOI18N
                isNextUppercase = true;
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
