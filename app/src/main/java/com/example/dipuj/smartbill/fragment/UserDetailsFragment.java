package com.example.dipuj.smartbill.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dipuj.smartbill.R;
import com.example.dipuj.smartbill.activity.UserActivity;
import com.example.dipuj.smartbill.modal.User;
import com.example.dipuj.smartbill.utility.Constant;
import com.example.dipuj.smartbill.utility.Pref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserDetailsFragment extends Fragment {

    private final String TAG = "UserDetailsFragment";
    FirebaseFirestore dataBase;
    private User user;
    private TextView mTextViewName;
    private TextView mTextViewEmail;
    private TextView mTextViewMobileNumber;
    private TextView mTextViewAddress;
    private TextView mTextViewMeterId;

    private Context context;

    public UserDetailsFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_details, container, false);
        initializeView(view);
        user = ((UserActivity) getActivity()).getUser();
        updateView();
        return view;
    }

    private void initializeView(View view) {
        mTextViewName = view.findViewById(R.id.txt_name);
        mTextViewEmail = view.findViewById(R.id.txt_email);
        mTextViewMobileNumber = view.findViewById(R.id.txt_mobile_number);
        mTextViewAddress = view.findViewById(R.id.txt_address);
        mTextViewMeterId = view.findViewById(R.id.txt_meter_id);
    }

    private void updateView(){
        mTextViewName.setText(user.getName());
        mTextViewMeterId.setText(user.getMeterId());
        mTextViewEmail.setText(user.getEmail());
        mTextViewMobileNumber.setText(user.getMobileNo());
        mTextViewAddress.setText(user.getAddress());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
