package com.haife.app.nobles.spirits.kotlin.mvp.presenter;

import android.app.Application;

import com.haife.app.nobles.spirits.kotlin.mvp.contract.HomeContract;
import com.haife.app.nobles.spirits.kotlin.mvp.http.entity.base.BaseResponse;
import com.haife.app.nobles.spirits.kotlin.mvp.http.entity.base.Token;
import com.haife.app.nobles.spirits.kotlin.mvp.http.entity.multi.HRecommendMultiItemEntity;
import com.haife.app.nobles.spirits.kotlin.mvp.http.entity.result.HomeRecommendData;
import com.haife.app.nobles.spirits.kotlin.mvp.http.entity.result.RestaurantUnionBean;
import com.haife.app.nobles.spirits.kotlin.mvp.ui.adapter.HRecommendAdapter;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;

@FragmentScope
public class HomePresenter extends BasePresenter<HomeContract.Model, HomeContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;
    @Inject
    RestaurantUnionBean mRestaurantUnionBean;
    @Inject
    HomeRecommendData mHomeRecommendData;
    @Inject
    List<HRecommendMultiItemEntity> hRecommendMultiItemList;
    @Inject
    HRecommendAdapter mRecommendAdapter;
    private final String HOME_FRAGMENT_SIMPLE_NAME = "HomeFragment";
    private final String HRECOMMEND_FRAGMENT_SIMPLE_NAME = "HRecommendFragment";

    @Inject
    public HomePresenter(HomeContract.Model model, HomeContract.View rootView) {
        super(model, rootView);

    }

    /**
     * 首页联盟餐厅
     *
     * @param token
     */
    public void getHomeUnionRestaurant(Token token) {
        mModel.getUnionRestaurant(token).compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new RetryWithDelay(3, 2))
                .subscribe(new ErrorHandleSubscriber<BaseResponse<RestaurantUnionBean>>(mErrorHandler) {
                    @Override
                    public void onNext(BaseResponse<RestaurantUnionBean> response) {
                        if (response.getData().isSuccess())
                            mRestaurantUnionBean = response.getData().getResult();
                        else
                            mRootView.showMessage(response.getData().getMsg());
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);

                    }
                });
    }

    /**
     * 首页推荐
     *
     * @param fragmentName 请求的fragment实例 HomeFragment、hRecommendFragment
     * @param token
     */
    public void getHomeRecommendData(Token token, String fragmentName) {
        mModel.getHomeRecommendData(token).compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new RetryWithDelay(3, 2))
                .subscribe(new ErrorHandleSubscriber<BaseResponse<HomeRecommendData>>(mErrorHandler) {
                    @Override
                    public void onNext(BaseResponse<HomeRecommendData> response) {
                        if (response.getData().isSuccess()) {
                            mHomeRecommendData = response.getData().getResult();
                            if (fragmentName.equals(HOME_FRAGMENT_SIMPLE_NAME)) {
                                processHomeData();
                            }else if (fragmentName.equals(HRECOMMEND_FRAGMENT_SIMPLE_NAME)){
                                processRecommendData();
                            }

                        } else {
                            mRootView.showMessage(response.getData().getMsg());
                        }
                    }
                });
    }

    private void processHomeData() {
        List<String> magicList = new ArrayList<>();
        // 指示器数据
        if (mHomeRecommendData.getArr_table_data() != null) {
            for (HomeRecommendData.ArrTableDataBean arrTableDataBean : mHomeRecommendData.getArr_table_data()) {
                magicList.add(arrTableDataBean.getString_title_cn());
            }
        }
        mRootView.initMagicIndicatorView(magicList);
    }


    /**
     * 处理首页推荐的数据 提供给View层
     *
     * @param mHomeRecommendData
     */
    private void processRecommendData() {

        //判断banner是否为空
        if (mHomeRecommendData.getArr_index_banner_data() != null && mHomeRecommendData.getArr_index_banner_data().size() != 0) {
            HRecommendMultiItemEntity bannerEntity = new HRecommendMultiItemEntity();
            bannerEntity.setArr_index_banner_data(mHomeRecommendData.getArr_index_banner_data());
            bannerEntity.setTypeItem(HRecommendMultiItemEntity.BANNER_TYPE);
            hRecommendMultiItemList.add(bannerEntity);
        }
        //判断有无推荐餐厅
        if (mHomeRecommendData.getArr_index_recommend_shop() != null && mHomeRecommendData.getArr_index_recommend_shop().getArr_data() != null
                && mHomeRecommendData.getArr_index_recommend_shop().getArr_data().size() != 0) {
            HRecommendMultiItemEntity recommendShopEntity = new HRecommendMultiItemEntity();
            recommendShopEntity.setArr_index_recommend_shop(mHomeRecommendData.getArr_index_recommend_shop());
            recommendShopEntity.setTypeItem(HRecommendMultiItemEntity.RECOMMAND_RESTAURANT);
            hRecommendMultiItemList.add(recommendShopEntity);
        }

        mRecommendAdapter.notifyDataSetChanged();


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
        this.hRecommendMultiItemList = null;
    }

}
