package anime.project.dilidili.adapter;

import java.util.HashMap;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import anime.project.dilidili.main.home.WeekFragment;

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
                    fragment = new WeekFragment("Monday");
                    break;
                case 1:
                    fragment = new WeekFragment("Tuesday");
                    break;
                case 2:
                    fragment = new WeekFragment("Wednesday");
                    break;
                case 3:
                    fragment = new WeekFragment("Thursday");
                    break;
                case 4:
                    fragment = new WeekFragment("Friday");
                    break;
                case 5:
                    fragment = new WeekFragment("Saturday");
                    break;
                case 6:
                    fragment = new WeekFragment("Sunday");
                    break;
            }
            mFragmentHashMap.put(pos, fragment);
        }
        return fragment;
    }
}
