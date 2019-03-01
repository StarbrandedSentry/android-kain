package com.oddlid.karinderya.utils;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.oddlid.karinderya.R;
import com.oddlid.karinderya.models.ClusterMarker;

public class ClusterManagerRenderer extends DefaultClusterRenderer<ClusterMarker> {
    //private final IconGenerator iconGenerator;
    private ImageView imageView;
    private final int markerWidth, markerHeight;

    public ClusterManagerRenderer(Context context, GoogleMap map, ClusterManager<ClusterMarker> clusterManager) {
        super(context, map, clusterManager);

        //iconGenerator = new IconGenerator(context.getApplicationContext());
        imageView = new ImageView(context.getApplicationContext());
        markerWidth = (int) context.getResources().getDimension(R.dimen.marketWidth);
        markerHeight = (int) context.getResources().getDimension(R.dimen.marketHeight);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth, markerHeight));
        int padding = (int) context.getResources().getDimension(R.dimen.materialPadding);
        imageView.setPadding(padding, padding, padding, padding);
        //iconGenerator.setContentView(imageView);
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterMarker item, MarkerOptions markerOptions) {
        //imageView.setImageResource(item.getIconPicture());

        /*Picasso.get()
                .load(item.getIconPicture())
                .fit().centerCrop()
                .into(imageView);*/

        //Bitmap icon = iconGenerator.makeIcon();
        markerOptions.title(item.getTitle());

    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<ClusterMarker> cluster) {
        return false;
    }
}
