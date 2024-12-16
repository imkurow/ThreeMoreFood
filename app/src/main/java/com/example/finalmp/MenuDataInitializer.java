package com.example.finalmp;

import com.example.finalmp.Menu;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MenuDataInitializer {
    private DatabaseReference menuRef;

    public MenuDataInitializer() {
        menuRef = FirebaseDatabase.getInstance().getReference().child("menus");
    }

    public void initializeMenuData() {
        // Appetizer
        List<Menu> appetizers = new ArrayList<>();
        appetizers.add(new Menu(
                "app1",
                "Lumpia Goreng",
                "Lumpia goreng isi sayuran segar dengan saus sambal",
                "Appetizer",
                15000,
                "drawable/lumpia.jpeg"
        ));
        appetizers.add(new Menu(
                "app2",
                "Salad Caesar",
                "Salad segar dengan dressing caesar dan crouton",
                "Appetizer",
                25000,
                "drawable/caesarsalad.jpeg"
        ));
        appetizers.add(new Menu(
                "app3",
                "Risoles Mayo",
                "Risoles isi daging ayam dan mayonaise",
                "Appetizer",
                12000,
                "drawable/risoles.jpeg"
        ));

        // Main Course
        List<Menu> mainCourses = new ArrayList<>();
        mainCourses.add(new Menu(
                "main1",
                "Nasi Goreng Spesial",
                "Nasi goreng dengan telur, ayam, dan sayuran",
                "Main Course",
                35000,
                "drawable/nasigorengspecial.jpg"
        ));
        mainCourses.add(new Menu(
                "main2",
                "Mie Goreng Seafood",
                "Mie goreng dengan udang, cumi, dan sayuran",
                "Main Course",
                40000,
                "drawable/miegorengseafood.jpg"
        ));
        mainCourses.add(new Menu(
                "main3",
                "Ayam Bakar",
                "Ayam bakar bumbu kecap dengan nasi dan lalapan",
                "Main Course",
                38000,
                "drawable/ayambakar.jpg"
        ));

        // Dessert
        List<Menu> desserts = new ArrayList<>();
        desserts.add(new Menu(
                "des1",
                "Es Krim Vanilla",
                "Es krim vanilla dengan topping coklat",
                "Dessert",
                20000,
                "drawable/icecreamvanilla.jpeg"
        ));
        desserts.add(new Menu(
                "des2",
                "Pudding Coklat",
                "Pudding coklat dengan saus vanilla",
                "Dessert",
                18000,
                "drawable/puddingcoklat.jpeg"
        ));
        desserts.add(new Menu(
                "des3",
                "Pisang Goreng",
                "Pisang goreng crispy dengan topping keju dan coklat",
                "Dessert",
                15000,
                "drawable/pisanggoreng.jpeg"
        ));

        // Upload to Firebase
        uploadMenus(appetizers);
        uploadMenus(mainCourses);
        uploadMenus(desserts);
    }

    private void uploadMenus(List<Menu> menus) {
        for (Menu menu : menus) {
            menuRef.child(menu.getId()).setValue(menu)
                    .addOnSuccessListener(aVoid -> {
                        System.out.println("Menu " + menu.getName() + " berhasil ditambahkan");
                    })
                    .addOnFailureListener(e -> {
                        System.out.println("Error menambahkan menu " + menu.getName() + ": " + e.getMessage());
                    });
        }
    }
}