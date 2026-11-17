package com.example.template.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.template.R;
import com.example.template.network.models.CatalogProducto;
import com.example.template.utils.CatalogFavoritesStore;
import com.example.template.utils.ImageLoader;

import java.util.List;

public class CatalogProductAdapter extends RecyclerView.Adapter<CatalogProductAdapter.ViewHolder> {

    private List<CatalogProducto> list;
    private OnProductClickListener listener;
    private CatalogFavoritesStore favoritesStore;
    private OnFavoriteClickListener favoriteClickListener;

    public interface OnProductClickListener {
        void onProductClick(CatalogProducto product);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(CatalogProducto product);
    }

    public CatalogProductAdapter(
            List<CatalogProducto> list,
            OnProductClickListener listener,
            CatalogFavoritesStore favoritesStore,
            OnFavoriteClickListener favoriteClickListener
    ) {
        this.list = list;
        this.listener = listener;
        this.favoritesStore = favoritesStore;
        this.favoriteClickListener = favoriteClickListener;
    }

    public void updateData(List<CatalogProducto> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catalog_producto, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CatalogProducto p = list.get(position);

        holder.tvName.setText(p.getName());
        holder.tvPrice.setText(String.format("Bs %.2f", p.getPrecioVenta()));

        boolean isFavorite = favoritesStore != null && favoritesStore.isFavorite(p.getId());
        int favoritePadding = Math.round(14 * holder.itemView.getResources().getDisplayMetrics().density);
        holder.btnFavorite.setPadding(favoritePadding, favoritePadding, favoritePadding, favoritePadding);
        holder.btnFavorite.setSelected(isFavorite);
        holder.btnFavorite.setImageResource(
                isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border
        );
        holder.btnFavorite.setContentDescription(holder.itemView.getContext().getString(
                isFavorite ? R.string.catalog_favorite_remove : R.string.catalog_favorite_add
        ));
        holder.btnFavorite.setOnClickListener(v -> {
            if (favoriteClickListener != null) {
                favoriteClickListener.onFavoriteClick(p);
            }
        });

        if (p.getCategory() != null && !p.getCategory().trim().isEmpty()) {
            holder.tvCategory.setText(p.getCategory().toUpperCase());
            holder.tvCategory.setVisibility(View.VISIBLE);
        } else {
            holder.tvCategory.setVisibility(View.GONE);
        }

        if (p.getDescription() != null && !p.getDescription().trim().isEmpty()) {
            holder.tvDesc.setText(p.getDescription());
            holder.tvDesc.setVisibility(View.VISIBLE);
        } else {
            holder.tvDesc.setVisibility(View.GONE);
        }

        holder.cvImageContainer.setVisibility(View.VISIBLE);
        if (p.getImagenUrl() != null && !p.getImagenUrl().trim().isEmpty()) {
            holder.ivImage.setPadding(0, 0, 0, 0);
            holder.ivImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageLoader.loadImage(p.getImagenUrl(), holder.ivImage);
        } else {
            int padding = (int) (16 * holder.itemView.getContext().getResources().getDisplayMetrics().density);
            holder.ivImage.setPadding(padding, padding, padding, padding);
            holder.ivImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            holder.ivImage.setImageResource(R.drawable.ic_product_placeholder);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(p);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View cvImageContainer;
        ImageView ivImage;
        ImageButton btnFavorite;
        TextView tvCategory, tvName, tvDesc, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cvImageContainer = itemView.findViewById(R.id.cvImageContainer);
            ivImage = itemView.findViewById(R.id.ivProductImage);
            btnFavorite = itemView.findViewById(R.id.btnProductFavorite);
            tvCategory = itemView.findViewById(R.id.tvProductCategory);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvDesc = itemView.findViewById(R.id.tvProductDescription);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
        }
    }
}
