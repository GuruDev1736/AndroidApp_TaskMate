package com.example.taskmate.Activities;

import static com.example.taskmate.Activities.Constants.ErrorToast;
import static com.example.taskmate.Activities.Constants.SuccessToast;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.taskmate.Adapter.AttachmentAdapter;
import com.example.taskmate.Model.TaskModel;
import com.example.taskmate.R;
import com.example.taskmate.databinding.ActivityNotesAttachmentBinding;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;

public class Notes_Attachment extends AppCompatActivity {

    ActivityNotesAttachmentBinding binding ;
    FirebaseDatabase database ;
    DatabaseReference reference;
    FirebaseAuth auth ;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference("Images");
    String fileName = "image_" + System.currentTimeMillis() + ".png";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    AttachmentAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = ActivityNotesAttachmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());




        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Tasks");
        auth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        String key = intent.getStringExtra("key");


        ProgressDialog progressDialog = Constants.progressDialog(this,"Saving","Please Wait....");

        getdata(auth.getCurrentUser().getUid(),key);

        ProgressDialog attachmentdailog = Constants.progressDialog(Notes_Attachment.this,"Fetching Attachments" ,"Please Wait...");
        attachmentdailog.show();
        binding.attachmentRec.setLayoutManager(new LinearLayoutManager(Notes_Attachment.this));
        FirebaseRecyclerOptions<TaskModel> options = new FirebaseRecyclerOptions.Builder<TaskModel>().setQuery(FirebaseDatabase.getInstance().
                getReference("Tasks").child(auth.getCurrentUser().getUid()).child(key).child("Attachments"), TaskModel.class).build();
        adapter = new AttachmentAdapter(options)
        {
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                attachmentdailog.dismiss();
            }

            @Override
            public void onError(@NonNull DatabaseError error) {
                super.onError(error);
                Constants.ErrorToast(Notes_Attachment.this,"Error : "+error.getMessage());
            }
        };
        adapter.startListening();
        binding.attachmentRec.setAdapter(adapter);



        binding.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(Notes_Attachment.this).withPermissions(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .withListener(new MultiplePermissionsListener() {
                                    @Override
                                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                        take_picture();
                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                        permissionToken.continuePermissionRequest();
                                    }
                                }).check();

            }
        });

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String note = binding.etNotes.getText().toString();

                if (TextUtils.isEmpty(note))
                {
                    binding.etNotes.setError("Enter the Note Please");
                    return;
                }

                progressDialog.show();

                HashMap<String, Object> data = new HashMap<>();
                data.put("Note",note);

                reference.child(auth.getCurrentUser().getUid()).child(key).updateChildren(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        SuccessToast(getApplicationContext(),"Note Has Been Saved Successfully");
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ErrorToast(getApplicationContext(),"Failed To Save Note : "+e.getMessage());
                        progressDialog.dismiss();
                    }
                });


            }
        });

        binding.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Notes_Attachment.this,ShowFullImage.class).putExtra("key",key));
            }
        });

        binding.selectfromgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 101);
            }
        });

        binding.browseAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                browse_attachment();
            }
        });



    }

    private void browse_attachment() {
        Dexter.withContext(Notes_Attachment.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
             Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
             intent.setType("application/pdf");
             startActivityForResult(intent,102);
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                ErrorToast(Notes_Attachment.this,"Permission is necessary");
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    private void take_picture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager())!=null)
        {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ProgressDialog progressDialog = Constants.progressDialog(Notes_Attachment.this,"Uploading Image","Please Wait...");
        StorageReference imageRef = storageRef.child(auth.getCurrentUser().getUid()).child(fileName);
        Intent intent = getIntent();
        String key = intent.getStringExtra("key");

        if (requestCode==102 && resultCode==RESULT_OK && data!=null)
        {
            Uri doc = data.getData();
            upload_document(doc);
        }


        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            progressDialog.show();


            Uri imageUri = data.getData();
            UploadTask uploadTask = imageRef.putFile(imageUri);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Image upload successful
                    // Retrieve the download URL of the uploaded image
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            // Handle the download URL
                            String imageUrl = downloadUri.toString();
                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("Image_URL",imageUrl);
                            reference.child(auth.getCurrentUser().getUid()).child(key).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Constants.SuccessToast(getApplicationContext(),"Image Uploaded Successfully");
                                    progressDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    ErrorToast(getApplicationContext(),"Error : "+e.getMessage());
                                    progressDialog.dismiss();
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle any errors that occurred during getting the download URL
                            ErrorToast(getApplicationContext(),"Error : "+e.getMessage());
                            progressDialog.dismiss();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle any errors that occurred during the image upload
                    ErrorToast(getApplicationContext(),"Error : "+e.getMessage());
                    progressDialog.dismiss();
                }
            });
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data!=null) {
            progressDialog.show();

            // Image captured, display it in the ImageView
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] imageData = baos.toByteArray();

            UploadTask uploadTask = imageRef.putBytes(imageData);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Image upload successful
                    // Retrieve the download URL of the uploaded image
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            // Handle the download URL
                            String imageUrl = downloadUri.toString();
                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("Image_URL",imageUrl);
                            reference.child(auth.getCurrentUser().getUid()).child(key).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Constants.SuccessToast(getApplicationContext(),"Image Uploaded Successfully");
                                    progressDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    ErrorToast(getApplicationContext(),"Error : "+e.getMessage());
                                    progressDialog.dismiss();
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle any errors that occurred during getting the download URL
                            ErrorToast(getApplicationContext(),"Error : "+e.getMessage());
                            progressDialog.dismiss();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle any errors that occurred during the image upload
                    ErrorToast(getApplicationContext(),"Error : "+e.getMessage());
                    progressDialog.dismiss();
                }
            });

        }
    }

    private void upload_document(Uri doc) {
        ProgressDialog pd = Constants.progressDialog(Notes_Attachment.this,"Uploading Attachment","Please Wait....");
        pd.show();
        String filename = "attachment_"+System.currentTimeMillis();
        Intent intent = getIntent();
        String key = intent.getStringExtra("key");

        StorageReference docref = storage.getReference("Attachments").child(auth.getCurrentUser().getUid()).child(filename);
        UploadTask uploadTask = docref.putFile(doc);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                docref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("Attachment_URL",uri.toString());
                        hashMap.put("Attachment_name",filename);
                        database.getReference("Tasks").child(auth.getCurrentUser().getUid()).child(key).child("Attachments").child(reference.push().getKey()).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Constants.SuccessToast(Notes_Attachment.this,"Attachment Uploaded Successfully");
                                                pd.dismiss();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Constants.ErrorToast(Notes_Attachment.this,"Error : "+e.getMessage());
                                        pd.dismiss();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Constants.ErrorToast(Notes_Attachment.this,"Error : "+e.getMessage());
                        pd.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Constants.ErrorToast(Notes_Attachment.this,"Error : "+e.getMessage());
                pd.dismiss();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void getdata(String id , String key)
    {
        ProgressDialog progressDialog = Constants.progressDialog(this , "Fetching Data" , "Please Wait....");
        progressDialog.show();

        if (id!=null  && key!=null )
        {
            reference.child(id).child(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    TaskModel model = snapshot.getValue(TaskModel.class);

                    if (model!=null)
                    {
                        binding.etNotes.setText(model.getNote());
                        if (!isDestroyed())
                        {
                            Glide.with(Notes_Attachment.this).load(model.getImage_URL()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.placeholder)
                                    .transition(DrawableTransitionOptions.withCrossFade()).into(binding.image);
                        }


                    }
                    progressDialog.dismiss();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Constants.ErrorToast(getApplicationContext(),"Error : "+error.getMessage());
                    progressDialog.dismiss();
                }
            });
        }
        else
        {
            progressDialog.dismiss();
            Constants.ErrorToast(Notes_Attachment.this,"Invalid key and Id");
        }



    }

}