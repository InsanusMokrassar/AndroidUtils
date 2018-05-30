package com.github.insanusmokrassar.CommonAndroidUtils.front.utils.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.os.Parcelable

class NestedFragmentViewPagerAdapter(
    fragments: List<Fragment>,
    fm: FragmentManager,
    fragmentToTitleConverter: (Int, Fragment) -> String? = { _, _ -> null }
) : FragmentViewPagerAdapter(
    fragments, fm, fragmentToTitleConverter
) {
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
            } catch (e: NullPointerException) {

            } catch (e: IllegalStateException) {

            }
        }
    }
}
