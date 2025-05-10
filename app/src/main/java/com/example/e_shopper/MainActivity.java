package com.example.e_shopper;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private SearchView searchView;
    private List<ProductModel> originalProductList = new ArrayList<>();
    private DatabaseReference databaseRef;
    private HomeFragment homeFragment;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private SearchDebouncer searchDebouncer;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupToolbar();
        setupFirebase();
        setupBottomNavigation();
        setupSearchDebouncer();

        mAuth = FirebaseAuth.getInstance();
        checkUserAuthentication();

        if (savedInstanceState == null) {
            homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, homeFragment)
                    .commit();
            loadProducts();
        }
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        bottomNav = findViewById(R.id.bottomNavigation);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbarTitle.setText("E-Shopper");
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("products");
        databaseRef.keepSynced(true);
    }

    private void checkUserAuthentication() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, AuthActivity.class));
            finish();
        } else {
            setupAuthStateListener();
        }
    }

    private void setupAuthStateListener() {
        authStateListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                startActivity(new Intent(MainActivity.this, AuthActivity.class));
                finish();
            }
        };
        mAuth.addAuthStateListener(authStateListener);
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_category) {
                selectedFragment = new CategoriesFragment();
            } else if (itemId == R.id.nav_cart) {
                selectedFragment = new CartFragment();
            } else if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
                homeFragment = (HomeFragment) selectedFragment;
                loadProducts();
            } else if (itemId == R.id.nav_order) {
                selectedFragment = new OrderFragment();
            } else if (itemId == R.id.nav_settings) {
                selectedFragment = new SettingsFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in_left,
                                R.anim.slide_out_left,
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                        )
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

        // Set default selection
        bottomNav.setSelectedItemId(R.id.nav_home);
    }

    private void setupSearchDebouncer() {
        searchDebouncer = new SearchDebouncer(text -> {
            if (homeFragment != null && homeFragment.isAdded()) {
                filterProducts(text);
            }
        });
    }

    private void loadProducts() {
        if (homeFragment != null) {
            homeFragment.showLoading();
        }

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ProductModel> productList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ProductModel product = dataSnapshot.getValue(ProductModel.class);
                    if (product != null) {
                        product.setId(dataSnapshot.getKey());
                        productList.add(product);
                    }
                }
                originalProductList = new ArrayList<>(productList);
                updateHomeFragment(productList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                runOnUiThread(() -> {
                    if (homeFragment != null) {
                        homeFragment.hideLoading();
                        homeFragment.showError("Failed to load products: " + error.getMessage());
                    }
                });
            }
        });
    }

    private void updateHomeFragment(List<ProductModel> products) {
        runOnUiThread(() -> {
            if (homeFragment != null && homeFragment.isAdded()) {
                homeFragment.setOriginalProductList(products);
                homeFragment.hideLoading();
            }
        });
    }

    private void filterProducts(String query) {
        if (homeFragment == null || originalProductList.isEmpty()) return;

        if (query.isEmpty()) {
            homeFragment.setFilteredProductList(originalProductList);
            return;
        }

        String lowercaseQuery = query.toLowerCase().trim();
        List<ProductModel> filteredList = originalProductList.stream()
                .filter(product ->
                        product.getName().toLowerCase().contains(lowercaseQuery) ||
                                product.getCategory().toLowerCase().contains(lowercaseQuery) ||
                                product.getDescription().toLowerCase().contains(lowercaseQuery))
                .collect(Collectors.toList());

        homeFragment.setFilteredProductList(filteredList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        setupSearchView(menu);
        return true;
    }

    private void setupSearchView(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        if (searchView != null) {
            searchView.setMaxWidth(Integer.MAX_VALUE);
            searchView.setQueryHint("Search products by name, category...");
            setupSearchViewListeners();
            customizeSearchViewAppearance();
        }
    }

    private void setupSearchViewListeners() {
        searchView.setOnSearchClickListener(v -> toolbarTitle.setVisibility(View.GONE));
        searchView.setOnCloseListener(() -> {
            toolbarTitle.setVisibility(View.VISIBLE);
            return false;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchDebouncer.processQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchDebouncer.processQuery(newText);
                return true;
            }
        });
    }

    private void customizeSearchViewAppearance() {
        View searchPlate = searchView.findViewById(androidx.appcompat.R.id.search_plate);
        searchPlate.setBackgroundResource(R.drawable.search_view_background);

        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        searchIcon.setColorFilter(ContextCompat.getColor(this, R.color.white));

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(ContextCompat.getColor(this, R.color.white));
        searchEditText.setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
        searchDebouncer.destroy();
        if (authStateListener != null) {
            mAuth.removeAuthStateListener(authStateListener);
        }
    }
}






