package com.haife.app.nobles.spirits.kotlin.di.module;


import android.view.LayoutInflater;
import android.view.View;

import com.haife.app.nobles.spirits.kotlin.R;
import com.haife.app.nobles.spirits.kotlin.app.base.BaseSupportFragment;
import com.haife.app.nobles.spirits.kotlin.mvp.contract.HomeContract;
import com.haife.app.nobles.spirits.kotlin.mvp.http.entity.bean.UnionRestaurantBean;
import com.haife.app.nobles.spirits.kotlin.mvp.http.entity.multi.HRecommendMultiItemEntity;
import com.haife.app.nobles.spirits.kotlin.mvp.http.entity.result.HomeRecommendData;
import com.haife.app.nobles.spirits.kotlin.mvp.http.entity.result.RestaurantUnionBean;
import com.haife.app.nobles.spirits.kotlin.mvp.model.HomeModel;
import com.haife.app.nobles.spirits.kotlin.mvp.ui.adapter.HRecommendAdapter;
import com.haife.app.nobles.spirits.kotlin.mvp.ui.adapter.HUnionRestaurantAdapter;
import com.haife.app.nobles.spirits.kotlin.mvp.ui.fragment.HRecommendFragment;
import com.haife.app.nobles.spirits.kotlin.mvp.ui.fragment.HUnionRestaurantFragment;
import com.jess.arms.di.scope.FragmentScope;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.Module;
import dagger.Provides;

/**
 * @ author haife
 * @ since 2018/12/18
 * TODO：
 */
@Module
public class HomeModule {

    private HomeContract.View view;

    public HomeModule(HomeContract.View view) {
        this.view = view;
    }

    @FragmentScope
    @Provides
    public HomeContract.View provideHomeView() {
        return this.view;
    }

    @FragmentScope
    @Provides
    public HomeContract.Model provideHomeModel(HomeModel model) {
        return model;
    }


    @FragmentScope
    @Provides
    RecyclerView.LayoutManager provideLayoutManager() {
        return new LinearLayoutManager(view.getFragment().getContext()) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 500;
            }
        };
    }

    @FragmentScope
    @Provides
    @Named("NetErrorView")
    View provideNetErrorUiView() {
        return LayoutInflater.from(view.getFragment().getContext()).inflate(R.layout.include_home_recommend_net_wort_error, null);
    }

    @FragmentScope
    @Provides
    List<BaseSupportFragment> provideHomeFragmentList() {
        List<BaseSupportFragment> fragmentList = new ArrayList<>();
        fragmentList.add(HRecommendFragment.Companion.newInstance());
        fragmentList.add(HUnionRestaurantFragment.Companion.newInstance());
        return fragmentList;
    }

    /*---------------------------HomeRecommendFragment---------------------------------*/
    @FragmentScope
    @Provides
    HomeRecommendData provideHomeRecommendData() {
        return new HomeRecommendData();
    }

    @FragmentScope
    @Provides
    List<HRecommendMultiItemEntity> provideRecommendDataList() {
        return new ArrayList<>();
    }

    @FragmentScope
    @Provides
    HRecommendAdapter provideHRecommendAdapter(List<HRecommendMultiItemEntity> list) {
        return new HRecommendAdapter(list, view.getFragment().getActivity());
    }


    /*----------------------------UnionRestaurantFragment--------------------------------*/
    @FragmentScope
    @Provides
    RestaurantUnionBean provideRestaurantUnionResponse() {
        return new RestaurantUnionBean();
    }


    @FragmentScope
    @Provides
    List<UnionRestaurantBean> provideUnionRestaurantList() {
        return new ArrayList<>();
    }

    @FragmentScope
    @Provides
    HUnionRestaurantAdapter provideUnionRestaurantAdapter(List<UnionRestaurantBean> dataList) {
        return new HUnionRestaurantAdapter(R.layout.recycle_item_union_restaurant, dataList, view.getFragment().getContext());
    }


    @FragmentScope
    @Provides
    @Named("UnionRestaurantFilterView")
    View provideUnionRestaurantHeaderView() {
        return LayoutInflater.from(view.getFragment().getContext()).inflate(R.layout.header_union_restaurant_search_filter, null);
    }
}
