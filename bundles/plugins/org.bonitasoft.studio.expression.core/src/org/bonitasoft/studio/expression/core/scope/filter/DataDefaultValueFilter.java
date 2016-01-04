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
import org.bonitasoft.studio.expression.core.scope.ContextFinder;
import org.bonitasoft.studio.expression.core.scope.ModelLocation;
import org.bonitasoft.studio.expression.core.scope.PageFlowContextResolver;
import org.bonitasoft.studio.model.expression.Expression;
import org.bonitasoft.studio.model.process.AbstractProcess;
import org.bonitasoft.studio.model.process.Data;
import org.bonitasoft.studio.model.process.DataAware;
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

    @Override
    public boolean isRelevant(final ModelLocation location) {
        final EStructuralFeature containingFeature = location.getContainingFeature();
        return Objects.equals(ProcessPackage.Literals.DATA__DEFAULT_VALUE, containingFeature);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean apply(final ModelLocation location, final Expression expression) {
        final boolean validType = or(
                withVariableType(),
                withExpressionType(ExpressionConstants.CONSTANT_TYPE),
                withExpressionType(ExpressionConstants.SCRIPT_TYPE),
                withExpressionType(ExpressionConstants.PARAMETER_TYPE),
                withExpressionType(ExpressionConstants.CONTRACT_INPUT_TYPE)).apply(expression);
        final DataAware dataContainer = new ContextFinder(location).find(DataAware.class);
        final Set<String> availableDataNames = getDataNames(location, dataContainer);
        if (ExpressionConstants.CONTRACT_INPUT_TYPE.equals(expression.getType())) {
            return dataContainer instanceof Pool;
        }
        if (isExpressionOfVariableType(expression) && !isNullOrEmpty(expression.getName())) {
            return availableDataNames.contains(expression.getName());
        } else if (isExpressionOfVariableType(expression) && isNullOrEmpty(expression.getName())) {
            return !(dataContainer instanceof AbstractProcess)
                    || dataContainer instanceof Pool && pageFlowContextResolver.isOverviewContext(location);
        }
        return validType;
    }

    public Set<String> getDataNames(final ModelLocation location, final EObject container) {
        final Set<String> availableDataNames = new HashSet<>();
        if (!(container instanceof AbstractProcess)) {
            final List<Data> availableData = ModelHelper.getAccessibleData(ModelHelper.getParentProcess(container));
            if (pageFlowContextResolver.isPageFlowContext(location) && container instanceof Task) {
                availableData.addAll(((Task) container).getData());
            }
            for (final Data d : availableData) {
                availableDataNames.add(d.getName());
            }
        } else {
            if (container instanceof Pool && pageFlowContextResolver.isOverviewContext(location)) {
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
