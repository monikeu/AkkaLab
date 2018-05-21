package Server.Servants;

import MessageSrtuctures.OrderRequestServerServant;
import MessageSrtuctures.SimpleResponse;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.io.IOException;
import java.io.PrintWriter;

public class OrderActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    // todo czemu tu kurde nie zapisuje do pliku
//    private BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("orders.txt"));
    private PrintWriter writer;

    public OrderActor() throws IOException {
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(OrderRequestServerServant.class, o -> {
                    ActorRef actorRef = getSender();

                        writer = new PrintWriter("orders.txt", "UTF-8");
                        System.out.println("Replying");
                        writer.println(o.getBook() + "\n");
                        actorRef.tell(SimpleResponse.OK, getSelf());
                        writer.close();

                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
}
