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
import org.bonitasoft.studio.expression.core.scope.filter.DuplicableLabelAndTooltipFilter;
import org.bonitasoft.studio.expression.core.scope.filter.ExpressionScopeFilter;
import org.bonitasoft.studio.model.expression.Expression;

import com.google.common.base.Predicate;

public class ExpressionScopeResolver {

    private static final List<ExpressionScopeFilter> FILTERS;

    static {
        FILTERS = new ArrayList<>();
        FILTERS.add(new DuplicableLabelAndTooltipFilter());
    }

    private static final List<ExpressionScopeFilter> PROVIDED_EXPRESSIONS_FILTERS;

    static {
        PROVIDED_EXPRESSIONS_FILTERS = new ArrayList<>();
    }

    public ExpressionScope resolve(final ModelLocation location) {
        final List<Expression> expressions = resolveExpressions(location);
        final List<Expression> providedExpressions = resolveProvidedExpressions(location);
        return new ExpressionScope(location, expressions, providedExpressions);
    }

    private List<Expression> resolveProvidedExpressions(final ModelLocation location) {
        final List<Expression> providedExpressions = new ArrayList<>();
        final ExpressionScopeFilter filter = find(PROVIDED_EXPRESSIONS_FILTERS, isRelevant(location), null);
        for (final Expression expression : new ProvidedExpressionProvider().getExpressions(location.getModelElement())) {
            if (applyTo(filter, location, expression)) {
                providedExpressions.add(expression);
            }
        }
        return providedExpressions;
    }

    private List<Expression> resolveExpressions(final ModelLocation location) {
        final IExpressionNatureProvider provider = ExpressionContentProvider.getInstance();
        final List<Expression> expressions = new ArrayList<>();
        final ExpressionScopeFilter filter = find(FILTERS, isRelevant(location), null);
        for (final Expression expression : provider.getExpressions(location.getModelElement())) {
            if (applyTo(filter, location, expression)) {
                expressions.add(expression);
            }
        }
        return expressions;
    }

    public boolean applyTo(final ExpressionScopeFilter filter, final ModelLocation location, final Expression expression) {
        return filter != null && filter.apply(location, expression) || filter == null;
    }

    public boolean applyTo(final ModelLocation location, final Expression expression) {
        final ExpressionScopeFilter filter = find(FILTERS, isRelevant(location), null);
        return filter != null && filter.apply(location, expression) || filter == null;
    }

    private Predicate<? super ExpressionScopeFilter> isRelevant(final ModelLocation location) {
        return new Predicate<ExpressionScopeFilter>() {

            @Override
            public boolean apply(final ExpressionScopeFilter filter) {
                return filter.isRelevant(location);
            }
        };
    }

}
