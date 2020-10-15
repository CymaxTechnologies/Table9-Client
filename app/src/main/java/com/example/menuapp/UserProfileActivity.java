package com.example.menuapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.UUID;

public class UserProfileActivity extends AppCompatActivity  {
    ImageView pic,emailc,phonec,namec;
    TextView name,email,phone;
    UserProfile userProfile=new UserProfile();
    Button apply;
    String value="";
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().hide();
        progressDialog=new ProgressDialog(UserProfileActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait....");
        progressDialog.setTitle("T9  App");
        progressDialog.show();
        pic=(ImageView)findViewById(R.id.profileimage);
        emailc=(ImageView)findViewById(R.id.profile_email_change);
        phonec=(ImageView)findViewById(R.id.profile_mobile_change);
        namec=(ImageView)findViewById(R.id.profile_name_change);
        name=(TextView)findViewById(R.id.profile_name);
        email=(TextView)findViewById(R.id.profile_email);
        phone=(TextView)findViewById(R.id.profile_mobile);
        apply=(Button)findViewById(R.id.profile_apply_changes) ;
        DatabaseReference dr=FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("profile");
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists())
               {
                   userProfile=dataSnapshot.getValue(UserProfile.class);
                   name.setText(userProfile.getName());
                   email.setText(userProfile.email);
                   phone.setText(userProfile.phone);
                   if(!userProfile.picture.equals(""))
                   {
                       Glide.with(UserProfileActivity.this)
                               .load(userProfile.picture)
                               .circleCrop()
                               .into(pic);
                   }

               } progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

  emailc.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          Toast.makeText(getApplicationContext(),"You can not change your email",Toast.LENGTH_LONG).show();
         /* BottomSheetDialog dialog=new BottomSheetDialog( email,userProfile,'e');
          dialog.show(getSupportFragmentManager(),"Dialog");
          String s=dialog.value;

          if(!s.equals(""))
          {
              email.setText(s);
              userProfile.setEmail(s);
          }*/
      }
  });
        phonec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog dialog=new BottomSheetDialog(phone,userProfile,'p');
                dialog.show(getSupportFragmentManager(),"Dialog");
                String s=dialog.value;
                if(!s.equals(""))
                {
                    phone.setText(s);
                    userProfile.setPhone(s);
                }
            }
        });
        namec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog dialog=new BottomSheetDialog(name,userProfile,'n');
                dialog.show(getSupportFragmentManager(),"Dialog");
                String s=dialog.value;
                if(!s.equals(""))
                {
                    name.setText(s);
                    userProfile.setName(s);
                }
            }
        });
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
             //   uploadImage();
            }
        });

      apply.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(final View v) {
              DatabaseReference dr=FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("profile");
              dr.setValue(userProfile).addOnSuccessListener(new OnSuccessListener<Void>() {
                  @Override
                  public void onSuccess(Void aVoid) {
                      Snackbar.make(v,"Profile Updated Succesfuly",Snackbar.LENGTH_LONG).show();
                      FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                      UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                              .setDisplayName(userProfile.name).build();

                      user.updateProfile(profileUpdates);
                                    }
              }).addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                      Snackbar.make(v,"An Error occured",Snackbar.LENGTH_LONG).show();

                  }
              });
          }
      });















    }
    private void SelectImage()
    {


        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                1);
    }
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        if (requestCode == 1
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {
            userProfile.picture = data.getData().toString();
            uploadImage();

        }
    }
    private void uploadImage()
    {
        if (userProfile.picture != null) {

            final ProgressDialog progressDialog
                    = new ProgressDialog(UserProfileActivity.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();


            final StorageReference ref
                    =
                    FirebaseStorage.getInstance().getReference().child(
                            "images/"
                                    + UUID.randomUUID().toString());


            ref.putFile(Uri.parse(userProfile.picture))
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(UserProfileActivity.this,
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();

                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            userProfile.picture=uri.toString();
                                            if(!userProfile.picture.equals(""))
                                            {
                                                Glide.with(UserProfileActivity.this)
                                                        .load(userProfile.picture)
                                                        .circleCrop()
                                                        .into(pic);
                                            }

                                        }
                                    });
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            progressDialog.dismiss();
                            Toast
                                    .makeText(UserProfileActivity.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
        }
    }
}