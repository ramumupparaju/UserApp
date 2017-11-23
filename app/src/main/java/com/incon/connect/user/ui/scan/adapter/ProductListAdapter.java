package com.incon.connect.user.ui.scan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.incon.connect.user.R;
import com.incon.connect.user.apimodel.components.productinforesponse.ProductInfoResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by PC on 11/22/2017.
 */

public class ProductListAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<String> _listTitle;
    private HashMap<String, List<String>> _listDec;
    private List<ProductInfoResponse> responseList = new ArrayList<>();

    public ProductListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listTitle = listDataHeader;
        this._listDec = listChildData;
    }
    @Override
    public int getGroupCount() {
        return this._listTitle.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDec.get(this._listTitle.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listTitle.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this._listDec.get(this._listTitle.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_list_titel, null);
        }

        TextView txtTitle = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        txtTitle.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_list_desc, null);
        }

        TextView listDec = (TextView) convertView
                .findViewById(R.id.product_list_item);
        listDec.setText(childText);
        return convertView;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
