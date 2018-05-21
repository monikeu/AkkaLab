package Server;

import MessageSrtuctures.*;
import Server.Servants.OrderActor;
import MessageSrtuctures.OrderRequestServerServant;
import Server.Servants.SearchingActor;
import Server.Servants.StreamingActor;
import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import scala.concurrent.duration.Duration;

import java.io.IOException;
import java.util.*;

import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.resume;

public class ServerActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private int searchingActorCount = 0;
    private ActorSystem systemRef;

    private Map<Integer, ActorRef> clientsWaitingForSearchResponse = new HashMap<>();
    private Map<Integer, Set<ActorRef>> searchingActorsServingRequest = new HashMap<>();
    private Map<Integer, Boolean> resultWasZeroBefore = new HashMap<>();
    private Map<Integer, Boolean> secondMessage = new HashMap<>();
    private Materializer materializer;

    private int searchRequestId = 0;
    private int streamingActorsCount = 0;


    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(ActorSystem.class, s -> {
                    systemRef = s;
                    materializer = ActorMaterializer.create(systemRef);
                })
                .match(SearchingRequestClientServer.class, s -> {
                    log.info("Server: Received request from client for book: " + s.getTitle() + " Sending request to child actors");
                    ActorRef sender = getSender();


                    ++searchRequestId;
                    // todo actor pool

                    context().actorOf(Props.create(SearchingActor.class), "searchingActor" + (++searchingActorCount));
                    ActorRef actorRef = context().child("searchingActor" + searchingActorCount).get();
                    actorRef.tell(new SearchingRequestServerActor(1, searchRequestId, s), getSelf());
                    log.info("Server: Started actor " + searchingActorCount + ", who is searching through database 1");


                    context().actorOf(Props.create(SearchingActor.class), "searchingActor" + (++searchingActorCount));
                    ActorRef actorRef1 = context().child("searchingActor" + searchingActorCount).get();
                    actorRef1.tell(new SearchingRequestServerActor(2, searchRequestId, s), getSelf());
                    log.info("Server: Started actor " + searchingActorCount + ", who is searching through database 2");

                    clientsWaitingForSearchResponse.put(searchRequestId, sender);

                    HashSet<ActorRef> actorRefs = new HashSet<>();
                    actorRefs.add(actorRef);
                    actorRefs.add(actorRef1);
                    searchingActorsServingRequest.put(searchRequestId, actorRefs);
                    resultWasZeroBefore.put(searchRequestId, Boolean.FALSE);
                    secondMessage.put(searchRequestId, Boolean.FALSE);
                })
                .match(SearchingResponseServantServer.class, searchingResponse -> {
                    int idOfRequestSender = searchingResponse.getIdOfRequestSender();

                    if (searchingResponse.getAnswer() == 0.0f && resultWasZeroBefore.get(idOfRequestSender).equals(Boolean.FALSE)) {
                        resultWasZeroBefore.put(searchingResponse.getIdOfRequestSender(), Boolean.TRUE);
                        System.out.println("dupa");
                    } else {
                        if (!secondMessage.get(idOfRequestSender)) {

                            ActorRef actorRef = clientsWaitingForSearchResponse.remove(idOfRequestSender);
                            log.info("Server: Received searching result from servant actor");
                            actorRef.tell(searchingResponse.getAnswer(), getSelf());
                            Set<ActorRef> removed = searchingActorsServingRequest.remove(idOfRequestSender);
                            removed.forEach(e -> context().stop(e));
                            secondMessage.put(idOfRequestSender, Boolean.TRUE);
                        }

                    }

                })
                .match(OrderRequestClientServer.class, o -> {
                    System.out.println("Sending order request");
                    // todo search if in store
                    ActorRef actorRef1 = context().child("orderDatabaseManagingActor").get();
                    actorRef1.tell(new OrderRequestServerServant(o.getTitle()), getSender());
                })
                .match(StramingRequestClientServer.class, o -> {
                    System.out.println("Received streaming request for book " + o.getTitle());
                    context().actorOf(Props.create(StreamingActor.class), "streamingActor" + (++streamingActorsCount));
                    ActorRef actorRef = context().child("streamingActor" + streamingActorsCount).get();
                    actorRef.tell(o, getSender());

                })
                .matchAny(o -> {
                    log.info("received unknown message" + o.getClass());
                })
                .build();
    }

    private static SupervisorStrategy strategy
            = new OneForOneStrategy(10, Duration.create("1 minute"), DeciderBuilder.
                    match(IOException.class, o -> restart()).
                    build());

    @Override
    public void preStart() throws Exception {
        context().actorOf(Props.create(OrderActor.class), "orderDatabaseManagingActor");
    }
}
