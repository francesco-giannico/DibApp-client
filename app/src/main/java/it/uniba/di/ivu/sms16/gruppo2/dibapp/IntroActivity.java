package it.uniba.di.ivu.sms16.gruppo2.dibapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.authentication.AuthActivity;


public class IntroActivity extends AppCompatActivity {

    private static final int AUTH_REQUEST_ACTIVITY = 901;

    private SlidesPagerAdapter mSlidesPagerAdapter;
    private ViewPager mViewPager;

    private Button mSkipBtn, mNextBtn;
    private LinearLayout mDotsLayout;
    private TextView[] mDots;
    private int[] mSlidelayouts;
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            if (position == (mSlidelayouts.length - 1)) {
                // last page. make button next and skip GONE
                mNextBtn.setVisibility(View.GONE);
                mSkipBtn.setVisibility(View.GONE);
            } else {
                mNextBtn.setVisibility(View.VISIBLE);
                mSkipBtn.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            //DO NOTHING
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            //DO NOTHING
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // Making status bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        mDotsLayout = (LinearLayout) findViewById(R.id.layout_dots);
        mSkipBtn = (Button) findViewById(R.id.button_skip);
        mNextBtn = (Button) findViewById(R.id.button_next);

        mSlidelayouts = new int[]{
                R.layout.slide_intro_1,
                R.layout.slide_intro_2,
                R.layout.slide_intro_3
        };

        addBottomDots(0);

        mSlidesPagerAdapter = new SlidesPagerAdapter();

        mViewPager = (ViewPager) findViewById(R.id.view_pager_intro);

        if (mViewPager != null) {
            mViewPager.setAdapter(mSlidesPagerAdapter);
            mViewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        }

        mSkipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroActivity.this, SessionsBoardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Removed checking for last slide for next GONE on last
                mViewPager.setCurrentItem(getItem(+1));

            }
        });
    }

    private void addBottomDots(int currentPage) {
        mDots = new TextView[mSlidelayouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        mDotsLayout.removeAllViews();

        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(colorsInactive[currentPage]);
            mDotsLayout.addView(mDots[i]);
        }

        if (mDots.length > 0)
            mDots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return mViewPager.getCurrentItem() + i;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTH_REQUEST_ACTIVITY) {

            Intent intent = new Intent(IntroActivity.this, SessionsBoardActivity.class);
            startActivity(intent);

            Toast.makeText(getBaseContext(), "Authentication Successfull", Toast.LENGTH_SHORT).show();

            finish();
        } else {
            Toast.makeText(getBaseContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
        }
    }

    public class SlidesPagerAdapter extends PagerAdapter {
        private LayoutInflater mLayoutInflater;

        public SlidesPagerAdapter() {
            super();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = mLayoutInflater.inflate(mSlidelayouts[position], container, false);
            container.addView(view);

            if (position == (getCount() - 1)) {
                final Button startBtn = (Button) findViewById(R.id.button_start_email);
                final Button maybeBtn = (Button) findViewById(R.id.button_maybe_later);

                if (startBtn != null) {
                    startBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(IntroActivity.this, AuthActivity.class);
                            startActivityForResult(intent, AUTH_REQUEST_ACTIVITY);
                        }
                    });
                }

                if (maybeBtn != null) {
                    maybeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(IntroActivity.this, SessionsBoardActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }

            return view;
        }

        @Override
        public int getCount() {
            return mSlidelayouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}