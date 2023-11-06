package com.example.todolist;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG="AddNewTask";
    private TextView setDueDate;
    private EditText mTaskEdit;
    private Button mSavebtn;

    private Context context;
    private String id="";
    private String dueDate="";
    private ImageView speeching;
    private static final int RECOGNIZER_CODE=1;

    private FirebaseFirestore firestore;
    private String dueDateUpdate="";

    public static AddNewTask newInstance(){

        return new AddNewTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_new_task,container,false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setDueDate=view.findViewById(R.id.set_due_tv);
        mTaskEdit=view
                .findViewById(R.id.task_edittext);

        mSavebtn=view.findViewById(R.id.save_btn);
        speeching=view.findViewById(R.id.mic);
        //speeching.setOnClickListener(new View.OnClickListener() {
         //   @Override
         //   public void onClick(View view) {
          ///      Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
           //     intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);


             //   intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speek Up");
               // startActivityForResult(intent,RECOGNIZER_CODE);
            //}
        //});

        firestore=FirebaseFirestore.getInstance();

         boolean isupdate=false;
        final Bundle bundle=getArguments();
        if (bundle !=null){
            isupdate=true;
            String task=bundle.getString("task");
            id=bundle.getString("id");
            dueDateUpdate=bundle.getString("due");
            mTaskEdit.setText(task);
            setDueDate.setText(dueDateUpdate);
            if (task.length()>0){

                mSavebtn.setEnabled(false);
                mSavebtn.setBackgroundColor(Color.GRAY);
            }
        }

        mTaskEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.toString().equals("")){

                    mSavebtn.setEnabled(false);
                    mSavebtn.setBackgroundColor(Color.GRAY);

                }else {
                    mSavebtn.setEnabled(true);
                    mSavebtn.setBackgroundColor(getResources().getColor(R.color.green_blue));
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {



            }
        });

        setDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calender=Calendar.getInstance();
                int MONTH=calender.get(Calendar.MONTH);
                int YEAR=calender.get(Calendar.YEAR);
                int DAY=calender.get(Calendar.DATE);

                DatePickerDialog datePickerDialog=new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayofmonth) {

                        month=month + 1;
                        setDueDate.setText(dayofmonth +"/" + month + "/" + year);

                        dueDate=dayofmonth + "/" + month + "/" +year;



                    }
                },YEAR,MONTH,DAY);
                datePickerDialog.show();
            }
        });
        boolean finalIsUpdate=isupdate;

        mSavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String task =mTaskEdit.getText().toString();

                if (finalIsUpdate){
                    firestore.collection("task").document(id).update("task",task,"due,",dueDate);
                    Toast.makeText(context, "Task Updated", Toast.LENGTH_SHORT).show();


                }
                else {

                }
                if (task.isEmpty()){

                    Toast.makeText(context, "Empty Task not Allowed", Toast.LENGTH_SHORT).show();
                }else
                {

                    Map<String ,Object> taskMap=new HashMap<>();
                    taskMap.put("task",task);

                    taskMap.put("due",dueDate);
                    taskMap.put("status",0);
                    taskMap.put("time", FieldValue.serverTimestamp());
                    firestore.collection("task").add(taskMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()){

                                Toast.makeText(context, "Task Save", Toast.LENGTH_SHORT).show();
                            }else {

                                Toast.makeText(context,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context,e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                }

                dismiss();
            }
        });


    }

   // @Override
  //  public void onActivityResult(int requestCode, int resultCode, Intent data) {
       // super.onActivityResult(requestCode, resultCode, data);

      //  if (requestCode == RECOGNIZER_CODE && resultCode == RESULT_OK){

          //  ArrayList<String> taskText=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
          //  mTaskEdit.setText(taskText.get(0).toString());
      ///  }
    //}


    //@Override
    //public void onActivityResult(int requestCode, int resultCode, Intent data) {
      //  super.onActivityResult(requestCode, resultCode, data);
        //if (requestCode == RECOGNIZER_CODE && resultCode == RESULT_OK){

          //  ArrayList<String> taskText=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            //mTaskEdit.setText(taskText.get(0).toString());
        //}
   // }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        Activity activity=getActivity();

        if (activity instanceof ondialogcloselistener){

            ((ondialogcloselistener)activity).ondialogclose(dialog);
        }
    }
}
