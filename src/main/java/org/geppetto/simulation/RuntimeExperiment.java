/*******************************************************************************
 * The MIT License (MIT)
 * 
 * Copyright (c) 2011 - 2015 OpenWorm.
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
package org.geppetto.simulation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geppetto.core.common.GeppettoExecutionException;
import org.geppetto.core.common.GeppettoInitializationException;
import org.geppetto.core.model.IModel;
import org.geppetto.core.model.IModelInterpreter;
import org.geppetto.core.model.ModelInterpreterException;
import org.geppetto.core.model.runtime.RuntimeTreeRoot;
import org.geppetto.core.model.simulation.GeppettoModel;
import org.geppetto.core.model.state.visitors.SerializeTreeVisitor;
import org.geppetto.core.model.state.visitors.SetWatchedVariablesVisitor;
import org.geppetto.core.simulation.ISimulationCallbackListener;
import org.geppetto.core.simulation.ISimulationCallbackListener.SimulationEvents;
import org.geppetto.simulation.visitor.CreateModelInterpreterServicesVisitor;
import org.geppetto.simulation.visitor.CreateRuntimeTreeVisitor;
import org.geppetto.simulation.visitor.ExitVisitor;
import org.geppetto.simulation.visitor.LoadSimulationVisitor;
import org.geppetto.simulation.visitor.PopulateVisualTreeVisitor;

public class RuntimeExperiment
{

	private Map<String, IModelInterpreter> modelInterpreters = new HashMap<String, IModelInterpreter>();

	private Map<String, IModel> instancePathToIModelMap = new HashMap<>();

	// Head node that holds the entities
	private RuntimeTreeRoot runtimeTreeRoot = new RuntimeTreeRoot("scene");

	private static Log logger = LogFactory.getLog(RuntimeExperiment.class);

	public RuntimeExperiment(String requestId, RuntimeProject runtimeProject, ISimulationCallbackListener listener)
	{
		init(requestId, runtimeProject.getGeppettoModel(), listener);
	}

	private void init(String requestId, GeppettoModel geppettoModel, ISimulationCallbackListener listener)
	{
		// clear watch lists
		this.clearWatchLists();

		// retrieve model interpreters and simulators
		CreateModelInterpreterServicesVisitor createServicesVisitor = new CreateModelInterpreterServicesVisitor(modelInterpreters, listener);
		geppettoModel.accept(createServicesVisitor);

		// // populateScripts(simulation);
		//
		// // _sessionContext.setMaxBufferSize(appConfig.getMaxBufferSize());

		LoadSimulationVisitor loadSimulationVisitor = new LoadSimulationVisitor(modelInterpreters, instancePathToIModelMap, listener);
		geppettoModel.accept(loadSimulationVisitor);

		CreateRuntimeTreeVisitor runtimeTreeVisitor = new CreateRuntimeTreeVisitor(modelInterpreters, instancePathToIModelMap, runtimeTreeRoot, listener);
		geppettoModel.accept(runtimeTreeVisitor);

		runtimeTreeRoot = runtimeTreeVisitor.getRuntimeModel();

		PopulateVisualTreeVisitor populateVisualVisitor = new PopulateVisualTreeVisitor(listener);
		runtimeTreeRoot.apply(populateVisualVisitor);

		notifyExperimentLoaded(requestId, listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.simulation.ISimulation#clearWatchLists()
	 */
	public void clearWatchLists()
	{
		logger.info("Clearing watched variables in simulation tree");

		// Update the RunTimeTreeModel setting watched to false for every node
		SetWatchedVariablesVisitor clearWatchedVariablesVisitor = new SetWatchedVariablesVisitor();
		runtimeTreeRoot.apply(clearWatchedVariablesVisitor);

		// SIM TODO
		// instruct aspects to clear watch variables
		// for(ISimulator simulator : _sessionContext.getSimulators().values())
		// {
		// if(simulator != null)
		// {
		// IVariableWatchFeature watchFeature = ((IVariableWatchFeature) simulator.getFeature(GeppettoFeature.VARIABLE_WATCH_FEATURE));
		// if(watchFeature != null)
		// {
		// watchFeature.clearWatchVariables();
		// }
		// }
		// }

	}

	public void setWatchedVariables(List<String> watchedVariables) throws GeppettoExecutionException, GeppettoInitializationException
	{
		logger.info("Setting watched variables in simulation tree");

		// Update the RunTimeTreeModel
		SetWatchedVariablesVisitor setWatchedVariablesVisitor = new SetWatchedVariablesVisitor(watchedVariables);
		runtimeTreeRoot.apply(setWatchedVariablesVisitor);

		// SIM TODO
		// Call the function for each simulator
		// for(Simulator simulatorModel : _sessionContext.getSimulators().keySet())
		// {
		// ISimulator simulator = _sessionContext.getSimulator(simulatorModel);
		// IVariableWatchFeature watchFeature = ((IVariableWatchFeature) simulator.getFeature(GeppettoFeature.VARIABLE_WATCH_FEATURE));
		// if(watchFeature != null)
		// {
		// watchFeature.setWatchedVariables(watchedVariables);
		// }
		// }
	}

	/**
	 * Method that takes the oldest model in the buffer and send it to the client
	 * 
	 * @param event
	 * @throws GeppettoExecutionException
	 * @throws ModelInterpreterException
	 * 
	 */
	private void notifyExperimentLoaded(String requestID, ISimulationCallbackListener simulationListener)
	{

		SerializeTreeVisitor updateClientVisitor = new SerializeTreeVisitor();
		runtimeTreeRoot.apply(updateClientVisitor);

		ExitVisitor exitVisitor = new ExitVisitor(simulationListener);
		runtimeTreeRoot.apply(exitVisitor);

		String scene = updateClientVisitor.getSerializedTree();

		if(scene != null)
		{
			simulationListener.updateReady(SimulationEvents.LOAD_MODEL, requestID, scene);
			logger.info("Simulation sent to callback listener");
		}
	}

	public void release()
	{
		// TODO: release the instantiated services
	}

}