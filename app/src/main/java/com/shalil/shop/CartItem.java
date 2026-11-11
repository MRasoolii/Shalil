package com.shalil.shop;
public class CartItem { public long id; public String name; public double price; public int qty; public CartItem(long i,String n,double p){id=i;name=n;price=p;qty=1;} public double total(){return price*qty;} }
