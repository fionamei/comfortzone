# ComfortZone

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

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

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* Users can login to access previously inputted data and signup
    * Users will also be prompted to type their estimated comfort levels upon signup
* Users can see the weather for that exact time + location
* Users can input how cold/warm they feel for each corresponding day/time (0 for cold, 10 for warm, 5 for perfect!)
    * Users can also see their inputs for the day
* Users can see their calculated perfect temperature comfort zone
* Users can see a list of travel places to go to, and their current temperatures

**Optional Nice-to-have Stories**

* Map view of the cities to go to (displays the same data as list view, but im map form)
* Displays a description and picture for each city
* Users can be directed to another browser to book a flight to their chosen city
* Users are able to filter through comfort level ranges, sort by different filters, and search for specific cities
    * Bonus if it can work with map view!
* Double tap on list view to save (and unsave) a city 
    * Saved cities will show up on profile
    * Saved cities will have an outline around them in list view
* Improve efficiency by loading data only once, and getting them from one place
    * Implement a loading screen for when data is loading to help better user experience
* Allow users to choose if they want to see the temperatures in Fahrenheit or Celsius 
    * Saves to preferences so it remains when users log out and log in
* Facebook login
* Changing number of cities that are displayed in map view
* Dark mode support
* Get weather data only once
    * Refresh button for weather data if they wish to query again

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

<img src="YOUR_WIREFRAME_IMAGE_URL" width=600>

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
   
   
   ComfortLevelEntry Data

| Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user (default field) |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |
   | temp | int | temperature of entry |
   | comfortLevel | int | comfort level for that temperature |
   | LevelTracker | pointer | points to the level tracker that this entry belongs to |
   
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
