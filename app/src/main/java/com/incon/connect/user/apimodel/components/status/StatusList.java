package com.incon.connect.user.apimodel.components.status;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.incon.connect.user.apimodel.components.AssignedUser;
import com.incon.connect.user.apimodel.components.Customer;
import com.incon.connect.user.apimodel.components.PreferredUser;
import com.incon.connect.user.apimodel.components.Product;
import com.incon.connect.user.apimodel.components.ServiceCenter;
import com.incon.connect.user.apimodel.components.ServiceRequest;

public class StatusList implements Parcelable {

    @SerializedName("customer")
    @Expose
    private Customer customer;
    @SerializedName("product")
    @Expose
    private Product product;
    @SerializedName("serviceCenter")
    @Expose
    private ServiceCenter serviceCenter;
    @SerializedName("request")
    @Expose
    private ServiceRequest request;
    @SerializedName("assignedUser")
    @Expose
    private AssignedUser assignedUser;
    @SerializedName("preferredUser")
    @Expose
    private PreferredUser preferredUser;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ServiceCenter getServiceCenter() {
        return serviceCenter;
    }

    public void setServiceCenter(ServiceCenter serviceCenter) {
        this.serviceCenter = serviceCenter;
    }

    public ServiceRequest getRequest() {
        return request;
    }

    public void setRequest(ServiceRequest request) {
        this.request = request;
    }

    public AssignedUser getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(AssignedUser assignedUser) {
        this.assignedUser = assignedUser;
    }

    public PreferredUser getPreferredUser() {
        return preferredUser;
    }

    public void setPreferredUser(PreferredUser preferredUser) {
        this.preferredUser = preferredUser;
    }


    protected StatusList(Parcel in) {
        customer = (Customer) in.readValue(Customer.class.getClassLoader());
        product = (Product) in.readValue(Product.class.getClassLoader());
        serviceCenter = (ServiceCenter) in.readValue(ServiceCenter.class.getClassLoader());
        request = (ServiceRequest) in.readValue(ServiceRequest.class.getClassLoader());
        assignedUser = (AssignedUser) in.readValue(AssignedUser.class.getClassLoader());
        preferredUser = (PreferredUser) in.readValue(PreferredUser.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(customer);
        dest.writeValue(product);
        dest.writeValue(serviceCenter);
        dest.writeValue(request);
        dest.writeValue(assignedUser);
        dest.writeValue(preferredUser);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<StatusList> CREATOR = new Parcelable.Creator<StatusList>() {
        @Override
        public StatusList createFromParcel(Parcel in) {
            return new StatusList(in);
        }

        @Override
        public StatusList[] newArray(int size) {
            return new StatusList[size];
        }
    };
}