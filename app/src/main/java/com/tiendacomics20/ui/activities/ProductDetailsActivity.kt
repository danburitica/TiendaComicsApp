package com.tiendacomics20.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.tiendacomics20.R
import com.tiendacomics20.firestore.FirestoreClass
import com.tiendacomics20.models.Product
import com.tiendacomics20.ui.fragments.OrdersFragment
import com.tiendacomics20.utils.Constants
import com.tiendacomics20.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_product_details.*
import kotlinx.android.synthetic.main.activity_user_profile.*

class ProductDetailsActivity : BaseActivity() {

    private var mProductId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)){
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
        }

        getProductDetails()

        btn_buy.setOnClickListener{
            val dialog = AlertDialog.Builder(this)
                .setTitle("Información")
                .setMessage("Próximamente podrás comprar este producto a través de Tienda Comics")
                .setIcon(R.drawable.ic_info)
                .setPositiveButton("Aceptar") { view, _ ->
                    view.dismiss()
                }
                .setCancelable(false)
                .create()

            dialog.show()
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_product_details_activity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toolbar_product_details_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getProductDetails(){
        showProgressDialog("Por favor espera...")
        FirestoreClass().getProductDetails(this, mProductId)
    }

    @SuppressLint("SetTextI18n")
    fun productDetailsSuccess(product: Product){
        hideProgressDialog()
        GlideLoader(this).loadProductPicture(
            product.image,
            iv_product_detail_image
        )
        tv_product_details_title.text = product.title
        tv_product_details_price.text = "$ ${product.price}"
        tv_product_details_stock.text = "Cantidad disponible: ${product.stock_quantity}"
        tv_product_details_description.text = product.description
    }
}