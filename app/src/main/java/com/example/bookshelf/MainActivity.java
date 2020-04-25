package com.example.bookshelf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity implements BookListFragment.BookSelectedInterface {
    //todo: pass the ball to tucker by which i mean store the needed variables in a bundle onpause
    final static String BOOK_ARRAY_INDEX="book_index";
    FragmentManager fm;

    boolean twoPane;
    private static final String BOOK_LIST_KEY ="book_list";

    BookDetailsFragment bookDetailsFragment;
    BookListFragment bookListFragment;
    String searchString;
    String URLString;
    RequestQueue requestQueue;
    int selectedBook;
    boolean displayFragmentActive;
    boolean connected;
    AudiobookService.MediaControlBinder controlBinder;

    ArrayList<Book> books = new ArrayList<Book>();

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        selectedBook=-1;
        if (savedInstanceState != null){

            this.books=(ArrayList<Book>) savedInstanceState.getSerializable(BOOK_LIST_KEY);
            this.selectedBook=savedInstanceState.getInt(BOOK_ARRAY_INDEX);
            Log.d("Instace State Restored:",this.books.toString());

        }
        else{
            books=new ArrayList<Book>();
        }

        setContentView(R.layout.activity_main);


        requestQueue= Volley.newRequestQueue(this);

        fm = getSupportFragmentManager();

 //       if (savedInstanceState==null){
            //first run

            Log.d("Books", books.toString());
            bookListFragment = BookListFragment.newInstance(this.books);
//        }
//        else{
//
//            //subsequent run
//
//        }

        twoPane = findViewById(R.id.container2) != null;


        fm.beginTransaction()
                .replace(R.id.container1, bookListFragment)
                .commit();

        /*
        If we have two containers available, load a single instance
        of BookDetailsFragment to display all selected books
         */
        if (twoPane) {
            bookDetailsFragment = new BookDetailsFragment();
            fm.beginTransaction()
                    .replace(R.id.container2, bookDetailsFragment)
                    .commit();
                    displayFragmentActive=true;

        }


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
                                    selectedBook=-1;

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

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    /*
    Generate an arbitrary list of "books" for testing
     */


    @Override
    protected void onPostResume() {
        super.onPostResume();

        bookListFragment.dataUpdate(books);

        if (selectedBook >=0){
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
    public void bookSelected(int index) {

        selectedBook=index;

        if (twoPane)
            /*
            Display selected book using previously attached fragment
             */
            bookDetailsFragment.displayBook(books.get(index));
        else {
            /*
            Display book using new fragment
             */
            fm.beginTransaction()
                    .replace(R.id.container1, BookDetailsFragment.newInstance(books.get(index)))
                    // Transaction is reversible
                    .addToBackStack(null)
                    .commit();
        }
    }




    @Override
    public void onPause(){
        super.onPause();

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(BOOK_LIST_KEY,books);
        outState.putInt(BOOK_ARRAY_INDEX,selectedBook);
        Log.d("StoreBooks",books.toString());


    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }
}
