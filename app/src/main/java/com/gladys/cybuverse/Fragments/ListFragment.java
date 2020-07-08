package com.gladys.cybuverse.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gladys.cybuverse.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListFragment<T> extends Fragment {

    private FirestoreRecyclerOptions<T> mFirestoreRecyclerOptions;
    private OnPrepareViewHolder<T> mOnPrepareViewHolder;
    private RecyclerView.Adapter mAdapter;
    private List<T> mDataSet;
    private int mColumnCount = 1;
    private int mResourceID;
    private View view;

    public ListFragment(int resourceID, int columnCount, List<T> data, OnPrepareViewHolder<T> prepareViewHolder) {
        mResourceID = resourceID;
        mOnPrepareViewHolder = prepareViewHolder;
        mColumnCount = columnCount;
        mDataSet = data;
        startAdapterForDataSet();
    }

    public ListFragment(int resourceID, List<T> data, OnPrepareViewHolder<T> prepareViewHolder) {
        mResourceID = resourceID;
        mOnPrepareViewHolder = prepareViewHolder;
        mDataSet = data;
        startAdapterForDataSet();
    }

    public ListFragment(int resourceID, int columnCount, FirestoreRecyclerOptions<T> options, OnPrepareViewHolder<T> prepareViewHolder) {
        mResourceID = resourceID;
        mOnPrepareViewHolder = prepareViewHolder;
        mColumnCount = columnCount;
        mFirestoreRecyclerOptions = options;
        startAdapterForQuery();
    }

    public ListFragment(int resourceID, FirestoreRecyclerOptions<T> options, OnPrepareViewHolder<T> prepareViewHolder) {
        mResourceID = resourceID;
        mOnPrepareViewHolder = prepareViewHolder;
        mFirestoreRecyclerOptions = options;
        startAdapterForQuery();
    }

    public ListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            recyclerView.setAdapter(mAdapter);

            if (mFirestoreRecyclerOptions != null) {
                ((FirestoreRecyclerAdapter) mAdapter).startListening();
            }
        }
        return view;
    }

    private void startAdapterForDataSet() {
        mAdapter = new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(mResourceID, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                if (mOnPrepareViewHolder != null)
                    mOnPrepareViewHolder.onPrepare(holder, getDataSet().get(position), position);
            }

            @Override
            public int getItemCount() {
                return getDataSet().size();
            }
        };
    }

    private void startAdapterForQuery() {
        mAdapter = new FirestoreRecyclerAdapter<T, RecyclerView.ViewHolder>(mFirestoreRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i, @NonNull T t) {
                if (mOnPrepareViewHolder != null)
                    mOnPrepareViewHolder.onPrepare(holder, t, i);
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(mResourceID, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            }
        };
    }

    private View findViewById(int id) {
        return getView().findViewById(id);
    }

    public FirestoreRecyclerOptions<T> getFirestoreRecyclerOptions() {
        return mFirestoreRecyclerOptions;
    }

    public void setFirestoreRecyclerOptions(FirestoreRecyclerOptions<T> mFirestoreRecyclerOptions) {
        this.mFirestoreRecyclerOptions = mFirestoreRecyclerOptions;
    }

    public OnPrepareViewHolder<T> getOnPrepareViewHolder() {
        return mOnPrepareViewHolder;
    }

    public void setOnPrepareViewHolder(OnPrepareViewHolder<T> mOnPrepareViewHolder) {
        this.mOnPrepareViewHolder = mOnPrepareViewHolder;
    }

    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(RecyclerView.Adapter mAdapter) {
        this.mAdapter = mAdapter;
        if (getView() != null && view instanceof RecyclerView) {
            ((RecyclerView) getView()).setAdapter(mAdapter);
            mAdapter.notify();
        }
    }

    public List<T> getDataSet() {
        return mDataSet;
    }

    public void setDataSet(List<T> mDataSet) {
        this.mDataSet = mDataSet;
        startAdapterForDataSet();
        setAdapter(mAdapter);
    }

    public int getColumnCount() {
        return mColumnCount;
    }

    public void setColumnCount(int mColumnCount) {
        this.mColumnCount = mColumnCount;
    }

    public int getResourceID() {
        return mResourceID;
    }

    public void setResourceID(int mResourceID) {
        this.mResourceID = mResourceID;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPrepareViewHolder) {
            mOnPrepareViewHolder = (OnPrepareViewHolder) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnPrepareViewHolder = null;
    }

    @Nullable
    @Override
    public View getView() {
        return view;
    }

    @Override
    public void onResume() {
        if (mAdapter instanceof FirestoreRecyclerAdapter)
            ((FirestoreRecyclerAdapter) mAdapter).startListening();
        super.onResume();
    }

    @Override
    public void onPause() {
//        if (mAdapter instanceof FirestoreRecyclerAdapter)
//            ((FirestoreRecyclerAdapter) mAdapter).stopListening();
        super.onPause();
    }

    @Override
    public void onStop() {
        if (mAdapter instanceof FirestoreRecyclerAdapter)
            ((FirestoreRecyclerAdapter) mAdapter).stopListening();
        super.onStop();
    }
}
