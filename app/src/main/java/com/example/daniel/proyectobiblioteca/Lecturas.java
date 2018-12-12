package com.example.daniel.proyectobiblioteca;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.daniel.proyectobiblioteca.BDLocal.Ayudante;
import com.example.daniel.proyectobiblioteca.BDLocal.Gestor;
import com.example.daniel.proyectobiblioteca.Firebase.Firebase;
import com.example.daniel.proyectobiblioteca.POJOS.Autor;
import com.example.daniel.proyectobiblioteca.POJOS.Lectura;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Lecturas extends AppCompatActivity {

    public static final int INICIAR_DETALLE = 0;
    public static final int INICIAR_ADD = 1;
    private static final String TAG = "XYZ";

    private RecyclerView rvLecturas;
    private AdaptadorLibros adaptador;
    private RecyclerView.LayoutManager lymanager;

    private Ayudante ayudante;
    private Gestor gestor;

    private ArrayList<Lectura> lecturasLeidas = new ArrayList<>(); //primer array donde guardamos los libros leidos
    private ArrayList<Lectura> lecturasNoLeidas = new ArrayList<>(); //segundo array donde guardamos los libros no leidos
    private ArrayList<Lectura> lecturasPorLeer = new ArrayList<>(); //tercer array donde guardamos los libros por leer
    private ArrayList<Autor> autores = new ArrayList<>();

    Firebase firebase;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        /*
        Número asignado a cada estado:
            read = 1
            not_read = 2
            want_to_read = 3
         */

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.read:

                    setAdapter(lecturasLeidas);

                    return true;

                case R.id.not_read:

                    setAdapter(lecturasNoLeidas);

                    return true;

                case R.id.want_to_read:

                    setAdapter(lecturasPorLeer);

                    return true;
            }
            return false;
        }
    };
    private BottomNavigationView navigation;
    private android.support.constraint.ConstraintLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturas);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarLecturas);
        setSupportActionBar(toolbar);

        Resources res = getResources();
        getSupportActionBar().setIcon(res.getDrawable(R.mipmap.ic_logo));

        this.container = (ConstraintLayout) findViewById(R.id.container);
        this.navigation = (BottomNavigationView) findViewById(R.id.navigation);
        this.rvLecturas = (RecyclerView) findViewById(R.id.rvLecturas);

        ayudante = new Ayudante(this);
        gestor = new Gestor(this, true);

        firebase = new Firebase(getApplicationContext());

        //Obtenemos las lecturas de la bd
        getLecturasBD();
        
        //Cargamos el adaptador inicialmente con las lectuas leídas
        setAdapter(lecturasLeidas);

        rvLecturas.setAdapter(adaptador);

        lymanager = new LinearLayoutManager(this);

        rvLecturas.setLayoutManager(lymanager);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Log.v(TAG, "ONCREATE");
    }

    private void setAdapter(ArrayList<Lectura> nuevaLecturas){

        //Reemplaza el adaptador por una nueva instancia con un nuevo dataset.

        adaptador = new AdaptadorLibros(nuevaLecturas, new AdaptadorLibros.OnItemClickListener() {
            @Override
            public void onItemClick(Lectura l) {

                Log.v(TAG, "Lectura clickeada: " + l.getTitulo() + " - " + l.getAutor().getId());

                Intent i = new Intent(Lecturas.this, LecturaDetalle.class);
                i.putExtra("lectura", l);

                startActivityForResult(i, INICIAR_DETALLE);
            }
        });

        rvLecturas.setAdapter(adaptador);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_book) {

            Intent i = new Intent(Lecturas.this, LecturaDetalle.class);

            Lectura lec =  new Lectura();

            //Le pasamos una lectura vacía
            i.putExtra("lectura", lec);

            startActivityForResult(i, INICIAR_ADD);

            return true;
        }

        if (id == R.id.filtrar) {

            Intent i = new Intent(Lecturas.this, Filtrar.class);

            startActivity(i);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == INICIAR_ADD){
            if(resultCode == RESULT_OK){

                //Actualizamos los arrays de lecturas con los nuevos datos de la BD.
                getLecturasBD();

                setAdapter(lecturasLeidas);
            }
        }else if(requestCode == INICIAR_DETALLE){
            if(resultCode == RESULT_OK){

                //Actualizamos los arrays de lecturas con los nuevos datos de la BD.
                getLecturasBD();

                setAdapter(lecturasLeidas);
            }
        }

    }

    public void getLecturasBD(){

        lecturasLeidas = gestor.getLecturasPorEstado(1);
        lecturasNoLeidas = gestor.getLecturasPorEstado(2);
        lecturasPorLeer = gestor.getLecturasPorEstado(3);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebase.cerrarSesion();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        firebase.cerrarSesion();
    }


}
