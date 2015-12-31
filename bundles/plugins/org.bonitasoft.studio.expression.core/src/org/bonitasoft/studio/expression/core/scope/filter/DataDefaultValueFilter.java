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
import static com.google.common.base.Strings.isNullOrEmpty;
import static org.bonitasoft.studio.common.predicate.ExpressionPredicates.withExpressionType;
import static org.bonitasoft.studio.common.predicate.ExpressionPredicates.withVariableType;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.bonitasoft.studio.common.ExpressionConstants;
import org.bonitasoft.studio.common.emf.tools.ModelHelper;
import org.bonitasoft.studio.expression.core.scope.PageFlowContextResolver;
import org.bonitasoft.studio.model.expression.Expression;
import org.bonitasoft.studio.model.process.AbstractProcess;
import org.bonitasoft.studio.model.process.Data;
import org.bonitasoft.studio.model.process.Pool;
import org.bonitasoft.studio.model.process.ProcessPackage;
import org.bonitasoft.studio.model.process.Task;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class DataDefaultValueFilter implements ExpressionScopeFilter {

    private final PageFlowContextResolver pageFlowContextResolver;

    public DataDefaultValueFilter() {
        pageFlowContextResolver = new PageFlowContextResolver();
    }

    /*
     * (non-Javadoc)
     * @see org.bonitasoft.studio.expression.core.scope.ExpressionScopeResolver#isRelevant(org.eclipse.emf.ecore.EStructuralFeature)
     */
    @Override
    public boolean isRelevant(final Expression expression) {
        final EStructuralFeature containingFeature = expression.eContainingFeature();
        return Objects.equals(ProcessPackage.Literals.DATA__DEFAULT_VALUE, containingFeature);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean apply(final Expression expression, final Expression expressionToTest) {
        final boolean validType = or(
                withVariableType(),
                withExpressionType(ExpressionConstants.CONSTANT_TYPE),
                withExpressionType(ExpressionConstants.SCRIPT_TYPE),
                withExpressionType(ExpressionConstants.PARAMETER_TYPE),
                withExpressionType(ExpressionConstants.CONTRACT_INPUT_TYPE)).apply(expressionToTest);
        final EObject dataContainer = expression.eContainer().eContainer();
        final Set<String> availableDataNames = getDataNames(expression.eContainer(), dataContainer);
        if (ExpressionConstants.CONTRACT_INPUT_TYPE.equals(expressionToTest.getType())) {
            return dataContainer instanceof Pool;
        }
        if (isExpressionOfVariableType(expressionToTest) && !isNullOrEmpty(expressionToTest.getName())) {
            return availableDataNames.contains(expressionToTest.getName());
        } else if (isExpressionOfVariableType(expressionToTest) && isNullOrEmpty(expressionToTest.getName())) {
            return !(dataContainer instanceof AbstractProcess)
                    || dataContainer instanceof Pool && pageFlowContextResolver.isOverviewContext(expression.eContainer());
        }
        return validType;
    }

    public Set<String> getDataNames(final EObject data, final EObject container) {
        final Set<String> availableDataNames = new HashSet<>();
        if (!(container instanceof AbstractProcess)) {
            final List<Data> availableData = ModelHelper.getAccessibleData(ModelHelper.getParentProcess(container));
            if (pageFlowContextResolver.isPageFlowContext(data) && container instanceof Task) {
                availableData.addAll(((Task) container).getData());
            }
            for (final Data d : availableData) {
                availableDataNames.add(d.getName());
            }
        } else {
            if (container instanceof Pool && pageFlowContextResolver.isOverviewContext(data)) {
                final List<Data> availableData = ModelHelper.getAccessibleData(ModelHelper.getParentProcess(container));
                availableData.addAll(((Pool) container).getData());
                for (final Data d : availableData) {
                    availableDataNames.add(d.getName());
                }
            }
        }
        return availableDataNames;
    }

    protected boolean isExpressionOfVariableType(final Expression element) {
        return ExpressionConstants.VARIABLE_TYPE.equals(element.getType());
    }

}
