package com.tiendacomics20.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiendacomics20.R
import com.tiendacomics20.databinding.FragmentProductsBinding
import com.tiendacomics20.firestore.FirestoreClass
import com.tiendacomics20.models.Product
import com.tiendacomics20.ui.activities.AddProductActivity
import com.tiendacomics20.ui.adapters.MyProductsListAdapter
import kotlinx.android.synthetic.main.fragment_products.*

class ProductsFragment : BaseFragment() {

//    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentProductsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    private fun showAlertDialogToDeleteProduct(productID: String) {

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Eliminar")
        builder.setMessage("¿Está seguro que quiere eliminar el producto?")
        builder.setIcon(R.drawable.ic_caution)

        builder.setPositiveButton("Sí") { dialogInterface, _ ->

            showProgressDialog("Por favor espera...")
            FirestoreClass().deleteProduct(this, productID)
            dialogInterface.dismiss()
        }

        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun deleteProduct(productID: String) {
        showAlertDialogToDeleteProduct(productID)
    }

    fun productDeleteSuccess() {
        hideProgressDialog()

        Toast.makeText(
            requireActivity(),
            "¡El producto ha sido eliminado con éxito!",
            Toast.LENGTH_SHORT
        ).show()

        getProductsListFromFireStore()
    }

    fun successProductsListFromFireStore(productsList: ArrayList<Product>){
        hideProgressDialog()

        if (productsList.size > 0){
            rv_explore_products.visibility = View.VISIBLE
            tv_no_products_found.visibility = View.GONE

            rv_explore_products.layoutManager = LinearLayoutManager(activity)
            rv_explore_products.setHasFixedSize(true)
            val adapterProducts = MyProductsListAdapter(requireActivity(), productsList, this)
            rv_explore_products.adapter = adapterProducts
        }else{
            rv_explore_products.visibility = View.GONE
            tv_no_products_found.visibility = View.VISIBLE
        }
    }

    private fun getProductsListFromFireStore(){
        showProgressDialog("Por favor espera...")
        FirestoreClass().getProductsList(this)
    }

    override fun onResume() {
        super.onResume()
        getProductsListFromFireStore()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when(id){
            R.id.action_add_product -> {
                startActivity(Intent(activity, AddProductActivity::class.java))
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}