package com.itcrusaders.staffaccesssystems;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.itcrusaders.staffaccesssystems.Admin.BlankFragment1;
import com.itcrusaders.staffaccesssystems.Admin.MonthlyFragment;
import com.itcrusaders.staffaccesssystems.Admin.YearlyFragment;

public class MainTabbedActivity extends AppCompatActivity {

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabbed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        //new SectionsPagerAdapter(,MainTabbedActivity.this);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),MainTabbedActivity.this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_tabbed, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber,Context mContext1) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            mContext = mContext1;
            fragment.setArguments(args);
            return fragment;
        }
        static Context mContext;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Toast.makeText(mContext,getArguments().getInt(ARG_SECTION_NUMBER)+" hh",Toast.LENGTH_LONG).show();
            View rootView = inflater.inflate(R.layout.fragment_main_tabbed, container, false);


            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    Context mContext;
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm, Context mContext1) {
            super(fm);
            mContext = mContext1;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch(position){
                case 0:
                    BlankFragment1 blankFragment1 = new BlankFragment1();
                    //Toast.makeText(MainTabbedActivity.this,mContext.getPackageName().toString(),Toast.LENGTH_LONG).show();
                    blankFragment1.newInstance(position,mContext);
                    return blankFragment1.newInstance(position,mContext);
                case 1:
                    MonthlyFragment monthlyFragment = new MonthlyFragment();
                    //Toast.makeText(MainTabbedActivity.this,mContext.getPackageName().toString(),Toast.LENGTH_LONG).show();
                    monthlyFragment.newInstance(position,mContext);
                    return monthlyFragment.newInstance(position,mContext);
                case 2:
                    YearlyFragment yearlyFragment = new YearlyFragment();
                    //Toast.makeText(MainTabbedActivity.this,mContext.getPackageName().toString(),Toast.LENGTH_LONG).show();
                    yearlyFragment.newInstance(position,mContext);
                    return yearlyFragment.newInstance(position,mContext);
            }
            return PlaceholderFragment.newInstance(position + 1,mContext);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
