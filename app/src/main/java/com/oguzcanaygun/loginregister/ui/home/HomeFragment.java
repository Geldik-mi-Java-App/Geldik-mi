package com.oguzcanaygun.loginregister.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.oguzcanaygun.loginregister.MyAdapter;
import com.oguzcanaygun.loginregister.R;
import com.oguzcanaygun.loginregister.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<String> dataList; // Store data for the RecyclerView
    private int selectedPosition = -1; // Track the selected item position

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        recyclerView = binding.recyclerView;
        dataList = getSampleData();
        adapter = new MyAdapter(dataList, new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Handle item click, update selected position
                selectedPosition = position;
                adapter.setSelectedItem(selectedPosition);
                updateRemoveButtonVisibility(selectedPosition != RecyclerView.NO_POSITION);
            }

            @Override
            public int getSelectedPosition() {
                return selectedPosition;
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Button addButton = root.findViewById(R.id.buttonAddFriend);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddFriendDialog();
            }
        });

        Button removeButton = root.findViewById(R.id.buttonRemoveFriend);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFriend();
            }
        });

        // Initially, disable the remove button
            updateRemoveButtonVisibility(false);

        return root;
    }

    private List<String> getSampleData() {
        List<String> data = new ArrayList<>();
        data.add("Item 1");
        data.add("Item 2");
        data.add("Item 3");
        return data;
    }

    // Button click to add a friend
    private void showAddFriendDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle("Add Friend");
        builder.setMessage("Enter the username:");

        // Inflate the dialog layout
        View viewInflated = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_friend, (ViewGroup) getView(), false);

        final EditText input = viewInflated.findViewById(R.id.input);
        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            String username = input.getText().toString();
            if (!username.isEmpty()) {
                addFriend(username);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Button click to remove a friend
    private void removeFriend() {
        if (selectedPosition != -1) {
            dataList.remove(selectedPosition);
            adapter.notifyDataSetChanged();
            selectedPosition = -1;
            updateRemoveButtonVisibility(false);
        }
    }

    // Update remove button visibility based on item selection
    public void updateRemoveButtonVisibility(boolean isVisible) {
        if (getView() != null) {
            Button removeButton = getView().findViewById(R.id.buttonRemoveFriend);
            if (removeButton != null) {
                removeButton.setVisibility(isVisible ? View.VISIBLE : View.GONE);
            }
        }
    }

    // Add a friend to the RecyclerView
    private void addFriend(String username) {
        dataList.add(username);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}