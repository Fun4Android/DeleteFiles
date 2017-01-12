package org.onpanic.deletefiles.fragments;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.onpanic.deletefiles.R;
import org.onpanic.deletefiles.adapters.PathsAdapter;
import org.onpanic.deletefiles.providers.PathsProvider;

public class PathsListFragment extends Fragment {
    private ContentResolver mContentResolver;
    private PathsObserver mPathsObserver;
    private OnPathListener mListener;
    private Context mContext;
    private PathsAdapter mPaths;
    private FloatingActionButton mFab;

    private String[] mProjection = new String[]{
            PathsProvider.Path._ID,
            PathsProvider.Path.PATH,
            PathsProvider.Path.ENABLED
    };

    public PathsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paths_list, container, false);

        mFab = (FloatingActionButton) view.findViewById(R.id.fab);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        if (preferences.getBoolean(mContext.getString(R.string.pref_delete_all), false)) {
            mFab.hide();
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    R.string.all_files_will_be_deleted,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.disable,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SharedPreferences.Editor edit = preferences.edit();
                            edit.putBoolean(mContext.getString(R.string.pref_delete_all), false);
                            edit.apply();

                            mFab.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mListener.onFabClickCallback();
                                }
                            });

                            mFab.show();
                        }
                    }).show();
        } else {
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onFabClickCallback();
                }
            });
        }

        mPaths = new PathsAdapter(
                mContext,
                mContentResolver.query(
                        PathsProvider.CONTENT_URI, mProjection, null, null, null
                ),
                mListener);

        mContentResolver.registerContentObserver(PathsProvider.CONTENT_URI, true, mPathsObserver);

        RecyclerView list = (RecyclerView) view.findViewById(R.id.contact_list);
        list.setLayoutManager(new LinearLayoutManager(view.getContext()));
        list.setAdapter(mPaths);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mContentResolver = mContext.getContentResolver();
        mPathsObserver = new PathsObserver(new Handler());

        if (context instanceof OnPathListener) {
            mListener = (OnPathListener) mContext;
        } else {
            throw new RuntimeException(mContext.toString()
                    + " must implement OnPathListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mContentResolver.unregisterContentObserver(mPathsObserver);
    }

    public interface OnPathListener {
        void onPathListenerCallback(int id);

        void onFabClickCallback();
    }

    class PathsObserver extends ContentObserver {
        PathsObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            // New data
            mPaths.changeCursor(mContentResolver.query(
                    PathsProvider.CONTENT_URI, mProjection, null, null, null
            ));
        }

    }
}
