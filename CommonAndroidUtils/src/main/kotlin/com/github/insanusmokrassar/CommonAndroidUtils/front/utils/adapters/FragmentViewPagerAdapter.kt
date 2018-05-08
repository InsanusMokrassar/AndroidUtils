package com.github.insanusmokrassar.CommonAndroidUtils.front.utils.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.os.Parcelable



open class FragmentViewPagerAdapter(
        protected val fragments: List<Fragment>,
        fm: FragmentManager,
        protected val fragmentToTitleConverter: (Int, Fragment) -> String? = { _, _ -> null }
) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment = fragments[position]
    override fun getCount(): Int = fragments.size
    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentToTitleConverter(
                position,
                getItem(position)
        )
    }
}
