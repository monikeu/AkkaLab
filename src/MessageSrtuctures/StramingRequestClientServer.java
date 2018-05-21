package MessageSrtuctures;

import java.io.Serializable;

public class StramingRequestClientServer implements Serializable{
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    public StramingRequestClientServer(String title) {
        this.title = title;
    }
}
