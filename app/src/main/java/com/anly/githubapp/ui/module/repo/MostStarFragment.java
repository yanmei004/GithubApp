package com.anly.githubapp.ui.module.repo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anly.githubapp.R;
import com.anly.githubapp.common.util.AppLog;
import com.anly.githubapp.data.api.TrendingApi;
import com.anly.githubapp.data.model.Repo;
import com.anly.githubapp.data.model.TrendingRepo;
import com.anly.githubapp.di.component.MainComponent;
import com.anly.githubapp.presenter.main.MostStarPresenter;
import com.anly.githubapp.ui.base.BaseFragment;
import com.anly.githubapp.ui.module.repo.adapter.RepoListRecyclerAdapter;
import com.anly.mvp.lce.LceView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.clans.fab.FloatingActionMenu;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mingjun on 16/7/19.
 */
public class MostStarFragment extends BaseFragment implements LceView<ArrayList<Repo>> {

    @BindView(R.id.repo_list)
    RecyclerView mRepoListView;
    @BindView(R.id.menu)
    FloatingActionMenu mFloatMenu;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    private RepoListRecyclerAdapter mAdapter;

    @Inject
    MostStarPresenter mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent(MainComponent.class).inject(this);

        mPresenter.attachView(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.loadMostStars(mCurrentKey, mCurrentLang);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_most_star, null);
        ButterKnife.bind(this, view);
        initViews();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    private void initViews() {
        mRefreshLayout.setOnRefreshListener(mRefreshListener);

        mAdapter = new RepoListRecyclerAdapter(null);
        mAdapter.setOnRecyclerViewItemClickListener(mItemtClickListener);

        mRepoListView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mRepoListView.addItemDecoration(new HorizontalDividerItemDecoration
                .Builder(getContext())
                .color(Color.TRANSPARENT)
                .size(getResources().getDimensionPixelSize(R.dimen.divider_height))
                .build());
        mRepoListView.setAdapter(mAdapter);
    }

    private BaseQuickAdapter.OnRecyclerViewItemClickListener mItemtClickListener = new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Repo repo = mAdapter.getItem(position);
            RepoDetailActivity.launch(getActivity(), repo.getOwner().getLogin(), repo.getName());
        }
    };

    @Override
    public void showLoading() {
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void dismissLoading() {
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showContent(ArrayList<Repo> data) {
        AppLog.d("data:" + data);
        if (data != null) {
            mAdapter.setNewData(data);
        }
    }

    @Override
    public void showError(Throwable e) {

    }

    @Override
    public void showEmpty() {

    }

    @OnClick({R.id.repo_android,
            R.id.repo_ios,
            R.id.repo_python,
            R.id.repo_html,
            R.id.repo_js})
    public void onLangMenuClick(View view) {
        mFloatMenu.close(true);

        String key = "";
        String lang = "";

        switch (view.getId()) {
            case R.id.repo_android:
                key = "android";
                lang = "java";
                break;
            case R.id.repo_ios:
                key = "iOS";
                break;
            case R.id.repo_python:
                key = "python";
                lang = "python";
                break;
            case R.id.repo_html:
                key = "html";
                lang = "HTML";
                break;
            case R.id.repo_js:
                key = "js";
                lang = "JavaScript";
                break;
        }

        mCurrentKey = key;
        mCurrentLang = lang;
        mPresenter.loadMostStars(key, lang);
    }

    // default is java
    private String mCurrentLang = "java";
    private String mCurrentKey = "android";
    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            AppLog.d("onRefresh, mCurrentLang:" + mCurrentLang);
            mPresenter.loadMostStars(mCurrentKey, mCurrentLang);
        }
    };
}
