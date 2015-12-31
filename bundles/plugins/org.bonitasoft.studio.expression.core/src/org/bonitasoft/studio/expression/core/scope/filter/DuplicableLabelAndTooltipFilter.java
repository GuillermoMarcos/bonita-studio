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
package org.bonitasoft.studio.expression.core.scope.filter;

import static com.google.common.base.Predicates.or;
import static org.bonitasoft.studio.common.predicate.ExpressionPredicates.withExpressionType;
import static org.bonitasoft.studio.common.predicate.ExpressionPredicates.withVariableType;

import java.util.Objects;

import org.bonitasoft.studio.common.ExpressionConstants;
import org.bonitasoft.studio.common.emf.tools.ModelHelper;
import org.bonitasoft.studio.model.expression.Expression;
import org.bonitasoft.studio.model.form.FormPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

public class DuplicableLabelAndTooltipFilter implements ExpressionScopeFilter {

    /*
     * (non-Javadoc)
     * @see org.bonitasoft.studio.expression.core.scope.ExpressionScopeResolver#isRelevant(org.eclipse.emf.ecore.EStructuralFeature)
     */
    @Override
    public boolean isRelevant(final Expression expression) {
        final EStructuralFeature containingFeature = expression.eContainingFeature();
        return Objects.equals(FormPackage.Literals.DUPLICABLE__TOOLTIP_FOR_ADD, containingFeature) ||
                Objects.equals(FormPackage.Literals.DUPLICABLE__TOOLTIP_FOR_REMOVE, containingFeature) ||
                Objects.equals(FormPackage.Literals.DUPLICABLE__DISPLAY_LABEL_FOR_REMOVE, containingFeature) ||
                Objects.equals(FormPackage.Literals.DUPLICABLE__DISPLAY_LABEL_FOR_ADD, containingFeature);
    }

    /*
     * (non-Javadoc)
     * @see com.google.common.base.Predicate#apply(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean apply(final Expression expression, final Expression expressionToTest) {
        if (ModelHelper.isInEntryPageFlowOnAPool(ModelHelper.getParentWidget(expression))) {
            return withExpressionType(ExpressionConstants.CONSTANT_TYPE).apply(expressionToTest);
        }
        return or(
                withVariableType(),
                withExpressionType(ExpressionConstants.CONSTANT_TYPE),
                withExpressionType(ExpressionConstants.SCRIPT_TYPE),
                withExpressionType(ExpressionConstants.PARAMETER_TYPE),
                withExpressionType(ExpressionConstants.SEARCH_INDEX_TYPE)).apply(expressionToTest);
    }

}
