package com.tuoying.hykc.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.tuoying.hykc.entity.GoodsEntity;
import com.tuoying.hykc.fragment.BottomMsgFragment;

import java.lang.reflect.Method;
import java.util.List;

public class BottomPageAdapter extends FragmentStatePagerAdapter {
    List<GoodsEntity> list;
    FragmentManager fm;
    int type;

    public BottomPageAdapter(FragmentManager fm, List<GoodsEntity> list, int type) {
        super(fm);
        this.list = list;
        this.fm = fm;
        this.type=type;
    }

    public void setList(List<GoodsEntity> list) {

        this.list = list;
    }


    @Override
    public Fragment getItem(int position) {
        //return BottomMsgFragment.getInstance(list.get(position), type);
        return null;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // TODO Auto-generated method stub

        removeFragment(container, position);
        return super.instantiateItem(container, position);
    }


    private void removeFragment(ViewGroup container, int index) {
        String tag = getFragmentTag(container.getId(), index);
        Fragment fragment = fm.findFragmentByTag(tag);
        if (fragment == null)
            return;
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragment);
        ft.commit();
        ft = null;
        fm.executePendingTransactions();
    }

    private String getFragmentTag(int viewId, int index) {
        try {
            Class<FragmentPagerAdapter> cls = FragmentPagerAdapter.class;
            Class<?>[] parameterTypes = {int.class, long.class};
            Method method = cls.getDeclaredMethod("makeFragmentName",
                    parameterTypes);
            method.setAccessible(true);
            String tag = (String) method.invoke(this, viewId, index);
            return tag;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }
}
