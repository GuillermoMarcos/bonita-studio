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

import java.util.List;
import java.util.Objects;

import org.bonitasoft.studio.common.ExpressionConstants;
import org.bonitasoft.studio.common.emf.tools.ModelHelper;
import org.bonitasoft.studio.expression.core.scope.ContextFinder;
import org.bonitasoft.studio.expression.core.scope.ModelLocation;
import org.bonitasoft.studio.model.expression.Expression;
import org.bonitasoft.studio.model.form.Form;
import org.bonitasoft.studio.model.form.FormPackage;
import org.bonitasoft.studio.model.form.Widget;
import org.bonitasoft.studio.model.form.WidgetDependency;
import org.eclipse.emf.ecore.EStructuralFeature;

public class ValidatorExpressionFilter implements ExpressionScopeFilter {

    /*
     * (non-Javadoc)
     * @see org.bonitasoft.studio.expression.core.scope.ExpressionScopeResolver#isRelevant(org.eclipse.emf.ecore.EStructuralFeature)
     */
    @Override
    public boolean isRelevant(final ModelLocation location) {
        final EStructuralFeature containingFeature = location.getContainingFeature();
        return Objects.equals(FormPackage.Literals.VALIDATOR__PARAMETER, containingFeature) ||
                Objects.equals(FormPackage.Literals.VALIDATOR__DISPLAY_NAME, containingFeature);
    }

    /*
     * (non-Javadoc)
     * @see com.google.common.base.Predicate#apply(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean apply(final ModelLocation location, final Expression expression) {
            Widget parentWidget = null;
        final Widget widget = new ContextFinder(location).find(Widget.class);
        if (widget != null) {
            parentWidget = ModelHelper.getParentWidget(widget);
            if (ExpressionConstants.FORM_FIELD_TYPE.equals(expression.getType())
                    && !(expression.getReferencedElements().isEmpty())
                    && isContingentField(parentWidget, expression)) {
                return false;
            }
        }
        return or(
                withVariableType(),
                withExpressionType(ExpressionConstants.CONSTANT_TYPE),
                withExpressionType(ExpressionConstants.FORM_FIELD_TYPE),
                withExpressionType(ExpressionConstants.SCRIPT_TYPE),
                withExpressionType(ExpressionConstants.PARAMETER_TYPE),
                withExpressionType(ExpressionConstants.DOCUMENT_TYPE)).apply(expression);

    }

    private boolean isContingentField(final Widget parentWidget, final Expression formFieldExpression) {
        final Widget widget = (Widget) formFieldExpression.getReferencedElements().get(0);
        if (parentWidget == null) {
            return false;
        }
        final Widget originalWidget = getOriginalWidget(widget, ModelHelper.getForm(parentWidget));
        for (final WidgetDependency dep : parentWidget.getDependOn()) {
            if (dep.getWidget().equals(originalWidget)) {
                return true;
            }
        }
        return false;
    }

    private Widget getOriginalWidget(final Widget widget, final Form form) {
        final List<Widget> widgetInsideForm = ModelHelper.getAllWidgetInsideForm(form);
        for (final Widget w : widgetInsideForm) {
            if (w.getName().equals(widget.getName())) {
                return w;
            }
        }
        return null;
    }

}
