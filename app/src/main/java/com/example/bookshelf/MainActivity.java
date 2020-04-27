package com.example.bookshelf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import edu.temple.audiobookplayer.AudiobookService;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookSelectedInterface, BookDetailsFragment.playButtonInterface {
    final static String BOOK_ARRAY_INDEX="book_index";
    FragmentManager fm;

    boolean twoPane;

    private static final String PLAYBACK_BOOK_KEY ="playback_book";
    private static final String PLAYBACK_TIME_KEY ="playback_time";
    private static final String BOOK_LIST_KEY ="book_list";

    BookDetailsFragment bookDetailsFragment;
    BookListFragment bookListFragment;
    String searchString;
    String URLString;
    RequestQueue requestQueue;
    Book selectedBook;
    boolean displayFragmentActive;
    boolean connected;
    AudiobookService.MediaControlBinder controlBinder;
    int nowPlayingID;
    int playbackProgress;
    TextView titleTextView;
    SeekBar seekBar;

    ArrayList<Book> books = new ArrayList<Book>();
    Intent serviceIntent;
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            connected=true;
            controlBinder=(AudiobookService.MediaControlBinder) service;



        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            connected=false;
            controlBinder=null;

        }
    };

    Handler seekBarHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {

            if (controlBinder.isPlaying()) {

                updateSeekBar(((AudiobookService.BookProgress) msg.obj).getProgress());
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        titleTextView = findViewById(R.id.titleView);
        seekBar = findViewById(R.id.seekBar);
        //selectedBook=-1;
        if (savedInstanceState != null){

            this.books=(ArrayList<Book>) savedInstanceState.getSerializable(BOOK_LIST_KEY);
            this.selectedBook=(Book) savedInstanceState.getSerializable(PLAYBACK_BOOK_KEY);
            titleTextView.setText("NOW PLAYING: " +selectedBook.getTitle());
            seekBar.setMax(selectedBook.getDuration());
            //this is probably gonna break pausing and resuming
            //this.playbackProgress=savedInstanceState.getInt(PLAYBACK_TIME_KEY);

        }
        else{
            books=new ArrayList<Book>();
            selectedBook=null;
            //playbackProgress=0;
        }



        requestQueue= Volley.newRequestQueue(this);

        fm = getSupportFragmentManager();


            Log.d("Books", books.toString());
            bookListFragment = BookListFragment.newInstance(this.books);


        twoPane = findViewById(R.id.container2) != null;


        fm.beginTransaction()
                .replace(R.id.container1, bookListFragment)
                .commit();

        /*
        If we have two containers available, load a single instance
        of BookDetailsFragment to display all selected books
         */
        if (twoPane) {

            if (selectedBook!=null){bookDetailsFragment = BookDetailsFragment.newInstance(selectedBook);}
            else {bookDetailsFragment = new BookDetailsFragment();}

            fm.beginTransaction()
                    .replace(R.id.container2, bookDetailsFragment)
                    .commit();
                    displayFragmentActive=true;

        }

        serviceIntent= new Intent(MainActivity.this, AudiobookService.class);
        bindService(serviceIntent,serviceConnection,BIND_AUTO_CREATE);




        findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){


                searchString=((EditText)findViewById(R.id.searchText)).getText().toString();
                //Log.d("Got search", searchString);
                URLString="https://kamorris.com/lab/abp/booksearch.php?search=" + searchString;
                //Log.d("Got URL", URLString);
                JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET,URLString, null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {

                                try {
                                    //JSONArray jsonArray= new JSONArray((JSONObject) response);

                                    //Log.d("Got JSON", response.toString(0));
                                    books.clear();
                                    for (int i = 0; i < response.length(); i++) {
                                        books.add(new Book(response.getJSONObject(i)));
                                    }


                                    Log.d("kistenBooks", books.toString());


                                    if(!twoPane){
                                        fm.beginTransaction()
                                                .replace(R.id.container1, bookListFragment)
                                                .commit();
                                    }




                                    bookListFragment.dataUpdate(books);
                                    selectedBook=null;

                                }
                                catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }

                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, "WHY DID YOU SET ME ON FIRE, SPONGEBOB? WHY DIDN'T YOU JUST WRITE YOUR ESSAY?", Toast.LENGTH_LONG);
                                Log.d("JSON on error response", error.toString());

                            }
                        });

                requestQueue.add(jsonRequest);








            }
        });

        findViewById(R.id.pauseButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseBook();
            }
        });

        findViewById(R.id.stopButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopBook();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    controlBinder.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //findViewById(R.id.seekBar).

    }



    @Override
    protected void onPostResume() {
        super.onPostResume();

        bookListFragment.dataUpdate(books);

        if (selectedBook !=null){
            bookSelected(selectedBook);
        }



    }

    private ArrayList<Book> getTestBooks() {
        ArrayList<Book> books = new ArrayList<Book>();
        Book book;
        for (int i = 0; i < 10; i++) {
            book = new Book();
            book.setTitle("Book" + i);
            book.setAuthor("Author" + i);
            book.setId(i);
            book.setCoverURL("https://picsum.photos/200");
            books.add(book);
        }
        return books;
    };



    @Override
    public void bookSelected(Book book) {

        selectedBook=book;

        if (twoPane)
            /*
            Display selected book using previously attached fragment
             */
            bookDetailsFragment.displayBook(book);
        else {
            /*
            Display book using new fragment
             */
            fm.beginTransaction()
                    .replace(R.id.container1, BookDetailsFragment.newInstance(book))
                    // Transaction is reversible
                    .addToBackStack(null)
                    .commit();
        }
    }




    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(BOOK_LIST_KEY,books);
        //outState.putInt(BOOK_ARRAY_INDEX,selectedBook);
        outState.putSerializable(PLAYBACK_BOOK_KEY, selectedBook );
        Log.d("StoreBooks",books.toString());


    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    public void playBook(Book book){
        Log.d("got", book.getTitle());
        startService(serviceIntent);
        controlBinder.play(book.getId());
        seekBar.setMax(book.getDuration());

        setSeekBarHandler(seekBarHandler);
        nowPlayingID=book.getId();
        titleTextView.setText("NOW PLAYING: " +book.getTitle());

    }

    public void setSeekBarHandler(Handler seekBarHandler){
        controlBinder.setProgressHandler(seekBarHandler);
    }

    public void updateSeekBar(int time){
        seekBar.setProgress(time);
    }


    public void pauseBook(){
        controlBinder.pause();

    }

    public void stopBook(){
        controlBinder.stop();
        titleTextView.setText("");
        updateSeekBar(0);
        nowPlayingID=0;

        stopService(serviceIntent);
    }


}
