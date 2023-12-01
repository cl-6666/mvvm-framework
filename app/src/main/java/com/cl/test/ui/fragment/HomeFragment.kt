package com.cl.test.ui.fragment

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cl.test.base.BaseFragment
import com.cl.test.databinding.FragmentHomeBinding
import com.cl.test.ext.init
import com.cl.test.ui.adapter.ArticleListAdapter
import com.cl.test.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(){

    private val mViewModel: MainViewModel by viewModels()

    private val mArticleListAdapter: ArticleListAdapter by lazy { ArticleListAdapter(arrayListOf()) }

    override fun initView(savedInstanceState: Bundle?) {
        mViewModel.apiArticleListData()
        mDatabind.rvArticleList.init(LinearLayoutManager(activity), mArticleListAdapter, false)
        mViewModel.getArticleListData().observe(this) {
            mArticleListAdapter.submitList(it.datas)
        }

    }

}