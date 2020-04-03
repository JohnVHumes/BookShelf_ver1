package com.example.bookshelf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

public class MainActivity extends AppCompatActivity implements BookListFragment.BookSelectedInterface {

    FragmentManager fm;

    boolean twoPane;
    BookDetailsFragment bookDetailsFragment;
    BookListFragment bookListFragment;
    String searchString;
    String URLString;
    RequestQueue requestQueue;

    ArrayList<Book> books = new ArrayList<Book>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        twoPane = findViewById(R.id.container2) != null;

        requestQueue= Volley.newRequestQueue(this);

        fm = getSupportFragmentManager();

        bookListFragment = new BookListFragment();
        fm.beginTransaction()
                .replace(R.id.container1, bookListFragment = BookListFragment.newInstance(books))
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
        }

        findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener(){
            @Override
                    public void onClick(View v){


                searchString=((EditText)findViewById(R.id.searchText)).getText().toString();
                Log.d("Got search", searchString);
                URLString="https://kamorris.com/lab/abp/booksearch.php?search=" + searchString;
                Log.d("Got URL", URLString);
                JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET,URLString, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {

                            try {
                                //JSONArray jsonArray= new JSONArray((JSONObject) response);

                                Log.d("Got JSON", response.toString(0));
                                    books.clear();
                                for (int i = 0; i < response.length(); i++) {
                                    books.add(new Book(response.getJSONObject(i)));
                                }

                                bookListFragment.dataUpdate(books);

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

    /*
    Generate an arbitrary list of "books" for testing
     */
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

        if (twoPane)
            /*
            Display selected book using previously attached fragment
             */
            bookDetailsFragment.displayBook(getTestBooks().get(index));
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
}
