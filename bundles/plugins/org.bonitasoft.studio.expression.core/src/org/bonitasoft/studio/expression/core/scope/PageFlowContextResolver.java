/*******************************************************************************
 * Copyright (C) 2015 Bonitasoft S.A.
 * Bonitasoft is a trademark of Bonitasoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * Bonitasoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or Bonitasoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package org.bonitasoft.studio.expression.core.scope;

import java.util.HashSet;
import java.util.Set;

import org.bonitasoft.studio.model.process.ProcessPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

public class PageFlowContextResolver {

    private static final Set<EStructuralFeature> PAGE_FLOW_CONTEXT_FEATURES = new HashSet<>();
    private static final Set<EStructuralFeature> OVERVIEW_CONTEXT_FEATURES = new HashSet<>();

    static {
        PAGE_FLOW_CONTEXT_FEATURES.add(ProcessPackage.Literals.PAGE_FLOW__TRANSIENT_DATA);
        PAGE_FLOW_CONTEXT_FEATURES.add(ProcessPackage.Literals.PAGE_FLOW__PAGE_FLOW_CONNECTORS);
        PAGE_FLOW_CONTEXT_FEATURES.add(ProcessPackage.Literals.PAGE_FLOW__PAGE_FLOW_TRANSITIONS);
        PAGE_FLOW_CONTEXT_FEATURES.add(ProcessPackage.Literals.PAGE_FLOW__ENTRY_REDIRECTION_ACTIONS);

        OVERVIEW_CONTEXT_FEATURES.add(ProcessPackage.Literals.RECAP_FLOW__RECAP_FLOW_CONNECTORS);
        OVERVIEW_CONTEXT_FEATURES.add(ProcessPackage.Literals.RECAP_FLOW__RECAP_TRANSIENT_DATA);
        OVERVIEW_CONTEXT_FEATURES.add(ProcessPackage.Literals.RECAP_FLOW__RECAP_PAGE_FLOW_TRANSITIONS);
        OVERVIEW_CONTEXT_FEATURES.add(ProcessPackage.Literals.RECAP_FLOW__RECAP_PAGE_FLOW_REDIRECTION_URL);
    }

    public boolean isPageFlowContext(final ModelLocation location) {
        ModelLocation currentLocation = location;
        while (currentLocation != null) {
            if (PAGE_FLOW_CONTEXT_FEATURES.contains(currentLocation.getContainingFeature())) {
                return true;
            }
            currentLocation = currentLocation.getParent();
        }
        return false;
    }

    public boolean isOverviewContext(final ModelLocation location) {
        ModelLocation currentLocation = location;
        while (currentLocation != null) {
            if (OVERVIEW_CONTEXT_FEATURES.contains(currentLocation.getContainingFeature())) {
                return true;
            }
            currentLocation = currentLocation.getParent();
        }
        return false;
    }

}
