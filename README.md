# OpenWeather Application

## About

OpenWeather is an application that provides real-time weather updates for your current location or any city of your choice globally. It leverages the OpenWeather API to fetch weather data, providing information such as temperature, humidity, precipitation, and much more.

OpenWeather is built with Kotlin, utilizing modern Android development practices such as Coroutines for asynchronous programming, Jetpack DataStore for data persistence, and Hilt for dependency injection. 

The app follows a clean architecture approach.



## User Features

1. **Current Weather Information**: Get detailed weather information about your current location or any city worldwide.

2. **Weather Forecast**: View the forecast for the upcoming days with detailed data about temperature changes, rain probability, humidity, and more.

3. **Location Search**: Enter the name of a city to get the weather information for that location.

4. **Data Persistence**: The app uses Jetpack DataStore for data persistence, storing your latest search and location data, providing you with relevant weather information each time you open the app.
  

## Data Layer

At the heart of the app, the data layer is responsible for fetching data from the OpenWeather API and storing the data locally for offline use. This layer is further divided into the following sub-layers:

Remote: This is responsible for making network calls to the OpenWeather API. We use Retrofit as the HTTP client, which converts the API's JSON responses into Kotlin objects using Moshi.

Local: This is responsible for local data persistence. The app uses Jetpack DataStore to persist data, ensuring that users can still access weather information when offline or in poor network conditions.

Repository: This is responsible for deciding whether to fetch data from the network or use locally stored data. It's the single source of truth for data in the app and provides a clean API for the upper layers (domain and presentation) to use.

Domain Layer
The domain layer contains all the business logic of the application. It defines use cases, which are single pieces of functionality that the app can perform. The domain layer is independent of the data source, meaning it doesn't know where the data comes from (the OpenWeather API, the local database, etc.). This allows for clear separation of concerns and easier testing.

Presentation Layer
The presentation layer handles the UI of the app, following the MVVM pattern. It contains the View (represented by Activities and Fragments) and the ViewModel. The ViewModel fetches data from the use cases defined in the domain layer and transforms it into a form that can be easily displayed by the View. The View observes data in the ViewModel and updates the UI accordingly.

The OpenWeather app leverages several Jetpack libraries to implement the presentation layer, including LiveData for data observation, Navigation Component for in-app navigation, and View Binding for easier interaction with Views.

Dependency Injection
To manage dependencies across the app, we use Hilt, a dependency injection library built on top of Dagger. Hilt simplifies the implementation of DI and reduces boilerplate code, making the codebase cleaner and easier to understand.
