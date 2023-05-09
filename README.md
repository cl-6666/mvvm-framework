# Jetpack MVVM框架
Android Jetpack MVVM框架开发，基于AndroidX开发，傻瓜式使用，适用于所有项目


## 版本更新历史：  
[![](https://jitpack.io/v/cl-6666/mvvm-framework.svg)](https://jitpack.io/#cl-6666/mvvm-framework)

- v2.0.11：(2023年05月01日)
  - 框架内部使用kotlin代码编写  
  - 代码优化  

- v1.0.0：(2021年4月15日)
  - 上线第一个版本，满足日常项目开发  

## 效果图
<img src="https://github.com/cl-6666/mvvm-framework/blob/master/img/img.png" alt="演示"/>  

## 项目依赖
``` Gradle
allprojects {
   repositories {
	 maven { url 'https://jitpack.io' }
    }
}
```

Step 2. Add the dependency

``` Gradle
dependencies {
      implementation 'com.github.cl-6666:mvvm-framework:v2.0.11'
}
```  

## 使用介绍
``` kotlin
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
```

``` kotlin
class MainViewModel : BaseViewModel() {

    private var articleListData: MutableLiveData<Data> = MutableLiveData()
    
    /**
     * 网络请求
     */
    fun apiArticleListData() {
        request({ apiService.getEntryAndExitData()},{
            articleListData.value=it
        },{
            //失败
        },true)
    }


    /**
     * 获取数据
     */
    fun getArticleListData(): MutableLiveData<Data> {
        return articleListData
    }
}
```
