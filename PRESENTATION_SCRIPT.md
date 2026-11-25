# VEHICLE PARKING MANAGEMENT SYSTEM (PARKFLOW)
## Simple, Fluent 6-Minute Presentation Script
**Course:** CSE 213 — Advanced Java Programming  
**University:** SRM University – AP  
**Faculty Supervisor:** Dr. Prakash Ankur  
**Team Members:** Chandrala Rishit Prem, M. Hiranya Kumar, K. Komal, G.V.N.S.S. Jitin  

---

### Key Guidelines for Presenting:
- **Total Duration:** Exactly 6 Minutes (~360 seconds).
- **Word Count:** ~620 words total (~100–105 words per minute). This gives you plenty of breathing room, natural pauses, and clear pronunciation with zero tongue twisters.
- **Language:** Clean, simple, conversational English. No complex jargon or twisted phrasing.

---

### Slide 1: Welcome & Team Intro (0:00 – 0:25 | ~45 words)
> *"Good morning respected sir and evaluators.  
> Today, our team—Rishit Prem, Hiranya Kumar, Komal, and Jitin—is excited to present **ParkFlow**, our desktop **Vehicle Parking Management System**.  
> We built this project in Java and SQLite under the guidance of our faculty supervisor, **Dr. Prakash Ankur**."*

---

### Slide 2: Presentation Outline (0:25 – 0:45 | ~35 words)
> *"Here is what we will cover today:  
> We will look at why manual parking fails, how our Java application solves it, our code design and billing rules, live screenshots of our app, and our automated testing results."*

---

### Slide 3: The Problem We Solved (0:45 – 1:10 | ~45 words)
> *"In manual parking lots, attendants write tickets on paper.  
> This causes slots to get double-booked, attendants to make calculation mistakes, and long queues at the exit.  
> Our Java desktop app fixes all this by tracking slots automatically, finding vehicles instantly, and calculating exact bills."*

---

### Slide 4: System Architecture (1:10 – 1:30 | ~40 words)
> *"Our project has three clean layers:  
> First is the **User Interface**, built with Java Swing.  
> Second is the **Service Layer**, where our Java classes manage bookings and billing.  
> Third is the **Database Layer**, which saves all tickets securely in SQLite."*

---

### Slide 5: Core OOP Concepts Used (1:30 – 1:50 | ~40 words)
> *"We applied core Object-Oriented principles:  
> We have a common `Vehicle` class, and child classes for `Bike`, `Car`, and `Truck`.  
> Each vehicle type knows its own rate and slot requirement.  
> This keeps our code clean, modular, and easy to maintain."*

---

### Slide 6: Fast Lookups & Modern Java (1:50 – 2:15 | ~45 words)
> *"We used modern Java features for high performance:  
> - We used **HashMaps** so we can find any parked vehicle in constant time without looping through lists.  
> - We used **Java Streams** to filter history records and calculate total revenue.  
> - And we used `LocalDateTime` to track exact entry and exit times."*

---

### Slide 7: Our Billing Rule (2:15 – 2:40 | ~45 words)
> *"Our pricing is simple and fair:  
> Every vehicle is charged **50 rupees per hour**, with a **minimum charge of one hour**.  
> If someone parks for even one extra minute, the system rounds up to the next full hour.  
> For example: 10 minutes costs 50 rupees, while 70 minutes rounds up to 2 hours, which is 100 rupees."*

---

### Slide 8: Database & 30 Parking Slots (2:40 – 3:00 | ~35 words)
> *"We used an embedded SQLite database.  
> When the app runs for the first time, it automatically creates **30 parking slots**:  
> 10 for Bikes, 15 for Cars, and 5 for Trucks.  
> Everything is saved locally with no server setup needed."*

---

### Slide 9: How a Vehicle Moves Through the System (3:00 – 3:20 | ~40 words)
> *"The workflow is straightforward:  
> When a vehicle arrives, the attendant enters the number plate and driver name.  
> The system finds the next free slot, marks it occupied, and prints a ticket.  
> At checkout, entering the vehicle number calculates the bill and frees the slot."*

---

### Slide 10: Handling Errors Safely (3:20 – 3:40 | ~35 words)
> *"We created custom Java exceptions so the app never crashes:  
> If someone types an invalid number plate, tries to park the same car twice, or if a section is completely full, a clear pop-up message guides the user."*

---

### Slide 11: Multithreading & Auto-Save (3:40 – 4:05 | ~40 words)
> *"We used two separate threads to keep the app smooth:  
> The main Swing thread updates the screen and the live clock.  
> Meanwhile, a background thread runs quietly every 30 seconds to save all pending changes to the database.  
> This ensures the screen never freezes."*

---

### Slide 12: Live App: Dashboard & Check-In (4:05 – 4:35 | ~50 words)
> *"Now let's look at the actual application:  
> On the left, you can see our **Dashboard**. It shows total slots, occupied slots, live revenue, and a visual grid of all 30 parking spots.  
> On the right is the **Check-In screen**, where entering a car assigns slot C-01 and generates a digital ticket."*

---

### Slide 13: Live App: Check-Out & Receipt (4:35 – 5:00 | ~45 words)
> *"When a vehicle leaves:  
> On the left, the attendant enters the vehicle number. The app instantly shows entry time, total minutes, and the calculated fee.  
> On the right is the official **Receipt dialog**, showing the company header, all parking details, and the total amount paid."*

---

### Slide 14: Live App: Parking History (5:00 – 5:20 | ~35 words)
> *"This is our **Parking History** screen.  
> Supervisors can search past records by vehicle number, driver name, or date.  
> The green bar at the bottom automatically sums up the total completed vehicles and collected revenue."*

---

### Slide 15: Testing & Verification (5:20 – 5:40 | ~40 words)
> *"To ensure everything works properly, we wrote **11 automated tests**.  
> These tests check our billing calculations, slot full warnings, database reloads, and background auto-save.  
> Every single test passed with a **100% success rate**."*

---

### Slide 16: Team Roles (5:40 – 5:55 | ~35 words)
> *"All four of us contributed across the project:  
> Rishit built the user interface and dashboard;  
> Hiranya handled the database and SQLite connections;  
> Komal built the billing logic and exception handling;  
> And Jitin developed the background auto-save and testing suite."*

---

### Slide 17: Summary & Conclusion (5:55 – 6:10 | ~35 words)
> *"To conclude, ParkFlow successfully automates parking operations with a fast, modern desktop application.  
> In the future, we can add camera number plate recognition and UPI payments.  
> We thank our mentor **Dr. Prakash Ankur** for his support.  
> We are now happy to answer your questions. Thank you!"*
