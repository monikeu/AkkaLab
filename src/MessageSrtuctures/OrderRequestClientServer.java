package MessageSrtuctures;

import scala.Serializable;

public class OrderRequestClientServer implements Serializable{
    private String title;

    public OrderRequestClientServer(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
