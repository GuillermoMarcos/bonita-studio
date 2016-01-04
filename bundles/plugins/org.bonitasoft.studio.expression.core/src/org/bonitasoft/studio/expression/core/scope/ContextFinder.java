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

import static com.google.common.base.Preconditions.checkState;

import org.bonitasoft.studio.model.expression.Expression;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class ContextFinder {

    private final ModelLocation location;

    public ContextFinder(ModelLocation location) {
        this.location = location;
    }

    @SuppressWarnings("unchecked")
    public <T extends EObject> T find(Class<T> type) {
        ModelLocation currentLocation = location;
        while (currentLocation != null && !type.isAssignableFrom(currentLocation.getModelElement().getClass())) {
            currentLocation = currentLocation.getParent();
        }
        return (T) (currentLocation != null ? currentLocation.getModelElement() : null);
    }

    public EObject findExpressionContext() {
        EObject context = location.getModelElement();
        if (context instanceof Expression) {
            context = location.getParent().getModelElement();
        }
        checkState(context != null, "expression context is null");
        return context;
    }

    public EObject find(EStructuralFeature feature) {
        ModelLocation currentLocation = location;
        while (currentLocation != null && !java.util.Objects.equals(feature, currentLocation.getContainingFeature())) {
            currentLocation = currentLocation.getParent();
        }
        return currentLocation != null ? currentLocation.getModelElement() : null;
    }

}
