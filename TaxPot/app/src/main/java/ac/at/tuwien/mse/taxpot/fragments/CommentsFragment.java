package ac.at.tuwien.mse.taxpot.fragments;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

        binding.rlComments.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("TaxPot", "outside comments layout touched");
                mainActivity.getFragmentManager().popBackStack();
            }
        });

        final DatabaseReference myRef = mainActivity.getDatabase().getReference();

        mainActivity.getDatabase().getReference(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = dataSnapshot.getValue(Boolean.class);
                if(connected){

                    final RecyclerView commentSection = binding.commentSection;
                    commentAdapter = new CommentAdapter(Comment.class, R.layout.viewholder_comment, CommentViewHolder.class, myRef.child(taxPot.getId()).child("comments"), myRef.child(taxPot.getId()).child("comments"));
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    layoutManager.setStackFromEnd(true);
                    layoutManager.setReverseLayout(true);
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
                            commentSection.smoothScrollToPosition(commentAdapter.getItemCount());
                            binding.ownComment.setText("");
                        }
                    });

                } else {
                    Toast.makeText(mainActivity.getApplicationContext(),getString(R.string.internet_connection_lost), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.on_request_fail_message), Toast.LENGTH_LONG).show();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(commentAdapter != null) {
            commentAdapter.cleanup();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(commentAdapter != null) {
            commentAdapter.cleanup();
        }
    }
}
