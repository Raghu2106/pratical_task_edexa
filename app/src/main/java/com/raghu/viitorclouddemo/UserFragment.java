package com.raghu.viitorclouddemo;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.raghu.viitorclouddemo.retrofit.EmployeeModel;

import java.util.ArrayList;
import java.util.List;

import okhttp3.internal.Util;

public class UserFragment extends Fragment {
    private static final String TAG = UserFragment.class.getSimpleName();
    private View view;
    private ArrayList<EmployeeModel> mainEmployeeList = new ArrayList<>();
    private RecyclerView recyclerView;
    private EditText edtName;
    private MyListAdapter adapter;

    ActionCallback actionCallback;

    public UserFragment(ArrayList<EmployeeModel> employeeList) {
        this.mainEmployeeList = employeeList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user, container, false);

        actionCallback = new ActionCallback();
        edtName = view.findViewById(R.id.edtName);
        recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new MyListAdapter();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        adapter.setList(mainEmployeeList);

        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String strName = edtName.getText().toString().trim();
                if (strName == null || strName.length() == 0) {
                    adapter.setList(mainEmployeeList);
                } else {
                    ArrayList<EmployeeModel> searchEmployeeList = new ArrayList<>();
                    for (int i = 0; i < mainEmployeeList.size(); i++) {
                        if (mainEmployeeList.get(i).getFirstName().toLowerCase().contains(strName.toLowerCase())) {
                            searchEmployeeList.add(mainEmployeeList.get(i));
                        }
                    }
                    adapter.setList(searchEmployeeList);
                }
            }
        });
        return view;
    }

    class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
        ArrayList<EmployeeModel> employeeList = new ArrayList<>();
        private SparseBooleanArray selectedItems;
        private int selectedIndex = -1;

        public MyListAdapter() {
            selectedItems = new SparseBooleanArray();
        }

        void setList(ArrayList<EmployeeModel> employeeList) {
            this.employeeList = employeeList;
            notifyDataSetChanged();
        }

        public void clearSelection() {
            selectedItems.clear();
            notifyDataSetChanged();
        }

        public void toggleSelection(int position) {
            selectedIndex = position;
            if (selectedItems.get(position, false)) {
                selectedItems.delete(position);
            } else {
                selectedItems.put(position, true);
            }
            notifyItemChanged(position);
        }

        public int selectedItemCount() {
            return selectedItems.size();
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employee, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.txtCity.setText(employeeList.get(position).getCity());
            holder.txtFName.setText(employeeList.get(position).getFirstName());
            holder.txtLName.setText(employeeList.get(position).getLastName());

            if (selectedItems.get(position, false)) {
                holder.imgChecked.setVisibility(View.VISIBLE);
                if (selectedIndex == position) selectedIndex = -1;
            } else {
                holder.imgChecked.setVisibility(View.GONE);
                if (selectedIndex == position) selectedIndex = -1;
            }
        }

        @Override
        public int getItemCount() {
            return employeeList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            TextView txtCity, txtFName, txtLName;
            private ImageView imgChecked;

            public ViewHolder(View itemView) {
                super(itemView);
                imgChecked = itemView.findViewById(R.id.imgChecked);
                txtFName = itemView.findViewById(R.id.txtFName);
                txtCity = itemView.findViewById(R.id.txtCity);
                txtLName = itemView.findViewById(R.id.txtLName);

                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if (view == itemView) {
                    if (adapter.selectedItemCount() > 0) {
                        toggleActionBar(getAdapterPosition());
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Name: ");
                        builder.setMessage(employeeList.get(getAdapterPosition()).getFirstName());
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                    }
                }
            }

            @Override
            public boolean onLongClick(View v) {
                toggleActionBar(getAdapterPosition());
                return false;
            }
        }
    }

    private void toggleActionBar(int position) {
        if (((MainActivity)getActivity()).actionMode == null) {
            ((MainActivity)getActivity()).actionMode = ((MainActivity) getActivity()).startSupportActionMode(actionCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        adapter.toggleSelection(position);
        int count = adapter.selectedItemCount();
        if (count == 0) {
            ((MainActivity)getActivity()).actionMode.finish();
        } else {
            ((MainActivity)getActivity()).actionMode.setTitle(String.valueOf(count));
            ((MainActivity)getActivity()).actionMode.invalidate();
        }
    }

    private class ActionCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.select_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.clearSelection();
            ((MainActivity)getActivity()).actionMode = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();

    }
}