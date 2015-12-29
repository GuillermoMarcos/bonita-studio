/**
 * Copyright (C) 2012 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
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
package org.bonitasoft.studio.expression.core.provider;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import org.bonitasoft.studio.common.extension.BonitaStudioExtensionRegistryManager;
import org.bonitasoft.studio.common.extension.ExtensionContextInjectionFactory;
import org.bonitasoft.studio.common.log.BonitaStudioLog;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.e4.core.contexts.IEclipseContext;

/**
 * @author Romain Bioteau
 */
@Singleton
public class ExpressionProviderService {

    private static final String PROVIDER_CLASS_ATTRIBUTE = "providerClass";
    private static final String EXPRESSION_PROVIDER_ID = "org.bonitasoft.studio.expression.expressionProvider";

    private static ExpressionProviderService INSTANCE;

    private Set<IExpressionProvider> expressionProviders;
    private final ExtensionContextInjectionFactory extensionContextInjectionFactory;

    public static ExpressionProviderService getInstance() {
        return INSTANCE;
    }

    ExpressionProviderService() {
        INSTANCE = this;
        extensionContextInjectionFactory = new ExtensionContextInjectionFactory();
    }

    @PostConstruct
    protected void init(final IEclipseContext context) {
        expressionProviders = new HashSet<IExpressionProvider>();
        final IConfigurationElement[] elements = BonitaStudioExtensionRegistryManager.getInstance().getConfigurationElements(EXPRESSION_PROVIDER_ID);
        for (final IConfigurationElement element : elements) {
            try {
                expressionProviders.add(extensionContextInjectionFactory.make(element, PROVIDER_CLASS_ATTRIBUTE, IExpressionProvider.class, context));
            } catch (final Exception e) {
                BonitaStudioLog.error(e);
            }
        }
        context.set(ExpressionProviderService.class, this);
    }

    public Set<IExpressionProvider> getExpressionProviders() {
        return expressionProviders;
    }



    public synchronized IExpressionProvider getExpressionProvider(final String type) {
        for (final IExpressionProvider provider : getExpressionProviders()) {
            if (provider.getExpressionType().equals(type)) {
                return provider;
            }
        }
        return null;
    }
}
