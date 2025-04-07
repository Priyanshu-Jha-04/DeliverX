# 🚚 Deliver X

Deliver X is a smart delivery route optimization app that helps delivery agents and logistics providers find the most efficient routes in real-time. It leverages Google Maps for navigation and a Genetic Algorithm to solve the Traveling Salesman Problem (TSP) for multiple destinations. Whether you're managing a delivery fleet or just planning your own multi-stop trip, Deliver X has you covered.

---

## 📱 Features

- 🗺️ **Real-time Google Maps Integration**
- 🧠 **Shortest Path Finder using Genetic Algorithm**
- ➕ **Add Upto 10 Custom Locations**
- 🔁 **Optimized Route Visualization** (Coming Soon)
- ⚡ **Fast & Lightweight UI using Jetpack Compose**
- 🧩 **Modular Architecture with MVVM + Clean Architecture**
- 🔐 **Secure API key management**

---

## 🧬 How the Genetic Algorithm Works

Deliver X uses a Genetic Algorithm (GA) to solve the **Traveling Salesman Problem (TSP)** — optimizing the shortest route that visits all locations once and returns to the origin.

### GA Components:
- **Chromosomes** = Route permutations
- **Population** = Set of possible route permutations
- **Fitness Function** = Total travel distance of a route
- **Selection** = Tournament or roulette wheel
- **Crossover** = Ordered crossover (OX)
- **Mutation** = Swap mutation
- **Elitism** = Retains best routes across generations

---

## 🧑‍💻 Tech Stack

- **Kotlin** with **Jetpack Compose**
- **Google Maps SDK**
- **Genetic Algorithm** for route optimization
- **Hilt** for Dependency Injection
- **Coroutines & Flow** for asynchronous operations
- **Material 3 UI**
- **LiveData/ViewModel** for state management
- **Permissions Handling** with Accompanist

---

## 🖼️ UI Overview (Screens)

- **Splash Screen** – App logo & animation
- **Home Screen** – Current location + destination input
- **Map Screen** – Real-time map with optimized route
- **Route Summary** – Distance, estimated time, stops

---

## 📦 Architecture

```plaintext
com.deliverx
│
├── data
│   ├── model          # Data classes
│   └── repository     # Google Maps & location API handling
│
├── domain
│   ├── usecase        # Route calculation & optimization
│   └── algorithm      # Genetic Algorithm implementation
│
├── presentation
│   ├── ui             # Jetpack Compose screens
│   └── viewmodel      # State & event management
│
├── di                 # Hilt modules
└── utils              # Helpers, constants, permissions
