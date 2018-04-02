package com.nyc.polymerse.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nyc.polymerse.Constants;
import com.nyc.polymerse.FilterUsersClass;
import com.nyc.polymerse.HomeActivity;
import com.nyc.polymerse.R;
import com.nyc.polymerse.User;
import com.nyc.polymerse.UserSingleton;
import com.nyc.polymerse.controller.UserResultAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserResultsFragment extends Fragment {


    private View rootView;
    private final String TAG = "UserResultsFragment";
    private ArrayList<User> userList = new ArrayList<>();
    private Button filter;
    private ProgressBar progressBar;

    private DatabaseReference mDatabase;

    private UserResultAdapter adapter;

    private User currentUser;

    public UserResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_user_results, container, false);
        ButterKnife.bind(this, rootView);
        progressBar = rootView.findViewById(R.id.results_progressbar);

        return rootView;

    }

    @OnClick(R.id.filter_button)
    public void showFilterFragment() {
        Toast.makeText(getActivity().getApplicationContext(), "CLICK", Toast.LENGTH_SHORT).show();
        UserResultsFilterFragment userResultsFilterFragment = new UserResultsFilterFragment();
        userResultsFilterFragment.setAdapter(adapter, userList);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.filter_container, userResultsFilterFragment).commit();
        FrameLayout layout = (FrameLayout) getActivity().findViewById(R.id.filter_container);
        layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "ON VIEW CREATED");

        currentUser = UserSingleton.getInstance().getUser();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mDatabaseUser = mDatabase.child(Constants.USERS);
        mDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "count " + dataSnapshot.getChildrenCount());
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    User user = d.getValue(User.class);
                    Map<String, String> blockedUser = new HashMap<>();
                    if (UserSingleton.getInstance().getUser().getBlocked() != null) {
                        blockedUser = UserSingleton.getInstance().getUser().getBlocked();
                    }
                    boolean userBlocked = false;
                    for (String s : blockedUser.values()) {
                        if (user != null) {
                            if (user.getuID().equals(s)) {
                                userBlocked = true;
                            }
                        }
                    }
                    if (!userBlocked && !user.getuID().equals(currentUser.getuID()) && !user.getUsername().isEmpty()) {
                        userList.add(user);
                        userBlocked = false;
                    }
                    Log.d(TAG, "onDataChange: user " + user.getUsername());

                    //This is the test user only;
                    Log.d(TAG, "onSuccess: " + user.getCity());
                }
                checkBlocked();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("The read failed: ", databaseError.getMessage());

            }


        });

    }

    private void checkBlocked() {

        mDatabase.child(Constants.BLOCKED_USER_KEY).child(currentUser.getuID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: dataSnapShot blocked " + dataSnapshot.getChildrenCount());
                Log.d(TAG, "onDataChange: userlistsize beginning blocked " + userList.size());

                for (DataSnapshot d: dataSnapshot.getChildren()) {
                        String s = d.getValue(String.class);
                        if (s != null) {
                            Log.d(TAG, "onDataChange: blockedId " + s);
                            for (User u : userList.toArray(new User[userList.size()])) {
                                //checking if the user blocked anyone and a case to remove the user himself from the list.
                                if (s.equals(u.getuID()) || UserSingleton.getInstance().getUser().getuID().equals(u.getuID())) {
                                    userList.remove(u);
                                }
                            }
                        }
                }
                Log.d(TAG, "onDataChange: userListSize end blocked " + userList.size());


                RecyclerView recyclerView = rootView.findViewById(R.id.user_results_rec_view);
                LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(manager);
                adapter = new UserResultAdapter(userList, rootView.getContext(), progressBar);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
