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

import java.awt.Component;
import java.io.File;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.nbphpcouncil.modules.php.yii2.Yii2ProjectType;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;

/**
 *
 * @author junichi11
 */
public class Yii2CustomizerPanel extends JPanel {

    private static final long serialVersionUID = 9153535482640816795L;
    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private final File basePathFile;

    /**
     * Creates new form Yii2CustomizerPanel
     */
    public Yii2CustomizerPanel(File basePathFile) {
        initComponents();
        basePathTextField.getDocument().addDocumentListener(new DefaultDocumentListener());
        basePathTextField.getDocument().addDocumentListener(new BasePathDocumentListener());
        pathAliasesPathTextField.getDocument().addDocumentListener(new DefaultDocumentListener());
        for (Yii2ProjectType type : Yii2ProjectType.values()) {
            projectTypeComboBox.addItem(type.getRepositoryName());
        }
        this.basePathFile = basePathFile;
        updateBasePathLocation();
    }

    public boolean isYii2Enabled() {
        return enabledCheckBox.isSelected();
    }

    public void setYii2Enabled(boolean isEnabled) {
        enabledCheckBox.setSelected(isEnabled);
        setAllEnalbled(isEnabled);
    }

    public String getBasePath() {
        return basePathTextField.getText().trim();
    }

    public void setBasePath(String basePath) {
        basePathTextField.setText(basePath);
    }

    public Yii2ProjectType getProjectType() {
        String type = (String) projectTypeComboBox.getSelectedItem();
        return Yii2ProjectType.toEnum(type);
    }

    public void setProjectType(Yii2ProjectType projectType) {
        projectTypeComboBox.setSelectedItem(projectType.toString());
    }

    public String getPathAliasesPath() {
        return pathAliasesPathTextField.getText().trim();
    }

    public void setPathAliasesPaht(String path) {
        pathAliasesPathTextField.setText(path);
    }

    void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    private void fireChange() {
        changeSupport.fireChange();
    }

    private void updateBasePathLocation() {
        String basePath = getBasePath();
        File file = new File(basePathFile, basePath);
        String absolutePath = FileUtil.normalizePath(file.getAbsolutePath());
        basePathLocationTextField.setText(absolutePath);

    }

    private void setAllEnalbled(boolean isEnabled) {
        Component[] components = getComponents();
        for (Component component : components) {
            if (component == enabledCheckBox) {
                continue;
            }
            component.setEnabled(isEnabled);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        enabledCheckBox = new javax.swing.JCheckBox();
        basePathLabel = new javax.swing.JLabel();
        basePathTextField = new javax.swing.JTextField();
        projectTypeLabel = new javax.swing.JLabel();
        projectTypeComboBox = new javax.swing.JComboBox<String>();
        basePathLocationLabel = new javax.swing.JLabel();
        basePathLocationTextField = new javax.swing.JTextField();
        pathAliasesPathLabel = new javax.swing.JLabel();
        pathAliasesPathTextField = new javax.swing.JTextField();

        org.openide.awt.Mnemonics.setLocalizedText(enabledCheckBox, org.openide.util.NbBundle.getMessage(Yii2CustomizerPanel.class, "Yii2CustomizerPanel.enabledCheckBox.text")); // NOI18N
        enabledCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enabledCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(basePathLabel, org.openide.util.NbBundle.getMessage(Yii2CustomizerPanel.class, "Yii2CustomizerPanel.basePathLabel.text")); // NOI18N

        basePathTextField.setText(org.openide.util.NbBundle.getMessage(Yii2CustomizerPanel.class, "Yii2CustomizerPanel.basePathTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(projectTypeLabel, org.openide.util.NbBundle.getMessage(Yii2CustomizerPanel.class, "Yii2CustomizerPanel.projectTypeLabel.text")); // NOI18N

        projectTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectTypeComboBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(basePathLocationLabel, org.openide.util.NbBundle.getMessage(Yii2CustomizerPanel.class, "Yii2CustomizerPanel.basePathLocationLabel.text")); // NOI18N

        basePathLocationTextField.setEditable(false);
        basePathLocationTextField.setText(org.openide.util.NbBundle.getMessage(Yii2CustomizerPanel.class, "Yii2CustomizerPanel.basePathLocationTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(pathAliasesPathLabel, org.openide.util.NbBundle.getMessage(Yii2CustomizerPanel.class, "Yii2CustomizerPanel.pathAliasesPathLabel.text")); // NOI18N

        pathAliasesPathTextField.setText(org.openide.util.NbBundle.getMessage(Yii2CustomizerPanel.class, "Yii2CustomizerPanel.pathAliasesPathTextField.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(basePathLabel)
                    .addComponent(projectTypeLabel)
                    .addComponent(enabledCheckBox)
                    .addComponent(basePathLocationLabel)
                    .addComponent(pathAliasesPathLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(basePathTextField)
                    .addComponent(projectTypeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(basePathLocationTextField)
                    .addComponent(pathAliasesPathTextField, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(enabledCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(basePathLabel)
                    .addComponent(basePathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(basePathLocationLabel)
                    .addComponent(basePathLocationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(projectTypeLabel)
                    .addComponent(projectTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pathAliasesPathLabel)
                    .addComponent(pathAliasesPathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void projectTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectTypeComboBoxActionPerformed
        fireChange();
    }//GEN-LAST:event_projectTypeComboBoxActionPerformed

    private void enabledCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enabledCheckBoxActionPerformed
        setAllEnalbled(enabledCheckBox.isSelected());
        fireChange();
    }//GEN-LAST:event_enabledCheckBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel basePathLabel;
    private javax.swing.JLabel basePathLocationLabel;
    private javax.swing.JTextField basePathLocationTextField;
    private javax.swing.JTextField basePathTextField;
    private javax.swing.JCheckBox enabledCheckBox;
    private javax.swing.JLabel pathAliasesPathLabel;
    private javax.swing.JTextField pathAliasesPathTextField;
    private javax.swing.JComboBox<String> projectTypeComboBox;
    private javax.swing.JLabel projectTypeLabel;
    // End of variables declaration//GEN-END:variables

    private class DefaultDocumentListener implements DocumentListener {

        public DefaultDocumentListener() {
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            processUpdate();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            processUpdate();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            processUpdate();
        }

        private void processUpdate() {
            fireChange();
        }
    }

    private class BasePathDocumentListener implements DocumentListener {

        public BasePathDocumentListener() {
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            processUpdate();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            processUpdate();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            processUpdate();
        }

        private void processUpdate() {
            updateBasePathLocation();
        }
    }
}
