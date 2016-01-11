/**
 * Copyright (C) 2015 Bonitasoft S.A.
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
package org.bonitasoft.studio.designer.core.expression;

import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;

import org.bonitasoft.studio.common.log.BonitaStudioLog;
import org.bonitasoft.studio.common.repository.RepositoryAccessor;
import org.bonitasoft.studio.designer.core.FormScope;
import org.bonitasoft.studio.designer.core.PageDesignerURLFactory;
import org.bonitasoft.studio.designer.core.operation.CreateFormFromContractOperation;
import org.bonitasoft.studio.designer.core.repository.WebPageRepositoryStore;
import org.bonitasoft.studio.expression.core.scope.ContextFinder;
import org.bonitasoft.studio.expression.core.scope.ModelLocation;
import org.bonitasoft.studio.expression.editor.provider.IProposalAdapter;
import org.bonitasoft.studio.model.process.Contract;
import org.bonitasoft.studio.model.process.ContractContainer;
import org.bonitasoft.studio.model.process.PageFlow;
import org.bonitasoft.studio.model.process.ProcessPackage;
import org.bonitasoft.studio.model.process.Task;
import org.bonitasoft.studio.preferences.BonitaPreferenceConstants;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Romain Bioteau
 */
@Creatable
public class CreateNewFormProposalListener extends IProposalAdapter implements BonitaPreferenceConstants {

    private final IProgressService progressService;

    private final PageDesignerURLFactory pageDesignerURLFactory;

    protected final RepositoryAccessor repositoryAccessor;

    @Inject
    public CreateNewFormProposalListener(final PageDesignerURLFactory pageDesignerURLFactory, final IProgressService progressService,
            final RepositoryAccessor repositoryAccessor) {
        this.progressService = progressService;
        this.pageDesignerURLFactory = pageDesignerURLFactory;
        this.repositoryAccessor = repositoryAccessor;
    }

    /*
     * (non-Javadoc)
     * @see org.bonitasoft.studio.expression.editor.provider.IProposalListener#handleEvent(org.eclipse.emf.ecore.EObject, java.lang.String)
     */
    @Override
    public String handleEvent(final ModelLocation location, final String fixedReturnType) {
        final PageFlow pageFlow = new ContextFinder(location).find(PageFlow.class);
        checkState(pageFlow != null);
        final CreateFormFromContractOperation operation = doCreateFormOperation(pageDesignerURLFactory, "newForm", contractFor(location),
                formScopeFor(location));

        try {
            progressService.busyCursorWhile(operation);
        } catch (InvocationTargetException | InterruptedException e) {
            BonitaStudioLog.error(e);
        }

        final String newPageId = operation.getNewPageId();
        repositoryAccessor.getRepositoryStore(WebPageRepositoryStore.class).getChild(newPageId).open();
        return newPageId;
    }

    private FormScope formScopeFor(final ModelLocation location) {
        final ContextFinder contextFinder = new ContextFinder(location);
        return contextFinder.find(ProcessPackage.Literals.RECAP_FLOW__OVERVIEW_FORM_MAPPING) != null ? FormScope.OVERVIEW
                : contextFinder.find(Task.class) != null ? FormScope.TASK : FormScope.PROCESS;
    }

    private Contract contractFor(final ModelLocation location) {
        EObject contractContainer = new ContextFinder(location).find(ContractContainer.class);
        while (contractContainer != null && !(contractContainer instanceof ContractContainer)) {
            contractContainer = contractContainer.eContainer();
        }
        if (contractContainer instanceof ContractContainer) {
            return ((ContractContainer) contractContainer).getContract();
        }
        throw new IllegalStateException("No contract found for location " + location);
    }

    protected CreateFormFromContractOperation doCreateFormOperation(final PageDesignerURLFactory pageDesignerURLBuilder, final String formName,
            final Contract contract, final FormScope formScope) {
        return new CreateFormFromContractOperation(pageDesignerURLBuilder, formName, contract, formScope);
    }

}
