package com.gladys.cybuverse.Adapters;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public abstract class FirebaseAdapter extends RecyclerView.Adapter {

    private final Query mQuery;

    private List<DocumentSnapshot> mDocumentSnapshots;


    public FirebaseAdapter(Query query) {
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    mDocumentSnapshots = task.getResult().getDocuments();
                }
            }
        });
        this.mQuery = query;
    }

    public List<DocumentSnapshot> getDocumentSnapshots() {
        return mDocumentSnapshots;
    }

    public Query getQuery() {
        return mQuery;
    }

    public DocumentSnapshot getItem(int index) {
        if (index < getDocumentSnapshots().size() && index > -1)
            return getDocumentSnapshots().get(index);
        return null;
    }

    @Override
    public int getItemCount() {
        return getDocumentSnapshots().size();
    }

}
