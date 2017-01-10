package org.onpanic.deletefiles;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import org.onpanic.deletefiles.constants.DeleteFilesConstants;
import org.onpanic.deletefiles.fragments.AllFilesLock;
import org.onpanic.deletefiles.fragments.DeleteFilesSettings;
import org.onpanic.deletefiles.fragments.LockedByPermissions;
import org.onpanic.deletefiles.fragments.TriggerApps;
import org.onpanic.deletefiles.permissions.PermissionManager;

public class DeleteFilesActivity extends AppCompatActivity implements
        DeleteFilesSettings.OnTriggerAppsListener {

    private FragmentManager mFragmentManager;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFragmentManager = getFragmentManager();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Do not overlapping fragments.
        if (savedInstanceState != null) return;

        if (PermissionManager.isLollipopOrHigher() && !PermissionManager.hasExternalWritePermission(this)) {
            PermissionManager.requestExternalWritePermissions(this, DeleteFilesConstants.REQUEST_WRITE_STORAGE);
        } else {
            if (mPrefs.getBoolean(getString(R.string.pref_delete_all), false)) {
                mFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new AllFilesLock())
                        .commit();
            } else {
                // TODO
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case DeleteFilesConstants.REQUEST_WRITE_STORAGE: {
                FragmentTransaction transaction = mFragmentManager.beginTransaction();

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // TODO
                } else {
                    transaction.replace(R.id.fragment_container, new LockedByPermissions());
                }

                transaction.commit();

                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete_files, menu);

        MenuItem deleteAll = menu.findItem(R.id.pref_delete_all);
        Switch actionView = (Switch) deleteAll.getActionView();
        actionView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor edit = mPrefs.edit();
                edit.putBoolean(getString(R.string.pref_delete_all), isChecked);
                edit.apply();

                if (isChecked) {
                    mFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, new AllFilesLock())
                            .commit();
                } else {
                    // TODO
                }
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            mFragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragment_container, new DeleteFilesSettings())
                    .commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mFragmentManager.getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            mFragmentManager.popBackStack();
        }
    }

    @Override
    public void onTriggerAppsCallback() {
        mFragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, new TriggerApps())
                .commit();
    }
}
