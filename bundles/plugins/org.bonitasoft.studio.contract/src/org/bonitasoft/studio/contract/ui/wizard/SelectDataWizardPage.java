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
package org.bonitasoft.studio.contract.ui.wizard;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newHashSet;
import static org.bonitasoft.studio.common.jface.databinding.UpdateStrategyFactory.neverUpdateValueStrategy;
import static org.bonitasoft.studio.common.jface.databinding.UpdateStrategyFactory.updateValueStrategy;

import java.util.List;

import org.bonitasoft.studio.businessobject.core.repository.BusinessObjectModelRepositoryStore;
import org.bonitasoft.studio.businessobject.ui.BusinessObjectDataStyledLabelProvider;
import org.bonitasoft.studio.common.NamingUtils;
import org.bonitasoft.studio.common.jface.ElementForIdLabelProvider;
import org.bonitasoft.studio.common.widgets.CustomStackLayout;
import org.bonitasoft.studio.contract.i18n.Messages;
import org.bonitasoft.studio.model.process.BusinessObjectData;
import org.bonitasoft.studio.model.process.Contract;
import org.bonitasoft.studio.model.process.ContractInput;
import org.bonitasoft.studio.model.process.Data;
import org.bonitasoft.studio.model.process.Document;
import org.bonitasoft.studio.model.process.ProcessPackage;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.SelectObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.MultiValidator;
import org.eclipse.emf.databinding.EMFObservables;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.IViewerObservableValue;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.google.common.base.Function;

/**
 * @author aurelie
 */
public class SelectDataWizardPage extends WizardPage {

    List<Data> availableBusinessData;
    final WritableValue selectedDataObservable;
    private final BusinessObjectModelRepositoryStore businessObjectStore;
    private Button businessVariableButton;
    private Button documentButton;
    private Composite businessVariableTableViewerComposite;
    private Composite documentTableViewerComposite;
    private final List<Document> availableDocuments;
    private final WritableValue rootNameObservable;
    private final Contract contract;
    private String rootName;
    private SelectObservableValue selectionTypeObservable;