//package com.example.e_shopper;
//
//import android.os.Bundle;
//import com.example.e_shopper.R;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.SearchView;
//import androidx.appcompat.widget.Toolbar;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import java.util.ArrayList;
//import java.util.List;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.ValueEventListener;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.stream.Collectors;
//
//
//public class MainActivity extends AppCompatActivity {
//    private BottomNavigationView bottomNav;
//    private FirebaseAuth mAuth;
//    private Toolbar toolbar;
//    private TextView toolbarTitle;
//    private SearchView searchView;
//    private List<ProductModel> originalProductList = new ArrayList<>();
//    private DatabaseReference databaseRef;
//    private HomeFragment homeFragment;
//    private ExecutorService executorService = Executors.newSingleThreadExecutor();
//    private SearchDebouncer searchDebouncer;
//    private FirebaseAuth.AuthStateListener authStateListener;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        initializeViews();
//        setupToolbar();
//        setupFirebase();
//        setupBottomNavigation();
//        setupSearchDebouncer();
//
//        // Start with HomeFragment
//        if (savedInstanceState == null) {
//            homeFragment = new HomeFragment();
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, homeFragment)
//                    .commit();
//            loadProducts();
//        }
//        mAuth = FirebaseAuth.getInstance();
//
//        // Check if user is already logged in
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser == null) {
//            // Show login fragment only if user is not logged in
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, new LoginFragment())
//                    .commit();
//        } else {
//            // Show main content (e.g., home fragment)
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, new HomeFragment())
//                    .commit();
//        }
//    }
//
//    private void initializeViews() {
//        toolbar = findViewById(R.id.toolbar);
//        toolbarTitle = findViewById(R.id.toolbar_title);
//        bottomNav = findViewById(R.id.bottomNavigation);
//    }
//
//    private void setupToolbar() {
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayShowTitleEnabled(false);
//        }
//        toolbarTitle.setText("E-Shopper");
//    }
//
//    private void setupFirebase() {
//        mAuth = FirebaseAuth.getInstance();
//        databaseRef = FirebaseDatabase.getInstance().getReference().child("products");
//    }
//
//    private void setupBottomNavigation() {
//        bottomNav.setOnItemSelectedListener(item -> {
//            Fragment selectedFragment = null;
//            int itemId = item.getItemId();
//
//            if (itemId == R.id.nav_home) {
//                selectedFragment = new HomeFragment();
//            } else if (itemId == R.id.nav_categories) {
//                selectedFragment = new CategoriesFragment();
//            } else if (itemId == R.id.nav_cart) {
//                selectedFragment = new CartFragment();
//            } else if (itemId == R.id.nav_settings) {
//                selectedFragment = new SettingsFragment();
//            }
//
//            if (selectedFragment != null) {
//                getSupportFragmentManager().beginTransaction()
//                        .setCustomAnimations(
//                                R.anim.slide_in_left,
//                                R.anim.slide_out_left,
//                                R.anim.slide_in_right,
//                                R.anim.slide_out_left
//                        )
//                        .replace(R.id.fragment_container, selectedFragment)
//                        .commit();
//                return true;
//            }
//            return false;
//        });
//    }
//
//    private void setupSearchDebouncer() {
//        searchDebouncer = new SearchDebouncer(text -> {
//            if (homeFragment != null && homeFragment.isAdded()) {
//                filterProducts(text);
//            }
//        });
//    }
//
//    private void loadProducts() {
//        if (homeFragment != null) {
//            homeFragment.showLoading();
//        }
//        executorService.execute(() -> {
//            databaseRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot snapshot) {
//                    List<ProductModel> productList = new ArrayList<>();
//                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                        ProductModel product = dataSnapshot.getValue(ProductModel.class);
//                        if (product != null) {
//                            product.setId(dataSnapshot.getKey());
//                            productList.add(product);
//                        }
//                    }
//                    originalProductList = new ArrayList<>(productList);
//                    updateHomeFragment(productList);
//                }
//
//                @Override
//                public void onCancelled(DatabaseError error) {
//                    runOnUiThread(() -> {
//                        if (homeFragment != null) {
//                            homeFragment.hideLoading();
//                            homeFragment.showError("Failed to load products: " + error.getMessage());
//                        }
//                    });
//                }
//            });
//        });
//    }
//
//    private void updateHomeFragment(List<ProductModel> products) {
//        runOnUiThread(() -> {
//            if (homeFragment != null && homeFragment.isAdded()) {
//                homeFragment.setOriginalProductList(products);
//                homeFragment.hideLoading();
//            }
//        });
//    }
//
//    private void filterProducts(String query) {
//        if (homeFragment == null || originalProductList.isEmpty()) return;
//
//        if (query.isEmpty()) {
//            homeFragment.setFilteredProductList(originalProductList);
//            return;
//        }
//
//        String lowercaseQuery = query.toLowerCase().trim();
//        List<ProductModel> filteredList = originalProductList.stream()
//                .filter(product ->
//                        product.getName().toLowerCase().contains(lowercaseQuery) ||
//                                product.getCategory().toLowerCase().contains(lowercaseQuery) ||
//                                product.getDescription().toLowerCase().contains(lowercaseQuery))
//                .collect(Collectors.toList());
//
//        homeFragment.setFilteredProductList(filteredList);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.search_menu, menu);
//        setupSearchView(menu);
//        return true;
//    }
//
//    private void setupSearchView(Menu menu) {
//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        searchView = (SearchView) searchItem.getActionView();
//
//        if (searchView != null) {
//            searchView.setMaxWidth(Integer.MAX_VALUE);
//            searchView.setQueryHint("Search products by name, category...");
//
//            setupSearchViewListeners();
//            customizeSearchViewAppearance();
//        }
//    }
//
//    private void setupSearchViewListeners() {
//        searchView.setOnSearchClickListener(v -> toolbarTitle.setVisibility(View.GONE));
//        searchView.setOnCloseListener(() -> {
//            toolbarTitle.setVisibility(View.VISIBLE);
//            return false;
//        });
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                searchDebouncer.processQuery(query);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                searchDebouncer.processQuery(newText);
//                return true;
//            }
//        });
//    }
//
//    private void customizeSearchViewAppearance() {
//        View searchPlate = searchView.findViewById(androidx.appcompat.R.id.search_plate);
//        searchPlate.setBackgroundResource(R.drawable.search_view_background);
//
//        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
//        searchIcon.setColorFilter(ContextCompat.getColor(this, R.color.white));
//
//        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
//        searchEditText.setTextColor(ContextCompat.getColor(this, R.color.white));
//        searchEditText.setHintTextColor(ContextCompat.getColor(this, R.color.colorAccent));
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        executorService.shutdown();
//        searchDebouncer.destroy();
//    }
//}
