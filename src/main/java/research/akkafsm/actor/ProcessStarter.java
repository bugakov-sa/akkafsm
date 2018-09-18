package research.akkafsm.actor;

import akka.actor.AbstractActor;
import research.akkafsm.actor.message.ProcessFailure;
import research.akkafsm.actor.message.ProcessStarted;
import research.akkafsm.actor.message.StartProcess;
import research.akkafsm.service.ProcessExecutorAdapter;

public class ProcessStarter extends AbstractActor {
    private final ProcessExecutorAdapter processExecutorAdapter;

    public ProcessStarter(ProcessExecutorAdapter processExecutorAdapter) {
        this.processExecutorAdapter = processExecutorAdapter;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(StartProcess.class, message -> {
                    try {
                        String id = processExecutorAdapter.startProcess(message.processParam, message.executorParam);
                        ProcessStarted successMessage = new ProcessStarted(id);
                        getSender().tell(successMessage, getSelf());
                    } catch (Throwable error) {
                        ProcessFailure failureMessage = new ProcessFailure(error);
                        getSender().tell(failureMessage, getSelf());
                    }
                })
                .build();
    }
}
