# ComfortZone

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Users will input how cold/warm the weather is at that time, and will be able to see a trend of what their temperature comfort zone is. Then, users can then find places to vacation to based on which comfort zone they want to visit. 
### App Evaluation
- **Category: Lifestyle/tracker**
- **Mobile: This app is mobile focused because it utilizes location and push notifications**
- **Story: Analyzes daily weather trends and compares it with your own feel of the temperature to help you with travel places**
- **Market: Mainly for people living in places where the weather is not constant, or for people who are sensitive to temperature changes**
- **Habit: Users should use this app at least once a day**
- **Scope: Start with being able to record how warm it is each day, and then have an algorthm that analyzes the trend to predict how you'll feel for future**

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* Users can login to access previously inputted data
* Users can see the weather for that exact time + location
* Users can input how cold/warm they feel for each corresponding day/time
* Users can see their temperature comfort zone
* Push notifications daily to remind users to input data
* map of temp comfort levesl and suggest travel places based on if they want to go to a cold/warm place

**Optional Nice-to-have Stories**

* settings for push notifications (ie time of day)
* add more than just temperature for calculations
* fb login
* can direct you to a flight page
* can filter time of travel
* can save places to travel
* map will have more than just USA
* Users can see a graph trend of their data
* Users can input the type of clothes they were wearing (ie. short sleeves, long pants) and that will be accounted for in the data too
* Users can receive information on what to wear for that weather type based on previously inputted data 
* editing comfort input 

### 2. Screen Archetypes

* Login
* Profile Screen
    * Users can see what temperature their "perfect temperature" is
* Input Screen
    * Can tell the app how cold/warm you feel at that current 
* Flight Screen
    * Shows a map of places with comparison with your comfort zone
* Push notification
* Settings
    * Logout, change degrees F/C, push notification settings

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
* Flight
    * displays map of USA and can filter 
    * clicking on a pin will show description
    * can book fliht -> directs you to google flights

## Wireframes
![](https://i.imgur.com/DSi6fXX.jpg)
![](https://i.imgur.com/V8TqXbB.jpg)

<img src="YOUR_WIREFRAME_IMAGE_URL" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
### Models
Temp tracker data

| Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user (default field) |
   | isFahrenheit | boolean | tells you if its in fahrenheit or not|
   | temperatureData        | int | temperature at input time |
   | comfortLevel   | int   | comfort level at that time |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |
   
   User Data
   
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user (default field) |
   | perfectComfort        | int | perfect comfort level |
   | name   | String   | name of user |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |
   | lastUpdated | DateTime | if the update was made today, dont need to update again | 
   | tempData | pointer | points to the temp tracker data chart | 
   
   
### Networking
- Login Screen
    - (Read/GET) Get user information based on login information
- Register Screen
    - (Create/POST) Create a new user object
- Flight Screen
    - (Read/GET) top 50 states
    - (Read/GET) Weather data for top 50 states
- Profile Screen
    - (Read/GET) Query logged in user data 
    - (Read/GET) Perfect comfort zone
- Input screen
    - (Read/GET) Weather data for current location
    - (Create/POST) comfort level for the day
    - (Update/POST) new comfort level
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
