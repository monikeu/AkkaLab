package Server.Servants;

import MessageSrtuctures.StramingRequestClientServer;
import akka.Done;
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
import scala.collection.JavaConversions;
import scala.concurrent.duration.FiniteDuration;
import scala.io.Codec;

import java.io.*;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public class StreamingActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private BufferedReader bufferedReader;
    private ActorMaterializer actorMaterializer;


    // ten będzie robił to sink itd
    @Override
    public Receive createReceive() {

        return receiveBuilder()
                // TODO
                .match(StramingRequestClientServer.class, s -> {

                    String pathname ="src/books/" + s.getTitle() + ".txt";
                    File file = new File(pathname);
                    System.out.println("dupa");

                    if (file.exists()) {
                        System.out.println("Started streaming");

                        actorMaterializer = ActorMaterializer.create(context());

                        ActorRef run = Source.actorRef(1000, OverflowStrategy.dropNew())
                                .throttle(1, FiniteDuration.create(1, TimeUnit.SECONDS), 1, ThrottleMode.shaping())
                                .to(Sink.actorRef(getSender(), NotUsed.getInstance()))
                                .run(actorMaterializer);
                        Iterator<String> lines = scala.io.Source.fromFile(pathname, Codec.UTF8()).getLines();

                        JavaConversions.asJavaIterator(lines).forEachRemaining(line -> {
                            run.tell("**b** " + line, getSelf());
                            System.out.println("Streamed " + line);
                        });

                        context().stop(getSelf());

                    }
                })

                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
}
