package com.spectlog.fsm;

import org.junit.Test;
import org.squirrelframework.foundation.fsm.StateCompositeType;
import org.squirrelframework.foundation.fsm.StateMachineBuilderFactory;
import org.squirrelframework.foundation.fsm.StateMachineConfiguration;
import org.squirrelframework.foundation.fsm.UntypedStateMachine;
import org.squirrelframework.foundation.fsm.UntypedStateMachineBuilder;
import org.squirrelframework.foundation.fsm.annotation.State;
import org.squirrelframework.foundation.fsm.annotation.StateMachineParameters;
import org.squirrelframework.foundation.fsm.annotation.States;
import org.squirrelframework.foundation.fsm.annotation.Transit;
import org.squirrelframework.foundation.fsm.annotation.Transitions;
import org.squirrelframework.foundation.fsm.impl.AbstractUntypedStateMachine;

public class Example {

	
    // 1. Define State Machine Event
    enum FSMEvent {

        ToB,

        ToP,
        ToP_1,
        ToP_2,
        ToS,

        ToPASSIVE,

        ;
    }
    
    // 2. Define State Machine Class
    @StateMachineParameters(
    	stateType = String.class,
    	eventType = FSMEvent.class,
    	contextType = Integer.class
    )

    @States({

    	@State(name = "A"),
    	@State(name = "B"),

    	@State(
    		name = "P",
    		compositeType = StateCompositeType.PARALLEL,
    		entryCallMethod = "genericEntry",
    		exitCallMethod = "genericExit"
    	),

    	@State(
    		parent = "P",
    		name = "P_1",
    		entryCallMethod = "genericEntry",
    		exitCallMethod = "genericExit"
    	),
    	@State(
    		parent = "P_1",
    		name = "P_1_POWER_SAVING",
    		compositeType = StateCompositeType.SEQUENTIAL,
    		entryCallMethod = "genericEntry",
    		exitCallMethod = "genericExit"
    	),
    	@State(
    		parent = "P_1_POWER_SAVING",
    		name = "P_1_POWER_SAVING_ACTIVE",
    		entryCallMethod = "genericEntry",
    		exitCallMethod = "genericExit",
    		initialState = true
    	),
    	@State(
    		parent = "P_1_POWER_SAVING",
    		name = "P_1_POWER_SAVING_PASSIVE",
    		entryCallMethod = "genericEntry",
    		exitCallMethod = "genericExit"
    	),

    	@State(
    		parent = "P",
    		name = "P_2",
    		entryCallMethod = "genericEntry",
    		exitCallMethod = "genericExit"	
    	),
    	@State(
    		parent = "P_2",
    		name = "P_2_S",
    		compositeType = StateCompositeType.SEQUENTIAL,
    		entryCallMethod = "genericEntry",
    		exitCallMethod = "genericExit"
    	),
    	@State(
    		parent = "P_2_S",
    		name = "P_2_S_1",
    		entryCallMethod = "genericEntry",
    		exitCallMethod = "genericExit"
    	),
    	@State(
    		parent = "P_2_S",
    		name = "P_2_S_2",
    		entryCallMethod = "genericEntry",
    		exitCallMethod = "genericExit",
    		initialState = true
    	),
    	
    	@State(
    		name = "S",
    		compositeType = StateCompositeType.SEQUENTIAL,
    		entryCallMethod = "genericEntry",
    		exitCallMethod = "genericExit"
    	),
    	@State(
    		parent = "S",
    		name = "S_1",
    		entryCallMethod = "genericEntry",
    		exitCallMethod = "genericExit"
    	),
    	@State(
    		parent = "S",
    		name = "S_2",
    		entryCallMethod = "genericEntry",
    		exitCallMethod = "genericExit"
    	),
    	
    	
    	@State(name = "T")
    })

    @Transitions({
    	@Transit(from = "A", to = "B", on = "ToB", callMethod = "fromAToB"),
    	@Transit(from = "B", to = "P", on = "ToP", callMethod = "genericTransit"),
    	
    	@Transit(from = "P", to = "P_1", on = "ToP_1", callMethod = "genericTransit"),
    	@Transit(from = "P", to = "P_2", on = "ToP_2", callMethod = "genericTransit"),

    	@Transit(from = "P", to = "S", on = "ToS", callMethod = "genericTransit"),
    	
    	@Transit(from = "P_1", to = "P_1_POWER_SAVING_PASSIVE", on = "ToPASSIVE", callMethod = "genericTransit"),
    })

    static class StateMachineSample extends AbstractUntypedStateMachine {

        protected void fromAToB(String from, String to, FSMEvent event, Integer context) {
            System.out.println("Transition from '" + from + "' to '" + to + "' on event '" + event + "' with context '" + context + "'.");
        }

        protected void ontoB(String from, String to, FSMEvent event, Integer context) {
            System.out.println("Entry State \'" + to + "\'.");
        }
        
        protected void genericTransit(String from, String to, FSMEvent event, Integer context) {
            System.out.println("Transition from '" + from + "' to '" + to + "' on event '" + event + "' with context '" + context + "'.");
        }

        protected void genericEntry(String from, String to, FSMEvent event, Integer context) {
            System.out.println("Entry to '" + to + "' on event '" + event + "' with context '" + context + "'.");
        }

        protected void genericExit(String from, String to, FSMEvent event, Integer context) {
            System.out.println("Exit from '" + from + "' on event '" + event + "' with context '" + context + "'.");
        }

    }

    @Test
    public void quickTest() {

        // Build State Transitions.
    	// Whatever is provided by annotation is merged with added programmatically. 
        UntypedStateMachineBuilder builder = StateMachineBuilderFactory.create(StateMachineSample.class);
        /*
        builder.externalTransition().from("A").to("B").on(FSMEvent.ToB).callMethod("fromAToB");
        builder.onEntry("B").callMethod("ontoB");
        */

        // Use State Machine.
        UntypedStateMachine fsm = builder.newStateMachine(
        		"A",
        		StateMachineConfiguration
        			.create()
        			//.enableDebugMode(true)
        );
        fsm.fire(FSMEvent.ToB, 10);

        fsm.fire(FSMEvent.ToP, 10);
        System.out.println("Current state is " + fsm.getCurrentState());
        
        fsm.fire(FSMEvent.ToP_1, 10);
        System.out.println("Current state is " + fsm.getCurrentState());
        fsm.fire(FSMEvent.ToP_2, 10);
        System.out.println("Current state is " + fsm.getCurrentState());
        
        fsm.fire(FSMEvent.ToPASSIVE, 10);

        System.out.println("Current state is " + fsm.getCurrentState());

        fsm.getSubStatesOn("S").stream().forEach(s -> System.out.println("Substates on S: " + s));
        fsm.getSubStatesOn("P").stream().forEach(s -> System.out.println("Substates on P: " + s));
        fsm.getSubStatesOn("P_1").stream().forEach(s -> System.out.println("Substates on P_1: " + s));
        fsm.getSubStatesOn("P_2").stream().forEach(s -> System.out.println("Substates on P_2: " + s));
        fsm.getSubStatesOn("P_1_POWER_SAVING").stream().forEach(s -> System.out.println("Substates on P_1_POWER_SAVING: " + s));
        fsm.getSubStatesOn("P_1_POWER_SAVING_PASSIVE").stream().forEach(s -> System.out.println("Substates on P_1_POWER_SAVING_PASSIVE: " + s));
    }

}
