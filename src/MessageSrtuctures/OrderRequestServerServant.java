package MessageSrtuctures;

import akka.actor.ActorRef;

public class OrderRequestServerServant {
    private String book;

    public OrderRequestServerServant(String book ) {
        this.book = book;
    }


    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

}
