package com.cl.test.ui

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.cl.test.base.BaseActivity
import com.cl.test.databinding.ActivityMainBinding
import com.cl.test.ext.init
import com.cl.test.ui.adapter.ArticleListAdapter
import com.cl.test.ui.viewmodel.MainViewModel
import com.maxvision.mvvm.ext.util.logi
import com.maxvision.mvvm.ext.util.toJson

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {


    private val mArticleListAdapter: ArticleListAdapter by lazy { ArticleListAdapter(arrayListOf()) }


    override fun initView(savedInstanceState: Bundle?) {
        mViewModel.apiArticleListData()
        initRv()
    }

    private fun initRv() {
        mDatabind.rvArticleList.init(LinearLayoutManager(this), mArticleListAdapter, false)

        mViewModel.getArticleListData().observe(this) {
            mArticleListAdapter.submitList(it.datas)
            mArticleListAdapter.notifyDataSetChanged()
        }

    }

}