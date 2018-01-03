package com.github.insanusmokrassar.CommonAndroidUtils.front.utils.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class FragmentViewPagerAdapter(fragments: List<Fragment>, fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    private val fragments = ArrayList<Fragment>(fragments)
    override fun getItem(position: Int): Fragment = fragments[position]
    override fun getCount(): Int = fragments.size
}
