/**
 * Copyright (C) 2014 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.studio.exporter.bpmn.transfo.expression;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.find;
import static org.bonitasoft.studio.common.emf.tools.ModelHelper.getAccessibleData;
import static org.bonitasoft.studio.common.emf.tools.ModelHelper.getParentProcess;

import org.bonitasoft.studio.common.ExpressionConstants;
import org.bonitasoft.studio.common.emf.tools.ModelHelper;
import org.bonitasoft.studio.exporter.bpmn.transfo.data.DataScope;
import org.bonitasoft.studio.model.expression.Expression;
import org.bonitasoft.studio.model.process.AbstractProcess;
import org.bonitasoft.studio.model.process.Data;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.omg.spec.bpmn.model.TFormalExpression;
import org.omg.spec.bpmn.model.TItemDefinition;

import com.google.common.base.Predicate;

/**
 * @author Romain Bioteau
 *
 */
public class VariableFormalExpressionTransformer extends FormalExpressionTransformer {

    private static final String DATA_OBJECT_PATTERN = "getDataObject('%s')";
    private static final String ACTIVITY_PROPERTY_PATTERN = "getActivityProperty('%s','%s')";
    private final DataScope dataScope;

    public VariableFormalExpressionTransformer(final DataScope dataScope) {
        this.dataScope = dataScope;
    }

    @Override
    protected TFormalExpression addContent(final Expression bonitaExpression, final TFormalExpression formalExpression) {
        checkNotNull(bonitaExpression);
        checkNotNull(formalExpression);
        checkArgument(ExpressionConstants.VARIABLE_TYPE.equals(bonitaExpression.getType()), "Expression type is invalid. Expected %s but was %s",
                ExpressionConstants.VARIABLE_TYPE, bonitaExpression.getType());
        final EList<EObject> referencedElements = bonitaExpression.getReferencedElements();
        checkArgument(!referencedElements.isEmpty(), "Missing referenced elements for variable expression %s", bonitaExpression.getName());

        final Data bonitaData = (Data) referencedElements.get(0);
        checkNotNull(bonitaData);
        final TItemDefinition bpmnData = dataScope.get(resolveData(bonitaData));
        FeatureMapUtil.addText(formalExpression.getMixed(), createContentFor(bpmnData, bonitaData, bonitaExpression.getContent()));
        return formalExpression;
    }

    private String createContentFor(final TItemDefinition bpmnData, final Data bonitaData, final String expressionContent) {
        if (bonitaData.isTransient()) {
            return createContentForTransientData(bpmnData, bonitaData, expressionContent);
        }
        return createContentForData(bpmnData, bonitaData, expressionContent);
    }

    private String createContentForData(final TItemDefinition bpmnData, final Data bonitaData, final String expressionContent) {
        return String.format(DATA_OBJECT_PATTERN, bpmnData != null ? bpmnData.getId() : expressionContent);
    }

    private String createContentForTransientData(final TItemDefinition bpmnData, final Data bonitaData, final String expressionContent) {
        final AbstractProcess parentProcess = ModelHelper.getParentProcess(bonitaData);
        return String.format(ACTIVITY_PROPERTY_PATTERN, parentProcess.getName(), bpmnData != null ? bpmnData.getId() : expressionContent);
    }

    private static Data resolveData(final Data referencedData) {
        return find(getAccessibleData(getParentProcess(referencedData)), new Predicate<Data>() {

            @Override
            public boolean apply(final Data data) {
                return data.getName().equals(referencedData.getName());
            }
        }, null);
    }

}
