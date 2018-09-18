package research.akkafsm.actor.message;

public final class ProcessStarted {
    public final String id;

    public ProcessStarted(String id) {
        this.id = id;
    }
}
