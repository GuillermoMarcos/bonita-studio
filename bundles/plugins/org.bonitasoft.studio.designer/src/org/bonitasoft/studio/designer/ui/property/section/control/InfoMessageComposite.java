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
package org.bonitasoft.studio.designer.ui.property.section.control;

import org.bonitasoft.studio.model.process.FormMappingType;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class InfoMessageComposite extends Composite {

    private final Label info;

    public InfoMessageComposite(final Composite parent, final TabbedPropertySheetWidgetFactory widgetFactory) {
        super(parent, SWT.NONE);
        setLayout(GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(10, 0, 10, 0).create());
        info = widgetFactory.createLabel(this, "", SWT.WRAP);
        info.setLayoutData(GridDataFactory.swtDefaults().span(2, 1).align(SWT.RIGHT, SWT.CENTER).create());
        widgetFactory.adapt(this);
    }

    public void doBindControl(final DataBindingContext context, final IObservableValue formMappingObservable, final FormMappingType type) {
        doBindInfo(context, formMappingObservable, type);
    }

    protected void doBindInfo(final DataBindingContext context, final IObservableValue formMappingObservable, final FormMappingType type) {
        final UpdateValueStrategy infoStrategy = new UpdateValueStrategy();
        infoStrategy.setConverter(new InfoMessageConverter(type));
        context.bindValue(SWTObservables.observeText(info), formMappingObservable, null, infoStrategy);
    }
}
