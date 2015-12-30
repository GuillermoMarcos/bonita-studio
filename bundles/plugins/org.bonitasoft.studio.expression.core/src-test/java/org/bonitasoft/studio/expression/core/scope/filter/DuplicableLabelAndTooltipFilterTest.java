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
package org.bonitasoft.studio.expression.core.scope.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bonitasoft.studio.model.expression.builders.ExpressionBuilder.aVariableExpression;
import static org.bonitasoft.studio.model.expression.builders.ExpressionBuilder.anExpression;
import static org.bonitasoft.studio.model.process.builders.PoolBuilder.aPool;
import static org.bonitasoft.studio.model.process.builders.TaskBuilder.aTask;

import org.bonitasoft.studio.common.ExpressionConstants;
import org.bonitasoft.studio.expression.core.scope.ModelLocation;
import org.bonitasoft.studio.model.form.FormPackage;
import org.bonitasoft.studio.model.form.TextFormField;
import org.bonitasoft.studio.model.form.builders.FormBuilder;
import org.bonitasoft.studio.model.form.builders.TextFormFieldBuilder;
import org.junit.Test;


public class DuplicableLabelAndTooltipFilterTest {

    @Test
    public void should_filter_variable_expression_type_if_widget_is_on_an_instantiation_form() throws Exception {
        final DuplicableLabelAndTooltipFilter filter = new DuplicableLabelAndTooltipFilter();

        final TextFormField widget = widgetInInstantiationForm();
        widget.setTooltipForAdd(aVariableExpression().build());

        assertThat(filter.apply(new ModelLocation(widget, FormPackage.Literals.DUPLICABLE__TOOLTIP_FOR_ADD), widget.getTooltipForAdd())).isFalse();
    }

    @Test
    public void should_filter_search_index_expression_type_if_widget_is_on_an_instantiation_form() throws Exception {
        final DuplicableLabelAndTooltipFilter filter = new DuplicableLabelAndTooltipFilter();

        final TextFormField widget = widgetInInstantiationForm();
        widget.setTooltipForAdd(anExpression().withExpressionType(ExpressionConstants.SEARCH_INDEX_TYPE).build());

        assertThat(filter.apply(new ModelLocation(widget, FormPackage.Literals.DUPLICABLE__TOOLTIP_FOR_ADD), widget.getTooltipForAdd())).isFalse();
    }

    @Test
    public void should_not_filter_expression_if_widget_is_not_on_an_instantiation_form() throws Exception {
        final DuplicableLabelAndTooltipFilter filter = new DuplicableLabelAndTooltipFilter();

        final TextFormField widget = widgetNotInInstantiationForm();
        widget.setTooltipForAdd(aVariableExpression().build());

        assertThat(filter.apply(new ModelLocation(widget, FormPackage.Literals.DUPLICABLE__TOOLTIP_FOR_ADD), widget.getTooltipForAdd())).isTrue();
    }

    @Test
    public void should_be_relevant_for_duplicqble_label_tooltip_features() throws Exception {
        final DuplicableLabelAndTooltipFilter filter = new DuplicableLabelAndTooltipFilter();

        final TextFormField widget = widgetNotInInstantiationForm();
        widget.setTooltipForAdd(aVariableExpression().build());

        assertThat(filter.isRelevant(new ModelLocation(null, FormPackage.Literals.DUPLICABLE__TOOLTIP_FOR_ADD))).isTrue();
        assertThat(filter.isRelevant(new ModelLocation(null, FormPackage.Literals.DUPLICABLE__TOOLTIP_FOR_REMOVE))).isTrue();
        assertThat(filter.isRelevant(new ModelLocation(null, FormPackage.Literals.DUPLICABLE__DISPLAY_LABEL_FOR_ADD))).isTrue();
        assertThat(filter.isRelevant(new ModelLocation(null, FormPackage.Literals.DUPLICABLE__DISPLAY_LABEL_FOR_REMOVE))).isTrue();
    }

    @Test
    public void should_not_be_relevant_for_other_than_DUPLICABLE__TOOLTIP_FOR_ADD_feature() throws Exception {
        final DuplicableLabelAndTooltipFilter filter = new DuplicableLabelAndTooltipFilter();

        final TextFormField widget = widgetNotInInstantiationForm();
        widget.setTooltipForAdd(aVariableExpression().build());

        assertThat(filter.isRelevant(new ModelLocation(null, FormPackage.Literals.DUPLICABLE__MAX_NUMBER_OF_DUPLICATION))).isFalse();
    }

    private TextFormField widgetInInstantiationForm() {
        final TextFormField widget = TextFormFieldBuilder.aTextFormField().build();
        aPool().havingForm(FormBuilder.aForm().havingWidget(widget)).build();
        return widget;
    }

    private TextFormField widgetNotInInstantiationForm() {
        final TextFormField widget = TextFormFieldBuilder.aTextFormField().build();
        aTask().havingForm(FormBuilder.aForm().havingWidget(widget)).build();
        return widget;
    }

}
