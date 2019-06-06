package com.example.abhishekaryan.contextualactionbar;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private HashSet<Customer> slectedCustomer;
    private CustomerAdapter adapter;
    private ActionMode actionMode;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        slectedCustomer=new HashSet<>();
        adapter=new CustomerAdapter();
        for(int i=0;i<50;i++){

            adapter.add(new Customer("Cutomer" + Integer.toString(i+1)));
        }
        listView=(ListView)findViewById(R.id.activity_main_listview);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Customer customer=adapter.getItem(position);
                if(actionMode !=null){
                    toggleCustomerSlection(customer);
                }
                else {
                    showCustomer(customer);
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Customer customer=adapter.getItem(position);
                toggleCustomerSlection(customer);
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.activity_main_menu_add_cust){

            adapter.add(new Customer("Inserted Customer"));

            return true;

        }
        return true ;

    }

    private void showCustomer(Customer customer){

        Toast.makeText(this,"showing customer " + customer.getName(),Toast.LENGTH_SHORT).show();

    }
    private void deleteCustomer(Iterable<Customer> customers){

        adapter.setNotifyOnChange(false);
        for(Customer customer:customers){
            adapter.remove(customer);
        }
        adapter.setNotifyOnChange(true);
        adapter.notifyDataSetChanged();


    }
    private void toggleCustomerSlection(Customer customer){

        if(slectedCustomer.contains(customer)){

           slectedCustomer.remove(customer);
        }
        else {
            slectedCustomer.add(customer);
        }

        if(slectedCustomer.size()==0 && actionMode !=null){

            actionMode.finish();
            return;
        }
        if(actionMode==null){

            actionMode=startSupportActionMode(new CustomerActionModeCallback());
        }
        else {

            actionMode.invalidate();
        }
        adapter.notifyDataSetChanged();

    }

    private class CustomerAdapter extends ArrayAdapter<Customer>{

        public CustomerAdapter() {
            super(MainActivity.this,android.R.layout.simple_list_item_1);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view= super.getView(position, convertView, parent);
            Customer customer=adapter.getItem(position);
            if(slectedCustomer.contains(customer)){

                view.setBackgroundColor(Color.parseColor("#B2EBF2"));
            }
            else {
                view.setBackground(null);
            }

            return view;
        }
    }

    private class CustomerActionModeCallback implements ActionMode.Callback{

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
           getMenuInflater().inflate(R.menu.menu_main_cutomer,menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

            if(slectedCustomer.size()==1){

                menu.setGroupVisible(R.id.menu_main_customer_singleOnlyGroup,true);
            }
            else {
                menu.setGroupVisible(R.id.menu_main_customer_singleOnlyGroup,false);
            }
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            int id=item.getItemId();
            if(id==R.id.menu_main_cust_del){

                deleteCustomer(slectedCustomer);
                actionMode.finish();
                return true;
            }

            if(id==R.id.menu_main_cust_show){

                if(slectedCustomer.size()!=1){
                    throw new RuntimeException("the show button on be pressed if one cutomer slected");
                }

                Customer customer=slectedCustomer.iterator().next();
                showCustomer(customer);
                actionMode.finish();
                return true;
            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

            actionMode=null;
            slectedCustomer.clear();
            adapter.notifyDataSetChanged();

        }
    }
}
