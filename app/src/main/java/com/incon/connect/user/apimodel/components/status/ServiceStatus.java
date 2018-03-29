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

import java.util.ArrayList;
import java.util.List;

public class ServiceStatus implements Parcelable {

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
    @SerializedName("statusList")
    @Expose
    private List<StatusList> statusList = null;

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

    public List<StatusList> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<StatusList> statusList) {
        this.statusList = statusList;
    }


    protected ServiceStatus(Parcel in) {
        customer = (Customer) in.readValue(Customer.class.getClassLoader());
        product = (Product) in.readValue(Product.class.getClassLoader());
        serviceCenter = (ServiceCenter) in.readValue(ServiceCenter.class.getClassLoader());
        request = (ServiceRequest) in.readValue(ServiceRequest.class.getClassLoader());
        assignedUser = (AssignedUser) in.readValue(AssignedUser.class.getClassLoader());
        preferredUser = (PreferredUser) in.readValue(PreferredUser.class.getClassLoader());
        if (in.readByte() == 0x01) {
            statusList = new ArrayList<StatusList>();
            in.readList(statusList, StatusList.class.getClassLoader());
        } else {
            statusList = null;
        }
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
        if (statusList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(statusList);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ServiceStatus> CREATOR = new Parcelable.Creator<ServiceStatus>() {
        @Override
        public ServiceStatus createFromParcel(Parcel in) {
            return new ServiceStatus(in);
        }

        @Override
        public ServiceStatus[] newArray(int size) {
            return new ServiceStatus[size];
        }
    };
}