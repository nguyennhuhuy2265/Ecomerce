package com.example.ecommerce

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NotificationFragment : Fragment() {
    private var notificationRecyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_user_notification, container, false)

        notificationRecyclerView = view.findViewById<RecyclerView?>(R.id.notification_recycler_view)


        // Thiết lập RecyclerView cho thông báo
        setupNotificationRecyclerView()

        return view
    }

    private fun setupNotificationRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(getContext())
        notificationRecyclerView!!.setLayoutManager(linearLayoutManager)


        // NotificationAdapter adapter = new NotificationAdapter(getNotificationList());
        // notificationRecyclerView.setAdapter(adapter);
    }
}