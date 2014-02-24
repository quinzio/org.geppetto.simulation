/*******************************************************************************
 * The MIT License (MIT)
 * 
 * Copyright (c) 2011, 2013 OpenWorm.
 * http://openworm.org
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License
 * which accompanies this distribution, and is available at
 * http://opensource.org/licenses/MIT
 *
 * Contributors:
 *     	OpenWorm - http://openworm.org/people.html
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights 
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
 * copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE 
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package org.geppetto.simulation.visitor;

import org.geppetto.core.visualisation.model.Scene;
import org.geppetto.simulation.SessionContext;
import org.geppetto.simulation.model.Aspect;
import org.geppetto.simulation.model.Entity;
import org.geppetto.simulation.model.Model;
import org.geppetto.simulation.model.Simulation;
import org.geppetto.simulation.model.Simulator;

import com.massfords.humantask.BaseVisitor;
import com.massfords.humantask.TraversingVisitor;

/**
 * This is the simulation visitor which traverse the simulation tree and orchestrates
 * the simulation of the different models.
 * 
 * 
 * @author matteocantarelli
 *
 */
public class SimulationVisitor extends TraversingVisitor
{
	
	private SessionContext _sessionContext;
	private Scene _scene;

	public SimulationVisitor(SessionContext sessionContext)
	{
		super(new DepthFirstTraverserEntitiesFirst(), new BaseVisitor());
		_sessionContext=sessionContext;
		_scene=new Scene();
	}

	/* (non-Javadoc)
	 * @see com.massfords.humantask.TraversingVisitor#visit(org.geppetto.simulation.model.Aspect)
	 */
	@Override
	public void visit(Aspect aspect)
	{
		super.visit(aspect);
	}

	/* (non-Javadoc)
	 * @see com.massfords.humantask.TraversingVisitor#visit(org.geppetto.simulation.model.Entity)
	 */
	@Override
	public void visit(Entity entity)
	{
		//This happens before visiting the child entities
		super.visit(entity);
		//This happens after visiting the child entities
	}

	/* (non-Javadoc)
	 * @see com.massfords.humantask.TraversingVisitor#visit(org.geppetto.simulation.model.Model)
	 */
	@Override
	public void visit(Model model)
	{
		super.visit(model);
		//IModelInterpreter modelInterpreter=_sessionContext.getModelInterpreter(model);
	}

	/* (non-Javadoc)
	 * @see com.massfords.humantask.TraversingVisitor#visit(org.geppetto.simulation.model.Simulation)
	 */
	@Override
	public void visit(Simulation simulation)
	{
		super.visit(simulation);
	}

	/* (non-Javadoc)
	 * @see com.massfords.humantask.TraversingVisitor#visit(org.geppetto.simulation.model.Simulator)
	 */
	@Override
	public void visit(Simulator simulator)
	{
		// TODO Auto-generated method stub
		super.visit(simulator);
	}

}
