package socket;

import java.io.Serializable;

public class Message implements Serializable {

    private String title;
    private Object object;

    public Message(String title, Object object) {
        this.title = title;
        this.object = object;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
