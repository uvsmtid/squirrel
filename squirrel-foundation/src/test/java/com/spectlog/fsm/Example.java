package com.spectlog.fsm;

import org.junit.Test;
import org.squirrelframework.foundation.fsm.StateMachineBuilderFactory;
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
        ToA, ToB, ToC, ToD
    }
    
    // 2. Define State Machine Class
    @StateMachineParameters(
    	stateType = String.class,
    	eventType = FSMEvent.class,
    	contextType = Integer.class
    )
    @States({
    	@State(name = "A"),
    	@State(name = "B")
    })
    @Transitions({
    	@Transit(from = "A", to = "B", on = "ToB", callMethod = "fromAToB")
    })
    static class StateMachineSample extends AbstractUntypedStateMachine {

        protected void fromAToB(String from, String to, FSMEvent event, Integer context) {
            System.out.println("Transition from '" + from + "' to '" + to + "' on event '" + event + "' with context '"+context+"'.");
        }

        protected void ontoB(String from, String to, FSMEvent event, Integer context) {
            System.out.println("Entry State \'" + to + "\'.");
        }

    }

    @Test
    public void quickTest() {

        // 3. Build State Transitions
        UntypedStateMachineBuilder builder = StateMachineBuilderFactory.create(StateMachineSample.class);
        /*
        builder.externalTransition().from("A").to("B").on(FSMEvent.ToB).callMethod("fromAToB");
        builder.onEntry("B").callMethod("ontoB");
        */

        // 4. Use State Machine
        UntypedStateMachine fsm = builder.newStateMachine("A");
        fsm.fire(FSMEvent.ToB, 10);
        
        System.out.println("Current state is "+fsm.getCurrentState());

    }

}
