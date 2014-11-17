/**
 * Copyright (C) 2014 Bonitasoft S.A.
 * Bonitasoft, 32 rue Gustave Eiffel - 38000 Grenoble
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
package org.bonitasoft.studio.exporter.bpmn;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bonitasoft.engine.bpm.bar.BarResource;
import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder;
import org.bonitasoft.studio.common.emf.tools.ModelHelper;
import org.bonitasoft.studio.common.extension.BarResourcesProviderUtil;
import org.bonitasoft.studio.common.log.BonitaStudioLog;
import org.bonitasoft.studio.exporter.bpmn.transfo.BonitaToBPMN;
import org.bonitasoft.studio.exporter.extension.BonitaModelExporterImpl;
import org.bonitasoft.studio.model.process.AbstractProcess;
import org.bonitasoft.studio.model.process.diagram.edit.parts.MainProcessEditPart;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.diagram.ui.OffscreenEditPartFactory;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.emf.core.GMFEditingDomainFactory;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.swt.widgets.Shell;

public class AddBpmnBarResourceRunnable implements RunnableWithResult<List<BarResource>> {

    private final BusinessArchiveBuilder builder;
    private final AbstractProcess process;
    final List<BarResource> res = new ArrayList<BarResource>();

    public AddBpmnBarResourceRunnable(final BusinessArchiveBuilder builder, final AbstractProcess process) {
        this.builder = builder;
        this.process = process;
    }

    @Override
    public void run() {
        final Diagram diagramFor = ModelHelper.getDiagramFor(ModelHelper.getMainProcess(process), null);
        if (diagramFor != null) {
            final Resource eResource = diagramFor.eResource();
            if (eResource != null) {
                final MainProcessEditPart mped = createMainEditPart(diagramFor, eResource);
                createBPMNFileAndAddContents(mped);
                for (final BarResource barResource : res) {
                    builder.addExternalResource(barResource);
                }
            }
        }
    }

    protected void createBPMNFileAndAddContents(final MainProcessEditPart mped) {
        File destFile = null;
        try {
            destFile = File.createTempFile(process.getName() + "-" + process.getVersion(), ".bpmn");
            new BonitaToBPMN().transform(new BonitaModelExporterImpl(mped), destFile, new NullProgressMonitor());
            BarResourcesProviderUtil.addFileContents(res, destFile, "process.bpmn");
        } catch (final IOException e) {
            BonitaStudioLog.error(e);
        } finally {
            if (destFile != null) {
                destFile.delete();
            }
        }
    }

    protected MainProcessEditPart createMainEditPart(Diagram diagramFor, final Resource eResourceuseless) {
        final ResourceSet rSet = new ResourceSetImpl();

        final TransactionalEditingDomain editingDomain = GMFEditingDomainFactory.getInstance().createEditingDomain(rSet);
        final Resource resource = rSet.createResource(diagramFor.eResource().getURI());
        try {
            resource.load(rSet.getLoadOptions());
        } catch (final IOException e1) {
            BonitaStudioLog.error(e1);
        }
        diagramFor = (Diagram) resource.getEObject(diagramFor.eResource().getURIFragment(diagramFor));

        DiagramEditPart dep;
        final Shell shell = new Shell();
        try {
            dep = OffscreenEditPartFactory.getInstance().createDiagramEditPart(diagramFor, shell);
        } catch (final Exception ex) {
            dep = OffscreenEditPartFactory.getInstance().createDiagramEditPart(diagramFor, shell);
        } finally {
            if (editingDomain != null) {
                editingDomain.dispose();
            }
            if (shell != null) {
                shell.dispose();
            }
        }

        return (MainProcessEditPart) dep;
    }

    @Override
    public List<BarResource> getResult() {
        return res;
    }

    @Override
    public void setStatus(final IStatus status) {
    }

    @Override
    public IStatus getStatus() {
        return Status.OK_STATUS;
    }
}