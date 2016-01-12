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
package org.bonitasoft.studio.expression.core.scope;

import static com.google.common.base.Preconditions.checkArgument;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

public class ModelLocationFactory {

    public ModelLocation newLocation(EObject modelElement) {
        checkArgument(modelElement != null);
        final ModelLocation result = new ModelLocation(modelElement, modelElement.eContainingFeature());
        ModelLocation current = result;
        while (current.getModelElement().eContainer() != null) {
            final EObject eContainer = current.getModelElement().eContainer();
            final ModelLocation parent = new ModelLocation(eContainer, eContainer.eContainingFeature());
            current.setParent(parent);
            current = parent;
        }
        return result;
    }

    public ModelLocation newLocation(EObject container, EObject modelElement) {
        final ModelLocation result = new ModelLocation(modelElement, modelElement.eContainingFeature());
        ModelLocation current = result;
        EObject eContainer = modelElement.eContainer();
        while (eContainer != null && !EcoreUtil.equals(container, eContainer)) {
            current.setParent(new ModelLocation(eContainer, eContainer.eContainingFeature()));
            eContainer = eContainer.eContainer();
            current = current.getParent();
        }
        current.setParent(new ModelLocation(container, container.eContainingFeature()));
        current = current.getParent();
        while (current.getModelElement().eContainer() != null) {
            eContainer = current.getModelElement().eContainer();
            final ModelLocation parent = new ModelLocation(eContainer, eContainer.eContainingFeature());
            current.setParent(parent);
            current = parent;
        }
        return result;
    }

    public ModelLocation newLocation(ModelLocation location, EObject modelElement) {
        return new ModelLocation(location, modelElement, modelElement.eContainingFeature());
    }

    public ModelLocation newLocation(EObject container, EObject modelElement, EStructuralFeature modelElementContainingFeature) {
        final ModelLocation result = new ModelLocation(modelElement, modelElementContainingFeature);
        ModelLocation current = result;
        EObject eContainer = modelElement.eContainer();
        while (eContainer != null && !EcoreUtil.equals(container, eContainer)) {
            current.setParent(new ModelLocation(eContainer, eContainer.eContainingFeature()));
            eContainer = eContainer.eContainer();
            current = current.getParent();
        }
        current.setParent(new ModelLocation(container, container.eContainingFeature()));
        current = current.getParent();
        while (current.getModelElement().eContainer() != null) {
            eContainer = current.getModelElement().eContainer();
            final ModelLocation parent = new ModelLocation(eContainer, eContainer.eContainingFeature());
            current.setParent(parent);
            current = parent;
        }
        return result;
    }

}
