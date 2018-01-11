package com.incon.connect.user.apimodel.components.status;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.incon.connect.user.apimodel.components.AssignedUser;
import com.incon.connect.user.apimodel.components.Customer;
import com.incon.connect.user.apimodel.components.PreferredUser;
import com.incon.connect.user.apimodel.components.Product;
import com.incon.connect.user.apimodel.components.ServiceCenter;
import com.incon.connect.user.apimodel.components.ServiceRequest;

import java.util.List;

public class ServiceStatus {

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

}