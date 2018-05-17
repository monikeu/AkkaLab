package MessageSrtuctures;


import scala.Serializable;

public class SearchingRequestClientServer implements Serializable {
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SearchingRequestClientServer(String title) {

        this.title = title;
    }
}
