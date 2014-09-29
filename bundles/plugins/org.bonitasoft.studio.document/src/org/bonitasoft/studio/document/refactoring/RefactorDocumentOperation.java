/**
 * Copyright (C) 2014 BonitaSoft S.A.
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
package org.bonitasoft.studio.document.refactoring;

import java.util.List;

import org.bonitasoft.studio.common.ExpressionConstants;
import org.bonitasoft.studio.common.emf.tools.ExpressionHelper;
import org.bonitasoft.studio.common.emf.tools.ModelHelper;
import org.bonitasoft.studio.model.expression.Expression;
import org.bonitasoft.studio.model.expression.ExpressionPackage;
import org.bonitasoft.studio.model.process.Document;
import org.bonitasoft.studio.model.process.Pool;
import org.bonitasoft.studio.model.process.ProcessPackage;
import org.bonitasoft.studio.refactoring.core.AbstractRefactorOperation;
import org.bonitasoft.studio.refactoring.core.AbstractScriptExpressionRefactoringAction;
import org.bonitasoft.studio.refactoring.core.RefactoringOperationType;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.DeleteCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;

public class RefactorDocumentOperation extends AbstractRefactorOperation<Document, Document, DocumentRefactorPair> {

    public RefactorDocumentOperation(final RefactoringOperationType operationType) {
        super(operationType);
    }

    @Override
    protected void doExecute(final IProgressMonitor monitor) {
        final CompoundCommand deleteCommands = new CompoundCommand("Compound commands containing all delete operations to do at last step");
        for (final DocumentRefactorPair pairToRefactor : pairsToRefactor) {
            if (pairToRefactor.getNewValue() != null) {
                updateDocumentInDocumentExpressions(compoundCommand, pairToRefactor);
                final Pool container = getContainer(pairToRefactor.getOldValue());
                final List<Document> documents = container.getDocuments();
                final int index = documents.indexOf(pairToRefactor.getOldValue());
                compoundCommand.append(RemoveCommand.create(domain, container, ProcessPackage.Literals.POOL__DOCUMENTS, pairToRefactor.getOldValue()));
                compoundCommand.append(AddCommand.create(domain, container, ProcessPackage.Literals.POOL__DOCUMENTS, pairToRefactor.getNewValue(), index));
            } else {
                removeAllDocumentReferences(compoundCommand, pairToRefactor);
                deleteCommands.append(DeleteCommand.create(domain, pairToRefactor.getOldValue()));
            }
        }
        compoundCommand.appendIfCanExecute(deleteCommands);
    }

    protected void updateDocumentInDocumentExpressions(final CompoundCommand cc, final DocumentRefactorPair pairToRefactor) {
        final Document newValue = pairToRefactor.getNewValue();
        if (newValue != null) {
            final List<Expression> expressions = ModelHelper.getAllItemsOfType(getContainer(pairToRefactor.getOldValue()),
                    ExpressionPackage.Literals.EXPRESSION);
            for (final Expression exp : expressions) {
                if (isMatchingDocumentExpression(pairToRefactor, exp)) {
                    // update name and content
                    cc.append(SetCommand.create(domain, exp, ExpressionPackage.Literals.EXPRESSION__NAME, newValue.getName()));
                    cc.append(SetCommand.create(domain, exp, ExpressionPackage.Literals.EXPRESSION__CONTENT, newValue.getName()));
                    for (final EObject dependency : exp.getReferencedElements()) {
                        if (dependency instanceof Document) {
                            if (((Document) dependency).getName().equals(pairToRefactor.getOldValueName())) {
                                cc.append(RemoveCommand.create(domain, exp, ExpressionPackage.Literals.EXPRESSION__REFERENCED_ELEMENTS, dependency));
                                cc.append(AddCommand.create(domain, exp, ExpressionPackage.Literals.EXPRESSION__REFERENCED_ELEMENTS,
                                        ExpressionHelper.createDependencyFromEObject(pairToRefactor.getNewValue())));
                            }
                        }
                    }
                }
            }
        } else {
            removeAllDocumentReferences(compoundCommand, pairToRefactor);
        }
    }

    private boolean isMatchingDocumentExpression(final DocumentRefactorPair pairToRefactor, final Expression exp) {
        return (ExpressionConstants.DOCUMENT_TYPE.equals(exp.getType())
                || ExpressionConstants.CONSTANT_TYPE.equals(exp.getType())
                || ExpressionConstants.DOCUMENT_REF_TYPE.equals(exp.getType()))
                && exp.getName() != null
                && exp.getName()
                .equals(
                        pairToRefactor
                        .getOldValue()
                        .getName());
    }

    private void removeAllDocumentReferences(final CompoundCommand cc, final DocumentRefactorPair pairToRefactor) {
        final List<Expression> expressions = retrieveExpressionsInTheContainer(pairToRefactor);
        for (final Expression exp : expressions) {
            if (isMatchingDocumentExpression(pairToRefactor, exp)) {
                // update name and content
                cc.append(SetCommand.create(domain, exp, ExpressionPackage.Literals.EXPRESSION__NAME, ""));
                cc.append(SetCommand.create(domain, exp, ExpressionPackage.Literals.EXPRESSION__CONTENT, ""));
                // update return type
                cc.append(SetCommand.create(domain, exp, ExpressionPackage.Literals.EXPRESSION__RETURN_TYPE, String.class.getName()));
                cc.append(SetCommand.create(domain, exp, ExpressionPackage.Literals.EXPRESSION__TYPE, ExpressionConstants.CONSTANT_TYPE));
                // update referenced document
                cc.append(RemoveCommand.create(domain, exp, ExpressionPackage.Literals.EXPRESSION__REFERENCED_ELEMENTS, exp.getReferencedElements()));
            }
        }
    }

    private List<Expression> retrieveExpressionsInTheContainer(final DocumentRefactorPair pairToRefactor) {
        final List<Expression> expressions = ModelHelper.getAllItemsOfType(getContainer(pairToRefactor.getOldValue()), ExpressionPackage.Literals.EXPRESSION);
        return expressions;
    }

    @Override
    protected AbstractScriptExpressionRefactoringAction<DocumentRefactorPair> getScriptExpressionRefactoringAction(
            final List<DocumentRefactorPair> pairsToRefactor,
            final List<Expression> scriptExpressions,
            final List<Expression> refactoredScriptExpression,
            final CompoundCommand compoundCommand,
            final EditingDomain domain,
            final RefactoringOperationType operationType) {
        return new DocumentScriptExpressionRefactoringAction(pairsToRefactor, scriptExpressions, refactoredScriptExpression, compoundCommand, domain,
                operationType);
    }

    @Override
    protected DocumentRefactorPair createRefactorPair(final Document newItem, final Document oldItem) {
        return new DocumentRefactorPair(newItem, oldItem);
    }

    @Override
    protected Pool getContainer(final Document oldValue) {
        return (Pool) ModelHelper.getParentProcess(oldValue);
    }

}
