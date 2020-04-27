package com.example.movie

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import com.example.movie.adapter.SlidePagerAdapter
import com.example.movie.myFragments.LikeFragment
import com.example.movie.myFragments.MainFragment
import com.example.movie.myFragments.ProfileFragment
import com.example.movie.pager.LockableViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var pager: LockableViewPager
    private lateinit var pagerAdapter: PagerAdapter
    private var f1: Fragment = MainFragment()
    private var f2: Fragment = LikeFragment()
    private var f3: Fragment = ProfileFragment()
    private var list: MutableList<Fragment> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page)
        bindView()
        list.add(f1)
        list.add(f2)
        list.add(f3)
        pager.setSwipable(false)
        pagerAdapter = SlidePagerAdapter(supportFragmentManager, list)
        pager.adapter = pagerAdapter

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    pager.setCurrentItem(0, false)
                }
                R.id.like_posts -> {
                    pager.setCurrentItem(1, false)
                }
                R.id.about -> {
                    pager.setCurrentItem(2, false)
                }
            }
            false
        }
    }

    fun bindView() {
        pager = findViewById(R.id.pager)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
    }


}