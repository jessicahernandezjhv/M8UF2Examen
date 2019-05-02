package com.dam.jessicahv.pruebamultimedia;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements FragmentReportarIncidencia.OnFragmentInteractionListener, FragmentListaIncidencias.OnFragmentInteractionListener {

    Toolbar toolbar;
    static MediaPlayer audioPlayer;
    boolean musicButtonState = true;

    FragmentReportarIncidencia reportarIncidencia;
    FragmentListaIncidencias listaIncidencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        reportarIncidencia = new FragmentReportarIncidencia();
        listaIncidencias = new FragmentListaIncidencias();

        getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragments, reportarIncidencia).commit();

        audioPlayer = new MediaPlayer();
        audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        String audioUrl = "http://docs.google.com/uc?export=download&id=1SDCUlrEoxzcU2pehSMljEyxyAJkc3-mw";

        try {
            audioPlayer.setDataSource(audioUrl);
            audioPlayer.prepare();
            audioPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflate = getMenuInflater();
        inflate.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (item.getItemId()) {
            case R.id.musicItem:

                if (audioPlayer.isPlaying()) {
                    // STOP MUSIC
                    toolbar.getMenu().getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_play_circle_outline_black_24dp));
                    audioPlayer.pause();
                    musicButtonState = false;
                    Toast.makeText(this, "Pause", Toast.LENGTH_SHORT).show();
                } else {
                    // PLAY MUSIC
                    toolbar.getMenu().getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_pause_circle_outline_black_24dp));
                    audioPlayer.start();
                    musicButtonState = true;
                    Toast.makeText(this, "Play", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.item1:
                transaction.replace(R.id.contenedorFragments, reportarIncidencia);
                break;
            case R.id.item2:
                transaction.replace(R.id.contenedorFragments, listaIncidencias);
                break;
        }
        transaction.commit();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        audioPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (musicButtonState) {
            audioPlayer.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        audioPlayer.pause();
    }
}