package aodev.blue.rxsandbox.ui.screen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import aodev.blue.rxsandbox.R

class MainActivity : AppCompatActivity(), NavigationLabelListener {

    private val navController: NavController by lazy {
        findNavController(R.id.nav_host_fragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupActionBarWithNavController(navController)
    }

    override fun onSupportNavigateUp() = navController.navigateUp()

    override fun updateLabel(label: String) {
        supportActionBar?.title = label
    }
}

// This is temporary until Jetpack's Navigation supports dynamic titles
interface NavigationLabelListener {
    fun updateLabel(label: String)
}
