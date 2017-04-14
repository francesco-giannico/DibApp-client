package it.uniba.di.ivu.sms16.gruppo2.dibapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;

import de.hdodenhof.circleimageview.CircleImageView;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.adapter.LeaderBoardAdapter;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.User;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.profile.ProfiloUtenteActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.settings.SettingsActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.viewholder.UserViewHolderOthers;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.viewholder.UserViewHolderThree;


public class LeaderBoardActivity extends DrawerActivity {
    private static RecyclerView mRecyclerView_otherClassified;
    private static RecyclerView mRecyclerView_threeClassified;
    private static RecyclerView mRecyclerView_currentUser;
    private static FirebaseRecyclerAdapter<User, UserViewHolderOthers> adapterOthers;
    private static FirebaseRecyclerAdapter<User, UserViewHolderThree> adapterThree;
    private DatabaseReference mref;
    private FirebaseStorage storage;
    private LeaderBoardAdapter leaderBoardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        mref = FirebaseDatabase.getInstance().getReference().child("users");
        storage = FirebaseStorage.getInstance();
        Toolbar t= (Toolbar)findViewById(R.id.toolbar);
        t.setTitle(R.string.activity_leaderboard);
        leaderBoardAdapter = new LeaderBoardAdapter(storage, (LinearLayout) findViewById(R.id.intestazione),
                (ProgressBar) findViewById(R.id.progressBarLeader_Three),
                (ProgressBar) findViewById(R.id.progressBarLeader_Others));
        setDrawer();
        impostaClickSulRiepilogoDati();
        //impostaRecyclerViews();

    }
    @Override
    protected void onStop() {
        super.onStop();

    }

    private void aggiornaDatiUtenteCorrente() {
        TableRow tabLayout = (TableRow) findViewById(R.id.TableRaw);
        //controllo se è loggato o meno altrimenti nascondo il layout.
        if(mCurrentUser==null && tabLayout!=null){
            tabLayout.setVisibility(View.GONE);
        }
        else{
            ((TextView) tabLayout.findViewById(R.id.current_user_name)).setText(mCurrentUser.name);
            ((RatingBar)tabLayout.findViewById(R.id.current_u_rating)).setRating((Float.valueOf(-1)*BaseActivity.mCurrentUser.avgRating));
            CircleImageView img = ( CircleImageView) tabLayout.findViewById(R.id.current_u_photo);
            if (mCurrentUser.photoUrl != null) {
                Glide.with(this)
                        .load(mCurrentUser.photoUrl)
                        .into(img);

                img.setImageDrawable(img.getDrawable());
            } else {
                img.setImageResource(R.drawable.ic_account_circle_white);
            }}
    }

    /**
     * mette il listener per l'utente corrente
     **/
    private void impostaClickSulRiepilogoDati() {
        LinearLayout linLayout = (LinearLayout) findViewById(R.id.intestazione);
        aggiornaDatiUtenteCorrente();
        if (linLayout != null) {
            linLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LeaderBoardActivity.this, ProfiloUtenteActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    /**
     * imposto le varie RecyclerView
     **/
    private void impostaRecyclerViews() {
        // mRecyclerView_currentUser = (RecyclerView) findViewById(R.id.RecyclerView_currentUser);
        mRecyclerView_threeClassified = (RecyclerView) findViewById(R.id.RecyclerView_firstThree);
        mRecyclerView_otherClassified = (RecyclerView) findViewById(R.id.RecyclerView_other);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView_otherClassified.setHasFixedSize(true);
        mRecyclerView_threeClassified.setHasFixedSize(true);
        LinearLayoutManager linManagerThree = new LinearLayoutManager(this);
        linManagerThree.setOrientation(LinearLayoutManager.HORIZONTAL);
        LinearLayoutManager linManagerOthers = new LinearLayoutManager(this);

        linManagerOthers.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView_otherClassified.setLayoutManager(linManagerOthers);
        mRecyclerView_threeClassified.setLayoutManager(linManagerThree);

    }


    @Override
    protected void onStart() {
        super.onStart();
        impostaRecyclerViews();
        Query avgRatingQueryOthers = mref.orderByChild("avgRating");
        adapterOthers = leaderBoardAdapter.getAdapterOthers(avgRatingQueryOthers);
        mRecyclerView_otherClassified.setAdapter(adapterOthers);
        Query avgRatingQueryThree = mref.orderByChild("avgRating").limitToFirst(3);
        adapterThree = leaderBoardAdapter.getAdapterThree(avgRatingQueryThree);
        mRecyclerView_threeClassified.setAdapter(adapterThree);
       // ((ViewGroup) R.layout.activity_leaderboard.getParent()).removeView(contentView);
    }

    /**
     * imposto il drawer
     */
    private void setDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
