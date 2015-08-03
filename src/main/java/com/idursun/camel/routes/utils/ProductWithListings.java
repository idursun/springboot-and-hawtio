package com.idursun.camel.routes.utils;

import java.util.ArrayList;
import java.util.List;

public class ProductWithListings {

    Product product;

    List<Listing> listings;

    public ProductWithListings() {
        listings = new ArrayList<>();
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public List<Listing> getListings() {
        return listings;
    }
}
