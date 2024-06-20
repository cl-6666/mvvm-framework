package com.cl.test.ui.adapter

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import com.cl.test.R
import com.cl.test.bean.DataX
import com.cl.test.util.ImageLoadingUtils

/**
 * name：cl
 * date：2023/5/9
 * desc：首页文章列表适配器
 */
class ArticleListAdapter(data: MutableList<String>) : BaseQuickAdapter<DataX, QuickViewHolder>() {
    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: DataX?) {
        //作者：极客时间 分类： 干货资源 / 干货资源 时间：2023-03-11 11:38
        holder.setText(
            R.id.item_project_content,
            "作者:" + item?.shareUser
        )
            .setText(R.id.item_project_title, item?.title)
            .setText(R.id.item_project_type, item?.superChapterName + "/" + item?.chapterName)
            .setText(R.id.item_project_date, item?.niceDate)
        ImageLoadingUtils.loadImage(
           "https://www.wanandroid.com/blogimgs/42da12d8-de56-4439-b40c-eab66c227a4b.png",
            holder.getView(R.id.item_project_imageview)
        )


    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_article_list, parent)
    }


}