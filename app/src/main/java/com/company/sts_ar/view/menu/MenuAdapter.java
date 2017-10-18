package com.company.sts_ar.view.menu;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.company.sts_ar.R;
import com.company.sts_ar.util.SquareImage;
import com.company.sts_ar.vo.Project;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thong.le on 10/17/2017.
 */

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private List<Project> mProjects = new ArrayList<>();
    private Context mContext;

    public MenuAdapter(Context context, List<Project> projects) {
        this.mProjects = projects;
        this.mContext = context;
    }

    public void addItems(List<Project> projects) {
        mProjects.addAll(projects);
        notifyItemRangeChanged(0, projects.size());
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        holder.bindData(mProjects.get(position));
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_PROJECT,mProjects.get(position));
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mProjects.size();
    }

    class MenuViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        SquareImage ivImage;

        public MenuViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            ivImage = itemView.findViewById(R.id.iv_image);
        }

        public void bindData(Project project) {
            tvName.setText(project.name);
            // load image
            try {
                // get input stream
                InputStream ims = mContext.getAssets().open(project.folder + File.separator + project.cover);
                // load image as Drawable
                Drawable d = Drawable.createFromStream(ims, null);
                // set image to ImageView
                ivImage.setImageDrawable(d);
            }
            catch(IOException ex) {
                return;
            }
        }
    }

}
