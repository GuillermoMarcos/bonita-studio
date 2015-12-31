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
package org.bonitasoft.studio.expression.core.scope;

import static com.google.common.collect.Iterables.find;

import java.util.ArrayList;
import java.util.List;

import org.bonitasoft.studio.expression.core.provider.ExpressionContentProvider;
import org.bonitasoft.studio.expression.core.provider.IExpressionNatureProvider;
import org.bonitasoft.studio.expression.core.provider.ProvidedExpressionProvider;
import org.bonitasoft.studio.expression.core.scope.filter.DataDefaultValueFilter;
import org.bonitasoft.studio.expression.core.scope.filter.DuplicableLabelAndTooltipFilter;
import org.bonitasoft.studio.expression.core.scope.filter.ExpressionScopeFilter;
import org.bonitasoft.studio.model.expression.Expression;
import org.bonitasoft.studio.model.process.MainProcess;
import org.eclipse.emf.ecore.EObject;

import com.google.common.base.Predicate;

public class ExpressionScopeResolver {

    private static final List<ExpressionScopeFilter> FILTERS;

    static {
        FILTERS = new ArrayList<>();
        FILTERS.add(new DuplicableLabelAndTooltipFilter());
        FILTERS.add(new DataDefaultValueFilter());
    }

    private static final List<ExpressionScopeFilter> PROVIDED_EXPRESSIONS_FILTERS;

    static {
        PROVIDED_EXPRESSIONS_FILTERS = new ArrayList<>();
    }

    public ExpressionScope resolve(final Expression expression) {
        final EObject context = resolveContext(expression);
        final List<Expression> expressions = resolveExpressions(context, expression);
        final List<Expression> providedExpressions = resolveProvidedExpressions(context, expression);
        return new ExpressionScope(context, expression, expressions, providedExpressions);
    }

    private EObject resolveContext(final Expression expression) {
        EObject eContainer = expression.eContainer();
        while (eContainer instanceof Expression) {
            eContainer = eContainer.eContainer();
        }
        if (eContainer == null) {
            throw new IllegalStateException("context is null");
        }
        if (!(eContainer instanceof MainProcess) && eContainer.eContainer() == null) {
            throw new IllegalStateException("context is not contained in the model");
        }
        return eContainer;
    }

    private List<Expression> resolveProvidedExpressions(final EObject context, final Expression expression) {
        final List<Expression> providedExpressions = new ArrayList<>();
        final ExpressionScopeFilter filter = find(PROVIDED_EXPRESSIONS_FILTERS, isRelevant(expression), null);
        for (final Expression exp : new ProvidedExpressionProvider().getExpressions(context)) {
            if (applyTo(expression, filter, exp)) {
                providedExpressions.add(exp);
            }
        }
        return providedExpressions;
    }

    private List<Expression> resolveExpressions(final EObject context, final Expression expression) {
        final IExpressionNatureProvider provider = ExpressionContentProvider.getInstance();
        final List<Expression> expressions = new ArrayList<>();
        final ExpressionScopeFilter filter = find(FILTERS, isRelevant(expression), null);
        for (final Expression exp : provider.getExpressions(context)) {
            if (applyTo(expression, filter, exp)) {
                expressions.add(exp);
            }
        }
        return expressions;
    }

    public boolean applyTo(final Expression expression, final ExpressionScopeFilter filter, final Expression expressionToTest) {
        return filter != null && filter.apply(expression, expressionToTest) || filter == null;
    }

    public boolean applyTo(final Expression expression, final Expression exp) {
        final ExpressionScopeFilter filter = find(FILTERS, isRelevant(expression), null);
        return filter != null && filter.apply(expression, exp) || filter == null;
    }

    private Predicate<? super ExpressionScopeFilter> isRelevant(final Expression expression) {
        return new Predicate<ExpressionScopeFilter>() {

            @Override
            public boolean apply(final ExpressionScopeFilter filter) {
                return filter.isRelevant(expression);
            }
        };
    }

}
