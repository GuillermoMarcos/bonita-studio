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
package org.bonitasoft.studio.expression.core.scope.resolver;

import java.util.Objects;

import org.bonitasoft.studio.common.emf.tools.ModelHelper;
import org.bonitasoft.studio.expression.core.scope.ExpressionScope;
import org.bonitasoft.studio.expression.core.scope.ExpressionScopeResolver;
import org.bonitasoft.studio.expression.core.scope.ModelLocation;
import org.bonitasoft.studio.model.form.FormPackage;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;


public class DuplicableTooltipForAddResolver implements ExpressionScopeResolver {

    /* (non-Javadoc)
     * @see org.bonitasoft.studio.expression.core.scope.ExpressionScopeResolver#applyTo(org.bonitasoft.studio.expression.core.scope.ModelLocation)
     */
    @Override
    public ExpressionScope applyTo(ModelLocation location) {
        final EObject modelElement = location.getModelElement();
        if (ModelHelper.isInEntryPageFlowOnAPool(ModelHelper.getParentWidget(modelElement))) {

        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.bonitasoft.studio.expression.core.scope.ExpressionScopeResolver#isRelevant(org.eclipse.emf.ecore.EStructuralFeature)
     */
    @Override
    public boolean isRelevant(EStructuralFeature containgFeature) {
        return Objects.equals(FormPackage.Literals.DUPLICABLE__TOOLTIP_FOR_ADD, containgFeature);
    }

}
