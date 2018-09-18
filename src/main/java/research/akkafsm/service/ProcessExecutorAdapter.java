package research.akkafsm.service;

import research.akkafsm.actor.message.ProcessFinished;
import research.akkafsm.actor.message.ProcessInProgress;
import research.akkafsm.actor.message.ProcessStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Заглушка настоящего исполнителя процессов.
 * В проде этот класс может быть адаптером, например, к Apache Oozie
 * В данной заглушке исполнение процесса заключается в ожидании промежутка времени, который задается в миллисекундах
 * в параметре процесса
 */
public class ProcessExecutorAdapter {

    private class Process {
        public final long createTime;
        public final long duration;

        public Process(long createTime, long duration) {
            this.createTime = createTime;
            this.duration = duration;
        }
    }

    private final Map<String, Process> processMap = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    /**
     * Запускает экземпляр процесса
     * @param processParam значение параметра процесса
     * @param executorParam значение параметра исполнителя
     * @return идентификатор экземпляра процесса (id)
     */
    public String startProcess(String processParam, String executorParam) {
        String id = String.valueOf(idGenerator.incrementAndGet());
        processMap.put(id, new Process(System.currentTimeMillis(), Long.valueOf(processParam)));
        return id;
    }

    /**
     * Вычисляет текущий статус экземпляра процесса
     * @param id идентификатор экземпляра процесса
     * @return статус процесса
     */
    public ProcessStatus checkProcessStatus(String id) {
        Process process = processMap.getOrDefault(id, null);
        if(process == null) {
            return new ProcessFinished();
        }
        if(process.createTime + process.duration > System.currentTimeMillis()) {
            return new ProcessInProgress();
        }
        else {
            processMap.remove(id);
            return new ProcessFinished();
        }
    }
}
