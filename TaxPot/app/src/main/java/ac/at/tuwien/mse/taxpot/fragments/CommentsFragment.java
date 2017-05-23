package ac.at.tuwien.mse.taxpot.fragments;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import ac.at.tuwien.mse.taxpot.R;
import ac.at.tuwien.mse.taxpot.databinding.LayoutCommentsBinding;
import ac.at.tuwien.mse.taxpot.models.Comment;
import ac.at.tuwien.mse.taxpot.models.TaxPot;
import ac.at.tuwien.mse.taxpot.view.MapsActivity;

/**
 * Created by Aileen on 5/3/2017.
 */

public class CommentsFragment extends Fragment {

    private CommentAdapter commentAdapter;
    private TaxPot taxPot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, final Bundle savedInstanceState) {
        final LayoutCommentsBinding binding = DataBindingUtil.inflate(inflater, R.layout.layout_comments, container, false);

        taxPot = (TaxPot) getArguments().get("taxpot");
        binding.setTaxpot(taxPot);

        final MapsActivity mainActivity = (MapsActivity) getActivity();
        if(mainActivity.getmMap() != null){
            mainActivity.getmMap().getUiSettings().setAllGesturesEnabled(false);
        }
        if(mainActivity.getMyLocationButton() != null) {
            mainActivity.getMyLocationButton().hide();
        }

        final DatabaseReference myRef = mainActivity.getDatabase().getReference();
/*
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String comment = dataSnapshot.child("comment").getValue(String.class);
                binding.comment1.setText(comment);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

*/
        RecyclerView commentSection = binding.commentSection;
        commentAdapter = new CommentAdapter(Comment.class, R.layout.viewholder_comment, CommentViewHolder.class, myRef.child(taxPot.getId()).child("comments"), myRef.child(taxPot.getId()).child("comments"));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        commentSection.setLayoutManager(layoutManager);
        commentSection.setHasFixedSize(false);
        commentSection.setAdapter(commentAdapter);

        binding.postCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(binding.ownComment == null) {
                    Log.d("TaxPot", "comment null");
                }
                if(binding.ownComment.getText().toString().isEmpty()){
                    return;
                }
                myRef.child(taxPot.getId()).child("comments").push().setValue(new Comment("defaultuser", binding.ownComment.getText().toString()));
                binding.ownComment.setText("");

            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        commentAdapter.cleanup();
    }
}
