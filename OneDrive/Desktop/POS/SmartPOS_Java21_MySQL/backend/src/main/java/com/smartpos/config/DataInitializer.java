package com.smartpos.config;

import com.smartpos.model.*;
import com.smartpos.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataInitializer {
    @Bean CommandLineRunner seed(UserRepository users, CategoryRepository cats, ProductRepository products, PasswordEncoder pe, AppProperties props){
        return args -> {
            if(users.count()==0){
                users.saveAll(List.of(
                        User.builder().employeeId("A001").name("System Owner").username("admin").passwordHash(pe.encode(props.getBootstrap().getAdminPassword())).role(RoleName.ADMIN).active(true).build(),
                        User.builder().employeeId("M001").name("Floor Manager").username("manager").passwordHash(pe.encode(props.getBootstrap().getManagerPassword())).role(RoleName.MANAGER).active(true).build(),
                        User.builder().employeeId("C001").name("Main Cashier").username("cashier").passwordHash(pe.encode(props.getBootstrap().getCashierPassword())).role(RoleName.CASHIER).active(true).build()
                ));
            }
            if(cats.count()==0){
                cats.saveAll(List.of(
                        Category.builder().name("Coffee").icon("coffee").active(true).build(),
                        Category.builder().name("Food").icon("utensils").active(true).build(),
                        Category.builder().name("Juice").icon("cup-soda").active(true).build(),
                        Category.builder().name("Pizza").icon("pizza").active(true).build(),
                        Category.builder().name("Dessert").icon("cake").active(true).build()
                ));
            }
            if(products.count()==0){
                Category coffee=cats.findByActiveTrueOrderByNameAsc().stream().filter(c->c.getName().equals("Coffee")).findFirst().orElse(null);
                Category food=cats.findByActiveTrueOrderByNameAsc().stream().filter(c->c.getName().equals("Food")).findFirst().orElse(null);
                Category juice=cats.findByActiveTrueOrderByNameAsc().stream().filter(c->c.getName().equals("Juice")).findFirst().orElse(null);
                Category pizza=cats.findByActiveTrueOrderByNameAsc().stream().filter(c->c.getName().equals("Pizza")).findFirst().orElse(null);
                Category dessert=cats.findByActiveTrueOrderByNameAsc().stream().filter(c->c.getName().equals("Dessert")).findFirst().orElse(null);
                products.saveAll(List.of(
                        p("CF001","Cappuccino",coffee,650,280,40,10,"/images/cappuccino.svg"),
                        p("CF002","Latte",coffee,700,300,35,10,"/images/latte.svg"),
                        p("FD001","Chicken Burger",food,1200,650,22,5,"/images/burger.svg"),
                        p("PZ001","Margherita Pizza",pizza,1800,900,14,4,"/images/pizza.svg"),
                        p("JU001","Orange Juice",juice,550,220,28,8,"/images/juice.svg"),
                        p("DS001","Chocolate Cake",dessert,800,350,12,4,"/images/cake.svg")
                ));
            }
        };
    }
    private Product p(String code,String name,Category cat,int sell,int cost,int stock,int min,String image){
        return Product.builder().itemCode(code).name(name).category(cat).sellingPrice(BigDecimal.valueOf(sell)).costPrice(BigDecimal.valueOf(cost))
                .unit("EA").currentStock(BigDecimal.valueOf(stock)).minStock(BigDecimal.valueOf(min)).active(true).trackInventory(true).allowDiscount(true).imageUrl(image).build();
    }
}
