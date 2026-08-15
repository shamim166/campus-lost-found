package com.lostfound.config;

import com.lostfound.factory.ItemFactory;
import com.lostfound.model.Category;
import com.lostfound.model.Item;
import com.lostfound.model.ItemType;
import com.lostfound.model.User;
import com.lostfound.repository.ItemRepository;
import com.lostfound.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DemoDataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (true) { // Force seed for now
            System.out.println("Seeding demo data...");

            // Create Demo Users if not exist
            User alice = userRepository.findByEmail("alice@demo.com").orElse(null);
            if (alice == null) {
                alice = new User();
                alice.setName("Alice Demo");
                alice.setEmail("alice@demo.com");
                alice.setPassword(passwordEncoder.encode("password"));
                userRepository.save(alice);
            }

            User bob = userRepository.findByEmail("bob@demo.com").orElse(null);
            if (bob == null) {
                bob = new User();
                bob.setName("Bob Demo");
                bob.setEmail("bob@demo.com");
                bob.setPassword(passwordEncoder.encode("password"));
                userRepository.save(bob);
            }

            // --- 5 LOST ITEMS ---

            // 1
            Item item1 = ItemFactory.createItem(Category.ELECTRONICS);
            item1.setType(ItemType.LOST);
            item1.setTitle("Lost MacBook Pro M2");
            item1.setDescription("Left it in the main library on the 2nd floor near the study area.");
            item1.setCategory(Category.ELECTRONICS);
            item1.setLocation("Main Library");
            item1.setItemDate(LocalDate.now().minusDays(2));
            item1.setItemTime(LocalTime.of(14, 30));
            item1.setImageUrl("https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=500&q=80");
            item1.setUser(alice);
            itemRepository.save(item1);

            // 2
            Item item3 = ItemFactory.createItem(Category.BAG);
            item3.setType(ItemType.LOST);
            item3.setTitle("Black NorthFace Backpack");
            item3.setDescription("Contains my notebooks and a water bottle.");
            item3.setCategory(Category.BAG);
            item3.setLocation("Science Building Room 101");
            item3.setItemDate(LocalDate.now());
            item3.setItemTime(LocalTime.of(9, 0));
            item3.setImageUrl("https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=500&q=80");
            item3.setUser(alice);
            itemRepository.save(item3);

            // 3
            Item item5 = ItemFactory.createItem(Category.OTHER);
            item5.setType(ItemType.LOST);
            item5.setTitle("Set of House Keys");
            item5.setDescription("3 keys with a Pikachu keychain.");
            item5.setCategory(Category.OTHER);
            item5.setLocation("Parking Lot B");
            item5.setItemDate(LocalDate.now());
            item5.setItemTime(LocalTime.of(8, 20));
            item5.setImageUrl("https://images.unsplash.com/photo-1582139329536-e7284fece509?w=500&q=80");
            item5.setUser(bob);
            itemRepository.save(item5);

            // 4
            Item item7 = ItemFactory.createItem(Category.ACCESSORY);
            item7.setType(ItemType.LOST);
            item7.setTitle("Ray-Ban Aviator Glasses");
            item7.setDescription("Lost my sunglasses near the cafeteria.");
            item7.setCategory(Category.ACCESSORY);
            item7.setLocation("Cafeteria");
            item7.setItemDate(LocalDate.now().minusDays(1));
            item7.setItemTime(LocalTime.of(12, 0));
            item7.setImageUrl("https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=500&q=80");
            item7.setUser(bob);
            itemRepository.save(item7);

            // 5
            Item item9 = ItemFactory.createItem(Category.DOCUMENT);
            item9.setType(ItemType.LOST);
            item9.setTitle("Leather Wallet");
            item9.setDescription("Contains ID and some cash. Please return if found.");
            item9.setCategory(Category.DOCUMENT);
            item9.setLocation("Gym locker room");
            item9.setItemDate(LocalDate.now().minusDays(3));
            item9.setItemTime(LocalTime.of(18, 30));
            item9.setImageUrl("https://images.unsplash.com/photo-1627123424574-724758594e93?w=500&q=80");
            item9.setUser(alice);
            itemRepository.save(item9);


            // --- 5 FOUND ITEMS ---

            // 6
            Item item2 = ItemFactory.createItem(Category.DOCUMENT);
            item2.setType(ItemType.FOUND);
            item2.setTitle("Student ID Card - John Doe");
            item2.setDescription("Found a student ID card near the cafeteria entrance.");
            item2.setCategory(Category.DOCUMENT);
            item2.setLocation("Cafeteria");
            item2.setItemDate(LocalDate.now().minusDays(1));
            item2.setItemTime(LocalTime.of(10, 15));
            item2.setImageUrl("https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=500&q=80"); // book/id placeholder
            item2.setUser(bob);
            itemRepository.save(item2);

            // 7
            Item item4 = ItemFactory.createItem(Category.ACCESSORY);
            item4.setType(ItemType.FOUND);
            item4.setTitle("Silver Casio Watch");
            item4.setDescription("Found on the bench near the sports complex.");
            item4.setCategory(Category.ACCESSORY);
            item4.setLocation("Sports Complex Bench");
            item4.setItemDate(LocalDate.now().minusDays(3));
            item4.setItemTime(LocalTime.of(16, 45));
            item4.setImageUrl("https://images.unsplash.com/photo-1523170335258-f5ed11844a49?w=500&q=80");
            item4.setUser(alice);
            itemRepository.save(item4);

            // 8
            Item item6 = ItemFactory.createItem(Category.ELECTRONICS);
            item6.setType(ItemType.FOUND);
            item6.setTitle("Sony Wireless Headphones");
            item6.setDescription("Black headphones left on a desk in the library.");
            item6.setCategory(Category.ELECTRONICS);
            item6.setLocation("Library");
            item6.setItemDate(LocalDate.now());
            item6.setItemTime(LocalTime.of(15, 0));
            item6.setImageUrl("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500&q=80");
            item6.setUser(bob);
            itemRepository.save(item6);

            // 9
            Item item8 = ItemFactory.createItem(Category.ELECTRONICS);
            item8.setType(ItemType.FOUND);
            item8.setTitle("iPhone 13 Pro");
            item8.setDescription("Found a phone with a blue case near the north gate.");
            item8.setCategory(Category.ELECTRONICS);
            item8.setLocation("North Gate");
            item8.setItemDate(LocalDate.now().minusDays(2));
            item8.setItemTime(LocalTime.of(9, 30));
            item8.setImageUrl("https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500&q=80");
            item8.setUser(alice);
            itemRepository.save(item8);

            // 10
            Item item10 = ItemFactory.createItem(Category.OTHER);
            item10.setType(ItemType.FOUND);
            item10.setTitle("Blue Water Bottle");
            item10.setDescription("HydroFlask found in lecture hall A.");
            item10.setCategory(Category.OTHER);
            item10.setLocation("Lecture Hall A");
            item10.setItemDate(LocalDate.now().minusDays(1));
            item10.setItemTime(LocalTime.of(11, 0));
            item10.setImageUrl("https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=500&q=80");
            item10.setUser(bob);
            itemRepository.save(item10);


            System.out.println("Demo data seeded successfully with 10 items.");
        }
    }
}
