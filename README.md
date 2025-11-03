# Explore Boston – A Guided City Tour App

This project demonstrates how to build a **multi-screen navigation-based app** in **Jetpack Compose (Material 3)** that simulates a “tour” through Boston.  
The app allows users to navigate deeper through destinations (Home → Categories → List → Detail) while maintaining proper backstack behavior and argument passing.

---

## Features
- **Four Core Destinations**
  - **Home:** Introductory screen that starts the tour.
  - **Categories:** Lists main types of attractions (Museums, Parks, Restaurants).
  - **List:** Displays all locations within a selected category.
  - **Detail:** Shows information about a specific location.
- **Navigation and Stack Management**
  - Structured navigation routes with both `String` and `Int` arguments.
  - Proper stack control — returning “Home” clears the entire stack (`popUpTo` with `inclusive = true`).
  - Reusable top app bar (`AppTopBar`) on every screen with dynamic **Back** and **Home** buttons.
  - The **Back** button is disabled once the user returns to Home after a full navigation cycle.
- **Clean NavController Usage**
  - `rememberNavController()` is only used once in `MainActivity`.
  - All navigation logic is isolated inside a clean, separate `ExploreNavGraph.kt` file.
- **Material 3 Design**
  - Consistent theme and layout using Material 3 components: `Scaffold`, `CenterAlignedTopAppBar`, `NavigationBarItem`, `Button`, and `Card`.
- **Data and State Management**
  - In-memory dataset (`CityViewModel`) manages locations and categories.
  - Uses sealed `Route` objects to define type-safe routes and structured navigation paths.

---

## Technologies Used
- **Jetpack Compose (Material 3)**
- **Navigation for Compose**
- **Lifecycle ViewModel**
- **Kotlin Coroutines (Compose Runtime)**
- **PopUpTo and Backstack Control APIs**

---

## Documentation on AI Usage and Navigation Misunderstandings

**AI Usage:**  
This app was developed with assistance from **OpenAI’s ChatGPT (GPT-5, October 2025)** to help accelerate design and implementation.  
I used AI to generate the basic `NavGraph` structure, route definitions, and screen scaffolds, along with guidance for handling arguments (`String` and `Int` types).  
I personally reviewed, refined, and tested all navigation transitions, argument passing, and backstack behavior to ensure correctness, and implemented the more complicated parts of the code and structure myself, 
with some guidance from the AI on anything I hadn't learned before.  I also used it to help me write the README in a visually appealing manner.

**Where AI Misunderstood Navigation:**  
Initially, AI proposed:
- Placing `rememberNavController()` inside each screen, which would have created multiple independent navigation graphs and broken stack consistency.  
- Using `Crossfade` for transitions (which caused runtime errors and confused argument usage.  It seems to really love crossfade for some reason).  
- Not setting `inclusive = true` on the `popUpTo` call when returning Home, resulting in stale destinations persisting in the backstack.  

I corrected these misunderstandings by:
- Keeping one single shared `NavController` in `MainActivity`.  
- Moving all navigation routes and logic to a **dedicated `ExploreNavGraph.kt`** file for clarity and separation of concerns.  
- Implementing `popUpTo(..., inclusive = true)` and a BackHandler for proper full-stack clearing and disabled back behavior after returning Home.

- Returning to Home disables the system back button.  
- Navigation arguments (`category`, `id`) correctly display details for each location.
