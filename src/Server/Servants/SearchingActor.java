package Server.Servants;

import MessageSrtuctures.SearchingRequestServerActor;
import MessageSrtuctures.SearchingResponseServantServer;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SearchingActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private BufferedReader bufferedReader;


    public SearchingActor() {
    }

    @Override
    public Receive createReceive() {

        return receiveBuilder()
                // TODO
                .match(SearchingRequestServerActor.class, s -> {
                    bufferedReader = new BufferedReader(new FileReader("src/database" + String.valueOf(s.getDatabaseNo()) + ".txt"));
                    float res = search(s.getTitleRequest().getTitle());
                    getSender().tell(new SearchingResponseServantServer(s.getRequesterId(), res),getSelf());
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }

    private float search(String title) throws IOException {

        String line = bufferedReader.readLine();

        while (line != null) {
            String[] splitted = line.split(":");
            if (splitted[0].equals(title))
                return Float.valueOf(splitted[1]);
            line=bufferedReader.readLine();
        }
        return 0.0f;
    }


}
