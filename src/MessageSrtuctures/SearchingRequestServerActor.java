package MessageSrtuctures;


import scala.Serializable;

public class SearchingRequestServerActor implements Serializable {
    private int databaseNo;
    private int requesterId;
    private SearchingRequestClientServer titleRequest;

    public SearchingRequestServerActor(int databaseNo, int requesterId, SearchingRequestClientServer titleRequest) {
        this.databaseNo = databaseNo;
        this.requesterId = requesterId;
        this.titleRequest = titleRequest;
    }

    public int getDatabaseNo() {
        return databaseNo;
    }

    public void setDatabaseNo(int databaseNo) {
        this.databaseNo = databaseNo;
    }

    public int getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(int requesterId) {
        this.requesterId = requesterId;
    }

    public SearchingRequestClientServer getTitleRequest() {
        return titleRequest;
    }

    public void setTitleRequest(SearchingRequestClientServer titleRequest) {
        this.titleRequest = titleRequest;
    }
}
