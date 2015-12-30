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
package org.bonitasoft.studio.expression.core.provider;

import java.util.ArrayList;
import java.util.List;

import org.bonitasoft.engine.expression.ExpressionConstants;
import org.bonitasoft.studio.common.emf.tools.ModelHelper;
import org.bonitasoft.studio.model.expression.Expression;
import org.bonitasoft.studio.model.expression.ExpressionFactory;
import org.bonitasoft.studio.model.form.Form;
import org.bonitasoft.studio.model.form.Widget;
import org.bonitasoft.studio.model.process.AbstractProcess;
import org.bonitasoft.studio.model.process.Activity;
import org.bonitasoft.studio.model.process.MultiInstanceType;
import org.bonitasoft.studio.model.process.Task;
import org.eclipse.emf.ecore.EObject;

public class ProvidedExpressionProvider {

    public List<Expression> getExpressions(EObject context, final boolean isPageFlowContext) {
        final List<Expression> result = new ArrayList<Expression>();
        result.add(toExpression(ExpressionConstants.API_ACCESSOR));
        result.add(toExpression(ExpressionConstants.PROCESS_DEFINITION_ID));
        result.add(toExpression(ExpressionConstants.ROOT_PROCESS_INSTANCE_ID));
        result.add(toExpression(ExpressionConstants.PROCESS_INSTANCE_ID));
        result.add(toExpression(ExpressionConstants.ACTIVITY_INSTANCE_ID));
        if (context instanceof Expression) {
            context = context.eContainer();
        }
        if (isPageFlowContext) {
            result.add(toExpression(ExpressionConstants.LOGGED_USER_ID));
        }
        if (context instanceof Activity) {
            if (((Activity) context).getType() == MultiInstanceType.PARALLEL || ((Activity) context).getType() == MultiInstanceType.SEQUENTIAL) {
                result.add(toExpression(ExpressionConstants.NUMBER_OF_TERMINATED_INSTANCES));
                result.add(toExpression(ExpressionConstants.NUMBER_OF_COMPLETED_INSTANCES));
                result.add(toExpression(ExpressionConstants.NUMBER_OF_INSTANCES));
            } else if (((Activity) context).getType() == MultiInstanceType.STANDARD) {
                result.add(toExpression(ExpressionConstants.LOOP_COUNTER));
            }
        }
        if (context instanceof Task) {
            result.add(toExpression(ExpressionConstants.TASK_ASSIGNEE_ID));
        } else if (context instanceof Widget
                && ModelHelper.getPageFlow((Widget) context) != null) {
            result.add(toExpression(ExpressionConstants.LOGGED_USER_ID));
            if (!(ModelHelper.getPageFlow((Widget) context) instanceof AbstractProcess)) {
                result.add(toExpression(ExpressionConstants.TASK_ASSIGNEE_ID));
            }
        } else if (context instanceof Form) {
            result.add(toExpression(ExpressionConstants.LOGGED_USER_ID));
            if (!(((Form) context).eContainer() instanceof AbstractProcess)) {
                result.add(toExpression(ExpressionConstants.TASK_ASSIGNEE_ID));
            }
        }

        return result;
    }

    private Expression toExpression(ExpressionConstants expressionConstant) {
        final Expression expression = ExpressionFactory.eINSTANCE.createExpression();
        expression.setName(expressionConstant.getEngineConstantName());
        expression.setContent(expressionConstant.getEngineConstantName());
        expression.setReturnType(expressionConstant.getReturnType());
        return expression;
    }
}
