package Server.Servants;

import MessageSrtuctures.OrderRequestServerServant;
import MessageSrtuctures.OrderResponseServantServer;
import MessageSrtuctures.SimpleResponse;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class OrderActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("orders.txt"));

    public OrderActor() throws IOException {
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(OrderRequestServerServant.class, o -> {
                    bufferedWriter.write(o.getBook() + "\n");
//                    getSender().tell(new OrderResponseServantServer(SimpleResponse.OK,o.getOrderRequestId()), getSelf());
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
}
