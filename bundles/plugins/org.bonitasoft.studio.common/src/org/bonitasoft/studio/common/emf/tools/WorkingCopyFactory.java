/*******************************************************************************
 * Copyright (C) 2015 Bonitasoft S.A.
 * Bonitasoft is a trademark of Bonitasoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * Bonitasoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or Bonitasoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package org.bonitasoft.studio.common.emf.tools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bonitasoft.studio.common.log.BonitaStudioLog;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;

public class WorkingCopyFactory {

    public static <T extends EObject> T newWorkingCopy(final T eObject) {
        final Copier copier = new Copier();
        final EObject result = copier.copy(eObject);
        copier.copyReferences();
        if (result != null) {
            try {
                final Method method = EObjectImpl.class.getDeclaredMethod("eBasicSetContainer", InternalEObject.class, int.class);
                method.setAccessible(true);
                method.invoke(result, eObject.eContainer(), -1 - eObject.eContainer().eClass().getFeatureID(eObject.eContainingFeature()));
            } catch (SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                BonitaStudioLog.error(e);
            }
        }
        @SuppressWarnings("unchecked")
        final T t = (T) result;
        return t;
    }

    public static <T extends EObject> T newWorkingCopy(final EClass eClass, final EObject container, final EStructuralFeature feature) {
        final EObject result = EcoreUtil.create(eClass);
        if (result != null) {
            try {
                final Method method = EObjectImpl.class.getDeclaredMethod("eBasicSetContainer", InternalEObject.class, int.class);
                method.setAccessible(true);
                method.invoke(result, container, -1 - container.eClass().getFeatureID(feature));
            } catch (SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                BonitaStudioLog.error(e);
            }
        }
        @SuppressWarnings("unchecked")
        final T t = (T) result;
        return t;
    }

}
