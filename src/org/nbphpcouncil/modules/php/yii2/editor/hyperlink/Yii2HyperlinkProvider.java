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
package org.nbphpcouncil.modules.php.yii2.editor.hyperlink;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.Document;
import org.nbphpcouncil.modules.php.yii2.api.Yii2Module;
import org.nbphpcouncil.modules.php.yii2.api.Yii2ModuleUtils;
import org.nbphpcouncil.modules.php.yii2.editor.navi.GoToDefaultItem;
import org.nbphpcouncil.modules.php.yii2.editor.navi.GoToItem;
import org.nbphpcouncil.modules.php.yii2.editor.navi.GoToViewItem;
import org.nbphpcouncil.modules.php.yii2.utils.Yii2DocUtils;
import org.nbphpcouncil.modules.php.yii2.utils.Yii2Utils;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.lib.editor.hyperlink.spi.HyperlinkProviderExt;
import org.netbeans.lib.editor.hyperlink.spi.HyperlinkType;
import org.netbeans.modules.csl.api.UiUtils;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.editor.CodeUtils;
import org.netbeans.modules.php.editor.api.ElementQuery;
import org.netbeans.modules.php.editor.api.ElementQueryFactory;
import org.netbeans.modules.php.editor.api.NameKind;
import org.netbeans.modules.php.editor.api.QualifiedName;
import org.netbeans.modules.php.editor.api.QuerySupportFactory;
import org.netbeans.modules.php.editor.api.elements.ClassElement;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;
import org.netbeans.modules.php.editor.parser.api.Utils;
import org.netbeans.modules.php.editor.parser.astnodes.Expression;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.MethodInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.Scalar;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;

/**
 *
 * @author junichi11
 */
@MimeRegistration(mimeType = FileUtils.PHP_MIME_TYPE, service = HyperlinkProviderExt.class)
public class Yii2HyperlinkProvider implements HyperlinkProviderExt {

    private final List<GoToItem> items = new ArrayList<>();
    private int startOffset;
    private int endOffset;
    private static final int DEFAULT_OFFSET = 0;
    private static final int TIMEOUT_TIME = 300;
    private static final RequestProcessor RP = new RequestProcessor(Yii2HyperlinkProvider.class);
    private static final Logger LOGGER = Logger.getLogger(Yii2HyperlinkProvider.class.getName());

    @Override
    public Set<HyperlinkType> getSupportedHyperlinkTypes() {
        return EnumSet.of(HyperlinkType.GO_TO_DECLARATION);
    }

    private void init() {
        items.clear();
        startOffset = 0;
        endOffset = 0;
    }

