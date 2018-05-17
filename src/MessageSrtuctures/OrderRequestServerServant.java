package MessageSrtuctures;

public class OrderRequestServerServant {
    private String book;
    private int orderRequestId;

    public OrderRequestServerServant(String book, int orderRequestId) {
        this.book = book;
        this.orderRequestId = orderRequestId;
    }

    public int getOrderRequestId() {
        return orderRequestId;
    }

    public void setOrderRequestId(int orderRequestId) {
        this.orderRequestId = orderRequestId;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }
}
