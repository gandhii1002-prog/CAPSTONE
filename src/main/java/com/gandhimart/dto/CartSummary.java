package com.gandhimart.dto;

import com.gandhimart.model.CartItem;

import java.math.BigDecimal;
import java.util.List;

public class CartSummary {

    private List<CartItem> items;
    private BigDecimal total;

    public CartSummary() {
    }

    public CartSummary(List<CartItem> items, BigDecimal total) {
        this.items = items;
        this.total = total;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}