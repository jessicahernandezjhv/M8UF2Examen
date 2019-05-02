package com.dam.jessicahv.pruebamultimedia;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentListaIncidencias.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentListaIncidencias#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentListaIncidencias extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    RecyclerView recyclerView;
    ArrayList<ImageUploadInfo> myList;
    public static FirebaseStorage firebaseStorage;
    public static Bitmap bitmap;
    public static FirebaseDatabase firebaseDatabase;
    public static RecyclerAdapter adapter;

    public FragmentListaIncidencias() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentListaIncidencias.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentListaIncidencias newInstance(String param1, String param2) {
        FragmentListaIncidencias fragment = new FragmentListaIncidencias();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lista_incidencias, container, false);

        myList = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //llenarLista();
        adapter = new RecyclerAdapter(myList);
        recyclerView.setAdapter(adapter);
        // Obtener datos de Realtime Database
        firebaseDatabase = FirebaseDatabase.getInstance(); // Apunta el objeto a mi proyecto de Firebase

        // Para cada hijo del nodo raiz hace un listener
        firebaseDatabase.getReference().child("images").addChildEventListener(new ChildEventListener() { // Añadir el child para cada elemento hijo del padre (musicplayer-97ddf) algo así
            // En este caso es /songs/ y de ahi ya se cogen todos los hijos
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { // Leer de Firebase
                ImageUploadInfo incidencia;

                incidencia = dataSnapshot.getValue(ImageUploadInfo.class); // Pillo el elemento del Firebase como tipo Song, tiene que tener un constructor vacio para que funcione

                myList.add(incidencia); // Añadir el elemento sacado de firebase a la lista de canciones
                adapter.notifyDataSetChanged(); // Avisar al adapter que se ha añadido un elemento
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        // Generar instacia que apunta mi Firebase Storage
        firebaseStorage = FirebaseStorage.getInstance();

        return view;
    }


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