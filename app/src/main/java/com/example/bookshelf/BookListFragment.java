package com.example.bookshelf;

import android.content.Context;
import android.os.Bundle;

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

    private static final String BOOK_LIST_KEY = "booklist";
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
        //args.putSerializable(BOOK_LIST_KEY, books);
        fragment.setArguments(args);
        return fragment;
    }

    public void dataUpdate(final ArrayList<Book> books) {

        booksAdapter = new BooksAdapter(getContext(), books);
        booksAdapter.notifyDataSetChanged();
        lView.setAdapter(booksAdapter);

        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                book = books.get(position);
                ((BookSelectedInterface) context).bookSelected(position);
            }
        });
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
        if (getArguments() != null) {
            books = (ArrayList) getArguments().getSerializable(BOOK_LIST_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view =  inflater.inflate(R.layout.fragment_book_list, container, false);
        lView=view.findViewById(R.id.fragmentListView);
        books = new ArrayList<>();
        booksAdapter=new BooksAdapter(getContext(), books);
        lView.setAdapter(booksAdapter);


/*        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                parentActivity.bookSelected(position);
            }
        });*/
        return lView;
    }

    /*
    Interface for communicating with attached activity
     */
    interface BookSelectedInterface {
        void bookSelected(int index);

    }
}
