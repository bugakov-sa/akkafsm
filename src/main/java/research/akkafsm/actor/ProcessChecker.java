package research.akkafsm.actor;

import akka.actor.AbstractActor;
import research.akkafsm.actor.message.CheckProcessStatus;
import research.akkafsm.actor.message.ProcessInProgress;
import research.akkafsm.actor.message.ProcessStatus;
import research.akkafsm.service.ProcessExecutorAdapter;

public class ProcessChecker extends AbstractActor {
    private final ProcessExecutorAdapter processExecutorAdapter;

    public ProcessChecker(ProcessExecutorAdapter processExecutorAdapter) {
        this.processExecutorAdapter = processExecutorAdapter;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(CheckProcessStatus.class, message -> {
                    try {
                        ProcessStatus successMessage = processExecutorAdapter.checkProcessStatus(message.id);
                        getSender().tell(successMessage, getSelf());
                    }
                    catch (Throwable error) {
                        ProcessInProgress failureMessage = new ProcessInProgress();
                        getSender().tell(failureMessage, getSelf());
                    }
                })
                .build();
    }
}
