package com.saad.expensemanager;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saad.expensemanager.Model.Data;

import java.text.DateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class IncomeFragment extends Fragment {

    //Firebase..

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;

    //Recyclerview..
    RecyclerView recyclerView;

    //TextView..
    private TextView incomeTotalSum;

    //Update edit text..

    private EditText editAmount;
    private EditText editType;
    private EditText editNote;

    //button for update and delete..

    private Button btnUpdate;
    private Button btnDelete;

    //Data item value..

    private String type;
    private String note;
    private int amount;

    private String postKey;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView =inflater.inflate(R.layout.fragment_income, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeDatabase").child(uid);

        incomeTotalSum = myView.findViewById(R.id.income_txt_result);

        recyclerView = myView.findViewById(R.id.recycler_id_income);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);


        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalValue = 0;

                for (DataSnapshot mySnapshot: snapshot.getChildren()){

                    Data data = mySnapshot.getValue(Data.class);
                    totalValue+= data.getAmount();
                    
                    String stTotalSum = String.valueOf(totalValue);

                    incomeTotalSum.setText(stTotalSum + ".00");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return myView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(mIncomeDatabase, Data.class)
                        .build();

        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Data model) {

                holder.setAmount(model.getAmount());
                holder.setNote(model.getNote());
                holder.setDate(model.getDate());
                holder.setType(model.getType());

                holder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        postKey = getRef(position).getKey();

                        type = model.getType();
                        note = model.getNote();
                        amount = model.getAmount();

                        updateDataItem();

                    }
                });

            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.income_recycler_data,parent,false);
                return new MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    private static  class MyViewHolder extends RecyclerView.ViewHolder{

        View myView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myView = itemView;
        }

        private void setType(String type){
            TextView mType = myView.findViewById(R.id.type_txt_income);
            mType.setText(type);
        }

        private void setNote(String note){
            TextView mNote = myView.findViewById(R.id.note_txt_income);
            mNote.setText(note);
        }

        private  void setDate(String date){
            TextView mDate = myView.findViewById(R.id.date_txt_income);
            mDate.setText(date);
        }

        private void setAmount(int amount){
            TextView mAmount = myView.findViewById(R.id.amount_txt_income);
            String stAmount = String.valueOf(amount);
            mAmount.setText(stAmount);
        }
    }

    private void updateDataItem(){

        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
        View myView = LayoutInflater.from(getActivity())
                .inflate(R.layout.update_data_item,null);
        myDialog.setView(myView);

        editAmount = myView.findViewById(R.id.amount_edt);
        editType = myView.findViewById(R.id.type_edt);
        editNote = myView.findViewById(R.id.note_edt);


        //Set data to edit text..

        editType.setText(type);
        editType.setSelection(type.length());

        editNote.setText(note);
        //setSelection used to set the cursor at the end of the length
        editNote.setSelection(note.length());

        editAmount.setText(String.valueOf(amount));
        editAmount.setSelection(String.valueOf(amount).length());



        btnUpdate = myView.findViewById(R.id.btnUpd_update);
        btnDelete = myView.findViewById(R.id.btnUpd_Delete);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type = editType.getText().toString().trim();
                note = editNote.getText().toString().trim();

                String updAmount = String.valueOf(amount);
                updAmount = editAmount.getText().toString();

                int IntAmount = Integer.parseInt(updAmount);

                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(IntAmount,type,note,postKey,mDate);
                mIncomeDatabase.child(postKey).setValue(data);

                Toast.makeText(getActivity(), "Data Updated", Toast.LENGTH_SHORT).show();

                dialog.dismiss();

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mIncomeDatabase.child(postKey).removeValue();

                dialog.dismiss();

            }
        });

        dialog.show();
    }


}