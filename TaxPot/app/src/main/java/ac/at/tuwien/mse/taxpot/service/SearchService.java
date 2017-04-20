package ac.at.tuwien.mse.taxpot.service;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by markj on 4/20/2017.
 */

public class SearchService implements FloatingSearchView.OnQueryChangeListener,
                                        FloatingSearchView.OnSearchListener{

    private FloatingSearchView searchBar;
    private GoogleApiClient client;
    private GoogleMap mMap;
    private Marker positionMarker;

    private final LatLngBounds VIENNA = new LatLngBounds.Builder()
                                        .include(new LatLng(16.272469, 48.265241))
                                        .include(new LatLng(16.489449, 48.141678))
                                        .build();

    //private final LatLngBounds VIENNA = new LatLngBounds(new LatLng(48.128912, 16.255474),
    //                                                   new LatLng(48.279473, 16.490307));

    public SearchService(GoogleApiClient client, GoogleMap mMap, FloatingSearchView searchBar){
        super();
        this.client = client;
        this.mMap = mMap;
        this.searchBar = searchBar;
    }

    @Override
    public void onSearchTextChanged(String oldQuery, String newQuery) {
        PendingResult<AutocompletePredictionBuffer> results = Places.GeoDataApi.getAutocompletePredictions(client,
                                                                    newQuery, VIENNA,
                                                                    new AutocompleteFilter.Builder()
                                                                            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE).setCountry("AT").build());


        results.setResultCallback(new OnResultCallback());
    }

    @Override
    public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
        PlaceSuggestion placeSuggestion = (PlaceSuggestion) searchSuggestion;

        PendingResult<PlaceBuffer> place = Places.GeoDataApi.getPlaceById(client, placeSuggestion.getPlaceId());
        place.setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(@NonNull PlaceBuffer places) {
                Iterator<Place> it = places.iterator();
                while(it.hasNext()){
                    Place place = it.next();

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                    if(positionMarker == null)
                        positionMarker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));

                    positionMarker.setPosition(place.getLatLng());
                    positionMarker.setTitle(place.getName().toString());
                }
            }
        });
    }

    @Override
    public void onSearchAction(String currentQuery) {

    }

    private class OnResultCallback implements ResultCallback<AutocompletePredictionBuffer>{

        @Override
        public void onResult(@NonNull AutocompletePredictionBuffer autocompletePredictions) {

            List<PlaceSuggestion> suggestions = new ArrayList<>();

            Iterator<AutocompletePrediction> it = autocompletePredictions.iterator();
            while(it.hasNext()){
                final AutocompletePrediction prediction = it.next();

                PlaceSuggestion suggestion = new PlaceSuggestion(prediction.getPlaceId(), prediction.getFullText(null).toString());
                suggestions.add(suggestion);
            }
            searchBar.swapSuggestions(suggestions);
        }
    }

    public static class PlaceSuggestion implements SearchSuggestion {

        private String placeId;
        private String placeName;

        public PlaceSuggestion(String placeId, String placeName) {
            this.placeId = placeId;
            this.placeName = placeName;
        }

        public PlaceSuggestion(Parcel source) {
            List<String> vals = new ArrayList<>();
            source.readStringList(vals);
            Log.d("TaxPot", "result size: "+vals.size());
            this.placeId = vals.size() > 0 ? vals.get(0) : "empty";
            this.placeName = vals.size() > 1 ? vals.get(1) : "empty";
        }

        public String getPlaceId() {
            return placeId;
        }

        public void setPlaceId(String placeId) {
            this.placeId = placeId;
        }

        public String getPlaceName() {
            return placeName;
        }

        public void setPlaceName(String placeName) {
            this.placeName = placeName;
        }

        @Override
        public String getBody() {
            return placeName;
        }

        public static final Creator<PlaceSuggestion> CREATOR = new Creator<PlaceSuggestion>() {
            @Override
            public PlaceSuggestion createFromParcel(Parcel in) {
                return new PlaceSuggestion(in);
            }

            @Override
            public PlaceSuggestion[] newArray(int size) {
                return new PlaceSuggestion[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            List<String> vals = new ArrayList<>();
            vals.add(placeId);
            vals.add(placeName);
            dest.writeStringList(vals);
        }
    }
}
