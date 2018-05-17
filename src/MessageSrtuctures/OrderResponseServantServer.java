package MessageSrtuctures;

public class OrderResponseServantServer {
    private SimpleResponse isOk;
    private int requesterId;

    public OrderResponseServantServer(SimpleResponse isOk, int requesterId) {
        this.isOk = isOk;
        this.requesterId = requesterId;
    }

    public int getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(int requesterId) {
        this.requesterId = requesterId;
    }

    public SimpleResponse getIsOk() {
        return isOk;
    }

    public void setIsOk(SimpleResponse isOk) {
        this.isOk = isOk;
    }
}
