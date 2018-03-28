package app.myquizapp.com.myquizapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by vaam on 3/21/2018.
 */

public class CustomAdapter extends FragmentPagerAdapter {

    public CustomAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0)
            return new SampleFragment();
        else
            return new SampleFragmentTwo();
    }

    @Override
    public int getCount() {
        return 2;
    }
}
