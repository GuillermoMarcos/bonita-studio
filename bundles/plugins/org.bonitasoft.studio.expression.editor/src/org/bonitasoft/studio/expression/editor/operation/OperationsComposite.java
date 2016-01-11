/**
 * Copyright (C) 2012 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
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
package org.bonitasoft.studio.expression.editor.operation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bonitasoft.studio.common.ExpressionConstants;
import org.bonitasoft.studio.expression.core.provider.IExpressionNatureProvider;
import org.bonitasoft.studio.expression.core.scope.ContextFinder;
import org.bonitasoft.studio.expression.core.scope.ModelLocation;
import org.bonitasoft.studio.expression.core.scope.ModelLocationFactory;
import org.bonitasoft.studio.expression.editor.i18n.Messages;
import org.bonitasoft.studio.expression.editor.provider.IExpressionValidator;
import org.bonitasoft.studio.model.expression.ExpressionFactory;
import org.bonitasoft.studio.model.expression.Operation;
import org.bonitasoft.studio.model.expression.Operator;
import org.bonitasoft.studio.model.form.Form;
import org.bonitasoft.studio.model.form.FormPackage;
import org.bonitasoft.studio.model.form.SubmitFormButton;
import org.bonitasoft.studio.model.process.AbstractCatchMessageEvent;
import org.bonitasoft.studio.model.process.Connector;
import org.bonitasoft.studio.model.process.OperationContainer;
import org.bonitasoft.studio.model.process.ProcessPackage;
import org.bonitasoft.studio.model.process.ReceiveTask;
import org.bonitasoft.studio.pics.Pics;
import org.eclipse.core.databinding.Binding;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

/**
 * @author Aurelien Pupier
 * @author Aurelie Zara
 * @author Romain Bioteau
 */
public abstract class OperationsComposite extends Composite {

    public static final String SWTBOT_ID_REMOVE_LINE = "actionLinesCompositeRemoveButton";
    protected List<OperationViewer> operationViewers = new ArrayList<OperationViewer>();
    protected ArrayList<Button> removes = new ArrayList<Button>();
    protected TabbedPropertySheetPage tabbedPropertySheetPage;
    protected EMFDataBindingContext dbc;
    protected ArrayList<List<Binding>> bindings = new ArrayList<List<Binding>>();
    private TabbedPropertySheetWidgetFactory widgetFactory;
    protected final Composite parent;
    private final ViewerFilter storageExpressionFilter;
    private final ViewerFilter actionExpressionFilter;
    private IExpressionNatureProvider storageExpressionNatureProvider;
    private IExpressionNatureProvider actionExpressionNatureProvider;
    private final List<IExpressionValidator> validators = new ArrayList<IExpressionValidator>();
    private final Composite operationComposite;
    private ModelLocation location;
    private EStructuralFeature operationContainerFeature;

