/**
 * Copyright (C) 2014 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.studio.tests.refactoring.widget;

import static org.assertj.core.api.Assertions.assertThat;

import org.bonitasoft.studio.common.ExpressionConstants;
import org.bonitasoft.studio.model.expression.assertions.ExpressionAssert;
import org.bonitasoft.studio.model.form.CheckBoxSingleFormField;
import org.bonitasoft.studio.preferences.BonitaPreferenceConstants;
import org.bonitasoft.studio.preferences.BonitaStudioPreferencesPlugin;
import org.bonitasoft.studio.swtbot.framework.application.BotApplicationWorkbenchWindow;
import org.bonitasoft.studio.swtbot.framework.diagram.BotProcessDiagramPerspective;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swtbot.eclipse.gef.finder.SWTBotGefTestCase;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * @author Romain Bioteau
 *
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class RefactorWidgetIT extends SWTBotGefTestCase {


    private boolean askRename;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        askRename = BonitaStudioPreferencesPlugin.getDefault().getPreferenceStore().getBoolean(BonitaPreferenceConstants.ASK_RENAME_ON_FIRST_SAVE);
        BonitaStudioPreferencesPlugin.getDefault().getPreferenceStore().setValue(BonitaPreferenceConstants.ASK_RENAME_ON_FIRST_SAVE, false);
    }

    @After
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        bot.saveAllEditors();
        bot.closeAllEditors();
        BonitaStudioPreferencesPlugin.getDefault().getPreferenceStore().setValue(BonitaPreferenceConstants.ASK_RENAME_ON_FIRST_SAVE, askRename);
    }

    /**
     * @throws Exception
     */
    @Test
    public void should_rename_a_widget_update_reference_expression() throws Exception {
        final BotApplicationWorkbenchWindow botApplicationWorkbenchWindow = new BotApplicationWorkbenchWindow(bot);
        final BotProcessDiagramPerspective diagramPerspective = botApplicationWorkbenchWindow.createNewDiagram();
        diagramPerspective.activeProcessDiagramEditor().selectElement("Step1");
        diagramPerspective.getDiagramPropertiesPart().selectApplicationTab().selectPageflowTab().addForm().finish();

        diagramPerspective.activeFormDiagramEditor().addWidget("Checkbox", 1, 1).save();
        diagramPerspective.activeFormDiagramEditor().selectElement("Checkbox1");
        diagramPerspective.getFormPropertiesPart().selectGeneralTab().selectDataTab().editOutputOperationExpression().selectFormFieldType()
                .selectFormField("Check Box Single Form Field Checkbox1").ok();

        diagramPerspective.getFormPropertiesPart().selectGeneralTab().selectGeneralTab().setName("ValidCheckbox");

        final EObject checkbox = diagramPerspective.activeFormDiagramEditor().getSelectedSemanticElement();
        assertThat(checkbox).isInstanceOf(CheckBoxSingleFormField.class);
        assertThat(((CheckBoxSingleFormField)checkbox).getAction().getRightOperand()).isNotNull();
        ExpressionAssert.assertThat(((CheckBoxSingleFormField) checkbox).getAction().getRightOperand()).hasType(ExpressionConstants.FORM_FIELD_TYPE)
                .hasName("field_ValidCheckbox");

    }

}