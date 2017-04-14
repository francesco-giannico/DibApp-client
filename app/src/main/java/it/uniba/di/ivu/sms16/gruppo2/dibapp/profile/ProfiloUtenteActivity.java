package it.uniba.di.ivu.sms16.gruppo2.dibapp.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.DrawerActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.firebase.FirebaseHandler;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.User;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.utils.DividerItemDecoration;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.utils.NFCHandler;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.viewholder.FollowingFollowerViewHolder;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.viewholder.PersonalDetailsHolder;


public class ProfiloUtenteActivity extends DrawerActivity {

    private AppBarLayout appToolbarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RatingBar ratingBar;
    private View bottom_shadow;
    private View top_shadow;
    private Toolbar toolbar;
    private TextView nomeC;
    private ImageView profileImage;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private FirebaseHandler mDatabase;
    private NFCHandler nfcHandler;

    private int[] imageIconsId = {
            R.drawable.ic_menu_history,
            R.drawable.ic_menu_info_outline,
            R.drawable.ic_menu_follower,
            R.drawable.ic_menu_follower
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_drawer_main);
        appToolbarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        ratingBar = (RatingBar) findViewById(R.id.ratingBarProfiloUtente);
        bottom_shadow = findViewById(R.id.shadow_gradient_bottom);
        top_shadow = findViewById(R.id.shadow_gradient_top);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        nomeC = (TextView) findViewById(R.id.title_nome_cognome);
        profileImage = (ImageView) findViewById(R.id.profileImageView);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        assert appToolbarLayout != null;
        assert collapsingToolbarLayout != null;
        appToolbarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                        float opacity = (float) (appToolbarLayout.getHeight() + verticalOffset);
                        float denominator = appToolbarLayout.getTotalScrollRange();
                        float alpha = (denominator + verticalOffset)/denominator;
                        bottom_shadow.setAlpha(alpha - 0.3f);
                        top_shadow.setAlpha(alpha - 0.3f);
                        ratingBar.setAlpha(alpha);
                        nomeC.setTextSize(20.0f + alpha*15);

                    }
            });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();


        // Gestione TabLayout
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        mSectionsPagerAdapter.setTabIcons(tabLayout);

        // INIT NFC COMMUNICATION
        nfcHandler = new NFCHandler(this.getBaseContext(), this, mCurrentUid);
        nfcHandler.initializeNFCCommunication();
        //nfcHandler.listenNFCCommunication(getIntent(), mCurrentUser);

        // INIT DATABASE COMMUNICATION
        mDatabase = new FirebaseHandler();
    }

    @Override
    protected void onStart() {
        super.onStart();

        nomeC.setText(mCurrentUser.name);
        ratingBar.setRating(-1 * mCurrentUser.avgRating);
        String url = mCurrentUser.photoUrl;
        if(mCurrentUser.photoUrl != null) {
            //Ottengo immagine da url e inserisco in imageview
            Glide.with(this).load(mCurrentUser.photoUrl).crossFade().into(profileImage);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //nfcHandler.listenNFCCommunication(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        nfcHandler.listenNFCCommunication(intent, mCurrentUser);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_edit) {
            Intent intent = new Intent(getBaseContext(), ModificaProfiloActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    //CLassi per la gestione del TabLayout
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
        private static final int NUM_ITEMS = 3;
        private List<Drawable> icons = new ArrayList<Drawable>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position, mCurrentUid);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

        private void setTabIcons(TabLayout tabLayout) {

            Drawable icon1 = getBaseContext().getResources().getDrawable(R.drawable.ic_info);
            Drawable icon2 = getBaseContext().getResources().getDrawable(R.drawable.ic_following);
            Drawable icon3 = getBaseContext().getResources().getDrawable(R.drawable.ic_follower);

            icons.add(icon1);
            icons.add(icon2);
            icons.add(icon3);


            for(int i = 0; i < icons.size(); i++) {
                //SET COLORE INDIPENDENTE DALL'API
                //PorterDuff.Mode.SRC_ATOP Sovrappone il colore scelto alla sagoma dell'icona
                icons.get(i).setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
                tabLayout.getTabAt(i).setIcon(icons.get(i));
            }
        }
    }

    public static class PlaceholderFragment extends Fragment {

        int mNum;
        private String mUserId;
        private FirebaseRecyclerAdapter<User, FollowingFollowerViewHolder> mAdapter;
        private RecyclerView.Adapter<PersonalDetailsHolder> mDetailsAdapter;
        private RecyclerView mRecyclerView;
        private ProgressDialog mProgressDialog;
        private User mioUser;

        /**
         * Crea una nuova istanza in base ad mNum
         */
        static PlaceholderFragment newInstance(int num, String uid) {
            PlaceholderFragment f = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt("num", num + 1);
            args.putString("uid", uid);
            f.setArguments(args);

            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
            mUserId = getArguments() != null ? getArguments().getString("uid") : null;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.framelayout_tab, container, false);
            mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);
            mRecyclerView.setHasFixedSize(true);

            return v;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setReverseLayout(true);
            linearLayoutManager.setStackFromEnd(true);
            mRecyclerView.setLayoutManager(linearLayoutManager);

            if (mNum == 1){

                String mioId = mUserId;
                final DatabaseReference detailsInfo = new FirebaseHandler().getmUserReference(mioId);
                detailsInfo.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final User utenteFollower = dataSnapshot.getValue(User.class);

                        mDetailsAdapter = new RecyclerView.Adapter<PersonalDetailsHolder>() {
                            @Override
                            public PersonalDetailsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                                View itemView = inflater.inflate(R.layout.dettagli_profilo_personale, parent, false);
                                return new PersonalDetailsHolder(itemView);
                            }

                            @Override
                            public void onBindViewHolder(PersonalDetailsHolder holder, int position) {
                                holder.bindToDetails(utenteFollower);
                            }

                            @Override
                            public int getItemCount() {
                                return 1;
                            }
                        };
                        mDetailsAdapter.notifyDataSetChanged();
                        mRecyclerView.setAdapter(mDetailsAdapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getContext(), R.string.unable_to_load_user,
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }
            else if(mNum==2) {

                //QUERY FIREBASE/////////////////////////////////////////
                final String myId = mUserId;
                DatabaseReference followingReference = new FirebaseHandler().getmFollowingReference(myId);
                mAdapter = new FirebaseRecyclerAdapter<User, FollowingFollowerViewHolder>(User.class, R.layout.listitem_follower_following,
                        FollowingFollowerViewHolder.class, followingReference) {
                    @Override
                    protected void populateViewHolder(final FollowingFollowerViewHolder viewHolder, final User model, final int position) {
                        final DatabaseReference userRef = getRef(position);

                        // Set click listener for the whole post view
                        final String listItemKey = userRef.getKey();
                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), ProfiloUtenteNonPersonaleActivity.class);
                                intent.putExtra(ProfiloUtenteNonPersonaleActivity.EXTRA_PROFILE_KEY, listItemKey);
                                startActivity(intent);
                            }
                        });
                        viewHolder.bindToUser(model, true, listItemKey, myId, model);
                    }
                };
                mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
                mRecyclerView.setAdapter(mAdapter);
            }
            else if(mNum==3) {

                //QUERY FIREBASE/////////////////////////////////////////
                final String myId = mUserId;
                DatabaseReference followerReference = new FirebaseHandler().getmFollowerReference(myId);
                mAdapter = new FirebaseRecyclerAdapter<User, FollowingFollowerViewHolder>(User.class, R.layout.listitem_follower_following,
                        FollowingFollowerViewHolder.class, followerReference) {
                    @Override
                    protected void populateViewHolder(final FollowingFollowerViewHolder viewHolder, final User model, final int position) {
                        final DatabaseReference userRef = getRef(position);
                        final String listItemKey = userRef.getKey();
                        final HashMap<String, Boolean> imFollowing = new HashMap<String, Boolean>();
                        DatabaseReference isFollowerAFollowing = new FirebaseHandler().getmFollowingReference(myId).child(listItemKey);
                        isFollowerAFollowing.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                User amIFollowing = dataSnapshot.getValue(User.class);
                                if(amIFollowing != null) {
                                    User utenteFollower = new User(amIFollowing.name,
                                            null, amIFollowing.photoUrl);
                                    imFollowing.put(listItemKey, true);
                                    viewHolder.bindToUser(model, true, listItemKey, myId, model);
                                }
                                else {
                                    imFollowing.put(listItemKey, false);
                                    viewHolder.bindToUser(model, false, listItemKey, myId, model);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(getContext(), R.string.unable_to_load_user,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), ProfiloUtenteNonPersonaleActivity.class);
                                intent.putExtra(ProfiloUtenteNonPersonaleActivity.EXTRA_PROFILE_KEY, listItemKey);
                                HashMap<String, Boolean> temp = imFollowing;
                                System.out.println("check hashmap" + temp);
                                intent.putExtra(ProfiloUtenteNonPersonaleActivity.EXTRA_IMFOLLOW_KEY, imFollowing);
                                intent.putExtra(ProfiloUtenteNonPersonaleActivity.EXTRA_USER_KEY, model);
                                startActivity(intent);
                            }
                        });
                    }
                };
                mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
                mRecyclerView.setAdapter(mAdapter);
            }
        }

    }
}
