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

import org.bonitasoft.studio.expression.core.provider.ExpressionContentProvider;
import org.bonitasoft.studio.expression.core.provider.IExpressionNatureProvider;
import org.bonitasoft.studio.expression.core.scope.ExpressionScope;
import org.bonitasoft.studio.expression.core.scope.ExpressionScopeResolver;
import org.bonitasoft.studio.expression.core.scope.ModelLocation;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.google.common.collect.Lists;

public class DefaultExpressionScopeResolver implements ExpressionScopeResolver {

    public static final DefaultExpressionScopeResolver INSTANCE = new DefaultExpressionScopeResolver();

    /* (non-Javadoc)
     * @see org.bonitasoft.studio.expression.core.scope.ExpressionScopeResolver#applyTo(org.bonitasoft.studio.expression.core.scope.ModelLocation)
     */
    @Override
    public ExpressionScope applyTo(ModelLocation location) {
        final IExpressionNatureProvider provider = ExpressionContentProvider.getInstance();
        //        if (expressions != null) {
        //            filteredExpressions.addAll(Arrays.asList(expressions));
        //            if (context != null && filters != null) {
        //                for (final Expression exp : expressions) {
        //                    for (final ViewerFilter filter : filters) {
        //                        if (filter != null && !filter.select(viewer, input, exp)) {
        //                            filteredExpressions.remove(exp);
        //                        }
        //                    }
        //                }
        //            }
        //        }
        return new ExpressionScope(Lists.newArrayList(provider.getExpressions(location.getModelElement())));
    }

    /* (non-Javadoc)
     * @see org.bonitasoft.studio.expression.core.scope.ExpressionScopeResolver#isRelevant(org.eclipse.emf.ecore.EStructuralFeature)
     */
    @Override
    public boolean isRelevant(EStructuralFeature containgFeature) {
        return false;
    }

}
