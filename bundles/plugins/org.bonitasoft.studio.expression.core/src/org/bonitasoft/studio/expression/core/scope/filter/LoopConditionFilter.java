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

import java.util.Objects;

import org.bonitasoft.studio.expression.core.scope.ModelLocation;
import org.bonitasoft.studio.model.expression.Expression;
import org.bonitasoft.studio.model.process.ProcessPackage;


public class LoopConditionFilter implements ExpressionScopeFilter {

    /* (non-Javadoc)
     * @see org.bonitasoft.studio.expression.core.scope.filter.ExpressionScopeFilter#isRelevant(org.bonitasoft.studio.expression.core.scope.ModelLocation)
     */
    @Override
    public boolean isRelevant(ModelLocation location) {
        return Objects.equals(ProcessPackage.Literals.MULTI_INSTANTIABLE__LOOP_CONDITION, location.getContainingFeature());
    }

    /* (non-Javadoc)
     * @see org.bonitasoft.studio.expression.core.scope.filter.ExpressionScopeFilter#apply(org.bonitasoft.studio.expression.core.scope.ModelLocation, org.bonitasoft.studio.model.expression.Expression)
     */
    @Override
    public boolean apply(ModelLocation location, Expression expression) {
        // TODO Auto-generated method stub
        return false;
    }

}
