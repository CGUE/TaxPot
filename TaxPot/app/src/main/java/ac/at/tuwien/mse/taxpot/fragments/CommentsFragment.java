package ac.at.tuwien.mse.taxpot.fragments;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import ac.at.tuwien.mse.taxpot.models.TaxPot;
import ac.at.tuwien.mse.taxpot.view.MapsActivity;

/**
 * Created by Aileen on 5/3/2017.
 */

public class CommentsFragment extends Fragment {
    private TaxPot taxPot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, final Bundle savedInstanceState) {
        final LayoutCommentsBinding binding = DataBindingUtil.inflate(inflater, R.layout.layout_comments, container, false);

        taxPot = (TaxPot) getArguments().get("taxpot");
        binding.setTaxpot(taxPot);

        final MapsActivity mainActivity = (MapsActivity) getActivity();
        if(mainActivity.getMyLocationButton() != null) {
            mainActivity.getMyLocationButton().hide();
        }

        final DatabaseReference myRef = mainActivity.getDatabase().getReference(taxPot.getId());

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

        binding.postCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(binding.ownComment == null) {
                    Log.d("TaxPot", "comment null");
                }

                if(binding.commentSection == null){
                    Log.d("TaxPot", "section null");
                }
                myRef.child("comment").setValue(binding.ownComment.getText().toString());
                Log.d("TaxPot", "Comment added:"+ binding.ownComment.getText().toString());
                binding.ownComment.setText("");
                //commentSection.addView(ownComment);

            }
        });

        return binding.getRoot();
    }
}
