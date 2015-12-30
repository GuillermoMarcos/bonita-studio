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

import java.util.Collections;
import java.util.List;

import org.bonitasoft.studio.common.predicate.ExpressionPredicates;
import org.bonitasoft.studio.model.expression.Expression;

import com.google.common.collect.Iterables;

public class ExpressionScope {

    private final List<Expression> expressions;
    private final ModelLocation location;
    private final List<Expression> providedExpressions;

    public ExpressionScope(ModelLocation location, List<Expression> expressions, List<Expression> providedExpressions) {
        this.location = location;
        this.expressions = expressions;
        this.providedExpressions = providedExpressions;
    }

    public List<Expression> getExpressions() {
        return Collections.unmodifiableList(expressions);
    }

    public List<Expression> getProvidedExpressions() {
        return Collections.unmodifiableList(providedExpressions);
    }

    public ModelLocation getLocation() {
        return location;
    }

    public List<Expression> getExpressionsWithType(String expressionType) {
        return newArrayList(Iterables.filter(getExpressions(), ExpressionPredicates.withExpressionType(expressionType)));
    }

}
