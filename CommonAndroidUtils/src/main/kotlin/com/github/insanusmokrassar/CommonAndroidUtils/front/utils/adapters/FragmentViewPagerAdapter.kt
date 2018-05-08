package com.github.insanusmokrassar.CommonAndroidUtils.front.utils.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.os.Parcelable



class FragmentViewPagerAdapter(
        private val fragments: List<Fragment>,
        fm: FragmentManager,
        private val fragmentToTitleConverter: (Int, Fragment) -> String? = { _, _ -> null }
) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment = fragments[position]
    override fun getCount(): Int = fragments.size
    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentToTitleConverter(
                position,
                getItem(position)
        )
    }

    //-----------------------for uncaching oldest fragments
    override fun saveState(): Parcelable? {
        uncacheFragments()
        return null
    }

    private fun uncacheFragments() {
        fragments.forEach {
            fragment ->
            try {
                fragment.fragmentManager ?. beginTransaction() ?.let {
                    it.remove(fragment)
                    it.commit()
                }
            } catch (e: NullPointerException) { }
        }
    }

}
