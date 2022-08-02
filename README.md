# ComfortZone

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)
3. [Weekly Progression](#Weekly-Progression)

## Overview
### Description
ComfortZone is a comfort level tracker where users will input how cold/warm the weather feels to them at that time, and every day, they will be able to see their calculated perfect comfort level. Users can then find places to visit to based on which comfort zone they want to be in. 
### App Evaluation
- **Category: Lifestyle/tracker**
- **Mobile: This app is mobile focused because it utilizes location**
- **Story: Analyzes daily weather trends and compares it with your own feel of the temperature to help you with travel places**
- **Market: Mainly for people living in places where the weather is not constant, or for people who are sensitive to temperature changes**
- **Habit: Users should use this app at least once a day**
- **Scope: Start with being able to record how warm it is each day, and then have an algorthm that analyzes the trend to predict how you'll feel for future**

### Progression + Demos
<img src='./comfortZone.gif' title='Video Walkthrough' width='300' alt='Video Walkthrough' />

[Weekly Progress with Meta Internal Access](https://www.internalfb.com/intern/px/project/590975805500309)

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

- [x] Users can login to access previously inputted data and signup
    - [x] Users will also be prompted to type their estimated comfort levels upon signup
- [x] Users can see the weather for that exact time + location
- [x] Users can input how cold/warm they feel for each corresponding day/time (0 for cold, 10 for warm, 5 for perfect!)
    - [x] Users can also see their inputs for the day
- [x] Users can see their calculated perfect temperature comfort zone
- [x] Users can see a list of travel places to go to, and their current temperatures

**Optional Nice-to-have Stories**

- [x] Map view of the cities to go to (displays the same data as list view, but im map form)
- [x] Displays a description and picture for each city
- [x] Users can be directed to another browser to book a flight to their chosen city
- [x] Users are able to filter through comfort level ranges, sort by different filters, and search for specific cities
    - [x] Bonus if it can work with map view!
- [x] Double tap on list view to save (and unsave) a city 
    - [x] Saved cities will show up on profile
    - [x] Saved cities will have an outline around them in list view
- [x] Improve efficiency by loading data only once, and getting them from one place
    - [x] Implement a loading screen for when data is loading to help better user experience
- [x] Allow users to choose if they want to see the temperatures in Fahrenheit or Celsius 
    - [x] Saves to preferences so it remains when users log out and log in
- [x] Facebook login
- [x] Changing number of cities that are displayed in map view
- [x] Get weather data only once
    - [x] Refresh button for weather data if they wish to query again
- [x] Notifications
    - [x] Users can turn on/off
    - [x] Users can choose time 
    - [x] Users can choose notification frequency
- [x] Global cities
- [ ] Dark Mode Support

### 2. Screen Archetypes

* Login/Signup
* Profile Screen
    * Users can see what temperature their "perfect temperature" is
* Input Screen
    * Can tell the app how cold/warm you feel at that current 
* Cities Screen
    * Shows a list of cities with their temperatures

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Input Screen
* Profile Screen
* Flight Screen

**Flow Navigation** (Screen to Screen)

* Forced login/signup if not logged in
    * Asks for user to confirm their location/location permissions
* Input Screen
    * Asks for initial input for how warm/cold it is
* Profile
    * displays perfect temperature
* City
    * displays map of USA and can filter 
    * clicking on a pin will show description
* City detail
    * Shows description of city
    * can book flight -> directs you to flight booking page

## Wireframes
![](https://i.imgur.com/q7SNtv9.jpg)
![](https://i.imgur.com/hUCN7S0.jpg)

### [BONUS] Digital Wireframes & Mockups
[Figma Wireframe Link](https://www.figma.com/file/ZEIW18TantALBm7mWBTjZM/ComfortZone-Wireframes?node-id=0%3A1)
![](https://i.imgur.com/H6ouBfE.png)

## Schema 
### Models

 User Data
   
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user (default field) |
   | name   | String   | name of user |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |
   | perfectComfort| int | the calculated perfect comfort level |
   | levelTrackers | Array of levelTrackers | points to a list of 11 level trackers, 1 for each level of comfort |
   | todayEntries | Array of ComfortLevelEntries | points to a list of comfort level entries made today |
   
  <img width="1424" alt="parse_user" src="https://user-images.githubusercontent.com/82325190/181398647-a396fd65-37df-4c03-9dba-246f8e78ad19.png">
   
LevelTrackers Data

| Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user (default field) |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |
   | level | int | represents the comfort level (from 0-10)
   | count | int | number of entries made for that entry in total
   | entriesList | array of ComfortLevelEntries | list of all entries made by the user for that level |
   | average | int | the average of all temperatures from entries in entriesList, -999 if no values in entriesList |
   | tempAverage | int | temporary average, representing the average value after the comfort averages have been corrected (meaning they are in ascending order, nulling the out of place values) |
   | highRange | int | high range for comfort level|
   | lowRange | int | low range for comfort level |
   
   <img width="1425" alt="parse_level_tracker" src="https://user-images.githubusercontent.com/82325190/181398520-adeb0618-4b11-4f5d-a81d-be451a408189.png">
   
   ComfortLevelEntry Data

| Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user (default field) |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |
   | temp | int | temperature of entry |
   | comfortLevel | int | comfort level for that temperature |
   | LevelTracker | pointer | points to the level tracker that this entry belongs to |
   
   <img width="1429" alt="parse_comfort_leevl_entry" src="https://user-images.githubusercontent.com/82325190/181398530-1a5ad9ca-185e-4e38-a057-fb952047ecb8.png">
   
### Networking
- Login Screen
    - (Read/GET) Get user information based on login information
- Register Screen
    - (Create/POST) Create a new user object
- City Screen
    - (Read/GET) 198 cities
    - (Read/GET) Weather data for top 198 cities
    - (Update/POST) Save and unsave cities
- Profile Screen
    - (Read/GET) Query logged in user data 
    - (Read/GET) Perfect comfort zone
    - (Update/POST) Perfect comfort zone every day
- Input screen
    - (Read/GET) Weather data for current location
    - (Create/POST) comfort level for the day
    - (Update/POST) new comfort level
    - (Delete) comfort entry

## Weekly Progression
### Week 4
- Goals: 
    - Setup Parse database
    - Login / signup page + initial questionaire
    - Set up bottom navigation with fragments
    - Set up weather data with OpenWeatherMap and get user's location
- Optional Goals: 
    - Design layouts for fragments with Figma
    - Implement fragment layout screens

### Week 5
- Goals: 
    - Input page displays weather data and asks for comfort level
        - Daily inputted comfort level is displayed and swipe to delete
    - Profile page displays perfect comfort temperature
    - Profile calculates and updates comfort temperature for the most recent day's entries 
    - Flights page shows a list of top cities in the US
        - Weather data for all cities are queried with OpenWeatherMap, stored in local database, and displayed 
    
### Week 6
- Goals: 
    - Flight detail screen displays city information
        - Shows animation from list view to city information
    - Algorithm to calculate which level a certain temperature falls under
    - Filtering for states with temps within your comfort zone
    - Sorting by distance (nearest/furthest)
    - Searching by cities 

- Optional Goals:
    - Sorting by temperature, popularity, and alphabetically

### Week 7
- Goals: 
    - Map View (Google maps) shows pins of places
        - Can switch between map view and city list view, keeping the filters, and sorting, and searches
    - Refactoring
    - Going to a flight booking link on another browser
    - Udpate city.json with the IATA codes, descriptions, and pictures for each city

- Optional Goals:
    - Animation when switching between fragments on bottom nav

### Week 8
- Goals: 
    - Double tap city to save and unsave
        - Saved cities are displayed on profile, and you can swipe to unsave them there
    - Loading screen where all async functions needed for app loads (for getting location, IATA, and saved cities list)
        - Goes to whichever fragment the user clicks on after loading (default is profile fragment)
        - Pre-loads data for ease of getting in other fragments (decrease wait-time)
    - Fix sorting so all filters and searches are working completely together

- Optional Goals:
    - Switch between Fahrenheit and Celsius on app (and saves)
    - FB login and signup
### Week 9
- Goals: 
    - Ui Polish
        - Drew a logo :D 
        - Fix cities' names that are long and will cut off
        - Make views scrollable
        - Let user know for cases where there are no flights
- Optional Goals:
    - Fetch weather data in loading screen, and allow user to refresh weather data
    - Change amount of pins shown in map view
    - Push Notifications remind user to input weather data, and brings to input fragment
        - Settings for turning on/off, for time of day for notification, and for how frequent user wants the notification to be
    - Include cities outside of the USA
