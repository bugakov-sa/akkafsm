package research.akkafsm.actor;

import akka.actor.AbstractFSM;
import akka.actor.ActorRef;
import akka.japi.pf.FI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import research.akkafsm.actor.message.*;
import research.akkafsm.actor.state.ProcessData;
import research.akkafsm.actor.state.ProcessState;

import java.time.Duration;
import java.util.Arrays;

import static research.akkafsm.actor.state.ProcessState.*;

public class ProcessDispatcher extends AbstractFSM<ProcessState, ProcessData> {
    private final Logger LOGGER = LoggerFactory.getLogger(ProcessDispatcher.class);

    public ProcessDispatcher(
            ProcessState initState,
            ProcessData initData,
            ActorRef processStarter,
            ActorRef processChecker
    ) {
        startWith(initState, initData);

        when(CREATED, Duration.ofSeconds(0), matchEvent(
                Arrays.asList(StateTimeout()), ProcessData.class, (message, data) -> goTo(STARTING)));

        FI.UnitApplyVoid sendStartProcessMessage = () ->
                processStarter.tell(new StartProcess(stateData().processParam, stateData().executorParam), getSelf());
        onTransition(
                matchState(CREATED, STARTING, sendStartProcessMessage)
                        .state(STARTING_FAILURE, STARTING, sendStartProcessMessage));

        when(STARTING, matchEvent(
                ProcessStarted.class,
                (message, data) -> {
                    LOGGER.info("Начато выполнение процесса {}", message.id);
                    return goTo(CHECKING_STATE).using(data.setId(message.id));
                }));

        when(STARTING, matchEvent(
                ProcessFailure.class,
                (message, data) -> goTo(STARTING_FAILURE)));

        when(STARTING_FAILURE, Duration.ofSeconds(5), matchEvent(
                Arrays.asList(StateTimeout()), ProcessData.class, (message, data) -> goTo(STARTING)));

        FI.UnitApplyVoid sendCheckStatusMessage = () ->
                processChecker.tell(new CheckProcessStatus(stateData().id), getSelf());
        onTransition(
                matchState(STARTING, CHECKING_STATE, sendCheckStatusMessage)
                        .state(EXECUTING, CHECKING_STATE, sendCheckStatusMessage));

        when(CHECKING_STATE, matchEvent(
                ProcessFinished.class,
                (message, data) -> {
                    LOGGER.info("Процесс {} завершен успешно", data.id);
                    return stop();
                }));

        when(CHECKING_STATE, matchEvent(
                ProcessFailure.class,
                (message, data) -> {
                    LOGGER.error("Процесс {} завершен с ошибкой {}", data.id, message.error);
                    return stop();
                }));

        when(CHECKING_STATE, matchEvent(
                ProcessInProgress.class,
                (message, data) -> {
                    LOGGER.info("Процесс {} выполняется", data.id);
                    return goTo(EXECUTING);
                }));

        when(EXECUTING, Duration.ofSeconds(5),
                matchEvent(Arrays.asList(StateTimeout()), ProcessData.class,
                        (message, data) -> goTo(CHECKING_STATE)));

        initialize();
    }
}