    @Override
    public boolean isHyperlinkPoint(Document document, final int offset, HyperlinkType hyperlinkType) {
        final FileObject fileObject = NbEditorUtilities.getFileObject(document);
        PhpModule phpModule = PhpModule.Factory.forFileObject(fileObject);

        // Check whether current project is yii2
        if (!Yii2Utils.isYii2(phpModule)) {
            return false;
        }

        init();

        TokenSequence<PHPTokenId> ts = Yii2DocUtils.getTokenSequence(document);
        if (ts == null) {
            return false;
        }
        ts.move(offset);
        if (!ts.movePrevious() || !ts.moveNext()) {
            return false;
        }
        Token<PHPTokenId> token = ts.token();
        PHPTokenId id = token.id();
        if (id != PHPTokenId.PHP_CONSTANT_ENCAPSED_STRING) {
            return false;
        }
        String caretPositionText = Yii2Utils.dequote(token.text().toString());

        // set offset
        startOffset = ts.offset();
        endOffset = startOffset + token.length();

        // class path
        FileObject classFileObject = getClassFileObject(phpModule, caretPositionText);
        if (classFileObject != null) {
            GoToItem item = new GoToDefaultItem(caretPositionText, fileObject, DEFAULT_OFFSET);
            items.add(item);
            return true;
        }

        // controller
        Yii2Module yii2Module = Yii2Module.forPhpModule(phpModule);
        if (yii2Module.isCategory(fileObject, Yii2Module.Category.CONTROLLERS)) {
            Future<ControllerVisitor> visitorFuture = RP.submit(new Callable<ControllerVisitor>() {

                @Override
                public ControllerVisitor call() throws Exception {
                    final ControllerVisitor visitor = new ControllerVisitor(offset);
                    try {
                        ParserManager.parse(Collections.singleton(Source.create(fileObject)), new UserTask() {
                            @Override
                            public void run(ResultIterator resultIterator) throws Exception {
                                ParserResult parseResult = (ParserResult) resultIterator.getParserResult();
                                visitor.scan(Utils.getRoot(parseResult));
                            }
                        });
                    } catch (ParseException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    return visitor;
                }
            });
            ControllerVisitor visitor;
            try {
                visitor = visitorFuture.get(TIMEOUT_TIME, TimeUnit.MILLISECONDS);
                String viewPath = visitor.getViewPath();
                if (!StringUtils.isEmpty(viewPath)) {
                    GoToItem goToItem = createGoToViewItem(viewPath, phpModule, fileObject);
                    if (goToItem != null) {
                        items.add(goToItem);
                        return true;
                    }
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } catch (TimeoutException ex) {
                LOGGER.log(Level.INFO, "Timeout for GoToView: {0}", TIMEOUT_TIME);
            }
        }
        return false;
    }

    /**
     * Get class file if target is class name.
     *
     * @param phpModule PhpModule
     * @return FileObject for class if file exists, otherwise null.
     */
    @CheckForNull
    private FileObject getClassFileObject(PhpModule phpModule, String target) {
        if (!StringUtils.isEmpty(target) && !target.contains(" ")) { // NOI18N
            ElementQuery.Index indexQuery = ElementQueryFactory.createIndexQuery(QuerySupportFactory.get(phpModule.getSourceDirectory()));
            Set<ClassElement> classElements = indexQuery.getClasses(NameKind.create(QualifiedName.create(target), QuerySupport.Kind.EXACT));
            for (ClassElement element : classElements) {
                return element.getFileObject();
            }
        }
        return null;
    }

    private GoToItem createGoToViewItem(String viewPath, PhpModule phpModule, FileObject controller) {
        if (!StringUtils.isEmpty(viewPath)) {
            Yii2Module yii2Module = Yii2Module.forPhpModule(phpModule);
            // in case of alias
            // starts with "@"
            FileObject view = null;
            if (Yii2Utils.isPathAlias(viewPath)) {
                int indexOfSlash = viewPath.lastIndexOf("/"); // NOI18N
                if (indexOfSlash != -1) {
                    String alias = viewPath.substring(0, indexOfSlash);
                    String viewName = viewPath.substring(indexOfSlash + 1);
                    List<FileObject> directories = yii2Module.getDirectriesFromPathAlias(alias);
                    for (FileObject directory : directories) {
                        view = directory.getFileObject(viewName + ".php"); // NOI18N
                        if (view != null) {
                            break;
                        }
                    }
                }
            }

            if (view == null) {
                view = Yii2ModuleUtils.getView(yii2Module, controller, viewPath);
            }
            return new GoToViewItem(viewPath, view, DEFAULT_OFFSET);
        }
        return null;
    }

    @Override
    public int[] getHyperlinkSpan(Document document, int offset, HyperlinkType hyperlinkType) {
        return new int[]{startOffset, endOffset};
    }

    @Override
    public void performClickAction(Document document, int offset, HyperlinkType hyperlinkType) {
        if (items.isEmpty()) {
            return;
        }

        GoToItem item = items.get(0);
        FileObject fileObject = item.getFileObject();
        if (fileObject == null) {
            return;
        }

        // try to get EditorCookie
        try {
            DataObject dataObject = DataObject.find(fileObject);
            EditorCookie editorCookie = dataObject.getLookup().lookup(EditorCookie.class);
            if (editorCookie != null) {
                editorCookie.open();
                return;
            }
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        UiUtils.open(fileObject, item.getOffset());
    }

    @Override
    public String getTooltipText(Document document, int offset, HyperlinkType hyperlinkType) {
        if (items.isEmpty()) {
            return ""; // NOI18N
        }
        GoToItem item = items.get(0);
        return item.getTooltipText();
    }

    private static class ControllerVisitor extends DefaultVisitor {

        private final int offset;
        private String viewPath;

        public ControllerVisitor(int offset) {
            this.offset = offset;
        }

        @Override
        public void visit(MethodInvocation node) {
            super.visit(node);
            FunctionInvocation method = node.getMethod();
            String methodName = CodeUtils.extractFunctionName(method);
            if (methodName.equals("render") // NOI18N
                    || methodName.equals("renderPartial") // NOI18N
                    || methodName.equals("renderAjax") // NOI18N
                    || methodName.equals("renderFile")) { // NOI18N
                int startOffset = node.getStartOffset();
                int endOffset = node.getEndOffset();
                if (startOffset < offset && offset < endOffset) {
                    List<Expression> parameters = method.getParameters();
                    for (Expression parameter : parameters) {
                        startOffset = parameter.getStartOffset();
                        endOffset = parameter.getEndOffset();
                        if (startOffset < offset && offset < endOffset) {
                            if (parameter instanceof Scalar) {
                                Scalar s = (Scalar) parameter;
                                String stringValue = s.getStringValue();
                                viewPath = Yii2Utils.dequote(stringValue);
                                break;
                            }
                        }
                    }
                }
            }
        }

        public String getViewPath() {
            return viewPath;
        }
    }
}
