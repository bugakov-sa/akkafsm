package research.akkafsm;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.RandomPool;
import research.akkafsm.actor.ProcessChecker;
import research.akkafsm.actor.ProcessDispatcher;
import research.akkafsm.actor.ProcessStarter;
import research.akkafsm.actor.state.ProcessData;
import research.akkafsm.actor.state.ProcessState;
import research.akkafsm.service.ProcessExecutorAdapter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AkkaFsmApplication {

	public static void main(String[] args) {

		ActorSystem actorSystem = ActorSystem.create("ProcessDispatcherSystem");

        ProcessExecutorAdapter processExecutorAdapter = new ProcessExecutorAdapter();

        ActorRef processStarter = actorSystem.actorOf(new RandomPool(10)
                .props(Props.create(ProcessStarter.class, processExecutorAdapter)));

        ActorRef processChecker = actorSystem.actorOf(new RandomPool(10)
                .props(Props.create(ProcessChecker.class, processExecutorAdapter)));

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(
                () -> actorSystem.actorOf(Props.create(
                        ProcessDispatcher.class,
                        ProcessState.CREATED,
                        new ProcessData().setParams("5000", ""),
                        processStarter,
                        processChecker)),
                1000L,
                1000L,
                TimeUnit.MILLISECONDS
        );
	}
}
