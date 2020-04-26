package com.example.bookshelf;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BookListFragment extends Fragment {
    private static final String BOOK_LIST_KEY ="book_list";
    private ArrayList<Book> books;
    private Context context;
    private ListView lView;
    private Book book;
    private  BooksAdapter booksAdapter;


    BookSelectedInterface parentActivity;



    public BookListFragment() {}

    public static BookListFragment newInstance(ArrayList<Book> books) {
        BookListFragment fragment = new BookListFragment();
        Bundle args = new Bundle();

        /*
         A HashMap implements the Serializable interface
         therefore we can place a HashMap inside a bundle
         by using that put() method.
         */
        args.putSerializable(BOOK_LIST_KEY, books);
        fragment.setArguments(args);
        return fragment;
    }

    public void dataUpdate(final ArrayList<Book> books) {

//        Bundle args = new Bundle();
//        args.putSerializable(BOOK_LIST_KEY, books);
//        this.setArguments(args);

        this.books=books;
        booksAdapter.books=books;
        booksAdapter.notifyDataSetChanged();

    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        /*
         This fragment needs to communicate with its parent activity
         so we verify that the activity implemented our known interface
         */
        if (context instanceof BookSelectedInterface) {
            parentActivity = (BookSelectedInterface) context;
        } else {
            throw new RuntimeException("Please implement the required interface(s)");
        }

        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_book_list, container, false);

        lView=view.findViewById(R.id.fragmentListView);

        if(books==null){
            books=new ArrayList<Book>();
            Log.e("Null Error","BookList Did not Exist on Create View");
        }


        booksAdapter = new BooksAdapter(this.getContext(), books);
        lView.setAdapter(booksAdapter);

        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                book = books.get(position);
                ((BookSelectedInterface) context).bookSelected(book);
            }
        });

        return lView;
    }





    /*
            Interface for communicating with attached activity
             */
    interface BookSelectedInterface {
        void bookSelected(Book book);

    }


}
