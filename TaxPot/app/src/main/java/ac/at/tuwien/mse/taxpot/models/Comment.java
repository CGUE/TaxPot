package ac.at.tuwien.mse.taxpot.models;

/**
 * Created by markj on 5/23/2017.
 */

public class Comment {

    private String user;
    private String comment;

    private Comment(){}

    public Comment(String user, String comment){
        this.user = user;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
