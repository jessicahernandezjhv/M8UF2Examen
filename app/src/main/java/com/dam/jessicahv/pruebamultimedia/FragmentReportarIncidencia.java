package com.dam.jessicahv.pruebamultimedia;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentReportarIncidencia.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentReportarIncidencia#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentReportarIncidencia extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //Declaración de variables
    ImageButton takePicture;
    Button uploadPicture;
    ImageView imageView;
    EditText editText;
    EditText editTexAula;

    //private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    private String [] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE"};

    // Firebase Database
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    String databasePath = "images";

    // Firebase Storage
    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentReportarIncidencia() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentReportarIncidencia.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentReportarIncidencia newInstance(String param1, String param2) {
        FragmentReportarIncidencia fragment = new FragmentReportarIncidencia();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reportar_incidencia, container, false);
        takePicture = view.findViewById(R.id.takePictureBtn);
        uploadPicture = view.findViewById(R.id.uploadImageBtn);
        imageView = view.findViewById(R.id.imageView);
        editText = view.findViewById(R.id.userPictureName);
        editTexAula = view.findViewById(R.id.userAula);

        //Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Firebase Database
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(databasePath);

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
                /*Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);*/

                //ESTO ES PARA CAPTURA
                /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,
                        CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);*/
            }
        });

        uploadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage(editText, editTexAula);
            }
        });

        uploadPicture.setAlpha(0);

        return view;
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    private void uploadImage(EditText editText, EditText editTexAula) {
        if(filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            final String fileName = editText.getText().toString();
            final String aulaName = editTexAula.getText().toString();

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String fireStoragePath = "https://firebasestorage.googleapis.com";
                            String imageUri = fireStoragePath +
                                    taskSnapshot.getUploadSessionUri().getPath() + "/" +
                                    taskSnapshot.getMetadata().getPath();

                            //ImageUploadInfo imageUploadInfo = new ImageUploadInfo(fileName, taskSnapshot.getMetadata().getReference().getDownloadUrl().toString(), false);
                            ImageUploadInfo imageUploadInfo = new ImageUploadInfo(fileName, aulaName, imageUri, false);

                            // Getting image upload ID
                            String ImageUploadId = databaseReference.push().getKey();
                            // Adding image upload id s child element into databaseReference.
                            databaseReference.child(ImageUploadId).setValue(imageUploadInfo);

                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null ) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
                uploadPicture.animate().alpha(1.0f).setDuration(2000).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /*public static final int SELECT_IMAGE = 1;
    Bitmap bitmap;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                        imageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED)  {
                Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                addToGallery();
                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // convert byte array to Bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                        byteArray.length);

                imageView.setImageBitmap(bitmap);
            }
        }
    } */

    /*private String pictureFilePath;

    private void addToGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(pictureFilePath);
        Uri picUri = Uri.fromFile(f);
        galleryIntent.setData(picUri);
        getActivity().sendBroadcast(galleryIntent);
    } */

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
