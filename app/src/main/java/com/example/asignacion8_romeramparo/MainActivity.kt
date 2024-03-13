package com.example.asignacion8_romeramparo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val firstFragment: FirstFragment = FirstFragment()
    private val secondFragment: SecondFragment = SecondFragment()
    private val thirdFragment: ThirdFragment = ThirdFragment()
    private val fourFragment: FourFragment = FourFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        loadFragment(firstFragment);
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.firstFragment -> {
                loadFragment(firstFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.secondFragment -> {
                loadFragment(secondFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.thirdFragment -> {
                loadFragment(thirdFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.fourFragment -> {
                loadFragment(fourFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, fragment)
        transaction.commit()
    }
}
