package nf.application.emanuele.tesi1;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PreferitiAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public PreferitiAdapter (FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Fragment tab1 = new PreferitiFragment();
                return tab1;
            case 1:
                Fragment tab2 = new EventiFragment();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}

}
