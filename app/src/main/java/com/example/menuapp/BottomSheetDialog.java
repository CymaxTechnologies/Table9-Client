package com.example.menuapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.w3c.dom.Text;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    TextView e;
    UserProfile u;
    char c;
    BottomSheetDialog(TextView e, UserProfile u,char c)
    {
        this.e=e;
        this.u=u;
        this.c=c;
    }
    BottomSheetDialog()
    {

    }
    EditText changed;
    Button accept;
    Button reject;
  static   public String value="";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottomsheet,
                container, false);
        changed=v.findViewById(R.id.changed_value);
        accept=v.findViewById(R.id.changedaccept);
        reject=v.findViewById(R.id.changereject);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                value=changed.getText().toString();
                if(c=='e')
                {
                    e.setText(value);
                    u.setEmail(value);
                }
                if(c=='n')
                {
                    e.setText(value);
                    u.setName(value);
                }
                if(c=='p')
                {
                    e.setText(value);
                    u.setPhone(value);
                }
                dismiss();
            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                value="";
                dismiss();
            }
        });
        return v;
    }
}
