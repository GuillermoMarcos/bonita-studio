/**
 * Copyright (C) 2012 BonitaSoft S.A.
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
package org.bonitasoft.studio.simulation.expression;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bonitasoft.studio.common.ExpressionConstants;
import org.bonitasoft.studio.common.jface.TableColumnSorter;
import org.bonitasoft.studio.expression.core.scope.ExpressionScope;
import org.bonitasoft.studio.expression.editor.provider.SelectionAwareExpressionEditor;
import org.bonitasoft.studio.model.expression.Expression;
import org.bonitasoft.studio.model.expression.ExpressionPackage;
import org.bonitasoft.studio.model.simulation.SimulationData;
import org.bonitasoft.studio.simulation.SimulationDataUtil;
import org.bonitasoft.studio.simulation.i18n.Messages;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.EMFObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

/**
 * @author Romain Bioteau
 * 
 */
public class SimulationDataExpressionEditor extends SelectionAwareExpressionEditor {

    private TableViewer viewer;

    private GridLayout gridLayout;

    private Expression editorInputExpression;

    private Composite mainComposite;

    private Text typeText;

    @Override
    public Control createExpressionEditor(Composite parent, EMFDataBindingContext ctx) {
        mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        gridLayout = new GridLayout(1, false);
        mainComposite.setLayout(gridLayout);

        viewer = new TableViewer(mainComposite, SWT.FULL_SELECTION | SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);

        final TableLayout layout = new TableLayout();
        layout.addColumnData(new ColumnWeightData(100, false));
        viewer.getTable().setLayout(layout);
        viewer.getTable().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

        final TableViewerColumn columnViewer = new TableViewerColumn(viewer, SWT.NONE);
        final TableColumn column = columnViewer.getColumn();
        column.setText(Messages.name);

        final TableColumnSorter sorter = new TableColumnSorter(viewer);
        sorter.setColumn(column);

        viewer.getTable().setHeaderVisible(true);
        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setLabelProvider(new LabelProvider() {

            @Override
            public String getText(Object element) {
                return ((SimulationData) element).getName();
            }
        });

        viewer.addPostSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (!event.getSelection().isEmpty()) {
                    SimulationDataExpressionEditor.this.fireSelectionChanged();
                }
            }
        });

        createReturnTypeComposite(parent);

        return mainComposite;
    }

    protected void createReturnTypeComposite(Composite parent) {
        final Composite typeComposite = new Composite(parent, SWT.NONE);
        typeComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        final GridLayout gl = new GridLayout(2, false);
        gl.marginWidth = 0;
        gl.marginHeight = 0;
        typeComposite.setLayout(gl);

        final Label typeLabel = new Label(typeComposite, SWT.NONE);
        typeLabel.setText(Messages.returnType);
        typeLabel.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).create());

        typeText = new Text(typeComposite, SWT.BORDER | SWT.READ_ONLY);
        typeText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).create());

    }

    protected void handleSpecificDatatypeEdition(SimulationData data) {
        if (gridLayout.numColumns > 1) {
            mainComposite.getChildren()[1].dispose();
            gridLayout.numColumns--;
            viewer.getTable().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
            mainComposite.layout();
        }
    }

    @Override
    public void bindExpression(EMFDataBindingContext dataBindingContext, Expression inputExpression, ExpressionScope scope) {
        editorInputExpression = inputExpression;
        final Set<SimulationData> input = new HashSet<SimulationData>();
        for (final Expression e : scope.getExpressionsWithType(ExpressionConstants.SIMULATION_VARIABLE_TYPE)) {
            if (inputExpression.isReturnTypeFixed()) {
                if (e.getReturnType().equals(inputExpression.getReturnType())) {
                    input.add((SimulationData) e.getReferencedElements().get(0));
                }
            } else {
                input.add((SimulationData) e.getReferencedElements().get(0));
            }
        }
        viewer.setInput(input);

        final IObservableValue contentObservable = EMFObservables.observeValue(inputExpression, ExpressionPackage.Literals.EXPRESSION__CONTENT);
        final IObservableValue nameObservable = EMFObservables.observeValue(inputExpression, ExpressionPackage.Literals.EXPRESSION__NAME);
        final IObservableValue returnTypeObservable = EMFObservables.observeValue(inputExpression, ExpressionPackage.Literals.EXPRESSION__RETURN_TYPE);
        final IObservableValue referenceObservable = EMFObservables.observeValue(inputExpression, ExpressionPackage.Literals.EXPRESSION__REFERENCED_ELEMENTS);

        final UpdateValueStrategy selectionToName = new UpdateValueStrategy();
        final IConverter nameConverter = new Converter(SimulationData.class, String.class) {

            @Override
            public Object convert(Object data) {
                return ((SimulationData) data).getName();
            }

        };
        selectionToName.setConverter(nameConverter);

        final UpdateValueStrategy selectionToContent = new UpdateValueStrategy();
        final IConverter contentConverter = new Converter(SimulationData.class, String.class) {

            @Override
            public Object convert(Object data) {
                return ((SimulationData) data).getName();
            }

        };
        selectionToContent.setConverter(contentConverter);

        final UpdateValueStrategy selectionToReturnType = new UpdateValueStrategy();
        final IConverter returnTypeConverter = new Converter(SimulationData.class, String.class) {

            @Override
            public Object convert(Object data) {
                return SimulationDataUtil.getTechnicalTypeFor((SimulationData) data);
            }

        };
        selectionToReturnType.setConverter(returnTypeConverter);

        final UpdateValueStrategy selectionToReferencedData = new UpdateValueStrategy();
        final IConverter referenceConverter = new Converter(SimulationData.class, List.class) {

            @Override
            public Object convert(Object data) {
                if (data != null) {
                    return Collections.singletonList(data);
                } else {
                    return Collections.emptyList();
                }
            }

        };
        selectionToReferencedData.setConverter(referenceConverter);

        final UpdateValueStrategy referencedDataToSelection = new UpdateValueStrategy();
        final IConverter referencetoDataConverter = new Converter(List.class, SimulationData.class) {

            @Override
            public Object convert(Object dataList) {
                final List<SimulationData> list = (List<SimulationData>) dataList;
                if (list.isEmpty()) {
                    return null;
                } else {
                    return list.get(0);
                }
            }

        };
        referencedDataToSelection.setConverter(referencetoDataConverter);

        dataBindingContext.bindValue(ViewersObservables.observeSingleSelection(viewer), nameObservable, selectionToName, new UpdateValueStrategy(
                UpdateValueStrategy.POLICY_NEVER));
        dataBindingContext.bindValue(ViewersObservables.observeSingleSelection(viewer), contentObservable, selectionToContent, new UpdateValueStrategy(
                UpdateValueStrategy.POLICY_NEVER));
        dataBindingContext.bindValue(ViewersObservables.observeSingleSelection(viewer), returnTypeObservable, selectionToReturnType, new UpdateValueStrategy(
                UpdateValueStrategy.POLICY_NEVER));
        dataBindingContext.bindValue(ViewersObservables.observeSingleSelection(viewer), referenceObservable, selectionToReferencedData,
                referencedDataToSelection);
        dataBindingContext.bindValue(SWTObservables.observeText(typeText, SWT.Modify), returnTypeObservable);
    }

    @Override
    public boolean canFinish() {
        return !viewer.getSelection().isEmpty();
    }

    @Override
    public void okPressed() {
        if (!editorInputExpression.getContent().equals(editorInputExpression.getName())) {
            editorInputExpression.setName(editorInputExpression.getContent());
        }
    }

    @Override
    public Control getTextControl() {
        return null;
    }

}
