package com.cl.test.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * name：cl
 * date：2023/4/17
 * desc： tab适配器
 */
class MainTabAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private var fragments: MutableList<Class<*>>? = null

    init {
        if (fragments == null) {
            fragments = ArrayList()
        }
    }

    fun addFragment(fragment: Fragment) {
        if (fragments != null) {
            fragments?.add(fragment.javaClass)
        }
    }

    override fun createFragment(position: Int): Fragment {
        try {
            return fragments!![position].newInstance() as Fragment
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        }
        return null!!
    }

    override fun getItemCount(): Int {
        return fragments!!.size
    }
}