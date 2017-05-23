package ac.at.tuwien.mse.taxpot.fragments;

import android.support.annotation.LayoutRes;
import android.util.Log;

import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.google.firebase.database.Query;

import ac.at.tuwien.mse.taxpot.models.Comment;

/**
 * Created by markj on 5/23/2017.
 */

public class CommentAdapter extends FirebaseIndexRecyclerAdapter<Comment, CommentViewHolder> {


    /**
     * @param modelClass      Firebase will marshall the data at a location into an instance
     *                        of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list.
     *                        You will be responsible for populating an
     *                        instance of the corresponding view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param keyRef          The Firebase location containing the list of keys to be found in {@code dataRef}.
     *                        Can also be a slice of a location, using some
     *                        combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     * @param dataRef         The Firebase location to watch for data changes.
     *                        Each key key found at {@code keyRef}'s location represents
     *                        a list item in the {@code RecyclerView}.
     */
    public CommentAdapter(Class<Comment> modelClass, @LayoutRes int modelLayout, Class<CommentViewHolder> viewHolderClass, Query keyRef, Query dataRef) {
        super(modelClass, modelLayout, viewHolderClass, keyRef, dataRef);
    }

    @Override
    protected void populateViewHolder(CommentViewHolder viewHolder, Comment model, int position) {
        viewHolder.commentBox.setText(model.getComment());
    }
}
