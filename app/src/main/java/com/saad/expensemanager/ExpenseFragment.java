package com.saad.expensemanager;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
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
public class ExpenseFragment extends Fragment {

    //Firebase..
    FirebaseAuth mAuth;
    DatabaseReference mExpenseDatabase;

    //Recyclerview..

    RecyclerView recyclerView;

    //TextView..

    TextView expenseSumResult;

    //Update EditText..

    private EditText editAmount;
    private EditText editType;
    private EditText editNote;

    private Button btnUpdate;
    private Button btnDelete;

    //Data Variable

    private String type;
    private String note;
    private int amount;

    private String postKey;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View myView =inflater.inflate(R.layout.fragment_expense, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);

        recyclerView = myView.findViewById(R.id.recycler_id_expense);

        expenseSumResult = myView.findViewById(R.id.expense_txt_result);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int expenseSum = 0;

                for (DataSnapshot mySnapshot : snapshot.getChildren()){

                    Data data = mySnapshot.getValue(Data.class);

                    expenseSum += data.getAmount();

                    String strExpenseSum = String.valueOf(expenseSum);

                    expenseSumResult.setText(strExpenseSum);

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
                        .setQuery(mExpenseDatabase, Data.class)
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
                        .inflate(R.layout.expense_recycler_data,parent,false);
                return new MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder{

        View myView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            myView = itemView;
        }

        private void  setAmount(int amount){
            TextView mAmount = myView.findViewById(R.id.amount_txt_expense);
            String stAmount = String.valueOf(amount);
            mAmount.setText(stAmount);
        }

        private void setType(String type){
            TextView mType = myView.findViewById(R.id.type_txt_expense);
            mType.setText(type);
        }

        private void setNote(String note){
            TextView mNote = myView.findViewById(R.id.note_txt_expense);
            mNote.setText(note);
        }

        private void setDate(String date){
            TextView mDate= myView.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }
    }

    private void updateDataItem(){

        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());

        View myView = LayoutInflater.from(getActivity())
                .inflate(R.layout.update_data_item,null);
        myDialog.setView(myView);

        editAmount = myView.findViewById(R.id.amount_edt);
        editNote = myView.findViewById(R.id.note_edt);
        editType = myView.findViewById(R.id.type_edt);

        editType.setText(type);
        editType.setSelection(type.length());

        editType.setText(note);
        editType.setSelection(note.length());

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
                String stAmount = String.valueOf(amount);
                stAmount = editAmount.getText().toString().trim();
                int intAmount = Integer.parseInt(stAmount);

                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(intAmount,type,note,postKey,mDate);
                mExpenseDatabase.child(postKey).setValue(data);

                Toast.makeText(getActivity(), "Data Updated", Toast.LENGTH_SHORT).show();

                dialog.dismiss();

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpenseDatabase.child(postKey).removeValue();

                dialog.dismiss();

            }
        });
        dialog.show();

    }
}