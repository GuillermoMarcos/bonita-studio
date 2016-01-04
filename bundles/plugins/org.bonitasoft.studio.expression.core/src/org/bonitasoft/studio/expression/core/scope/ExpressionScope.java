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

import static com.google.common.collect.Lists.newArrayList;
import static org.bonitasoft.studio.common.predicate.ExpressionPredicates.withExpressionType;

import java.util.Collections;
import java.util.List;

import org.bonitasoft.studio.expression.core.provider.ProvidedExpressionProvider;
import org.bonitasoft.studio.model.expression.Expression;
import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.Iterables;

public class ExpressionScope {

    private final List<Expression> expressions;
    private final ModelLocation location;

    public ExpressionScope(final ModelLocation location, final List<Expression> expressions) {
        this.expressions = expressions;
        this.location = location;
    }

    public List<Expression> getExpressions() {
        return Collections.unmodifiableList(expressions);
    }

    public List<Expression> getProvidedExpressions() {
        return Collections.unmodifiableList(new ProvidedExpressionProvider().getExpressions(location));
    }

    public ModelLocation getModelLocation() {
        return location;
    }

    public List<Expression> getExpressionsWithType(final String expressionType) {
        return newArrayList(Iterables.filter(getExpressions(), withExpressionType(expressionType)));
    }

    public EObject getContext() {
        return new ContextFinder(location).findExpressionContext();
    }

}
