package com.google.samples.quickstart.searchinapps.view;

import static com.google.android.libraries.searchinapps.SearchSuggestionsViewOptions.Layout.CAROUSEL;
import static com.google.android.libraries.searchinapps.SearchSuggestionsViewOptions.Layout.COMPACT_CAROUSEL;
import static com.google.android.libraries.searchinapps.SearchSuggestionsViewOptions.Layout.TILING;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.libraries.searchinapps.GetSearchSuggestionsViewGeneratorCallback;
import com.google.android.libraries.searchinapps.GetSearchSuggestionsViewOptions;
import com.google.android.libraries.searchinapps.GetTrendingSearchesViewOptions;
import com.google.android.libraries.searchinapps.SearchInAppsService;
import com.google.android.libraries.searchinapps.SearchSuggestionsViewGenerator;
import com.google.android.libraries.searchinapps.SearchSuggestionsViewOptions;

import java.util.Arrays;
import java.util.List;

public class NewsfeedActivity extends AppCompatActivity implements GetSearchSuggestionsViewGeneratorCallback{
    private SearchInAppsService service;
    private SearchInAppsViewModel model;
    private ConstraintLayout trendingContainer;

    public DrawerLayout drawerLayout;

    private Boolean includeCategory= true;
    private Boolean includeHeadline= true;
    private Boolean includeSnippet= true;

    class NewsfeedSuggestionHandler implements GetSearchSuggestionsViewGeneratorCallback{
        ConstraintLayout suggestionsView;

        NewsfeedSuggestionHandler(ConstraintLayout card){
            suggestionsView = card.findViewById(R.id.suggestions_container);
        }

        @Override
        public void onError(@NonNull String s) {
            Toast.makeText(NewsfeedActivity.this, s, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSuccess(@NonNull SearchSuggestionsViewGenerator searchSuggestionsViewGenerator) {
            model.setSearchSuggestionsViewGenerator(searchSuggestionsViewGenerator);
            suggestionsView.removeAllViews();
            View generatedView = searchSuggestionsViewGenerator.populateView(NewsfeedActivity.this);
            suggestionsView.addView(generatedView);
        }
    }

    @Override
    public void onSuccess(SearchSuggestionsViewGenerator generator) {
        model.setSearchSuggestionsViewGenerator(generator);
        trendingContainer.removeAllViews();
        trendingContainer.addView(generator.populateView(this));
    }

    @Override
    public void onError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newsfeed);

        populateDummyData();
        model = new ViewModelProvider(this).get(SearchInAppsViewModel.class);
        model.setCheckedLayout(TILING);

        trendingContainer = findViewById(R.id.trending_container);
        SearchSuggestionsViewOptions.Layout checkedLayout = model.getCheckedLayout();
        service = SearchInAppsService.create(this);
        service.getTrendingSearchesView(
                new GetTrendingSearchesViewOptions()
                        .setMaxNumTrends(6)
                        .setTrendingSearchesViewOptions(
                                new SearchSuggestionsViewOptions().setLayout(model.getCheckedLayout())),
                NewsfeedActivity.this);

        drawerLayout = findViewById(R.id.drawer_layout);
        ImageButton drawerButton = findViewById(R.id.menu_button);

        drawerButton.setOnClickListener(view -> drawerLayout.open());

        //Setup Drawer handlers
        setupIncludeCheckbox(R.id.checkbox_headline);
        setupIncludeCheckbox(R.id.checkbox_category);
        setupIncludeCheckbox(R.id.checkbox_snippet);
 }
    private void setupIncludeCheckbox(int id){

        CheckBox checkBox= findViewById(id);
        checkBox.setChecked(true);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switch (id){
                    case R.id.checkbox_headline:
                        includeHeadline= b;
                        break;
                    case R.id.checkbox_category:
                        includeCategory= b;
                        break;
                    case R.id.checkbox_snippet:
                        includeSnippet= b;
                        break;
                    default:
                }
            }
        });

    }

    private void setCardListener(ConstraintLayout card){
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = getSuggestionStringForCard((ConstraintLayout) view);
                List<String> searchContext = Arrays.asList(input);
                service.getSearchSuggestionsView(
                        new GetSearchSuggestionsViewOptions()
                                .setTextContext(searchContext)
                                .setSearchSuggestionsViewOptions(
                                        new SearchSuggestionsViewOptions().setLayout(model.getCheckedLayout())),
                        new NewsfeedSuggestionHandler(card));
            }
        });
    }

    private String getSuggestionStringForCard(ConstraintLayout cardView){
        String returnString= "";

        if(includeCategory){
            returnString += ((TextView)cardView.findViewById(R.id.category)).getText() + " ";
        }
        if(includeHeadline){
            returnString += ((TextView)cardView.findViewById(R.id.headline)).getText() + " ";
        }
        if(includeSnippet) {
            returnString += ((TextView) cardView.findViewById(R.id.snippet)).getText();
        }

        EditText tv = findViewById(R.id.last_input);
        tv.setText(returnString);
        return returnString;
    }



    public SearchInAppsViewModel getModel() {
        return model;
    }

    public void onRadioButtonClicked(View view) {
        switch (view.getId()) {

            case R.id.carousel_layout:
                model.setCheckedLayout(CAROUSEL);
                break;
            case R.id.tiling_layout:
                model.setCheckedLayout(TILING);
                break;
            default:
                model.setCheckedLayout(COMPACT_CAROUSEL);
        }

    }

    private void populateDummyData(){
        ConstraintLayout nc1= findViewById(R.id.newscard_1);
        ((TextView) nc1.findViewById(R.id.category)).setText(R.string.category_1);
        ((TextView) nc1.findViewById(R.id.headline)).setText(R.string.newsfeed_headline_1);
        ((TextView) nc1.findViewById(R.id.snippet)).setText(R.string.newsfeed_snippet_1);
        ((ImageView) nc1.findViewById(R.id.imageView)).setImageResource(R.drawable.spacex);

        ConstraintLayout nc2= findViewById(R.id.newscard_2);
        ((TextView) nc2.findViewById(R.id.category)).setText(R.string.category_2);
        ((TextView) nc2.findViewById(R.id.headline)).setText(R.string.newsfeed_headline_2);
        ((TextView) nc2.findViewById(R.id.snippet)).setText(R.string.newsfeed_snippet_2);
        ((ImageView) nc2.findViewById(R.id.imageView)).setImageResource(R.drawable.pills);

        ConstraintLayout nc3= findViewById(R.id.newscard_3);
        ((TextView) nc3.findViewById(R.id.category)).setText(R.string.category_3);
        ((TextView) nc3.findViewById(R.id.headline)).setText(R.string.newsfeed_headline_3);
        ((TextView) nc3.findViewById(R.id.snippet)).setText(R.string.newsfeed_snippet_3);
        ((ImageView) nc3.findViewById(R.id.imageView)).setImageResource(R.drawable.tiktok);

        //Setup handlers for News cards
        setCardListener(nc1);
        setCardListener(nc2);
        setCardListener(nc3);
    }
}
