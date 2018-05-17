package MessageSrtuctures;


import scala.Serializable;

public class SearchingResponseServantServer implements Serializable {
    private int idOfRequestSender;
    private float answer;

    public SearchingResponseServantServer(int idOfRequestSender, float answer) {
        this.idOfRequestSender = idOfRequestSender;
        this.answer = answer;
    }


    public int getIdOfRequestSender() {
        return idOfRequestSender;
    }

    public void setIdOfRequestSender(int idOfRequestSender) {
        this.idOfRequestSender = idOfRequestSender;
    }

    public float getAnswer() {
        return answer;
    }

    public void setAnswer(float answer) {
        this.answer = answer;
    }


}
