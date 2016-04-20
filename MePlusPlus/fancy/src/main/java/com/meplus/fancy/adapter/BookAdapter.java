package com.meplus.fancy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meplus.fancy.R;
import com.meplus.fancy.app.FancyApplication;
import com.meplus.fancy.model.entity.Book;
import com.meplus.fancy.viewholder.BookViewHolder;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookViewHolder> {
    private List<Book> mDatas;

    public BookAdapter(List<Book> books) {
        mDatas = books;
    }


    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = FancyApplication.getInstance();
        final View view = LayoutInflater.from(context).inflate(R.layout.layout_item_book, parent, false);
        BookViewHolder holder = new BookViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


}