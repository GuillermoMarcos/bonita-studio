/**
 * Copyright (C) 2016 Bonitasoft S.A.
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

import org.bonitasoft.engine.bpm.connector.ConnectorEvent;
import org.bonitasoft.studio.common.emf.tools.ExpressionHelper;
import org.bonitasoft.studio.common.emf.tools.ModelHelper;
import org.bonitasoft.studio.expression.core.scope.ModelLocationFactory;
import org.bonitasoft.studio.model.connectorconfiguration.ConnectorConfiguration;
import org.bonitasoft.studio.model.connectorconfiguration.ConnectorConfigurationFactory;
import org.bonitasoft.studio.model.form.FormFactory;
import org.bonitasoft.studio.model.form.TextFormField;
import org.bonitasoft.studio.model.parameter.Parameter;
import org.bonitasoft.studio.model.parameter.ParameterFactory;
import org.bonitasoft.studio.model.process.Activity;
import org.bonitasoft.studio.model.process.Connector;
import org.bonitasoft.studio.model.process.ContractInput;
import org.bonitasoft.studio.model.process.ContractInputType;
import org.bonitasoft.studio.model.process.Data;
import org.bonitasoft.studio.model.process.ProcessFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConnectorParamterExpressionFilterTest {

    private ConnectorParamterExpressionFilter filter;

    @Before
    public void setUp() throws Exception {
        filter = new ConnectorParamterExpressionFilter();
    }

    @Test
    public void should_select_returns_true_for_contract_input_expression_in_on_finish_connector_input()
            throws Exception {
        final Activity activity = ProcessFactory.eINSTANCE.createActivity();
        final Connector onFinishConnector = ProcessFactory.eINSTANCE.createConnector();
        onFinishConnector.setEvent(ConnectorEvent.ON_FINISH.name());
        activity.getConnectors().add(onFinishConnector);
        final ContractInput input = ProcessFactory.eINSTANCE.createContractInput();
        input.setName("myInput");
        input.setType(ContractInputType.TEXT);
        assertThat(filter.apply(new ModelLocationFactory().newLocation(onFinishConnector), ExpressionHelper.createContractInputExpression(input))).isTrue();
    }

    @Test
    public void should_select_returns_true_for_contract_input_expression_in_on_enter_connector_input() throws Exception {
        final Connector onFinishConnector = ProcessFactory.eINSTANCE.createConnector();
        onFinishConnector.setEvent(ConnectorEvent.ON_ENTER.name());
        final ConnectorConfiguration config = ConnectorConfigurationFactory.eINSTANCE.createConnectorConfiguration();
        onFinishConnector.setConfiguration(config);
        final ContractInput input = ProcessFactory.eINSTANCE.createContractInput();
        input.setName("myInput");
        input.setType(ContractInputType.TEXT);
        assertThat(filter.apply(new ModelLocationFactory().newLocation(config), ExpressionHelper.createContractInputExpression(input))).isFalse();
    }

    @Test
    public void should_select_returns_true_for_variable_expression_in_connector_input()
            throws Exception {
        final Data data = ProcessFactory.eINSTANCE.createData();
        data.setName("myData");
        data.setDataType(ModelHelper.createStringDataType());
        assertThat(filter.apply(new ModelLocationFactory().newLocation(ProcessFactory.eINSTANCE.createConnector()),
                ExpressionHelper.createVariableExpression(data))).isTrue();
    }

    @Test
    public void should_select_returns_false_for_form_filed_expression_in_connector_input() throws Exception {
        final Connector onFinishConnector = ProcessFactory.eINSTANCE.createConnector();
        onFinishConnector.setEvent(ConnectorEvent.ON_ENTER.name());
        final TextFormField field = FormFactory.eINSTANCE.createTextFormField();
        field.setName("myData");
        assertThat(filter.apply(new ModelLocationFactory().newLocation(onFinishConnector), ExpressionHelper.createWidgetExpression(field))).isFalse();
    }

    @Test
    public void should_select_returns_true_for_parameter_expression() throws Exception {
        final ConnectorConfiguration config = ConnectorConfigurationFactory.eINSTANCE.createConnectorConfiguration();
        final Parameter param = ParameterFactory.eINSTANCE.createParameter();
        param.setName("myParam");
        param.setTypeClassname(String.class.getName());
        assertThat(filter.apply(new ModelLocationFactory().newLocation(config), ExpressionHelper.createParameterExpression(param))).isTrue();
    }

}
