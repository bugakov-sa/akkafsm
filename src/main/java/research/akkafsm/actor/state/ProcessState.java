package research.akkafsm.actor.state;

public enum ProcessState {
    CREATED,
    STARTING,
    STARTING_FAILURE,
    CHECKING_STATE,
    EXECUTING
}
