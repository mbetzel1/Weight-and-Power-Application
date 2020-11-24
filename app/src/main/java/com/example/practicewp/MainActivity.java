package com.example.practicewp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {
    private static final Migration MIGRATION_1_2 = new Migration (1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE saved ADD COLUMN seatsWt INTEGER DEFAULT 0");
            database.execSQL("ALTER TABLE saved ADD COLUMN lDasDrag REAL DEFAULT 0");
            database.execSQL("ALTER TABLE saved ADD COLUMN rDasDrag REAL DEFAULT 0");
            database.execSQL("ALTER TABLE saved ADD COLUMN lPodDrag REAL DEFAULT 0");
            database.execSQL("ALTER TABLE saved ADD COLUMN rPodDrag REAL DEFAULT 0");
            database.execSQL("ALTER TABLE saved ADD COLUMN lGunDrag REAL DEFAULT 0");
            database.execSQL("ALTER TABLE saved ADD COLUMN rGunDrag REAL DEFAULT 0");

        }
    };
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabItem configTab;
    private TabItem wpTab;
    private PageAdapter pageAdapter;
    public static WPDatabase wpDatabase;
    private Configuration configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wpDatabase = Room.databaseBuilder(getApplicationContext(), WPDatabase.class, "saved")
                .allowMainThreadQueries().fallbackToDestructiveMigration().build();
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        configuration = new Configuration("Empty", 0, 0, 0,
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 3);
        configTab = (TabItem) findViewById(R.id.config_tab);
        wpTab = (TabItem) findViewById(R.id.weight_power_tab);
        viewPager = findViewById(R.id.view_pager);
        pageAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        //tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(pageAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    pageAdapter.notifyDataSetChanged();
                }
                else if (tab.getPosition() == 1) {
                    pageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }
    public void setConfiguration(Configuration c){
        configuration = c;
    }
    public Configuration getConfiguration() {
        return configuration;
    }
}