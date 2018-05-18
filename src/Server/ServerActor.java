package Server;

import MessageSrtuctures.*;
import Server.Servants.OrderActor;
import MessageSrtuctures.OrderRequestServerServant;
import Server.Servants.SearchingActor;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.util.*;

public class ServerActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private int searchingActorCount = 0;

    private Map<Integer, ActorRef> clientsWaitingForSearchResponse = new HashMap<>();
    private Map<Integer, ActorRef> clientsWaitingForOrderResponse = new HashMap<>();
    private Map<Integer, Set<ActorRef>> searchingActorsServingRequest = new HashMap<>();
    private Map<Integer, Boolean> resultWasZeroBefore = new HashMap<>();
    private Map<Integer, Boolean> secondMessage = new HashMap<>();

    private int searchRequestId = 0;
    private int orderRequestId = 0;


    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
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
                        if(!secondMessage.get(idOfRequestSender)) {

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
                    // todo search if in store
                    ActorRef actorRef1 = context().child("orderDatabaseManagingActor").get();
                    actorRef1.tell(new OrderRequestServerServant(o.getTitle(), ++orderRequestId), getSelf());
                    clientsWaitingForOrderResponse.put(orderRequestId, getSender());
                })
                .match(OrderResponseServantServer.class, o -> {
                    ActorRef actorRef = clientsWaitingForOrderResponse.remove(o.getRequesterId());
                    actorRef.tell(SimpleResponse.OK, getSelf());
                })
                .matchAny(o -> {
                    log.info("received unknown message" + o.getClass());
                })
                .build();
    }


    @Override
    public void preStart() throws Exception {
        context().actorOf(Props.create(OrderActor.class), "orderDatabaseManagingActor");
    }
}
