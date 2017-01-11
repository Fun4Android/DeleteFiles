package org.onpanic.deletefiles.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.onpanic.deletefiles.R;
import org.onpanic.deletefiles.adapters.FMItem;
import org.onpanic.deletefiles.ui.SimpleDividerItemDecoration;

import java.io.File;

import static android.os.Environment.getExternalStorageDirectory;

public class FileManagerFragment extends Fragment {
    private File prevDir = null;
    private File currentDir;
    private Context mContext;
    private RecyclerView recyclerView;

    public FileManagerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.filemanager_layout, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.fm_list);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(mContext));
        recyclerView.setHasFixedSize(true); // does not change, except in onResume()
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(new FMItem(currentDir.listFiles()));

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        currentDir = getExternalStorageDirectory();
    }
}
