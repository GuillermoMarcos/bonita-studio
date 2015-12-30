/**
 * Copyright (C) 2012 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.studio.expression.core.provider;

import java.util.Set;

import org.bonitasoft.studio.model.expression.Expression;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Image;

/**
 * @author Romain Bioteau
 */
public interface IExpressionProvider {

	Set<Expression> getExpressions(EObject context) ;
	
	String getExpressionType() ;
	
	Image getIcon(Expression expression) ;
	
	Image getTypeIcon() ;
	
	String getProposalLabel(Expression expression) ;

	boolean isRelevantFor(EObject context);

	String getTypeLabel();

	IExpressionEditor getExpressionEditor(Expression expression, EObject context) ;
}