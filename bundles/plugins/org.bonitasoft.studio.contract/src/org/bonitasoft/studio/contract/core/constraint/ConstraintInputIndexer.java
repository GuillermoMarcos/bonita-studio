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
package org.bonitasoft.studio.contract.core.constraint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bonitasoft.studio.model.process.ContractInput;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.eclipse.codeassist.requestor.CompletionNodeFinder;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistContext;
import org.codehaus.jdt.groovy.model.GroovyCompilationUnit;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;


/**
 * @author Romain Bioteau
 *
 */
public class ConstraintInputIndexer extends Job {

    public static final Object FAMILY = "ConstraintInputIndexerFamily";
    private String expression;
    private List<ContractInput> inputs = new ArrayList<ContractInput>();
    private final GroovyCompilationUnit groovyCompilationUnit;
    private final Set<String> referencedInputs = new HashSet<String>();

    public ConstraintInputIndexer(final List<ContractInput> availableInputs, final GroovyCompilationUnit groovyCompilationUnit) {
        super("Constraint inputs indexer");
        setPriority(Job.BUILD);
        setSystem(true);
        setUser(false);
        inputs = availableInputs;
        this.groovyCompilationUnit = groovyCompilationUnit;
    }

    @Override
    public boolean belongsTo(final Object family) {
        if (FAMILY.equals(family)) {
            return true;
        }
        return super.belongsTo(family);
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IStatus run(final IProgressMonitor monitor) {
        monitor.beginTask("Computing referenced inputs...", IProgressMonitor.UNKNOWN);
        referencedInputs.clear();
        final CompletionNodeFinder finder = new CompletionNodeFinder(0, 0, 0, "", "");
        final ContentAssistContext assistContext = finder.findContentAssistContext(groovyCompilationUnit);

        org.codehaus.groovy.ast.ASTNode astNode = null;
        if (assistContext != null) {
            astNode = assistContext.containingCodeBlock;
        }
        if (astNode instanceof BlockStatement) {
            final BlockStatement blockStatement = (BlockStatement) astNode;
            addRefrencedInputs(blockStatement);
        }
        return Status.OK_STATUS;
    }

    protected void addRefrencedInputs(final BlockStatement blockStatement) {
        final Iterator<Variable> referencedClassVariablesIterator = blockStatement.getVariableScope().getReferencedClassVariablesIterator();
        while (referencedClassVariablesIterator.hasNext()) {
            final Variable variable = referencedClassVariablesIterator.next();
            for (final ContractInput in : inputs) {
                if (in.getName().equals(variable.getName())) {
                    referencedInputs.add(variable.getName());
                }
            }
        }
    }

    public Set<String> getReferencedInputs() {
        return referencedInputs;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(final String expression) {
        this.expression = expression;
    }

}
