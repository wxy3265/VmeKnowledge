package com.wxy3265.vmeknowledge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 用户列表适配器
 */

public class MultiSelectAdapter<RealmResults> extends RecyclerView.Adapter<MultiSelectAdapter.ViewHolder> implements View.OnClickListener
        , View.OnLongClickListener {

    private RealmResults KnowledgeList;

    private onItemClickListener listener;

    //设置显示的数据
    public void setKnowledgeListList(RealmResults KnowledgeList) {
        this.KnowledgeList = KnowledgeList;
    }

    //设置监听器
    public void setListener(onItemClickListener listener) {
        this.listener = listener;
    }

    //是否删除模式
    private boolean inDeletionMode = false;

    //设置是否为删除模式，改变绑定视图
    public void setInDeletionMode(boolean inDeletionMode) {
        this.inDeletionMode = inDeletionMode;
        notifyDataSetChanged();
    }

    //选中要删除用户
    private Set<Knowledge> selectSet = new HashSet<>();

    /**
     * 获取选中要删除的列表
     *
     * @return
     */
    public Set<Knowledge> getSelectSet() {
        return selectSet;
    }

    public void setSelectSet(Set<Knowledge> selectSet) {
        this.selectSet = selectSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.knowledge_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //这里调用realm的isValid方法，判断当前数据是否可用，防止出现数据被删除仍然使用导致崩溃
        if (KnowledgeList.get(position).isValid()) {

            holder.KnowledgeCard.setText(KnowledgeList.get(position).getName());
            holder.DateCard.setText(KnowledgeList.get(position).getCardNo());
            //删除模式，checkbox显示，否则不显示
            holder.checkbox.setVisibility(inDeletionMode ? View.VISIBLE : View.GONE);


            if (inDeletionMode) {
                holder.rootView.setOnClickListener(null);
                holder.rootView.setOnLongClickListener(null);

                //设置当前item的checkbox是否为选中状态
                //item长按删除的时候，activity设置了当前长按的item对象保存在selectset里面了
                holder.checkbox.setChecked(selectSet.contains(KnowledgeList.get(position)));
                holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            selectSet.add(KnowledgeList.get(position));
                        } else {
                            selectSet.remove(KnowledgeList.get(position));
                        }

                        //全部选中，通知activity “全选” 变成 “全不选”
                        if (selectSet.size() == KnowledgeList.size()) {
                            listener.selectedAll();
                        }
                    }
                });
            } else {
                holder.checkbox.setOnCheckedChangeListener(null);
                holder.rootView.setOnClickListener(this);
                holder.rootView.setOnLongClickListener(this);
            }

            holder.itemView.setTag(KnowledgeList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return KnowledgeList.size();
    }

    @Override
    public void onClick(View v) {
        Knowledge user = (Knowledge) v.getTag();
        if (null != user && listener != null) {
            listener.onItemClick(Knowledge);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        Knowledge user = (Knowledge) v.getTag();
        if (null != user && listener != null) {
            listener.onItemLongCick(Knowledge);
        }
        return true;
    }

    public void swapData(int from, int to) {
        //This method is not supported by 'RealmResults' or 'OrderedRealmCollectionSnapshot'.
        Collections.swap((List<?>) KnowledgeList, from, to);
    }

    public void swipeDelete(int position) {
        if (null != listener) {
            listener.onSwipeToDeleteUser(position);
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public TextView KnowledgeCard;
        public TextView DateCard;
        public CheckBox checkbox;

        public ViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.KnowledgeCard = (TextView) rootView.findViewById(R.id.KnowledgeCard);
            this.DateCard = (TextView) rootView.findViewById(R.id.DateCard);
            this.checkbox = (CheckBox) rootView.findViewById(R.id.checkbox);
        }

    }

    public interface onItemClickListener {
        void onItemClick(Knowledge Knowledge);

        void onItemLongCick(Knowledge Knowledge);

        void onSwipeToDeleteUser(int position);

        void selectedAll();
    }

}