/**
 * Copyright (C) 2015 Bonitasoft S.A.
 * Bonitasoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.studio.parameters.wizard.page;

import org.bonitasoft.studio.expression.core.scope.ContextFinder;
import org.bonitasoft.studio.expression.core.scope.ModelLocation;
import org.bonitasoft.studio.expression.editor.provider.IProposalListener;
import org.bonitasoft.studio.model.parameter.Parameter;
import org.bonitasoft.studio.model.process.Pool;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Maxence Raoux
 *
 */
public class CreateParameterProposalListener implements IProposalListener {

    @Override
    public String handleEvent(final ModelLocation location, final String fixedReturnType) {
        Assert.isNotNull(location);
        final Pool parentProcess = new ContextFinder(location).find(Pool.class);
        final AddParameterWizard parameterWizard = new AddParameterWizard(parentProcess, TransactionUtil.getEditingDomain(parentProcess));
        final ParameterWizardDialog parameterDialog = new ParameterWizardDialog(
                Display.getCurrent().getActiveShell().getParent().getShell(),
                parameterWizard);
        if (parameterDialog.open() == Dialog.OK) {
            final Parameter param = parameterWizard.getNewParameter();
            if (param != null) {
                return param.getName();
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.bonitasoft.studio.expression.editor.provider.IProposalListener#setEStructuralFeature(org.eclipse.emf.ecore.EStructuralFeature)
     */
    @Override
    public void setEStructuralFeature(final EStructuralFeature feature) {

    }

    @Override
    public boolean isRelevant(final ModelLocation location) {
        return new ContextFinder(location).find(Pool.class) != null;
    }

}
