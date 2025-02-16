package com.c323FinalProject.larolsoncharfran;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.c323FinalProject.larolsoncharfran.BackgroundService.haversineDistance;

public class CustomMarkerInfoWindowView implements GoogleMap.InfoWindowAdapter {
    private final View markerItemView;

    public CustomMarkerInfoWindowView(Activity activity) {
        markerItemView = activity.getLayoutInflater().inflate(R.layout.marker_task, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        Task task = (Task) marker.getTag();
        if (task == null) return markerItemView;
        TextView title = markerItemView.findViewById(R.id.title);
        TextView address = markerItemView.findViewById(R.id.address);
        TextView description = markerItemView.findViewById(R.id.description);
        CircleImageView image = markerItemView.findViewById(R.id.image);
        TextView distance = markerItemView.findViewById(R.id.distance);

        title.setText("Title: " + task.getTitle());
        address.setText("Address: " + task.getAddressName());
        description.setText("Description: " + task.getDescription());
        if (task.getImage() != null) {
            image.setImageBitmap(task.getImage());
        } else {
            image.setImageResource(R.drawable.ic_task);
        }

        double kilometerDistance = haversineDistance(new LatLng(BackgroundService.location.getLatitude(), BackgroundService.location.getLongitude()), task.getLocation());
        double milesDistance = kilometerDistance * 0.621;
        distance.setText(String.valueOf(milesDistance).substring(0, String.valueOf(milesDistance).indexOf(".")) + "mi");
        return markerItemView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
