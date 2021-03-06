package edu.rosehulman.sunz1.rosechat.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeMap;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.SQLService.DatabaseConnectionService;
import edu.rosehulman.sunz1.rosechat.fragments.FeedbackSettingsFragment;
import edu.rosehulman.sunz1.rosechat.utils.Constants;
import edu.rosehulman.sunz1.rosechat.utils.SharedPreferencesUtils;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    //TODO: sync with DB server

    public static boolean NOTIFICATIONS = true;

    private Button mButtonAddCourse;
    private Button mButtonDeleteCourse;
    private Button mButtonLanguage;
    private Button mButtonLogOut;
    private Button mButtonFeedback;
    private Button mButtonDeleteAccount;
    private Button mButtonFontSize;
    private Button mButtonFontFamily;
    private View mProcessBar;
    private Handler mHandler;
    private boolean isFontChanged;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
//    private OnLogoutListener mLogoutListener;

    private DatabaseConnectionService mConService;
    private Connection mConnection;

    private ArrayList<String> mSettingsArray; //0:UID 1:FontSize 2:FontFamily 3:FontLanguage 4:Notification
    private boolean mIsFirstTimeReadServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mButtonAddCourse = (Button) findViewById(R.id.button_add_course);
        mButtonDeleteCourse= (Button) findViewById(R.id.button_delete_course);
        mButtonDeleteAccount = (Button) findViewById(R.id.button_settings_deleteAccount);
        mButtonFeedback = (Button) findViewById(R.id.button_settings_feedback);
        mButtonLanguage = (Button) findViewById(R.id.button_settings_Language);
        mButtonLogOut = (Button) findViewById(R.id.button_settings_logOut);
        mButtonFontSize = (Button) findViewById(R.id.button_settings_fontsize);
        mButtonFontFamily = (Button) findViewById(R.id.button_settings_fontfamily);

        isFontChanged = false;

        //font size
        mButtonDeleteAccount.setTextSize(20*(float)Constants.FONT_SIZE_FACTOR);
        mButtonFeedback.setTextSize(20*(float)Constants.FONT_SIZE_FACTOR);
        mButtonLanguage.setTextSize(20*(float)Constants.FONT_SIZE_FACTOR);
        mButtonLogOut.setTextSize(20*(float)Constants.FONT_SIZE_FACTOR);
        mButtonFontFamily.setTextSize(20*(float)Constants.FONT_SIZE_FACTOR);
        mButtonFontSize.setTextSize(20*(float)Constants.FONT_SIZE_FACTOR);
        mButtonAddCourse.setTextSize(20*(float)Constants.FONT_SIZE_FACTOR);
        mButtonDeleteCourse.setTextSize(20*(float)Constants.FONT_SIZE_FACTOR);

        //font family
        if (Constants.FONT_FAMILY == 1) { //monospace
            mButtonDeleteAccount.setTypeface(Typeface.MONOSPACE, 0);
            mButtonFeedback.setTypeface(Typeface.MONOSPACE, 0);
            mButtonLanguage.setTypeface(Typeface.MONOSPACE, 0);
            mButtonLogOut.setTypeface(Typeface.MONOSPACE, 0);
            mButtonFontFamily.setTypeface(Typeface.MONOSPACE, 0);
            mButtonFontSize.setTypeface(Typeface.MONOSPACE, 0);
            mButtonAddCourse.setTypeface(Typeface.MONOSPACE, 0);
            mButtonDeleteCourse.setTypeface(Typeface.MONOSPACE, 0);
        } else {
            mButtonDeleteAccount.setTypeface(Typeface.DEFAULT, 0);
            mButtonFeedback.setTypeface(Typeface.DEFAULT, 0);
            mButtonLanguage.setTypeface(Typeface.DEFAULT, 0);
            mButtonLogOut.setTypeface(Typeface.DEFAULT, 0);
            mButtonFontFamily.setTypeface(Typeface.DEFAULT, 0);
            mButtonFontSize.setTypeface(Typeface.DEFAULT, 0);
            mButtonAddCourse.setTypeface(Typeface.DEFAULT, 0);
            mButtonDeleteCourse.setTypeface(Typeface.DEFAULT, 0);
        }

        mButtonAddCourse.setOnClickListener(this);
        mButtonDeleteCourse.setOnClickListener(this);
        mButtonDeleteAccount.setOnClickListener(this);
        mButtonFeedback.setOnClickListener(this);
        mButtonLanguage.setOnClickListener(this);
        mButtonLogOut.setOnClickListener(this);
        mButtonFontSize.setOnClickListener(this);
        mButtonFontFamily.setOnClickListener(this);

        mConService = DatabaseConnectionService.getInstance();
        mConnection = mConService.getConnection();

        mSettingsArray = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        initializeListener();

        mProcessBar = findViewById(R.id.processbar_setting);
        mProcessBar.setVisibility(View.GONE);

        mIsFirstTimeReadServer = true;

        new SyncSettingTask().execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    private void initializeListener() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // empty
                } else {
                    LogInActivity.startActivity(SettingsActivity.this);
                }
            }
        };
    }

    @Override
    public void onClick(View v) { //mSettingsArray: {0:UID 1:FontSize 2:FontFamily 3:FontLanguage 4:Notification}
        int id = v.getId();
        String UID = SharedPreferencesUtils.getCurrentUser(getApplicationContext());
        switch (id) {
            case R.id.button_add_course:
                TreeMap<String, Integer> categoryList = new TreeMap<>();
                new GetCourseCategoryTask().execute(categoryList);
                return;
            case R.id.button_delete_course:
                new GetMyCoursesTask().execute(UID);
                return;
            case R.id.button_settings_Language:
                mSettingsArray.set(3, switchLanguage()); //set language
                new SyncSettingTask().execute();
                return;
            case R.id.button_settings_logOut:
                Log.d("setting", "logout clicked");
                logOutConfirmationDialog();
                return;
            case R.id.button_settings_deleteAccount:
                deleteAccountConfirmationDialog();
                return;
            case R.id.button_settings_feedback:
                FragmentTransaction feedbackTransaction = getSupportFragmentManager().beginTransaction();
                FeedbackSettingsFragment fragment = new FeedbackSettingsFragment();
                if (this.getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    feedbackTransaction.addToBackStack("feedback");
                    feedbackTransaction.add(R.id.settings_container, fragment);
                    feedbackTransaction.commit();
                } else {
                    getSupportFragmentManager().popBackStack("feedback", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    feedbackTransaction.commit();
                }
                return;
            case R.id.button_settings_fontsize:
                showFontSizeDialog();
                return;
            case R.id.button_settings_fontfamily:
                showFontFamilyDialog();
                return;
        }

    }

    private void showFontFamilyDialog() {
        DialogFragment df = new DialogFragment() {
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                Log.d("setting", "fontSize in dialog");
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                //BE CAREFUL: if R.array.fontfamily_array is changed, this variable needs to be changed as well
                final String[] fontfamilyArray = {"Default", "Monospace"};
                if (mSettingsArray.size() != 0) {
                    builder.setTitle("Pick a font family")
                            .setItems(R.array.fontfamily_array, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //TODO:
                                    String fontFamily = fontfamilyArray[which];

                                    if (fontFamily.equals("Default"))
                                        Constants.FONT_FAMILY = 0;
                                    else //Monospace
                                        Constants.FONT_FAMILY = 1;
                                    isFontChanged = true;

                                    mSettingsArray.set(2, fontFamily);
                                    Log.d(Constants.TAG_SETTING, "Set Font Size : " + mSettingsArray.get(2));
                                    new SyncSettingTask().execute();
                                    mProcessBar.setVisibility(View.VISIBLE);
                                    mProcessBar.setVisibility(View.GONE);
                                }
                            });
                }
                builder.setNegativeButton(android.R.string.cancel, null);
                return builder.create();
            }
        };
        df.show(getFragmentManager(), "fontSize");
    }

    private void showChooseCategoryDialog(TreeMap<String, Integer> data){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a course category");
        final TreeMap<String, Integer> map = data;
        final String[] categories = new String[map.size()];
        Iterator<String> iterator = map.keySet().iterator();
        for(int i=0;i<map.size();i++){
            categories[i] = iterator.next();
        }
        builder.setItems(categories, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String category = categories[which];
                Integer courseID = map.get(category);
                new GetCourseListTask().execute(courseID);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    private void showAddCourseDialog(TreeMap<String, Integer> data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a course");
        final TreeMap<String, Integer> map = data;
        final String[] courses = new String[map.size()];
        Iterator<String> iterator = map.keySet().iterator();
        for (int i = 0; i < map.size(); i++) {
            courses[i] = iterator.next();
        }
        builder.setItems(courses, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String courseName = courses[which];
                Integer courseID = map.get(courseName);
                new AddCourseTask().execute(courseID);
                Toast.makeText(SettingsActivity.this, "Course added successfully.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    private void showDeleteCourseDialog(TreeMap<String, Integer> data){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a course to delete");
        final TreeMap<String, Integer> map = data;
        final String[] courses = new String[map.size()];
        Iterator<String> iterator = map.keySet().iterator();
        for (int i = 0; i < map.size(); i++) {
            courses[i] = iterator.next();
        }
        builder.setItems(courses, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String courseName = courses[which];
                Integer courseID = map.get(courseName);
                new DeleteCourseTask().execute(courseID);
                Toast.makeText(SettingsActivity.this, "Course has been deleted.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    private void showFontSizeDialog() {
        DialogFragment df = new DialogFragment() {
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                Log.d("setting", "fontSize in dialog");
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final String[] sizefactorArray = {"0.5", "1", "2"};
                if (mSettingsArray.size() != 0) {
                    builder.setTitle("Pick a font size factor")
                            .setItems(R.array.fontsize_array, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String sizefactor = sizefactorArray[which];
                                    mSettingsArray.set(1, sizefactor);
                                    Log.d(Constants.TAG_SETTING, "Set Font Size : " + mSettingsArray.get(1));
                                    isFontChanged = true;
                                    new SyncSettingTask().execute();
                                }
                            });
                }
                builder.setNegativeButton(android.R.string.cancel, null);
                return builder.create();
            }
        };
        df.show(getFragmentManager(), "fontSize");
    }

    private void notificationDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("Notifications");
        mBuilder.setMessage("Would you like to turn notifications on or off?");
        mBuilder.setPositiveButton("On", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {NOTIFICATIONS =true;}
        });

        mBuilder.setNegativeButton("Off", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {NOTIFICATIONS = false;}
        });
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private void logOutConfirmationDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle(R.string.logout_title);
        mBuilder.setMessage(R.string.logout_message);
        mBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    DatabaseConnectionService.getInstance().getConnection().close();
                    logOut();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });

        mBuilder.setNegativeButton(android.R.string.cancel, null);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private void logOut() {
        SharedPreferencesUtils.removeCurrentUser(getApplicationContext());
        mAuth.signOut();
    }


    private void deleteAccount() {
        String UID = SharedPreferencesUtils.getCurrentUser(getApplicationContext());
        new DeleteAccountTask(UID).execute();
        FirebaseUser user = mAuth.getCurrentUser();
        SharedPreferencesUtils.removeCurrentUser(getApplicationContext());
        assert user != null;
        user.delete();
        mAuth.signOut(); //TODO: need improved
    }

    private class DeleteAccountTask extends AsyncTask<String, String, String> {

        private String mUID;

        public DeleteAccountTask (String uid) {
            mUID = uid;
        }

        protected String doInBackground(String... str) {
            try {
                CallableStatement stmt = mConnection.prepareCall("{call DeleteUser(?)}");
                stmt.setString(1, mUID);
                stmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class GetCourseCategoryTask extends AsyncTask<TreeMap<String, Integer>, String, TreeMap<String, Integer>>{
        @Override
        protected TreeMap<String, Integer> doInBackground(TreeMap<String, Integer>[] maps) {
            CallableStatement cs;
            TreeMap<String, Integer> map = maps[0];
            try {
                cs = mConnection.prepareCall("{call Get_CourseCategories}");
                cs.execute();
                ResultSet rs = cs.getResultSet();
                while(rs.next()){
                    Integer ID = rs.getInt(1);
                    String categoryName = rs.getString(2);
                    map.put(categoryName, ID);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return map;
        }

        @Override
        protected void onPostExecute(TreeMap<String, Integer> map) {
            super.onPostExecute(map);
            showChooseCategoryDialog(map);
        }
    }

    private class GetCourseListTask extends AsyncTask<Integer, String, TreeMap<String, Integer>>{

        @Override
        protected TreeMap<String, Integer> doInBackground(Integer... integers) {
            TreeMap<String, Integer> map = null;
            CallableStatement cs;
            Integer categoryID = integers[0];
            try {
                cs = mConnection.prepareCall("{call Get_CourseList(?)}");
                cs.setInt(1, categoryID);
                cs.execute();
                map = new TreeMap<>();
                ResultSet rs = cs.getResultSet();
                while(rs.next()){
                    Integer courseID = rs.getInt(1);
                    String courseName = rs.getString(2);
                    map.put(courseName, courseID);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return map;
        }

        @Override
        protected void onPostExecute(TreeMap<String, Integer> map) {
            super.onPostExecute(map);
            showAddCourseDialog(map);
        }
    }

    private class AddCourseTask extends AsyncTask<Integer, String, String>{

        @Override
        protected String doInBackground(Integer... integers) {
            Integer courseID = integers[0];
            String UID = SharedPreferencesUtils.getCurrentUser(getApplicationContext());
            CallableStatement cs;
            try {
                cs = mConnection.prepareCall("{call Course_Add(?,?)}");
                cs.setString(1, UID);
                cs.setInt(2, courseID);
                cs.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class GetMyCoursesTask extends AsyncTask<String, String, TreeMap<String, Integer>>{

        @Override
        protected TreeMap<String, Integer> doInBackground(String... strings) {
            String UID = strings[0];
            TreeMap<String, Integer> map = new TreeMap<>();
            try {
                CallableStatement cs = mConnection.prepareCall("{call Get_Courses(?)}");
                cs.setString(1, UID);
                cs.execute();
                ResultSet rs = cs.getResultSet();
                while (rs.next()) {
                    String courseName = rs.getString(1);
                    Integer courseID = rs.getInt(2);
                    map.put(courseName, courseID);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return map;
        }

        @Override
        protected void onPostExecute(TreeMap<String, Integer> map) {
            super.onPostExecute(map);
            showDeleteCourseDialog(map);
        }
    }

    private class DeleteCourseTask extends AsyncTask<Integer, String, String>{

        @Override
        protected String doInBackground(Integer... integers) {
            Integer courseID = integers[0];
            String UID = SharedPreferencesUtils.getCurrentUser(getApplicationContext());
            try {
                CallableStatement cs = mConnection.prepareCall("{call Course_Delete(?, ?)}");
                cs.setString(1, UID);
                cs.setInt(2, courseID);
                cs.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //0:UID 1:FontSize 2:FontFamily 3:FontLanguage 4:Notification
    private class SyncSettingTask extends AsyncTask<String, String, String> {

        protected String doInBackground(String... str) {
            String UID = SharedPreferencesUtils.getCurrentUser(getApplicationContext());
            try {

                CallableStatement cs = null;

                if (mSettingsArray.size() <= 0) { //if first time read, we add data into mSettingsArray
                    Log.d(Constants.TAG_SETTING, "First Time in Async");
                    mIsFirstTimeReadServer = false;
                    cs = mConnection.prepareCall("{call GetAllSettings(?)}"); //@Fontsize int output, @FontFamily nvarchar(20) output, @Language nvarchar(20) output, @Notification bit output, @UID string input
                    cs.setString(1, UID);
                    cs.execute();
                    ResultSet resultSet = cs.getResultSet();
                    while (resultSet.next()) {
                        mSettingsArray.add(0, UID);
                        mSettingsArray.add(1, Double.toString(resultSet.getFloat("Fontsize")));
                        mSettingsArray.add(2, resultSet.getString("FontFamily"));
                        mSettingsArray.add(3, resultSet.getString("Language"));
                        mSettingsArray.add(4, resultSet.getByte("Notification") + "");
                    }
                } else {
                    Log.d(Constants.TAG_SETTING, "Not First Time in Async");
                    //we need to create current user
                    cs = mConnection.prepareCall("{call SyncDisplaySettings(?, ?, ?, ?, ?)}");
                    cs.setString(1, UID); //@UID varchar(50)
                    cs.setFloat(2, Float.parseFloat(mSettingsArray.get(1))); //@FontSize int
                    cs.setString(3, mSettingsArray.get(2)); //@FontFamily nvarchar(20)
                    cs.setString(4, mSettingsArray.get(3)); //@FontLanguage nvarchar(10)
                    cs.setInt(5, Integer.parseInt(mSettingsArray.get(4))); //@Notification bit
                    cs.execute(); //add these data into db

                    /**
                     * This chunk of code restart the app
                     */
                    if(isFontChanged == true){
                        isFontChanged = false;
                        Intent mStartActivity = new Intent(getApplicationContext(), SplashActivity.class);
                        int mPendingIntentId = 123;
                        PendingIntent mPendingIntent = PendingIntent.getActivity(
                                getApplicationContext(),
                                mPendingIntentId,
                                mStartActivity,
                                PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager)getApplication().getSystemService(getApplicationContext().ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis(), mPendingIntent);
                        finishAffinity();
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void deleteAccountConfirmationDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle(R.string.delete_account_title);
        mBuilder.setMessage(R.string.delete_account_message);
        mBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAccount();
            }
        });

        mBuilder.setNegativeButton(android.R.string.cancel, null);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private String switchLanguage() {
        Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
        startActivity(intent);
        return Locale.getDefault().getISO3Language();
    }
}
