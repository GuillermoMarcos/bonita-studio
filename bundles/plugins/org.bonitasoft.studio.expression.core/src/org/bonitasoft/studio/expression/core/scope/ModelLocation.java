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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class ModelLocation {

    private final EObject modelElement;
    private final EStructuralFeature containingFeature;
    private ModelLocation parent;

    public ModelLocation(ModelLocation parent, EObject modelElement, EStructuralFeature containgFeature) {
        this.modelElement = modelElement;
        this.containingFeature = containgFeature;
        this.parent = parent;
    }

    public ModelLocation(EObject modelElement, EStructuralFeature containgFeature) {
        this.modelElement = modelElement;
        this.containingFeature = containgFeature;
    }

    public EObject getModelElement() {
        return modelElement;
    }

    public EStructuralFeature getContainingFeature() {
        return containingFeature;
    }

    public ModelLocation getParent() {
        return parent;
    }

    void setParent(ModelLocation parent) {
        this.parent = parent;
    }

}
