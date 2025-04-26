package com.example.ecommerce

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CategoryFragment : Fragment() {
    private var categoryRecyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_user_category, container, false)

        categoryRecyclerView = view.findViewById<RecyclerView?>(R.id.category_recycler_view)


        // Thiết lập RecyclerView cho danh mục
        setupCategoryRecyclerView()

        return view
    }

    private fun setupCategoryRecyclerView() {
        val gridLayoutManager = GridLayoutManager(getContext(), 2)
        categoryRecyclerView!!.setLayoutManager(gridLayoutManager)


        // CategoryListAdapter adapter = new CategoryListAdapter(getCategoryList());
        // categoryRecyclerView.setAdapter(adapter);
    }
}