package com.maxvision.mvvm.navigation

import android.view.View
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import com.maxvision.mvvm.R


/**
 * 作者　: cl
 * 时间　: 2023/04/12
 * 描述　: Hide - Show NavHostFragment
 */
class NavHostFragmentHideShow : NavHostFragment() {


    /**
     * @return 使用自己的FragmentNavigator
     */
    override fun createFragmentNavigator(): Navigator<out FragmentNavigator.Destination?> {
        return FragmentNavigatorHideShow(requireContext(), childFragmentManager, containerId)
    }


    private val containerId: Int
        get() {
            val id = id
            return if (id != 0 && id != View.NO_ID) {
                id
                // Fallback to using our own ID if this Fragment wasn't added via
                // add(containerViewId, Fragment)
            } else androidx.navigation.fragment.R.id.nav_host_fragment_container
        }
}