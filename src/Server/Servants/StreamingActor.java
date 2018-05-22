package Server.Servants;

import MessageSrtuctures.StreamingRequestServerServant;
import akka.NotUsed;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.OverflowStrategy;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import scala.collection.Iterator;
import scala.concurrent.duration.FiniteDuration;
import scala.io.Codec;
import java.io.File;
import java.util.concurrent.TimeUnit;

import static scala.collection.JavaConversions.asJavaIterator;
import static scala.io.Source.fromFile;

public class StreamingActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {

        return receiveBuilder()
                .match(StreamingRequestServerServant.class, s -> {

                    String pathname = "src/books/" + s.getStramingRequestClientServer().getTitle() + ".txt";
                    File file = new File(pathname);

                    System.out.println("Started streaming");

                    ActorRef client = Source.actorRef(1000, OverflowStrategy.fail())
                            .throttle(1, FiniteDuration.create(1, TimeUnit.SECONDS), 1, ThrottleMode.shaping())
                            .to(Sink.actorRef(getSender(), NotUsed.getInstance()))
                            .run(s.getMaterializer());

                    Iterator<String> lines = fromFile(pathname, Codec.UTF8()).getLines();

                    for (java.util.Iterator<String> it = asJavaIterator(lines); it.hasNext(); ) {
                        String line = it.next();
                        client.tell("**b** " + line, getSelf());
                        System.out.println("Streamed " + line);
                    }

                    context().stop(getSelf());
                })

                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
}
