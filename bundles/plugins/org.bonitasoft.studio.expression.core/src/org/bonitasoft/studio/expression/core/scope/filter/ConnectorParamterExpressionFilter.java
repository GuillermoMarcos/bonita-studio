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

import org.bonitasoft.engine.bpm.connector.ConnectorEvent;
import org.bonitasoft.studio.common.ExpressionConstants;
import org.bonitasoft.studio.expression.core.scope.ContextFinder;
import org.bonitasoft.studio.expression.core.scope.ModelLocation;
import org.bonitasoft.studio.model.connectorconfiguration.ConnectorConfigurationPackage;
import org.bonitasoft.studio.model.expression.Expression;
import org.bonitasoft.studio.model.form.Form;
import org.bonitasoft.studio.model.form.SubmitFormButton;
import org.bonitasoft.studio.model.form.Widget;
import org.bonitasoft.studio.model.process.Activity;
import org.bonitasoft.studio.model.process.ConnectableElement;
import org.bonitasoft.studio.model.process.Connector;
import org.bonitasoft.studio.model.process.Pool;

public class ConnectorParamterExpressionFilter implements ExpressionScopeFilter {

    /*
     * (non-Javadoc)
     * @see org.bonitasoft.studio.expression.core.scope.ExpressionScopeResolver#isRelevant(org.eclipse.emf.ecore.EStructuralFeature)
     */
    @Override
    public boolean isRelevant(final ModelLocation location) {
        return new ContextFinder(location).find(ConnectorConfigurationPackage.Literals.CONNECTOR_PARAMETER__EXPRESSION) != null;
    }

    /*
     * (non-Javadoc)
     * @see com.google.common.base.Predicate#apply(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean apply(final ModelLocation location, final Expression expression) {
        final Widget parentWidget = null;
        final ContextFinder contextFinder = new ContextFinder(location);
        final ConnectableElement container = contextFinder.find(ConnectableElement.class);
        if (container instanceof Form || container instanceof SubmitFormButton) {
            return or(
                    withVariableType(),
                    withExpressionType(ExpressionConstants.CONSTANT_TYPE),
                    withExpressionType(ExpressionConstants.FORM_FIELD_TYPE),
                    withExpressionType(ExpressionConstants.SCRIPT_TYPE),
                    withExpressionType(ExpressionConstants.PARAMETER_TYPE),
                    withExpressionType(ExpressionConstants.DOCUMENT_REF_TYPE)).apply(expression);
        }
        if (isSupportingContractInput(contextFinder.find(Connector.class))) {
            return or(
                    withVariableType(),
                    withExpressionType(ExpressionConstants.CONTRACT_INPUT_TYPE),
                    withExpressionType(ExpressionConstants.CONSTANT_TYPE),
                    withExpressionType(ExpressionConstants.SCRIPT_TYPE),
                    withExpressionType(ExpressionConstants.PARAMETER_TYPE),
                    withExpressionType(ExpressionConstants.DOCUMENT_REF_TYPE)).apply(expression);
        }
        return or(
                withVariableType(),
                withExpressionType(ExpressionConstants.CONSTANT_TYPE),
                withExpressionType(ExpressionConstants.SCRIPT_TYPE),
                withExpressionType(ExpressionConstants.PARAMETER_TYPE),
                withExpressionType(ExpressionConstants.DOCUMENT_REF_TYPE)).apply(expression);

    }

    private boolean isSupportingContractInput(final Connector connector) {
        if (connector != null) {
            return isSupportingContractInputOnActivity(connector)
                    || isSupportingContractInputOnPool(connector);
        } else {
            return false;
        }
    }

    private boolean isSupportingContractInputOnPool(final Connector connector) {
        return isConnectorIsOnPool(connector)
                && ConnectorEvent.ON_ENTER.name().equals(
                        connector.getEvent());
    }

    private boolean isSupportingContractInputOnActivity(final Connector connector) {
        return isConnectorIsOnActivity(connector)
                && ConnectorEvent.ON_FINISH.name().equals(
                        connector.getEvent());
    }

    protected boolean isConnectorIsOnPool(final Connector connector) {
        return connector.eContainer() instanceof Pool;
    }

    protected boolean isConnectorIsOnActivity(final Connector connector) {
        return connector.eContainer() instanceof Activity;
    }

}
