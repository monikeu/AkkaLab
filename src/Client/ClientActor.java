package Client;

import MessageSrtuctures.OrderRequestClientServer;
import MessageSrtuctures.SearchingRequestClientServer;
import MessageSrtuctures.SimpleResponse;
import MessageSrtuctures.StramingRequestClientServer;
import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ClientActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final String serverPath = "akka.tcp://remote_system@127.0.0.1:3552/user/remote";
    private ActorSelection serverSelection = getContext().actorSelection(serverPath);

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                // TODO
                .match(String.class, s -> {
                    if (s.startsWith("*s*")) {
                        String cleanTitle = s.replace("*s*", "");
                        serverSelection.tell(new SearchingRequestClientServer(cleanTitle), getSelf());
                    } else if (s.startsWith("*o*")) {
                        String cleanTitle = s.replace("*o*", "");
                        serverSelection = getContext().actorSelection(serverPath);
                        serverSelection.tell(new OrderRequestClientServer(cleanTitle), getSelf());
                    } else if (s.startsWith("*b*")) {
                        String cleanTitle = s.replace("*b*", "");
                        serverSelection = getContext().actorSelection(serverPath);
                        serverSelection.tell(new StramingRequestClientServer(cleanTitle), getSelf());
                    }
                    else if (s.startsWith("**b** ")) {
                        String line = s.replace("**b** ", "");
                        System.out.println("Streamed book:::  " + line);
                    }
                    else {
                        System.out.println("Undefined command");
                    }
                })

                .match(Float.class, f -> {
                    if (f != 0.0f)
                        System.out.println(f);
                    else System.out.println("No book in store");
                })
                .match(SimpleResponse.class, s -> {
                    System.out.println("Book ordered");
                })

                .matchAny(o -> {
                    log.info("received unknown message");
                    System.out.println(o.getClass());
                })
                .build();
    }
}