    public SelectDataWizardPage(final Contract contract, final List<Data> availableBusinessData, final List<Document> availableDocuments,
            final WritableValue selectedDataObservable,
            final WritableValue rootNameObservable,
            final BusinessObjectModelRepositoryStore businessObjectStore) {
        super(SelectDataWizardPage.class.getName());
        setTitle(Messages.SelectBusinessDataWizardPageTitle);
        setDescription(Messages.selectBusinessDataWizardPageDescription);
        this.availableBusinessData = availableBusinessData;
        this.availableDocuments = availableDocuments;
        this.selectedDataObservable = selectedDataObservable;
        this.businessObjectStore = businessObjectStore;
        this.rootNameObservable = rootNameObservable;
        this.contract = contract;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl(final Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(GridDataFactory.fillDefaults().create());
        composite.setLayout(GridLayoutFactory.fillDefaults().create());
        final DataBindingContext dbc = new DataBindingContext();
        createRadioButtonComposite(composite);
        final Composite stackedComposite = new Composite(composite, SWT.NONE);
        final CustomStackLayout stackLayout = new CustomStackLayout(stackedComposite);
        stackedComposite.setLayout(stackLayout);
        stackedComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        createbusinessVariableTableViewerComposite(stackedComposite, dbc);
        createDocumentTableViewerCOmposite(stackedComposite, dbc);
        createDocumentNameField(composite, dbc);
        bindRadioButtonsToComposite(dbc);
        WizardPageSupport.create(this, dbc);
        setControl(composite);
    }

    public void createRadioButtonComposite(final Composite parent) {
        final Composite radioButtonComposite = new Composite(parent, SWT.NONE);
        radioButtonComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        radioButtonComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).margins(20, 20).create());
        businessVariableButton = new Button(radioButtonComposite, SWT.RADIO);
        businessVariableButton.setText(Messages.businessVariable);
        documentButton = new Button(radioButtonComposite, SWT.RADIO);
        documentButton.setText(Messages.document);
        selectionTypeObservable = new SelectObservableValue(Boolean.class);
        selectionTypeObservable.addOption(Boolean.TRUE, SWTObservables.observeSelection(businessVariableButton));
        selectionTypeObservable.addOption(Boolean.FALSE, SWTObservables.observeSelection(documentButton));
        selectionTypeObservable.setValue(Boolean.TRUE);
    }

    public void createbusinessVariableTableViewerComposite(final Composite parent, final DataBindingContext dbc) {
        businessVariableTableViewerComposite = new Composite(parent, SWT.NONE);
        businessVariableTableViewerComposite.setLayout(GridLayoutFactory.fillDefaults().create());
        businessVariableTableViewerComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        final TableViewer businessDataTableViewer = new TableViewer(businessVariableTableViewerComposite, SWT.BORDER | SWT.SINGLE | SWT.NO_FOCUS | SWT.H_SCROLL
                | SWT.V_SCROLL);
        businessDataTableViewer.getTable().setLayout(GridLayoutFactory.fillDefaults().create());
        businessDataTableViewer.getTable().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).hint(200, 100).create());
        final ObservableListContentProvider contentProvider = new ObservableListContentProvider();
        businessDataTableViewer.setContentProvider(contentProvider);
        final IObservableSet knownElements = contentProvider.getKnownElements();
        final IObservableMap[] labelMaps = EMFObservables.observeMaps(knownElements, new EStructuralFeature[] { ProcessPackage.Literals.ELEMENT__NAME,
                ProcessPackage.Literals.DATA__MULTIPLE,
                ProcessPackage.Literals.JAVA_OBJECT_DATA__CLASS_NAME });
        businessDataTableViewer.setLabelProvider(new BusinessObjectDataStyledLabelProvider(businessObjectStore, labelMaps));
        businessDataTableViewer.setInput(new WritableList(availableBusinessData, ProcessPackage.Literals.BUSINESS_OBJECT_DATA));
        final IViewerObservableValue observeSingleSelection = ViewersObservables.observeSingleSelection(businessDataTableViewer);
        dbc.bindValue(observeSingleSelection, selectedDataObservable);
        final MultiValidator multiValidator = new BusinessDataSelectedValidator(availableBusinessData, selectedDataObservable, selectionTypeObservable,
                businessObjectStore);
        dbc.addValidationStatusProvider(multiValidator);
    }

    public void createDocumentTableViewerCOmposite(final Composite parent, final DataBindingContext dbc) {
        documentTableViewerComposite = new Composite(parent, SWT.NONE);
        documentTableViewerComposite.setLayout(GridLayoutFactory.fillDefaults().create());
        documentTableViewerComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        final TableViewer documentTableViewer = new TableViewer(documentTableViewerComposite, SWT.BORDER | SWT.SINGLE | SWT.NO_FOCUS | SWT.H_SCROLL
                | SWT.V_SCROLL);
        documentTableViewer.getTable().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).hint(200, 100).create());
        final ObservableListContentProvider contentProvider = new ObservableListContentProvider();
        documentTableViewer.setContentProvider(contentProvider);
        documentTableViewer.setLabelProvider(new ElementForIdLabelProvider());
        documentTableViewer.setInput(new WritableList(availableDocuments, ProcessPackage.Literals.DOCUMENT));
        final IViewerObservableValue observeSingleSelection = ViewersObservables.observeSingleSelection(documentTableViewer);
        dbc.bindValue(observeSingleSelection, selectedDataObservable);
        final MultiValidator multiValidator = new DocumentSelectedValidator(selectedDataObservable, selectionTypeObservable, availableDocuments);
        dbc.addValidationStatusProvider(multiValidator);
    }

    private void bindRadioButtonsToComposite(final DataBindingContext dbc) {
        dbc.bindValue(SWTObservables.observeVisible(businessVariableTableViewerComposite), SWTObservables.observeSelection(businessVariableButton));
        dbc.bindValue(SWTObservables.observeVisible(documentTableViewerComposite), SWTObservables.observeSelection(documentButton));
    }

    public void createDocumentNameField(final Composite parent, final DataBindingContext dbc) {
        final Composite documentInputNameComposite = new Composite(parent, SWT.NONE);
        documentInputNameComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
        documentInputNameComposite.setLayoutData(GridDataFactory.fillDefaults().create());
        final Label documentInputNameLabel = new Label(documentInputNameComposite, SWT.NONE);
        documentInputNameLabel.setLayoutData(GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).create());
        documentInputNameLabel.setText(Messages.rootContractInputName);
        final Text documentInputNameText = new Text(documentInputNameComposite, SWT.BORDER);
        documentInputNameText.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        final IObservableValue prefixObservable = PojoObservables.observeValue(this, "rootName");
        dbc.bindValue(prefixObservable,
                EMFObservables.observeDetailValue(Realm.getDefault(), selectedDataObservable, ProcessPackage.Literals.ELEMENT__NAME),
                neverUpdateValueStrategy().create(), updateValueStrategy().withConverter(documentToRootContractInputName()).create());
        dbc.bindValue(SWTObservables.observeText(documentInputNameText, SWT.Modify),
                prefixObservable);
        dbc.bindValue(rootNameObservable, prefixObservable);
    }

    private IConverter documentToRootContractInputName() {
        return new Converter(String.class, String.class) {

            @Override
            public Object convert(final Object fromObject) {
                final String name = selectedDataObservable.getValue() instanceof Document ? fromObject + "_doc_input" : fromObject + "_input";
                return NamingUtils.generateNewName(newHashSet(transform(contract.getInputs(), toContactInputName())), name, 0);

            }
        };
    }

    private Function<ContractInput, String> toContactInputName() {
        return new Function<ContractInput, String>() {

            @Override
            public String apply(final ContractInput input) {
                return input.getName();
            }
        };
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
     */
    @Override
    public boolean canFlipToNextPage() {
        if (selectedDataObservable.getValue() == null || selectedDataObservable.getValue() instanceof Document) {
            return false;
        }
        return super.canFlipToNextPage();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
     */
    @Override
    public boolean isPageComplete() {
        if (selectedDataObservable.getValue() instanceof BusinessObjectData) {
            return isNoBusinessDataSelected() ? false : super.isPageComplete();
        }
        if (selectedDataObservable.getValue() instanceof Document) {
            return true;
        }
        return super.isPageComplete();
    }

    protected boolean isNoBusinessDataSelected() {
        return availableBusinessData.isEmpty() || selectedDataObservable.getValue() == null;
    }

    /**
     * @return the rootName
     */
    public String getRootName() {
        return rootName;
    }

    /**
     * @param rootName the rootName to set
     */
    public void setRootName(final String rootName) {
        this.rootName = rootName;
    }

}
