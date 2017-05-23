package ac.at.tuwien.mse.taxpot.fragments;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import ac.at.tuwien.mse.taxpot.R;

/**
 * Created by markj on 5/23/2017.
 */

public class CommentViewHolder extends RecyclerView.ViewHolder {

    public TextView commentBox;

    public CommentViewHolder(View itemView) {
        super(itemView);
        this.commentBox = (TextView) itemView.findViewById(R.id.comment);
    }
}
