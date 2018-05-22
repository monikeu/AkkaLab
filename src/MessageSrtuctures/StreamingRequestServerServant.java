package MessageSrtuctures;

import akka.stream.ActorMaterializer;
import akka.stream.Materializer;

import java.io.Serializable;

public class StreamingRequestServerServant implements Serializable{
    private StramingRequestClientServer stramingRequestClientServer;
    private Materializer materializer;

    public StreamingRequestServerServant(StramingRequestClientServer stramingRequestClientServer, Materializer materializer) {
        this.stramingRequestClientServer = stramingRequestClientServer;
        this.materializer = materializer;
    }

    public Materializer getMaterializer() {
        return materializer;
    }

    public void setMaterializer(ActorMaterializer materializer) {
        this.materializer = materializer;
    }

    public void setStramingRequestClientServer(StramingRequestClientServer stramingRequestClientServer) {
        this.stramingRequestClientServer = stramingRequestClientServer;
    }

    public StramingRequestClientServer getStramingRequestClientServer() {
        return stramingRequestClientServer;
    }
}
