package anime.project.dilidili.adapter;

import java.util.HashMap;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import anime.project.dilidili.main.home.week.FridayFragment;
import anime.project.dilidili.main.home.week.MondayFragment;
import anime.project.dilidili.main.home.week.SaturdayFragment;
import anime.project.dilidili.main.home.week.SundayFragment;
import anime.project.dilidili.main.home.week.ThursdayFragment;
import anime.project.dilidili.main.home.week.TuesdayFragment;
import anime.project.dilidili.main.home.week.WednesdayFragment;

public class WeekAdapter extends FragmentPagerAdapter {
    private int num;
    private HashMap<Integer, Fragment> mFragmentHashMap = new HashMap<>();

    public WeekAdapter(FragmentManager fm, int num) {
        super(fm);
        this.num = num;
    }

    @Override
    public Fragment getItem(int position) {
        return createFragment(position);
    }

    @Override
    public int getCount() {
        return num;
    }

    private Fragment createFragment(int pos) {
        Fragment fragment = mFragmentHashMap.get(pos);

        if (fragment == null) {
            switch (pos) {
                case 0:
                    fragment = new MondayFragment();
                    break;
                case 1:
                    fragment = new TuesdayFragment();
                    break;
                case 2:
                    fragment = new WednesdayFragment();
                    break;
                case 3:
                    fragment = new ThursdayFragment();
                    break;
                case 4:
                    fragment = new FridayFragment();
                    break;
                case 5:
                    fragment = new SaturdayFragment();
                    break;
                case 6:
                    fragment = new SundayFragment();
                    break;
            }
            mFragmentHashMap.put(pos, fragment);
        }
        return fragment;
    }
}
