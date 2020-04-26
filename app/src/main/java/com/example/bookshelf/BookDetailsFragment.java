package com.example.bookshelf;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class BookDetailsFragment extends Fragment {

    private static final String BOOK_KEY = "book";
    private Book book;

    private Context context;

    TextView titleTextView, authorTextView;
    ImageView imageView;
    Button playButton;
    playButtonInterface parentActivity;

    public BookDetailsFragment() {}

    public static BookDetailsFragment newInstance(Book book) {
        BookDetailsFragment fragment = new BookDetailsFragment();
        Bundle args = new Bundle();

        /*
         A HashMap implements the Serializable interface
         therefore we can place a HashMap inside a bundle
         by using that put() method.
         */
        args.putSerializable(BOOK_KEY, book);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        /*
         This fragment needs to communicate with its parent activity
         so we verify that the activity implemented our known interface
         */
        if (context instanceof playButtonInterface) {
            parentActivity = (playButtonInterface) context;
        } else {
            throw new RuntimeException("Please implement the required interface(s)");
        }

        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            book = (Book) getArguments().getSerializable(BOOK_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_book_details, container, false);

        titleTextView = v.findViewById(R.id.titleTextView);
        authorTextView = v.findViewById(R.id.authorTextView);
        imageView= v.findViewById(R.id.imageView);
        playButton = v.findViewById(R.id.playButton);

        /*
        Because this fragment can be created with or without
        a book to display when attached, we need to make sure
        we don't try to display a book if one isn't provided
         */
        if (book != null)
            displayBook(book);
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((playButtonInterface) context).playBook(book);
                }
            });
        return v;
    }

    /*
    This method is used both internally and externally (from the activity)
    to display a book
     */
    public void displayBook(Book book) {
        titleTextView.setText(book.getTitle());
        authorTextView.setText(book.getAuthor());
        Picasso.get().load(book.getCoverURL()).into(imageView);
    }

    interface playButtonInterface{
        void playBook(Book book);
    }
}
