package com.codecool.shop.model;

/**
 * Created by joker on 2017.04.25..
 */
public class LineItem {

    private Product product;
    private int quantity;
    private float price;

    public LineItem(Product product, int quantity){
        this.product = product;
        this.quantity = quantity;
        this.price = calculatePrice();
    }

    public Product getProduct(){return product;}

    public float calculatePrice(){
        return quantity * product.getDefaultPrice();
    }
}