    public OperationsComposite(final TabbedPropertySheetPage tabbedPropertySheetPage,
            final Composite mainComposite, final ViewerFilter actionExpressionFilter,
            final ViewerFilter storageExpressionFilter) {
        super(mainComposite, SWT.NONE);
        parent = mainComposite;
        setLayout(GridLayoutFactory.fillDefaults().numColumns(1).margins(0, 0).extendedMargins(0, 20, 0, 0).create());
        operationComposite = new Composite(this, SWT.NONE);
        operationComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(1).margins(0, 0).create());
        operationComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        final Composite buttonComposite = new Composite(this, SWT.NONE);
        buttonComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(1).create());
        buttonComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        if (tabbedPropertySheetPage != null) {
            widgetFactory = tabbedPropertySheetPage.getWidgetFactory();
            if (widgetFactory != null) {
                widgetFactory.adapt(this);
                widgetFactory.adapt(operationComposite);
                widgetFactory.adapt(buttonComposite);
            }
            this.tabbedPropertySheetPage = tabbedPropertySheetPage;
        }
        this.actionExpressionFilter = actionExpressionFilter;
        this.storageExpressionFilter = storageExpressionFilter;

        createAddButton(buttonComposite);
    }

    private void createAddButton(final Composite mainComposite) {
        Button addButton = null;
        if (widgetFactory != null) {
            addButton = widgetFactory.createButton(mainComposite, "", SWT.PUSH);
        } else {
            addButton = new Button(mainComposite, SWT.PUSH);
        }
        addButton.setText(Messages.addAction);
        addButton.setLayoutData(GridDataFactory.fillDefaults()
                .align(SWT.BEGINNING, SWT.TOP).hint(85, SWT.DEFAULT).create());
        addButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                final Operation action = createOperation();
                addLineUI(action);
                refresh();
            }

            private Operation createOperation() {
                final Operation action = ExpressionFactory.eINSTANCE.createOperation();
                final Operator operator = ExpressionFactory.eINSTANCE.createOperator();
                operator.setType(ExpressionConstants.ASSIGNMENT_OPERATOR);
                action.setOperator(operator);
                final EditingDomain domain = getEditingDomain();
                if (domain != null) {
                    domain.getCommandStack().execute(
                            AddCommand.create(domain, getEObject(),
                                    getActionTargetFeature(), action));
                } else {
                    getActions().add(action);
                }
                return action;
            }
        });
    }

    protected EditingDomain getEditingDomain() {
        return TransactionUtil.getEditingDomain(getEObject());
    }

    private EObject getEObject() {
        return location.getModelElement();
    }

    public int getNbLines() {
        return operationViewers.size();
    }

    public void removeLinesUI() {
        if (!operationViewers.isEmpty()) {
            for (int i = operationViewers.size() - 1; i >= 0; i--) {
                removeLineUI(i);
            }
            refresh();
        }
    }

    private void removeLine(final int i) {
        final int lineNumber = removeLineUI(i);
        final EditingDomain domain = getEditingDomain();
        if (domain != null) {
            getEditingDomain().getCommandStack().execute(
                    RemoveCommand.create(getEditingDomain(), getEObject(),
                            getActionTargetFeature(),
                            getActions().get(lineNumber)));
        } else {
            getActions().remove(i);
        }
        refresh();
    }

    /**
     * @param i
     * @return
     */
    protected int removeLineUI(final int i) {
        final int lineNumber = i;
        removes.get(lineNumber).dispose();
        removes.remove(lineNumber);
        final OperationViewer operationViewer = operationViewers.get(lineNumber);
        operationViewer.dispose();
        operationViewers.remove(lineNumber);
        return lineNumber;
    }

    public void addLineUI(final Operation action) {
        final OperationViewer opViewer = createOperationViewer(action);
        operationViewers.add(opViewer);
        removes.add(createRemoveButton(opViewer));
    }

    protected OperationViewer createOperationViewer(final Operation action) {
        final OperationViewer viewer = new OperationViewer(operationComposite, widgetFactory, getEditingDomain(), actionExpressionFilter,
                storageExpressionFilter);
        viewer.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        if (dbc != null) {
            viewer.setDatabindingContext(dbc);
        }
        if (storageExpressionNatureProvider != null) {
            viewer.setStorageExpressionNatureProvider(storageExpressionNatureProvider);
        }
        if (actionExpressionNatureProvider != null) {
            viewer.setActionExpressionNatureProvider(actionExpressionNatureProvider);
        }
        for (final IExpressionValidator validator : validators) {
            viewer.addActionExpressionValidator(validator);
        }
        viewer.setInput(new ModelLocationFactory().newLocation(location, action));
        return viewer;
    }

    public void addActionExpressionValidator(final IExpressionValidator validator) {
        validators.add(validator);
    }

    protected Button createRemoveButton(final OperationViewer opViewer) {
        final GridLayout gridLayout = (GridLayout) opViewer.getLayout();
        gridLayout.numColumns++;
        final Button remove = new Button(opViewer, SWT.FLAT);
        if (widgetFactory != null) {
            widgetFactory.adapt(remove, false, false);
        }
        remove.setData("org.eclipse.swtbot.widget.key", SWTBOT_ID_REMOVE_LINE);
        remove.setLayoutData(GridDataFactory.swtDefaults().create());
        remove.setImage(Pics.getImage("delete.png"));
        remove.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                removeLine(removes.indexOf(e.getSource()));
            }
        });
        opViewer.layout(true, true);
        return remove;
    }

    protected String getExpressionTooltip() {
        return Messages.shortExpr_tooltip;
    }

    /**
     * add lines from the form
     */
    public void fillTable() {
        for (final Operation action : getActions()) {
            addLineUI(action);
        }
    }

    /*
     * (non-Javadoc)
     * @seeorg.eclipse.gmf.runtime.diagram.ui.properties.sections.
     * AbstractModelerPropertySection#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
        for (final OperationViewer text : operationViewers) {
            if (text != null) {
                text.dispose();
            }
        }
        for (final Button toDispose : removes) {
            if (toDispose != null && !toDispose.isDisposed()) {
                toDispose.dispose();
            }
        }
    }

    public void setDatabindingContext(final EMFDataBindingContext emfDataBindingContext) {
        dbc = emfDataBindingContext;
    }

    public abstract void refresh();

    protected EStructuralFeature getActionTargetFeature() {
        if (operationContainerFeature != null) {
            return operationContainerFeature;
        }
        final ContextFinder contextFinder = new ContextFinder(location);
        if (contextFinder.find(Form.class) != null) {
            return FormPackage.Literals.FORM__ACTIONS;
        } else if (contextFinder.find(SubmitFormButton.class) != null) {
            return FormPackage.Literals.SUBMIT_FORM_BUTTON__ACTIONS;
        } else if (contextFinder.find(ReceiveTask.class) != null) {
            return ProcessPackage.Literals.OPERATION_CONTAINER__OPERATIONS;
        } else if (contextFinder.find(AbstractCatchMessageEvent.class) != null) {
            return ProcessPackage.Literals.ABSTRACT_CATCH_MESSAGE_EVENT__MESSAGE_CONTENT;
        } else if (contextFinder.find(Connector.class) != null) {
            return ProcessPackage.Literals.CONNECTOR__OUTPUTS;
        } else if (contextFinder.find(OperationContainer.class) != null) {
            return ProcessPackage.Literals.OPERATION_CONTAINER__OPERATIONS;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private List<Operation> getActions() {
        final ContextFinder contextFinder = new ContextFinder(location);
        final EStructuralFeature feature = getActionTargetFeature();
        if (contextFinder.find(Form.class) != null) {
            return (List<Operation>) contextFinder.find(Form.class).eGet(FormPackage.Literals.FORM__ACTIONS);
        } else if (contextFinder.find(SubmitFormButton.class) != null) {
            return (List<Operation>) contextFinder.find(SubmitFormButton.class).eGet(FormPackage.Literals.SUBMIT_FORM_BUTTON__ACTIONS);
        } else if (contextFinder.find(AbstractCatchMessageEvent.class) != null) {
            return (List<Operation>) contextFinder.find(AbstractCatchMessageEvent.class)
                    .eGet(ProcessPackage.Literals.ABSTRACT_CATCH_MESSAGE_EVENT__MESSAGE_CONTENT);
        } else if (contextFinder.find(Connector.class) != null) {
            return (List<Operation>) contextFinder.find(Connector.class).eGet(ProcessPackage.Literals.CONNECTOR__OUTPUTS);
        } else if (contextFinder.find(OperationContainer.class) != null) {
            return (List<Operation>) contextFinder.find(OperationContainer.class).eGet(ProcessPackage.Literals.OPERATION_CONTAINER__OPERATIONS);
        }
        return Collections.emptyList();
    }


    /**
     * Set a specify IExpressionNatureProvider for the left operand
     *
     * @param dataFeature
     */
    public void setStorageExpressionNatureContentProvider(
            final IExpressionNatureProvider provider) {
        storageExpressionNatureProvider = provider;
    }

    public void setActionExpressionNatureContentProvider(
            final IExpressionNatureProvider provider) {
        actionExpressionNatureProvider = provider;
    }

    public void refreshViewers() {
        for (final OperationViewer v : operationViewers) {
            v.refresh();
        }
    }

    public List<OperationViewer> getOperationViewers() {
        return operationViewers;
    }

    public void setInput(ModelLocation location) {
        this.location = location;
    }

    public void setOperationContainmentFeature(EStructuralFeature operationContainerFeature) {
        this.operationContainerFeature = operationContainerFeature;
    }

}
