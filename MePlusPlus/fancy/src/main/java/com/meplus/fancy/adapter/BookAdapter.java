package com.meplus.fancy.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meplus.fancy.app.FancyApplication;
import com.meplus.fancy.dataholder.DataHolder;
import com.meplus.fancy.model.entity.Book;
import com.meplus.fancy.viewholder.BookViewHolder;

import java.util.ArrayList;
import java.util.List;

import cn.trinea.android.common.util.ListUtils;

public class BookAdapter extends RecyclerView.Adapter<BookViewHolder> {
    private List<DataHolder<Book>> mDataHolders = new ArrayList<>();

    public BookAdapter(List<Book> books) {
        addBooks(books);
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = FancyApplication.getInstance().getLayoutInflater();
        final View itemView = inflater.inflate(BookViewHolder.getLayout(), null);
        final BookViewHolder holder = new BookViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        holder.bindViewHolder(mDataHolders.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataHolders.size();
    }

    public void addBooks(List<Book> books) {
        final int size = ListUtils.getSize(books);
        for (int i = 0; i < size; i++) {
            mDataHolders.add(new DataHolder<Book>(books.get(i)));
        }
    }
}