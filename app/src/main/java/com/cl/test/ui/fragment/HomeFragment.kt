package com.cl.test.ui.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.cl.test.base.BaseFragment
import com.cl.test.databinding.FragmentHomeBinding
import com.cl.test.ext.init
import com.cl.test.ui.adapter.ArticleListAdapter
import com.cl.test.ui.viewmodel.MainViewModel
import com.maxvision.mvvm.ext.parseState


class HomeFragment : BaseFragment<MainViewModel, FragmentHomeBinding>(){

    private val mArticleListAdapter: ArticleListAdapter by lazy { ArticleListAdapter(arrayListOf()) }

    override fun initView(savedInstanceState: Bundle?) {
        mViewModel.apiArticleListData()
        mDatabind.rvArticleList.init(LinearLayoutManager(activity), mArticleListAdapter, false)
    }


    override fun createObserver() {
        super.createObserver()
        //脱壳
        mViewModel.articleListResult.observe(viewLifecycleOwner,
            Observer { resultState ->
                parseState(resultState, {
                    //请求成功 打印消息
                    mArticleListAdapter.submitList(it.datas)
                }, {
                    //请求失败(网络连接问题，服务器的结果码不正确...异常都会走在这里)
                    toast("请求失败")
                })
            })


        //不脱壳
        mViewModel.articleListResult2.observe(viewLifecycleOwner) { resultState ->
            parseState(resultState, {
                if (it.errorCode == 0) {
                    //请求成功 打印消息
                    toast(it.data.toString())
                } else {
                    //请求失败
                    toast(it.errorMsg)
                }
            }, {
                //请求发生了异常
                toast(it.errorMsg)
            })
        }
    }
}