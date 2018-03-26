package com.example.dipuj.smartbill.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.dipuj.smartbill.R;
import com.example.dipuj.smartbill.adapter.ReadingExpandableListAdapter;
import com.example.dipuj.smartbill.modal.Reading;
import com.example.dipuj.smartbill.utility.Constant;
import com.example.dipuj.smartbill.utility.Pref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jpardogo.android.googleprogressbar.library.FoldingCirclesDrawable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UsageFragment extends Fragment {

    private final String TAG = "UsageFragment";
    private FirebaseFirestore dataBase;

    private Spinner mSpinnerYear;
    private Spinner mSpinnerMonth;
    private ProgressBar mProgressBar;

    private ArrayList<Map<String,Object>> reading = new ArrayList<>();

    private ExpandableListView mExpandableListViewReading;
    private ReadingExpandableListAdapter mReadingExpandableListAdapter;
    private ArrayList<String> headerList;
    private HashMap<String, ArrayList<Reading>> childList;

    int index = 0;

    private Context context;

    public UsageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeFirebaseFirestore();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_usage, container, false);
        initializeProgressBar(view);
        initializeYearSpinner(view);
        initializeMonthSpinner(view);
        initializeExpandableListView(view);
        retrieveFireStoreData(getPath());

        //Log.i(TAG, "Fetched Data : " + reading.toString());
        return view;
    }

    private void initializeExpandableListView(View view){
        mExpandableListViewReading = view.findViewById(R.id.expandable_reading);
    }

    private void loadData(){

        for (int i=0; i<reading.size(); i++)
        {
            if(index == i){
                Log.e(TAG, "Index taken : " + index);
                Map<String,Object> map;
                map = reading.get(i);
                headerList = new ArrayList<>(map.keySet());
                childList = new HashMap<>();
                Object[] arr = map.values().toArray();
                ArrayList<Object> readingObList;
                for(int j=0; j<headerList.size(); j++){
                    readingObList=(ArrayList<Object>)arr[j];
                    ArrayList<Reading> readingList=new ArrayList<>();
                        for(Object ob:readingObList){
                            Reading reading=new Reading();
                            HashMap<String,Object>  hashMap=(HashMap<String,Object>) ob;
                            reading.setReading((Long) hashMap.get(Constant.KEY_READING));
                            reading.setTimestamp((Date) hashMap.get(Constant.KEY_TIME_STUMP));
                            Log.e(TAG,"date : " + reading.getTimestamp().toString());
                            readingList.add(reading);
                        }
                        childList.put(headerList.get(j), readingList);
                 }
            }
        }

        mReadingExpandableListAdapter = new ReadingExpandableListAdapter(
                context,headerList,childList);

        mExpandableListViewReading.setAdapter(mReadingExpandableListAdapter);
        mReadingExpandableListAdapter.notifyDataSetChanged();

    }

    private void initializeProgressBar(View view){
        mProgressBar = view.findViewById(R.id.google_progress);
        mProgressBar.setIndeterminateDrawable(new FoldingCirclesDrawable.Builder(context)
                .build());
    }

    private void initializeYearSpinner(View view) {
        ArrayList<String> years = new ArrayList();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 2015; i <= thisYear; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, years);

        mSpinnerYear = view.findViewById(R.id.spinner_year);
        mSpinnerYear.setAdapter(adapter);
        mSpinnerYear.setSelection(years.size() - 1);
    }

    private void initializeMonthSpinner(View view) {
        ArrayList<String> month = new ArrayList();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, Constant.months);

        mSpinnerMonth = view.findViewById(R.id.spinner_month);
        mSpinnerMonth.setAdapter(adapter);
        mSpinnerMonth.setSelection(0);

        mSpinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if(position != 0)
                {
                    index = position;
                    loadData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
                //nothing to do.
            }
        });
    }

    private void initializeFirebaseFirestore() {

        dataBase = FirebaseFirestore.getInstance();
    }

    private void retrieveFireStoreData(final String path){

        Log.e(TAG,"index : " + index);

        mProgressBar.bringToFront();
        mProgressBar.setVisibility(View.VISIBLE);

        DocumentReference documentReference = dataBase
                .collection(path)
                .document(Constant.months[index]);

        documentReference.get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                Map<String, Object> map = doc.getData();
                                reading.add(map);
                            }else {
                                reading.add(null);
                            }
                        }
                        index++;

                        if(index < 12){
                            retrieveFireStoreData(path);
                        }else{
                            index = 0;
                            loadData();
                            mProgressBar.setVisibility(View.INVISIBLE);
                            Log.e(TAG,reading.toString());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private String getPath() {

        String year = (mSpinnerYear != null && mSpinnerYear.getSelectedItem().toString() != null)
                ? mSpinnerYear.getSelectedItem().toString() : "2018";

        String path = "meterDetails/" +
                Pref.getValue(context, Constant.KEY_METER_ID, "",
                        Constant.PREF_NAME) + "/" + year;

        Log.d(TAG, "path : " + path);

        return path;
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

    /*@RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void retrieveFireBaseData(final String path) {
        for (final String month : Constant.months) {
            DocumentReference documentReference = dataBase
                    .collection(path)
                    .document(month);
            documentReference.get().addOnCompleteListener(
                    new OnCompleteListener<DocumentSnapshot>() {

                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot doc = task.getResult();
                                if (doc.exists()) {
                                    monthData = new ArrayList<>();
                                    for (String week : Constant.weeks) {
                                        weekData = new ArrayList<>();
                                        for (int i = 1; i <= 7; i++) {
                                            DocumentReference innerDocumentReference =
                                                    dataBase
                                                    .collection(path)
                                                    .document(month)
                                                    .collection(week)
                                                    .document(i + "");

                                            final int finalI = i;
                                            innerDocumentReference.get().
                                                    addOnCompleteListener(
                                                    new OnCompleteListener<DocumentSnapshot>()
                                                    {

                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> innerTask) {
                                                            if (innerTask.isSuccessful()) {
                                                                DocumentSnapshot innerDoc = innerTask.getResult();
                                                                if (innerDoc.exists()) {
                                                                    Reading innerreading = new Reading(
                                                                            innerDoc.get(Constant.KEY_READING),
                                                                            innerDoc.get(Constant.KEY_TIME_STUMP));
                                                                    weekData.add(innerreading);
                                                                }
                                                                if(Objects.equals(month, "march") && finalI == 7){
                                                                    Log.e(TAG,"DATA : " + reading.toString()+" "+reading.size());
                                                                }else {
                                                                    Log.e(TAG,month);
                                                                }
                                                            }
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });
                                        }
                                    }
                                    monthData.add(weekData);
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
            reading.add(monthData);
        }
    }*/
}
