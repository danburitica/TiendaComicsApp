package com.tiendacomics20.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiendacomics20.R
import com.tiendacomics20.databinding.FragmentDashboardBinding
import com.tiendacomics20.firestore.FirestoreClass
import com.tiendacomics20.models.Product
import com.tiendacomics20.ui.activities.AccountActivity
import com.tiendacomics20.ui.activities.ProductDetailsActivity
import com.tiendacomics20.ui.adapters.DashboardItemsListAdapter
import com.tiendacomics20.ui.adapters.MyProductsListAdapter
import com.tiendacomics20.utils.Constants
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_products.*

class DashboardFragment : BaseFragment() {

//    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        getDashboardItemsList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        dashboardViewModel =
//            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when(id) {
            R.id.action_account -> {
                startActivity(Intent(activity, AccountActivity::class.java))
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun successDashboardItemsList(dashboardItemsList: ArrayList<Product>){
        hideProgressDialog()

        if (dashboardItemsList.size > 0){
            rv_dashboard_items.visibility = View.VISIBLE
            tv_no_dashboard_items_found.visibility = View.GONE

            rv_dashboard_items.layoutManager = LinearLayoutManager(activity)
            rv_dashboard_items.setHasFixedSize(true)
            val adapterProducts = DashboardItemsListAdapter(requireActivity(), dashboardItemsList)
            rv_dashboard_items.adapter = adapterProducts
        }else{
            rv_dashboard_items.visibility = View.GONE
            tv_no_dashboard_items_found.visibility = View.VISIBLE
        }
    }

    fun getDashboardItemsList(){
        showProgressDialog("Por favor espera...")

        FirestoreClass().getDashboardItemsList(this)
    }
}