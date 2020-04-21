package com.example.bookofbooks;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bookofbooks.Adapters.NotificationsAdapter;
import com.example.bookofbooks.Interface.NotificationClickListener;
import com.example.bookofbooks.Models.Notification;
import com.example.bookofbooks.Utility.UsersInfo;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class NotificationsFragment extends Fragment implements NotificationClickListener {

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Query query;
    private FirestorePagingOptions<Notification> options;
    private NotificationsAdapter notificationsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notifications_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.notifications_recyclerView);
        swipeRefreshLayout = view.findViewById(R.id.notifications_swipe_refresh);
        firebaseFirestore = FirebaseFirestore.getInstance();

        setAdapter();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                notificationsAdapter.stopListening();
                setAdapter();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void setAdapter(){
        query = firebaseFirestore.collection("users").document(UsersInfo.getUserID())
                .collection("notifications").orderBy("timestamp", Query.Direction.DESCENDING);

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(5)
                .build();

        options = new FirestorePagingOptions.Builder<Notification>()
                .setLifecycleOwner(this)
                .setQuery(query, config, new SnapshotParser<Notification>() {
                    @NonNull
                    @Override
                    public Notification parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        Notification notification = snapshot.toObject(Notification.class);
                        return notification;
                    }
                })
                .build();
        notificationsAdapter = new NotificationsAdapter(options, this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(notificationsAdapter);
        notificationsAdapter.startListening();

    }

    @Override
    public void onItemClick(Notification notification) {
        Intent intent = new Intent(getContext(), PostDetails.class);
        intent.putExtra("postID", notification.getPostID());
        startActivity(intent);
    }
}
