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
package org.bonitasoft.studio.engine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import org.bonitasoft.studio.common.repository.RepositoryAccessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TomcatVmArgsBuilderTest {

    @Mock
    protected RepositoryAccessor repositoryAccessor;
    protected TomcatVmArgsBuilder tomcatVmArgsBuilder;

    @Before
    public void setUp() throws Exception {
        tomcatVmArgsBuilder = spy(new TomcatVmArgsBuilder(repositoryAccessor));
        doNothing().when(tomcatVmArgsBuilder).addUIDesignerOptions(any(StringBuilder.class));
        doNothing().when(tomcatVmArgsBuilder).addWatchDogProperties(any(StringBuilder.class));
        doReturn("test.bonita.product.application.id").when(tomcatVmArgsBuilder).getProductApplicationId();
    }

    @Test
    public void testGetVMArgsContainsRegisterWebPropertyToYesWhenPropertySet() throws Exception {
        System.setProperty(TomcatVmArgsBuilder.BONITA_WEB_REGISTER, "1");
        assertThat(tomcatVmArgsBuilder.getVMArgs("")).contains("-Dbonita.web.register=1");
    }

    @Test
    public void testGetVMArgsContainsRegisterWebPropertyToYesWhenPropertyNotSet() throws Exception {
        System.clearProperty(TomcatVmArgsBuilder.BONITA_WEB_REGISTER);
        assertThat(tomcatVmArgsBuilder.getVMArgs("")).contains("-Dbonita.web.register=1");
    }

    @Test
    public void testGetVMArgsContainsRegisterWebPropertyToNoWhenPropertySet() throws Exception {
        System.setProperty(TomcatVmArgsBuilder.BONITA_WEB_REGISTER, "0");
        assertThat(tomcatVmArgsBuilder.getVMArgs("")).contains("-Dbonita.web.register=0");
    }
}