package research.akkafsm.actor.state;

public class ProcessData {
    public final String id;
    public final String processParam;
    public final String executorParam;

    private ProcessData(String id, String processParam, String executorParam) {
        this.id = id;
        this.processParam = processParam;
        this.executorParam = executorParam;
    }

    public ProcessData() {
        this(null, null, null);
    }

    public ProcessData setId(String id) {
        return new ProcessData(id, processParam, executorParam);
    }

    public ProcessData setParams(String processParam, String executorParam) {
        return new ProcessData(id, processParam, executorParam);
    }
}
