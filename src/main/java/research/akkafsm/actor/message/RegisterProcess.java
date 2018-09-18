package research.akkafsm.actor.message;

import research.akkafsm.actor.state.ProcessData;
import research.akkafsm.actor.state.ProcessState;

public class RegisterProcess {
    public final ProcessState processState;
    public final ProcessData processData;

    public RegisterProcess(ProcessState processState, ProcessData processData) {
        this.processState = processState;
        this.processData = processData;
    }
}
